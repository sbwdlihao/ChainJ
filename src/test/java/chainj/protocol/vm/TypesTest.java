package chainj.protocol.vm;

import chainj.Case;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbwdlihao on 02/01/2017.
 */
public class TypesTest {

    @Test
    public void testBoolBytes() {
        Assert.assertArrayEquals(new byte[]{1}, Types.boolBytes(true));
        Assert.assertArrayEquals(new byte[]{}, Types.boolBytes(false));
    }
    
    @Test
    public void testAsBool() {
        List<Case<byte[], Boolean>> cases = new ArrayList<>();
        cases.add(new Case<>(new byte[]{0, 0, 0, 0}, false));
        cases.add(new Case<>(new byte[]{0}, false));
        cases.add(new Case<>(new byte[]{}, false));
        cases.add(new Case<>(new byte[]{1}, true));
        cases.add(new Case<>(new byte[]{1, 1, 1, 1}, true));
        cases.add(new Case<>(new byte[]{0, 0, 0, 1}, true));
        cases.add(new Case<>(new byte[]{1, 0, 0, 0}, true));
        cases.add(new Case<>(new byte[]{2}, true));

        for (Case<byte[], Boolean> aCase : cases) {
            Assert.assertEquals(aCase.want, Types.asBool(aCase.data));
        }
    }

    @Test()
    public void testInt64() {
        List<Case<Long, byte[]>> cases = new ArrayList<>();
        cases.add(new Case<>(0L, new byte[]{}));
        cases.add(new Case<>(1L, new byte[]{(byte)0x01}));
        cases.add(new Case<>(255L, new byte[]{(byte)0xff}));
        cases.add(new Case<>(256L, new byte[]{(byte)0x00, (byte)0x01,}));
        cases.add(new Case<>(65536L, new byte[]{(byte)0x00, (byte)0x00, (byte)0x01,}));
        cases.add(new Case<>(-1L, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}));
        cases.add(new Case<>(-2L, new byte[]{(byte)0xfe, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}));

        for (Case<Long, byte[]> aCase : cases) {
            byte[] got = Types.int64Bytes(aCase.data);
            Assert.assertArrayEquals(aCase.want, got);

            long num = Types.asInt64(got);
            Assert.assertEquals(aCase.data, new Long(num));
        }

        byte[] data = new byte[]{(byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01};
        try {
            Types.asInt64(data);
        } catch (VMRunTimeException e) {
            Assert.assertEquals(Errors.ErrBadValue, e.getMessage());
        }
    }
}
