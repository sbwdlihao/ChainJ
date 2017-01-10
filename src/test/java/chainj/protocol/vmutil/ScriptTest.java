package chainj.protocol.vmutil;

import chainj.Case;
import chainj.crypto.ed25519.chainkd.Util;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sbwdlihao on 03/01/2017.
 */
public class ScriptTest {

    @Test
    public void testIsUnSpendable() {
        List<Case<byte[], Boolean>> cases = Arrays.asList(
                new Case<>(new byte[]{0x6a, 0x04, 0x74, 0x65, 0x73, 0x74}, true),
                new Case<>(new byte[]{0x76, (byte)0xa9, 0x14, 0x29, (byte)0x95, (byte)0xa0,
                        (byte)0xfe, 0x68, 0x43, (byte)0xfa, (byte)0x9b, (byte)0x95, 0x45,
                        (byte)0x97, (byte)0xf0, (byte)0xdc, (byte)0xa7, (byte)0xa4, 0x4d, (byte)0xf6,
                        (byte)0xfa, 0x0b, 0x5c, (byte)0x88, (byte)0xac}, false)
        );
        cases.forEach(c -> Assert.assertEquals(c.want, Script.isUnSpendable(c.data)));
    }

    @Test
    public void testBlockMultiSig() {
        KeyPairGenerator keyPairGenerator = new KeyPairGenerator();
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        List<EdDSAPublicKey> want = new ArrayList<>();
        want.add((EdDSAPublicKey) keyPair.getPublic());
        keyPair = keyPairGenerator.generateKeyPair();
        want.add((EdDSAPublicKey) keyPair.getPublic());
        byte[] program = Script.blockMultiSigProgram(want, 1);
        int[] nRequired = new int[1];
        List<EdDSAPublicKey> got = Script.parseBlockMultiSigProgram(program, nRequired);
        Assert.assertEquals(1, nRequired[0]);
        Assert.assertEquals(want, got);
    }

    @Test
    public void testBlockMultiSig00() {
        List<EdDSAPublicKey> want = new ArrayList<>();
        byte[] program = Script.blockMultiSigProgram(want, 0);
        Assert.assertEquals(4, program.length); // {0xaf, 0, 0, 0xad}
        int[] nRequired = new int[1];
        List<EdDSAPublicKey> got = Script.parseBlockMultiSigProgram(program, nRequired);
        Assert.assertEquals(0, nRequired[0]);
        Assert.assertEquals(want, got);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlockMultiSig01() {
        List<EdDSAPublicKey> want = new ArrayList<>();
        want.add(Util.newEdDSAPublicKey(new byte[32]));
        Script.blockMultiSigProgram(want, 0);
    }

    @Test
    public void testP2SP() {
        KeyPairGenerator keyPairGenerator = new KeyPairGenerator();
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        List<EdDSAPublicKey> want = new ArrayList<>();
        want.add((EdDSAPublicKey) keyPair.getPublic());
        keyPair = keyPairGenerator.generateKeyPair();
        want.add((EdDSAPublicKey) keyPair.getPublic());
        byte[] program = Script.p2spMultiSigProgram(want, 1);
        int[] nRequired = new int[1];
        List<EdDSAPublicKey> got = Script.parseP2SPBlockMultiSigProgram(program, nRequired);
        Assert.assertEquals(1, nRequired[0]);
        Assert.assertEquals(want, got);
    }
}
