package test;

import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.woss.WossModule;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import droideye.util.BackUPImpl;
import droideye.util.ConfigurationImpl;
import droideye.woss.client.ClientImpl;
import droideye.woss.client.GatherImpl;
import droideye.woss.server.DBStoreImpl;
import droideye.woss.server.ServerImpl;

public class test {

    static Server server = new ServerImpl();

    @Test
    public void testReadLine() throws Exception {
        GatherImpl gather = new GatherImpl();
        gather.gather();
    }

    @Test
    public void testDBStore() throws Exception {
        //获取一下数据清单
        GatherImpl gather = new GatherImpl();
        Collection<BIDR> bidrs = gather.gather();
        new DBStoreImpl().saveToDB(bidrs);
    }

    @Test
    public void testDBStore2() throws Exception {
        //获取一下数据清单
        ConfigurationImpl configuration = new ConfigurationImpl();
        GatherImpl gather = (GatherImpl) configuration.getGather();
        Collection<BIDR> bidrs = gather.gather();

        configuration.getDBStore().saveToDB(bidrs);

    }

    @Test
    public void testGetTodayMethod() {
        /*System.out.println("----------------------------\nTest:");
        System.out.println("今天是:" + DBStoreImpl.getToday());*/
    }

    @Test//开启客户端
    public void testServerAndClient() throws Exception {
        Client client = new ClientImpl();

        Gather gather = new GatherImpl();

        Collection<BIDR> bidrs = gather.gather();
        client.send(bidrs);

    }

    @Test//开启服务器
    public void testServer() throws Exception {
        server = new ServerImpl();
        server.revicer();
    }

    @Test
    public void testServerShutdown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testStore() throws Exception {
        BackUP backUP = new BackUPImpl();
        BIDR bidr = new BIDR();

        bidr.setLogin_ip("127.0.0.1");

        backUP.store("test.txt",bidr,true);

    }

    @Test
    public void testLoad() throws Exception {
        BackUP backUP = new BackUPImpl();
        BIDR bidr = null;
        bidr = (BIDR) backUP.load("test.txt", true);
        System.out.println(bidr.getLogin_ip());
    }

    @Test
    public void testFileDelete() {
        File file = new File("src/droideye/file/test.txt");
        file.delete();
    }

    @Test
    public void testConfiguration() {
        ConfigurationImpl configuration = new ConfigurationImpl();

       /* Map<String, WossModule> map = configuration.getMap();
        Properties properties = configuration.getProperties();

        Set<String> strings = map.keySet();
        for (String s:
             strings) {
            System.out.println(s + " : " + map.get(s));
        }

        System.out.println("-----------------------------------------------");

        Set<Object> objects = properties.keySet();
        for (Object o:
             objects) {
            System.out.println(o + " : " + properties.get(o));

        }*/
    }

    @Test
    public void testConfigurationServer() throws Exception {
        new ConfigurationImpl().getServer().revicer();
    }

    @Test
    public void testConfigurationClient() throws Exception {
        ConfigurationImpl configuration = new ConfigurationImpl();
        Collection<BIDR> bidrs = configuration.getGather().gather();
        configuration.getClient().send(bidrs);
    }




}
