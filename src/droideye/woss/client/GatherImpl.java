package droideye.woss.client;


import com.briup.util.BIDR;
import com.briup.woss.client.Gather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import droideye.util.ConfigurationImpl;
import droideye.util.LoggerImpl;

public class GatherImpl implements Gather {

    private String filePath;

    @Override
    public void init(Properties properties) {
        filePath = properties.getProperty("data-file");
    }

    @Override
    public Collection<BIDR> gather() throws Exception {

        Map<String, Integer> maps = new HashMap<>();
        Map<String, BIDR> bidrMap = new HashMap<>();

        ArrayList<BIDR> bidrs = new ArrayList();

        File file = new File(filePath);

        if (!file.exists()) return new ArrayList<>();
        try (
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file)));
                PrintStream ps = new PrintStream(
                        new FileOutputStream("src/droideye/file/radwtmp2.txt"))
        ) {
            //System.out.println("开始采集文件信息...");
            new ConfigurationImpl().getLogger().info("开始采集文件信息...");

            String line;
            while ((line = br.readLine()) != null) {
                // 读取到数据 分割字符串
                String[] split = line.split("[|]");
                //System.out.println(split.length);

                /*
                 * split[0] AAA服务器登录名  || #占位符
                 * split[1] NAS服务器名称
                 * split[2] 上线(7上8下)
                 * split[3] 上线时间        || 下线时间 (单位秒,注意乘1000)
                 * split[4] 登陆IP地址
                 *
                 * */


                //开始对每行数据进行处理,转换成BIDR对象
                //判断是否是上线
                if (split[2].equals("7")) {
                    //如果是上线,判断是否已经有了上线记录
                    if (!maps.containsKey(split[4])) {//还没有上线记录
                        maps.put(split[4], 1); //插入一条记录,用户数为1
                        BIDR bidr = getBidr(split);
                        //新建一个不完整的BIDR对象存入bidrMap中
                        bidrMap.put(split[4], bidr);
                    } else {
                        int users = maps.get(split[4]);
                        maps.replace(split[4], ++users);
                    }
                } else if (split[2].equals("8")) { // 容错,防止读取的文件内容出现错误
                    if (maps.get(split[4]) > 1) { //如果用户数大于1
                        //获取当前用户数
                        int users = maps.get(split[4]);
                        //更新map,用户数减一
                        maps.replace(split[4], --users);
                    } else { //只剩下一个用户要下线,此时清空maps,补全bidr对象信息
                        maps.remove(split[4]);
                        BIDR bidr = bidrMap.get(split[4]);

                        bidrMap.remove(split[4]);

                        bidr.setLogout_date(new Timestamp(
                                Long.parseLong(split[3]) * 1000));

                        bidr.setTime_deration(
                                (int) (bidr.getLogout_date().getTime() -
                                        bidr.getLogin_date().getTime())
                        );
                        bidrs.add(bidr);
                    }

                }
            }
            //System.out.println(bidrs.size());
            /*int sum = 1;

            for (int i = 0; i < bidrs.size(); i++) {
                System.out.println(i + 1);
                BIDR b = bidrs.get(i);
                String t = sum++ + ":" + b.getLogin_ip() + " " +
                        b.getLogin_date() + " " + b.getLogout_date() + " " + b.getTime_deration();
                ps.println(t);
            }*/
            new ConfigurationImpl().getLogger().info("采集完毕,供采集到" + bidrs.size() + "条数据");


        } catch (Exception e) {
            e.printStackTrace();
        }


        return bidrs;
    }


    // 封装bidr对象并返回
    private static BIDR getBidr(String[] info) {
        BIDR bidr = new BIDR();

        bidr.setAAA_login_name(info[0]);
        bidr.setNAS_ip(info[1]);
        bidr.setLogin_date(new Timestamp(Long.parseLong(
                info[3]) * 1000));
        bidr.setLogin_ip(info[4]);

        return bidr;
    }

}
