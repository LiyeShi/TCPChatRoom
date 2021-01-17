package dome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @description: 客户端
 * @author: stone
 * @create: 2021-01-14 20:35
 **/
public class Cilent2 {
    private static Socket socket;

    public static void main(String[] args) {
        System.out.println("-----客户端2-----");
        try {
            String name;
            System.out.print("聊天前先取个昵称吧：");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            name=br.readLine();
            socket = new Socket("localhost", 1234);
                new Thread(new Send(socket, name)).start();
                new Thread(new Receive(socket)).start();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
