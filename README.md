# WOSS
1)	能够从文件中采集信息,包装成BIDR对象集合
2)	能够从客户端向服务器以对象流的方式将BIDR对象集合发送
3)	用线程来处理每一个客户端请求,能够接收客户端发送来的对象流,并将接收到的信息入库保存
4)	能够用日志记录每一个事件输出并保存在日志文件中
5)	能够在入库保存出错时及时将未保存数据进行备份
6)	能够解析XML文件来通过反射创建对象、获取参数信息，并能返回已经初始化好的对象
