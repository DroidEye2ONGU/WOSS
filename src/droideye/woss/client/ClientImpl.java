package droideye.woss.client;

import com.briup.util.BIDR;
import com.briup.woss.client.Client;

import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import droideye.util.ConfigurationImpl;
import droideye.util.LoggerImpl;

public class ClientImpl implements Client {

    private String ip;
    private int port;

    /*
     * 发送方法
     * 主要功能:将采集系统客户端采集形成的BIDR数据清单 发送给服务器端(中心处理系统)
     * 实现思路:
     * 1.创建Socket对象,指明需要连接的服务器的地址和端口号
     * 2.连接建立后,通过输出流向服务器端发送数据
     * 3.关闭相关资源
     * */
    @Override
    public void send(Collection<BIDR> collection) throws Exception {
        new ConfigurationImpl().getLogger().info("客户端开始向服务器发送信息...");

        Socket socket = new Socket(ip,port);

        ArrayList<BIDR> bidrs = (ArrayList<BIDR>) collection;

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());


        oos.writeObject(bidrs);

        //System.out.println("客户端已向服务器发送信息...");
        new ConfigurationImpl().getLogger().info("客户端已向服务器发送信息...");

        socket.close();

    }

    @Override
    public void init(Properties properties) {
        ip = properties.getProperty("server-ip");
        port = Integer.parseInt(properties.getProperty("server-port"));
    }
}
