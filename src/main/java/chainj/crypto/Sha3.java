package chainj.crypto;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class Sha3 {

    public static byte[] sum256(byte[] data) {
        // todo 这里创建digest需要进行性能优化，同时要注意线程安全
        Digest digest = new SHA3Digest(); // 256位
        if (data != null) {
            digest.update(data, 0, data.length);
        }
        byte[] out = new byte[digest.getDigestSize()];
        digest.doFinal(out, 0);
        return out;
    }
}
