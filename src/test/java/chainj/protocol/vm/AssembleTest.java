package chainj.protocol.vm;

import chainj.Case;
import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sbwdlihao on 06/01/2017.
 */
public class AssembleTest {

    @Test
    public void testAssemble() {
        List<Case<String, byte[]>> cases = new ArrayList<>(Arrays.asList(
                new Case<>("2 3 ADD 5 NUMEQUAL", Hex.decode("525393559c")),
                new Case<>("0x02 3 ADD 5 NUMEQUAL", Hex.decode("01025393559c")),
                new Case<>("19 14 SUB 5 NUMEQUAL", Hex.decode("01135e94559c")),
                new Case<>("'Hello' 'WORLD' CAT 'HELLOWORLD' EQUAL", Hex.decode("0548656c6c6f05574f524c447e0a48454c4c4f574f524c4487")),
                new Case<>("'H\\'E' 'W' CAT 'H\\'EW' EQUAL", Hex.decode("0348274501577e044827455787")),
                new Case<>("'HELLO '  'WORLD' CAT 'HELLO WORLD' EQUAL", Hex.decode("0648454c4c4f2005574f524c447e0b48454c4c4f20574f524c4487")),
                new Case<>("$alpha JUMP:$alpha", Hex.decode("6300000000"))
        ));
        cases.forEach(c -> {
            byte[] got = Assemble.assemble(c.data);
            Assert.assertArrayEquals(c.want, got);
        });

        List<Case<String, String>> errCases = new ArrayList<>(Arrays.asList(
                new Case<>("0x1", "exception decoding Hex string: String index out of range: 1"),
                new Case<>("BADTOKEN", Errors.ErrToken),
                new Case<>("'Unterminated quote", Errors.ErrToken)
        ));
        errCases.forEach(c -> {
            Exception err = null;
            try {
                Assemble.assemble(c.data);
            } catch (Exception e) {
                err = e;
            }
            Assert.assertNotNull(err);
            Assert.assertEquals(c.want, err.getMessage());
        });
    }

    @Test
    public void testDisassemble() {
        List<Case<byte[], String>> cases = new ArrayList<>(Arrays.asList(
                new Case<>(Hex.decode("525393559c"), "0x02 0x03 ADD 0x05 NUMEQUAL"),
                new Case<>(Hex.decode("01135e94559c"), "0x13 0x0e SUB 0x05 NUMEQUAL"),
                new Case<>(Hex.decode("6300000000"), "$alpha JUMP:$alpha"),
                new Case<>(new byte[]{(byte)0xff}, "NOPxff")
        ));
        cases.forEach(c -> {
            String got = Assemble.disAssemble(c.data);
            Assert.assertEquals(c.want, got);
        });
    }
}
