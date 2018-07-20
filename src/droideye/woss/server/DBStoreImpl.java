package droideye.woss.server;

import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.woss.server.DBStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import droideye.util.BackUPImpl;
import droideye.util.ConfigurationImpl;
import droideye.util.LoggerImpl;

public class DBStoreImpl implements DBStore {

  /*  private static final String driver = "oracle.jdbc.driver.OracleDriver";
    private static final String url = "jdbc:oracle:thin:@localhost:1521:XE";//固定格式@IP:端口:服务名
    private static final String user = "briup";
    private static final String password = "briup";*/

    private String driver;
    private String url;//固定格式@IP:端口:服务名
    private String user;
    private String password;

    @Override
    public void init(Properties properties) {
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        user = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    @Override
    public void saveToDB(Collection<BIDR> collection) throws Exception {
        Class.forName(driver);


        Connection connection = DriverManager.getConnection(url, user, password);
        String today = getToday();

        int i = 0;

        for (BIDR b :
                collection) {
            try {
                i++;
                String AAALoginName = b.getAAA_login_name();
                String loginip = b.getLogin_ip();
                String nasIp = b.getNAS_ip();
                int timeDuration = b.getTime_deration();
                java.sql.Date loginDate = new java.sql.Date(b.getLogin_date().getTime());
                java.sql.Date logoutDate = new java.sql.Date(b.getLogout_date().getTime());


                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO T_DETAIL_" + today + " values(?,?,?,?,?,?)");
                ps.setString(1, AAALoginName);
                ps.setString(2, loginip);
                ps.setDate(3, loginDate);
                ps.setDate(4, logoutDate);
                ps.setString(5, nasIp);
                ps.setInt(6, timeDuration);

                ps.executeUpdate();


                // 故意制造异常来测试备份功能
               /* if (i == 6) {
                    int a = 1 / 0;
                }

                if (i == 8) {
                    int a = 1 / 0;
                }*/

                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                ConfigurationImpl configuration = new ConfigurationImpl();

                BackUP backUP = configuration.getBackup();
                String uuid = UUID.randomUUID().toString();

                backUP.store(uuid, b, true);
                configuration.getLogger().error("第" + i +
                        "条数据入库时出现问题,入库中止.未入库信息已备份,备份文件名:" + uuid);
            }

            //System.out.println("第" + i + "条数据入库完毕...");

        }

        //System.out.println("全部数据入库完成");
        new ConfigurationImpl().getLogger().info("数据入库完成");


        try {
            connection.close();
        } catch (Exception e) {

        }
    }

    private static String getToday() throws Exception {
        //获取本地时间并格式化输出
        DateTimeFormatter df = DateTimeFormatter.ofPattern("d");
        String today = LocalDateTime.now().format(df);
        //System.out.println("获取到今天是" + today + "号");
        new ConfigurationImpl().getLogger().info("获取到今天是" + today + "号");
        return today;
    }
}
