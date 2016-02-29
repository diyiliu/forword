import com.tiza.client.Forward;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.concurrent.TimeUnit;

/**
 * Description: Test
 * Author: DIYILIU
 * Update: 2015-09-16 14:39
 */
public class Test {


    public static void main(String[] args) throws Exception {

        Forward.setSource(1);
        Forward.connect("localhost", 8989);


        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 3; i++) {
            String msg = "201509180000" + i + "\n";

            // TimeUnit.SECONDS.sleep(3);
            buf.writeBytes(msg.getBytes());
        }
        //Forward.sendPosition("123456", "1.0", 7568, 3247, 10, 1, 0, new byte[]{0,0,0,0,0}, "2015-09-23 00:00:00");
  }
}
