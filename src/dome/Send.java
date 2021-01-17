package dome;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @description: 发送消息线程
 * @author: stone
 * @create: 2021-01-15 14:48
 **/
public class Send implements Runnable {
    private DataOutputStream dos;
    private BufferedReader br;
    private Socket cilent;
    private String name;
    private boolean isRunning = true;

    public Send(Socket cilent, String name) {
        this.cilent = cilent;
        this.name=name ;
        try {
            dos = new DataOutputStream(cilent.getOutputStream());
            br = new BufferedReader(new InputStreamReader(System.in));
//          将该客户端的昵称上传到服务器
            sendMsg(this.name);
        } catch (IOException e) {
            e.printStackTrace();
            realse();
        }

    }

    @Override
    public void run() {
        while (isRunning) {
            String msg = getMsgFromConsole();
            sendMsg(msg);
        }

    }

    /**
     * 获取控制台输入的信息
     * @return
     */
    private String getMsgFromConsole()  {
        String msg = "";
        try {
            msg = br.readLine();
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            realse();
        }
        return "";
    }


    /**
     * 发送消息到服务器
     * @param msg
     */
    public void sendMsg(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
            realse();
        }
    }

    /**
     * 释放资源
     */
    public void realse() {
        this.isRunning = false;
        Utils.realse(br, dos, cilent);
    }
}
