package dome;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description: tcp聊天室服务器
 * @author: stone
 * @create: 2021-01-14 20:28
 **/
public class server {
    private static Socket client;
    private static CopyOnWriteArrayList<Channel> userList = new CopyOnWriteArrayList<>();
    //    记录加入聊天的客户端的数量
    private static int count = 0;

    public static void main(String[] args) {
        System.out.println("-----服务器-----");
        try {
            ServerSocket socket = new ServerSocket(1234);
            System.out.println("服务器已启动。。。。");
//            循环监听
            while (true) {
                client = socket.accept();
                System.out.println("第" + (++count) + "个客户端建立连接");
//                每进来一个用户 放进list
                Channel channel = new Channel(client);
                userList.add(channel);
//                开启一个线程
                new Thread(channel).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 每一个客户端都是一个Channel对象
     */
    static class Channel implements Runnable {
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket cilent;
        private String name;
        private boolean isRunning = true;

        public Channel(Socket client) {
            try {
                this.cilent = client;
                dis = new DataInputStream(cilent.getInputStream());
                dos = new DataOutputStream(cilent.getOutputStream());
                name = receive();
//               向自己展示  花里胡哨。。。。
                this.sendMsg("*********************************************");
                this.sendMsg("╭╮　╭☆╭──╮╭╮　　　☆╮　　　╭───╮╭╮\n" +
                        "││　│││╭─★││　　　││　　　★╭─╮│││\n" +
                        "│╰─╯││╰╮　││　　　││　　　││　││││\n" +
                        "│╭─╮││╭╯　││　　　││　　　││　││╰╯ \n" +
                        "││　│││╰─╮│╰──╮│╰──╮│╰─╯│ 〇\n" +
                        "╰★　╰╯╰──╯☆───╯★───╯╰───☆  ★");
                this.sendMsg("快来和小伙伴们一起玩耍吧！(*^▽^*)");
                this.sendMsg("*********************************************");
//                向其他人展示
                Msg msg = new Msg(name + "来到聊天室", true);
                sendMsgToOthers(msg);
            } catch (IOException e) {
                e.printStackTrace();
                realse();
            }

        }

        @Override
        public void run() {
            while (isRunning) {
//            接收客户端发来的消息
                String msg = receive();
//            将收到的消息转发回客户端
                if (!msg.equals("")) {
                    sendMsgToOthers(new Msg(msg, false));
                }
            }

        }


        /**
         * 接收客户端发来的消息
         *
         * @return the string
         */
        public String receive() {
            try {
                String msg = "";
                msg = dis.readUTF();
                return msg;
            } catch (IOException e) {
                e.printStackTrace();
                realse();
            }
            return "";
        }

        /**
         * 向客户端转发消息
         *
         * @param msg the msg
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
         * Send msg to others.
         * 向除了自己以外的客户端发送消息
         *
         * @param msg the msg
         */
        public void sendMsgToOthers(Msg msg) {
//            设置标志位 看私密消息能否找到要发送的对象
            boolean getUser = false;
//            如果是私密消息
            if (msg.isPrivate) {
                String targetUser = msg.getName();
                String s = msg.getMsg();
//                遍历在线用户列表 向该用户发送消息
                for (Channel user : userList) {
                    if (user.name.equals(targetUser)) {
//                      只要能找到该用户 将该标志位设为真
                        getUser = true;
//                      禁止自己向自己发送私密消息
                        if (targetUser.equals(this.name)) {
//                           🈲
                            this.sendMsg("\uD83C\uDE32你有病吗，自己@自己");
                        } else {
                            user.sendMsg(name + "悄悄的对您说:" + s);
                        }
                        break;
                    }
                }
//                没有找到要发送的对象，提示用户输入有误
                if (!getUser) {
                    this.sendMsg("☹聊天室中不存在\"" + targetUser + "\"" + "这个用户哦");
                }
//              如果不是私密消息
            } else {
//            遍历在线用户列表，向除了自己以外的用户发送信息
                for (Channel user : userList) {
//                排除自己
                    if (user != this) {
                        if (msg.isSys()) {
                            // 系统消息    📣 代表小喇叭符号
                            user.sendMsg("\uD83D\uDCE3系统消息:" + msg.getMsg());
                        } else {
//                            普通聊天消息
                            user.sendMsg(name + ":" + msg.getMsg());
                        }
                    }
                }
            }

        }

        /**
         * 释放资源
         */
        public void realse() {
            this.isRunning = false;
//            释放资源
            Utils.realse(dos, dis, cilent);
//            从容器中移除该用户
            userList.remove(this);
//            向其他人展示该用户离开的信息
            sendMsgToOthers(new Msg(this.name + "离开了聊天室", true));
//             客户端数量减一
            count--;
        }

    }


    static class Msg {
        //         是否为系统消息
        private boolean isSys;
        //         是否为私密消息
        private boolean isPrivate;
        private String msg;


        public void setPrivate(boolean aPrivate) {
            isPrivate = aPrivate;
        }

        public Msg(String msg, boolean isSys) {
            this.isSys = isSys;
            this.msg = msg;
//            检查消息是不是私密消息
            CheckMsg(msg);
        }

        public void CheckMsg(String msg) {
//            约定以@开头的消息都为私密消息
            if (msg.startsWith("@")) {
                isPrivate = true;
            }
        }


        public boolean isSys() {
            return isSys;
        }

        public void setSys(boolean sys) {
            isSys = sys;
        }

        /**
         * Gets msg.
         * 得到消息的内容
         *
         * @return the msg
         */
        public String getMsg() {
            String msg1 = "";
//            私密消息需要切割消息内容
            if (isPrivate) {
//                @xx:abc   abc 是消息内容 区分中英文两种情况
                int i = msg.indexOf(":");
                int i1 = msg.indexOf("：");
//                找到名字后面的消息内容
                if (i != -1) {
                    msg1 = msg.substring(i + 1);
                } else {
                    msg1 = msg.substring(i1 + 1);
                }
            } else {
//            其他情况下直接返回消息内容
                msg1 = msg;
            }
            return msg1;
        }

        /**
         * Get name string.
         * 获取要发送私密消息的对象名字
         *
         * @return the string
         */
        public String getName() {
            String name = "";
//            中文 ：英文 :
            int i = msg.indexOf(":");
            int i1 = msg.indexOf("：");
//            找到这个消息是向谁发送的 即@xx： 取出名字xx  这里的冒号 有可能是中文或英文两种情况
            if (i != -1) {
//                英文输入法的冒号
                name = msg.substring(1, i);
            } else {
//                中文输入法的冒号
                name = msg.substring(1, i1);
            }
            return name;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isPrivate() {
            return isPrivate;
        }
    }

}