package com.lihao.encoding.blockchain;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by sbwdlihao on 10/12/2016.
 */
public class BlockChainTest {

    @Test
    public void testVarInt31() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        assertThat(BlockChain.writeVarInt31(bo, 0)).isEqualTo(1);
        assertThat(bo.toByteArray()).isEqualTo(new byte[]{0});
        assertThat(BlockChain.readVarInt31(new ByteArrayInputStream(bo.toByteArray()))).isEqualTo(0);

        bo = new ByteArrayOutputStream();
        assertThat(BlockChain.writeVarInt31(bo, 500)).isEqualTo(2);
        assertThat(bo.toByteArray()).isEqualTo(new byte[]{(byte)0xf4, (byte)0x03});
        assertThat(BlockChain.readVarInt31(new ByteArrayInputStream(bo.toByteArray()))).isEqualTo(500);

        bo = new ByteArrayOutputStream();
        assertThat(BlockChain.writeVarInt31(bo, 0x7fffffff)).isEqualTo(5);
        assertThat(bo.toByteArray()).isEqualTo(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x07});
        assertThat(BlockChain.readVarInt31(new ByteArrayInputStream(bo.toByteArray()))).isEqualTo(0x7fffffff);

        assertThatThrownBy(()->BlockChain.writeVarInt31(new ByteArrayOutputStream(), 2147483648L)).isInstanceOf(ArithmeticException.class);
    }

    @Test
    public void testVarInt63() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        assertThat(BlockChain.writeVarInt63(bo, 0)).isEqualTo(1);
        assertThat(bo.toByteArray()).isEqualTo(new byte[]{0});
        assertThat(BlockChain.readVarInt63(new ByteArrayInputStream(bo.toByteArray()))).isEqualTo(0);

        bo = new ByteArrayOutputStream();
        assertThat(BlockChain.writeVarInt63(bo, 500)).isEqualTo(2);
        assertThat(bo.toByteArray()).isEqualTo(new byte[]{(byte)0xf4, (byte)0x03});
        assertThat(BlockChain.readVarInt63(new ByteArrayInputStream(bo.toByteArray()))).isEqualTo(500);

        bo = new ByteArrayOutputStream();
        assertThat(BlockChain.writeVarInt63(bo, 0x80000000L)).isEqualTo(5);
        assertThat(bo.toByteArray()).isEqualTo(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x08});
        assertThat(BlockChain.readVarInt63(new ByteArrayInputStream(bo.toByteArray()))).isEqualTo(0x80000000L);

        assertThatThrownBy(()->BlockChain.writeVarInt63(new ByteArrayOutputStream(), 0x8000000000000000L)).isInstanceOf(ArithmeticException.class);
    }

    @Test
    public void testVarStr() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] str = {10, 11, 12};
        assertThat(BlockChain.writeVarStr31(bo, str)).isEqualTo(4);
        assertThat(bo.toByteArray()).isEqualTo(new byte[]{3, 10, 11, 12});
        assertThat(BlockChain.readVarStr31(new ByteArrayInputStream(bo.toByteArray()))).isEqualTo(new byte[]{10, 11, 12});
    }
}
