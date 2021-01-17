package dome;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @description: 接收消息线程
 * @author: stone
 * @create: 2021-01-15 15:02
 **/
public class Receive implements Runnable{
    private DataInputStream dis;
    private Socket cilent;
    private boolean isRunning=true;

    public Receive(Socket cilent) {
        this.cilent = cilent;
        try {
//            获取输入流
            dis=new DataInputStream(cilent.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            realse();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
//                读取输入流的信息并打印在控制台
                String msg = dis.readUTF();
                System.out.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
//                出现异常直接释放资源
                realse();
            }
        }
    }


    /**
     * 释放资源
     */
    public void realse() {
        this.isRunning = false;
        Utils.realse(cilent, dis);
    }
}
