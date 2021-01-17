package dome;

import java.io.Closeable;
import java.io.IOException;

/**
 * @description: 释放资源工具类
 * @author: stone
 * @create: 2021-01-14 20:29
 **/
public class Utils {
    public static void realse(Closeable... targets) {
        for (Closeable target : targets) {
            if (target != null) {
                try {
                    target.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

    }
}
