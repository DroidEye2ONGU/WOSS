package droideye.woss.server;

import com.briup.util.BIDR;
import com.briup.util.Logger;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import droideye.util.ConfigurationImpl;
import droideye.util.LoggerImpl;

public class ServerImpl implements Server {

    private int port;

    ServerSocket serverSocket;

    /*
     * 接收方法
     * 主要功能:接收从客户端传来的数据,并调用入库模块保存数据功能
     * 实现思路:
     * 1.创建ServerSocket对象,绑定监听接口
     * 2.通过accept()方法监听客户端请求
     * 3.连接建立后,准备对象流,接收数据
     * 4.关闭数据资源
     * */
    @Override
    public Collection<BIDR> revicer() throws Exception {
        serverSocket = new ServerSocket(port);

        //System.out.println("服务器开启...");
        new ConfigurationImpl().getLogger().info("服务器开启,等待客户端连接...");
        while (true) {
            Socket socket = serverSocket.accept();
            //System.out.println("接收到一个客户端连接,启动新的线程,开始接收数据...");
            new ConfigurationImpl().getLogger().info("接收到一个客户端连接,启动新的线程,开始接收数据...");

            new Thread(() -> {
                try {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                    //BIDR bidr;
                    ArrayList<BIDR> bidrs;

                    bidrs = (ArrayList<BIDR>) ois.readObject();

                    //System.out.println("已接收完客户端信息,开始入库保存...");
                    new ConfigurationImpl().getLogger().info("已接收完客户端信息,开始入库保存...");

                       /* while ((bidr = (BIDR) ois.readObject()) != null) {
                            System.out.println(bidr.getLogin_ip() + ":" + bidr.getLogin_date() + "--" + bidr.getLogout_date() + "--" + bidr.getTime_deration());
                            bidrs.add(bidr);
                        }
                        */
                       //new DBStoreImpl().saveToDB(bidrs);

                    new ConfigurationImpl().getDBStore().saveToDB(bidrs);

                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        }

    }

    @Override
    public void shutdown() {
        if (this.serverSocket != null) {
            try {
                serverSocket.close();
                //System.out.println("服务器已关闭");
                new ConfigurationImpl().getLogger().info("服务器已关闭");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //System.out.println("服务器未开启,无需关闭");
            new ConfigurationImpl().getLogger().warn("服务器未开启,无需关闭");
            return;
        }
    }

    @Override
    public void init(Properties properties) {
        port = Integer.parseInt(properties.getProperty("port"));
    }


}
