import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2018-04-11 14:43
 */
public class TestMain {

    @Test
    public void test(){

        String str = "123456789";

        System.out.println(str.getBytes().length);

        ByteBuf buf = Unpooled.buffer();
        System.out.println(buf.array()[0]);
        buf.writeBytes(str.getBytes());

        System.out.println(buf.writerIndex());
    }
}
