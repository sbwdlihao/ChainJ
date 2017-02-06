package chainj.protocol.tx;

import chainj.protocol.bc.BCTest;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/**
 * Created by sbwdlihao on 06/02/2017.
 */
public class EntryTest {

    @Test
    public void testWriteForHash() {
        Spend spend = new Spend(
                new EntryRef(BCTest.mustDecodeHash("dd385f6fe25d91d8c1bd0fa58951ad56b0c5229dcc01f61d9f9e8b9eb92d3292")),
                new EntryRef(BCTest.mustDecodeHash("f7dfbb2133f09dab9f1225af4ca2b0d5e583e6ce6febe2cdb8b605712d7ac3c1")),
                1
        );
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Entry.writeForHash(buf, spend.body());
        Assert.assertEquals("dd385f6fe25d91d8c1bd0fa58951ad56b0c5229dcc01f61d9f9e8b9eb92d3292f7dfbb2133f09dab9f1225af4ca2b0d5e583e6ce6febe2cdb8b605712d7ac3c10000000000000000000000000000000000000000000000000000000000000000",
                Hex.encodeHexString(buf.toByteArray()));
        EntryRef id = Entry.entryID(spend);
        Assert.assertEquals("1df1d12f7c489de1f30ddc39f07e925321546bd6ca007aa69a1130b73f78cf76", Hex.encodeHexString(id.getValue()));
    }
}
