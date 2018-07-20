package droideye.util;

import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.WossModule;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class ConfigurationImpl implements Configuration {

    // map:存放的是每个模块的对象
    // key:字标签的名字,如:gather
    // value:反射对象,如droideye.src.client.GatherImpl ---> 反射对象
    private Map<String, WossModule> map = new HashMap<>();


    // 解析xml的一个方法:填充map集合

    // Properties类 存放每个模块的配置信息
    // key:三级标签的名字:如data-file
    // value:配置信息:如droideye/file/radwtmp
    private Properties properties = new Properties();

    // 解析xml的一个方法:填充map集合,把Properties类填充
    public ConfigurationImpl() {
        // 解析xml的一个方法

        //获得一个SAXReader对象
        SAXReader saxReader = new SAXReader();
        File file = new File("src/droideye/file/conf.xml");

        try {
            if (!file.exists()) throw new FileNotFoundException("无法找到配置文件!");

            // 读取要解析的XML文件
            Document document = saxReader.read(file);

            //获取document中的根节点
            Element rootElement = document.getRootElement();

            //获取根节点下的所有子节点
            List<Element> elements = rootElement.elements();

            //遍历elements集合,拿到每一个子节点
            for (Element e :
                    elements) {
                //获得标签名,作为map的key值
                String elementName = e.getName();
                //获得节点的class属性值
                String classValue = e.attributeValue("class");
                //由class属性值开始创建反射对象
                Class<WossModule> aClass = (Class<WossModule>) Class.forName(classValue);
                WossModule wossModule = aClass.newInstance();

                //填充map
                map.put(elementName, wossModule);
                //获得每个节点下的三级标签
                List<Element> elements2 = e.elements();
                //遍历三级标签集合,为Properties对象赋值
                for (Element e2 :
                        elements2) {
                    String nodeName = e2.getName();
                    String nodeValue = e2.getText();

                    properties.put(nodeName, nodeValue);
                }
            }

          /*  System.out.println(map);
            System.out.println("------------------------------------");
            System.out.println(properties);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //public Map<String, WossModule> getMap() {
    //
    //    return map;
    //}
    //
    //public void setMap(Map<String, WossModule> map) {
    //    this.map = map;
    //}
    //
    //public Properties getProperties() {
    //    return properties;
    //}
    //
    //public void setProperties(Properties properties) {
    //    this.properties = properties;
    //}

    @Override
    public Logger getLogger() {
        Logger logger = (Logger) map.get("logger");
        logger.init(properties);
        return logger;
    }

    @Override
    public BackUP getBackup() {
        BackUP backUP = (BackUP) map.get("backup");
        backUP.init(properties);
        return backUP;
    }

    @Override
    public Gather getGather() {
        Gather gather = (Gather) map.get("gather");
        gather.init(properties);
        return gather;
    }

    @Override
    public Client getClient() {
        Client client = (Client) map.get("client");
        client.init(properties);
        return client;
    }

    @Override
    public Server getServer() {
        Server server = (Server) map.get("server");
        server.init(properties);
        return server;
    }

    @Override
    public DBStore getDBStore() {
        DBStore dbStore = (DBStore) map.get("dbstore");
        dbStore.init(properties);
        return dbStore;
    }
}
