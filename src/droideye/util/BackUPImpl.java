package droideye.util;

import com.briup.util.BIDR;
import com.briup.util.BackUP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

public class BackUPImpl implements BackUP {
    private String filepath = "src/droideye/file/";

    //写入备份文件功能
    /*
     * fileName:备份文件的名字
     * data:准备备份的对象
     * append:是否追加
     * */
    @Override
    public void store(String fileName, Object data, boolean append) throws Exception {
        String path = filepath + fileName;
        File file = new File(path);

        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, append))
        ) {
            oos.writeObject(data);
        } catch (Exception e) {
            new ConfigurationImpl().getLogger().error("发生了致命错误,未能正确备份对象,文件名:" + fileName);
            e.printStackTrace();
        }
    }

    // 加载备份文件功能
    /*
     * fileName:备份文件名
     * flag:是否读完后删除备份文件?
     * */
    @Override
    public Object load(String fileName, boolean flag) throws Exception {
        String path = filepath + fileName;
        File file = new File(path);
        BIDR bidr = null;
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))
        ) {
            bidr = (BIDR) ois.readObject();
            ois.close();
            if (flag) { // 读取后删除文件
                System.out.println(file.delete());
            }
        } catch (Exception e) {
            new ConfigurationImpl().getLogger().error("发生了致命错误,未能读取备份对象,文件名:" + fileName);
            e.printStackTrace();
        }

        return bidr;
    }


    @Override
    public void init(Properties properties) {
        filepath = properties.getProperty("back-path");
    }
}
