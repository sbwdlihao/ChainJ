package chainj.protocol.vm;

import chainj.Case;
import chainj.protocol.bc.*;
import chainj.protocol.bc.txinput.EmptyTxInput;
import chainj.protocol.bc.txinput.IssuanceInput;
import chainj.protocol.bc.txinput.SpendInput;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sbwdlihao on 09/01/2017.
 */
public class VirtualMachineTest {

    @Test
    public void testProgramOK() {
        doOKNotOK(true);
    }

    @Test
    public void testProgramNotOK() {
        doOKNotOK(false);
    }

    @Test
    public void testVerifyTxInput() {
        List<Case<TxInput, Boolean>> cases = new ArrayList<>(Arrays.asList(
                new Case<>(
                        new SpendInput(
                                new Hash(),
                                0,
                                new byte[][]{new byte[]{2}, new byte[]{3}},
                                new AssetID(),
                                1,
                                new byte[]{OP.OP_ADD, OP.OP_5, OP.OP_NUMEQUAL},
                                new byte[]{}
                        ),
                        true
                ),
                new Case<>(
                        new IssuanceInput(
                                new byte[]{},
                                new AssetID(),
                                1,
                                new byte[]{},
                                new Hash(),
                                new byte[]{OP.OP_ADD, OP.OP_5, OP.OP_NUMEQUAL},
                                new byte[][]{new byte[]{2}, new byte[]{3}}
                        ),
                        true
                )
        ));

        cases.forEach(c -> {
            Transaction tx = new Transaction(new TxData(new TxInput[]{c.data}));
            boolean got = VirtualMachine.verifyTxInput(tx, 0);
            Assert.assertEquals(c.want, got);
        });

        List<Case<TxInput, String>> errCases = new ArrayList<>(Arrays.asList(
                new Case<>(
                        new IssuanceInput(2),
                        Errors.ErrUnsupportedVM
                ),
                new Case<>(
                        new SpendInput(2),
                        Errors.ErrUnsupportedVM
                ),
                new Case<>(
                        new IssuanceInput(
                                new byte[]{},
                                new AssetID(),
                                1,
                                new byte[]{},
                                new Hash(),
                                new byte[]{OP.OP_ADD, OP.OP_5, OP.OP_NUMEQUAL},
                                new byte[][]{new byte[50001]}
                        ),
                        Errors.ErrRunLimitExceeded
                ),
                new Case<>(
                        new EmptyTxInput(),
                        Errors.ErrUnsupportedTx
                )
        ));
        errCases.forEach(c -> {
            Transaction tx = new Transaction(new TxData(new TxInput[]{c.data}));
            Exception err = null;
             try {
                 VirtualMachine.verifyTxInput(tx, 0);
             } catch (VMRunTimeException e) {
                 err = e;
             }
             Assert.assertNotNull(err);
            Assert.assertEquals(c.want, err.getMessage());
        });
    }

    @Test
    public void testVerifyBlockHeader() {
        Block block = new Block(new BlockHeader(new byte[][]{new byte[]{2}, new byte[]{3}}));
        Block preBlock = new Block(new BlockHeader(new byte[]{OP.OP_ADD, OP.OP_5, OP.OP_NUMEQUAL}));
        Assert.assertTrue(VirtualMachine.verifyBlockHeader(preBlock.getBlockHeader(), block));

        block = new Block(new BlockHeader(new byte[][]{new byte[50000]}));
        try {
            VirtualMachine.verifyBlockHeader(preBlock.getBlockHeader(), block);
        } catch (VMRunTimeException e) {
            Assert.assertEquals(Errors.ErrRunLimitExceeded, e.getMessage());
        }
    }

    @Test
    public void testRun() {
        VirtualMachine vm = new VirtualMachine(50000, new byte[]{OP.OP_TRUE});
        boolean ok = vm.run();
        Assert.assertTrue(ok);

        vm = new VirtualMachine(50000, new byte[]{OP.OP_ADD});
        try {
            vm.run();
        } catch (VMRunTimeException e) {
            Assert.assertEquals(Errors.ErrDataStackUnderflow, e.getMessage());
        }
    }

    @Test
    public void testStep() {
        List<Case<VirtualMachine, VirtualMachine>> cases = new ArrayList<>(Arrays.asList(
                new Case<>(
                        new VirtualMachine(50000, new byte[]{OP.OP_TRUE}),
                        new VirtualMachine(
                                49990,
                                new byte[]{OP.OP_TRUE},
                                new ArrayList<>(Arrays.asList(new byte[]{1})),
                                1,
                                1,
                                new byte[]{1}
                        )
                ),
                new Case<>(
                        new VirtualMachine(
                                49990,
                                new byte[]{OP.OP_TRUE, OP.OP_JUMP, (byte)0xff, 0x00, 0x00, 0x00},
                                new ArrayList<>(),
                                1,
                                0,
                                new byte[]{}
                        ),
                        new VirtualMachine(
                                49989,
                                new byte[]{OP.OP_TRUE, OP.OP_JUMP, (byte)0xff, 0x00, 0x00, 0x00},
                                new ArrayList<>(),
                                255,
                                255,
                                new byte[]{(byte)0xff, 0x00, 0x00, 0x00},
                                0
                        )
                ),
                new Case<>(
                        new VirtualMachine(
                                49995,
                                new byte[]{OP.OP_TRUE, OP.OP_JUMPIF, 0x00, 0x00, 0x00, 0x00},
                                new ArrayList<>(Arrays.asList(new byte[]{1})),
                                1,
                                0,
                                new byte[]{}
                        ),
                        new VirtualMachine(
                                50003,
                                new byte[]{OP.OP_TRUE, OP.OP_JUMPIF, 0x00, 0x00, 0x00, 0x00},
                                new ArrayList<>(),
                                0,
                                0,
                                new byte[]{0x00, 0x00, 0x00, 0x00},
                                -9
                        )
                ),
                new Case<>(
                        new VirtualMachine(
                                49995,
                                new byte[]{OP.OP_FALSE, OP.OP_JUMPIF, 0x00, 0x00, 0x00, 0x00},
                                new ArrayList<>(Arrays.asList(new byte[]{})),
                                1,
                                0,
                                new byte[]{}
                        ),
                        new VirtualMachine(
                                50002,
                                new byte[]{OP.OP_FALSE, OP.OP_JUMPIF, 0x00, 0x00, 0x00, 0x00},
                                new ArrayList<>(),
                                6,
                                6,
                                new byte[]{0x00, 0x00, 0x00, 0x00},
                                -8
                        )
                ),
                new Case<>(
                        new VirtualMachine(
                                50000,
                                new byte[]{(byte)0xff},
                                new ArrayList<>()
                        ),
                        new VirtualMachine(
                                49999,
                                new byte[]{(byte)0xff},
                                new ArrayList<>(),
                                1,
                                1,
                                new byte[]{}
                        )
                ),
                new Case<>(
                        new VirtualMachine(
                                100,
                                new byte[]{(byte)0xff}
                        ),
                        new VirtualMachine(
                                99,
                                new byte[]{(byte)0xff},
                                new ArrayList<>(),
                                1,
                                1,
                                new byte[]{}
                        )
                )
        ));
        cases.forEach(c -> {
            c.data.step();
            Assert.assertEquals(c.want, c.data);
        });

        List<Case<VirtualMachine, String>> errCases = new ArrayList<>(Arrays.asList(
                new Case<>(new VirtualMachine(50000, new byte[]{OP.OP_ADD}), Errors.ErrDataStackUnderflow),
                new Case<>(new VirtualMachine(1, new byte[]{OP.OP_INDEX}, new Transaction()), Errors.ErrRunLimitExceeded),
                new Case<>(new VirtualMachine(1, new byte[]{(byte)255}, true), Errors.ErrDisallowedOpcode)
        ));
        errCases.forEach(c -> {
            Exception err = null;
            try {
                c.data.step();
            } catch (VMRunTimeException e) {
                err = e;
            }
            Assert.assertNotNull(err);
            Assert.assertEquals(c.want, err.getMessage());
        });
    }

    private void doOKNotOK(boolean expectOK) {
        class Case {
            private String program;
            private byte[][] args;

            private Case(String program, byte[][] args) {
                this.program = program;
                this.args = args;
            }
        }
        Case[] cases = new Case[] {
                new Case("TRUE", new byte[][]{}),
                // bitwise ops
                new Case("INVERT 0xfef0 EQUAL", new byte[][]{new byte[]{(byte)0x01, (byte)0x0f}}),
                new Case("AND 0x02 EQUAL", new byte[][]{new byte[]{(byte)0x03}, new byte[]{(byte)0x06}}),
                new Case("AND 0x02 EQUAL", new byte[][]{new byte[]{(byte)0x03, (byte)0xff}, new byte[]{(byte)0x06}}),
                new Case("OR 0x07 EQUAL", new byte[][]{new byte[]{(byte)0x03}, new byte[]{(byte)0x06}}),
                new Case("OR 0x07ff EQUAL", new byte[][]{new byte[]{(byte)0x03, (byte)0xff}, new byte[]{(byte)0x06}}),
                new Case("XOR 0x05 EQUAL", new byte[][]{new byte[]{(byte)0x03}, new byte[]{(byte)0x06}}),
                new Case("XOR 0x05ff EQUAL", new byte[][]{new byte[]{(byte)0x03, (byte)0xff}, new byte[]{(byte)0x06}}),
                // numeric and logical ops
                new Case("1ADD 2 NUMEQUAL", new byte[][]{Types.int64Bytes(1)}),
                new Case("1ADD 0 NUMEQUAL", new byte[][]{Types.int64Bytes(-1)}),
                new Case("1SUB 1 NUMEQUAL", new byte[][]{Types.int64Bytes(2)}),
                new Case("1SUB -1 NUMEQUAL", new byte[][]{Types.int64Bytes(0)}),
                new Case("2MUL 2 NUMEQUAL", new byte[][]{Types.int64Bytes(1)}),
                new Case("2MUL 0 NUMEQUAL", new byte[][]{Types.int64Bytes(0)}),
                new Case("2MUL -2 NUMEQUAL", new byte[][]{Types.int64Bytes(-1)}),
                new Case("2DIV 1 NUMEQUAL", new byte[][]{Types.int64Bytes(2)}),
                new Case("2DIV 0 NUMEQUAL", new byte[][]{Types.int64Bytes(1)}),
                new Case("2DIV 0 NUMEQUAL", new byte[][]{Types.int64Bytes(0)}),
                new Case("2DIV -1 NUMEQUAL", new byte[][]{Types.int64Bytes(-1)}),
                new Case("2DIV -1 NUMEQUAL", new byte[][]{Types.int64Bytes(-2)}),
                new Case("NEGATE -1 NUMEQUAL", new byte[][]{Types.int64Bytes(1)}),
                new Case("NEGATE 1 NUMEQUAL", new byte[][]{Types.int64Bytes(-1)}),
                new Case("NEGATE 0 NUMEQUAL", new byte[][]{Types.int64Bytes(0)}),
                new Case("ABS 1 NUMEQUAL", new byte[][]{Types.int64Bytes(1)}),
                new Case("ABS 1 NUMEQUAL", new byte[][]{Types.int64Bytes(-1)}),
                new Case("ABS 0 NUMEQUAL", new byte[][]{Types.int64Bytes(0)}),
                new Case("0NOTEQUAL", new byte[][]{Types.int64Bytes(1)}),
                new Case("0NOTEQUAL NOT", new byte[][]{Types.int64Bytes(0)}),
                new Case("ADD 5 NUMEQUAL", new byte[][]{Types.int64Bytes(2), Types.int64Bytes(3)}),
                new Case("SUB 2 NUMEQUAL", new byte[][]{Types.int64Bytes(5), Types.int64Bytes(3)}),
                new Case("MUL 6 NUMEQUAL", new byte[][]{Types.int64Bytes(2), Types.int64Bytes(3)}),
                new Case("DIV 2 NUMEQUAL", new byte[][]{Types.int64Bytes(6), Types.int64Bytes(3)}),
                new Case("MOD 0 NUMEQUAL", new byte[][]{Types.int64Bytes(6), Types.int64Bytes(2)}),
                new Case("MOD 0 NUMEQUAL", new byte[][]{Types.int64Bytes(-6), Types.int64Bytes(2)}),
                new Case("MOD 0 NUMEQUAL", new byte[][]{Types.int64Bytes(-6), Types.int64Bytes(2)}),
                new Case("MOD 0 NUMEQUAL", new byte[][]{Types.int64Bytes(6), Types.int64Bytes(-2)}),
                new Case("MOD 0 NUMEQUAL", new byte[][]{Types.int64Bytes(-6), Types.int64Bytes(-2)}),
                new Case("MOD 2 NUMEQUAL", new byte[][]{Types.int64Bytes(12), Types.int64Bytes(10)}),
                new Case("MOD 8 NUMEQUAL", new byte[][]{Types.int64Bytes(-12), Types.int64Bytes(10)}),
                new Case("MOD -8 NUMEQUAL", new byte[][]{Types.int64Bytes(12), Types.int64Bytes(-10)}),
                new Case("MOD -2 NUMEQUAL", new byte[][]{Types.int64Bytes(-12), Types.int64Bytes(-10)}),
                new Case("LSHIFT 2 NUMEQUAL", new byte[][]{Types.int64Bytes(1), Types.int64Bytes(1)}),
                new Case("LSHIFT 4 NUMEQUAL", new byte[][]{Types.int64Bytes(1), Types.int64Bytes(2)}),
                new Case("LSHIFT -2 NUMEQUAL", new byte[][]{Types.int64Bytes(-1), Types.int64Bytes(1)}),
                new Case("LSHIFT -4 NUMEQUAL", new byte[][]{Types.int64Bytes(-1), Types.int64Bytes(2)}),
                new Case("1 1 BOOLAND", new byte[][]{}),
                new Case("1 0 BOOLAND NOT", new byte[][]{}),
                new Case("0 1 BOOLAND NOT", new byte[][]{}),
                new Case("0 0 BOOLAND NOT", new byte[][]{}),
                new Case("1 1 BOOLOR", new byte[][]{}),
                new Case("1 0 BOOLOR", new byte[][]{}),
                new Case("0 1 BOOLOR", new byte[][]{}),
                new Case("0 0 BOOLOR NOT", new byte[][]{}),
                new Case("1 2 OR 3 EQUAL", new byte[][]{}),
                // splice ops
                new Case("0 CATPUSHDATA 0x0000 EQUAL", new byte[][]{new byte[]{0x00}}),
                new Case("0 0xff CATPUSHDATA 0x01ff EQUAL", new byte[][]{}),
                new Case("CATPUSHDATA 0x050105 EQUAL", new byte[][]{new byte[]{0x05}, new byte[]{0x05}}),
                new Case("CATPUSHDATA 0xff01ff EQUAL", new byte[][]{new byte[]{(byte)0xff}, new byte[]{(byte)0xff}}),
                new Case("0 0xcccccc CATPUSHDATA 0x03cccccc EQUAL", new byte[][]{}),
                new Case("0x05 0x05 SWAP 0xdeadbeef CATPUSHDATA DROP 0x05 EQUAL", new byte[][]{}),
                // control flow ops
                new Case("1 JUMP:7 0 1 EQUAL", new byte[][]{}),
                new Case("1 JUMP:$target 0 $target 1 EQUAL", new byte[][]{}),
                new Case("1 1 JUMPIF:8 0 1 EQUAL", new byte[][]{}),
                new Case("1 1 JUMPIF:$target 0 $target 1 EQUAL", new byte[][]{}),
                new Case("1 0 JUMPIF:8 0 1 EQUAL NOT", new byte[][]{}),
                new Case("1 0 JUMPIF:$target 0 $target 1 EQUAL NOT", new byte[][]{}),
                new Case("1 0 JUMPIF:1", new byte[][]{}),
                new Case("1 $target 0 JUMPIF:$target", new byte[][]{}),
                new Case("4 1 JUMPIF:14 5 EQUAL JUMP:16 4 EQUAL", new byte[][]{}),
                new Case("4 1 JUMPIF:$true 5 EQUAL JUMP:$end $true 4 EQUAL $end", new byte[][]{}),
                new Case("5 0 JUMPIF:14 5 EQUAL JUMP:16 4 EQUAL", new byte[][]{}),
                new Case("5 0 JUMPIF:$true 5 EQUAL JUMP:$end $true 4 $test EQUAL $end", new byte[][]{}),
                new Case("0 1 2 3 4 5 6 JUMP:13 DROP DUP 0 NUMNOTEQUAL JUMPIF:12 1", new byte[][]{}),
                new Case("0 1 2 3 4 5 6 JUMP:$dup $drop DROP $dup DUP 0 NUMNOTEQUAL JUMPIF:$drop 1", new byte[][]{}),
                new Case("0 JUMP:7 1ADD DUP 10 LESSTHAN JUMPIF:6 10 NUMEQUAL", new byte[][]{}),
                new Case("0 JUMP:$dup $add 1ADD $dup DUP 10 LESSTHAN JUMPIF:$add 10 NUMEQUAL", new byte[][]{}),
        };

        for (Case c : cases) {
            if (!expectOK) {
                c.program += " NOT";
            }
            byte[] program = Assemble.assemble(c.program);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            VirtualMachine.traceOut = new PrintStream(buf);
            VirtualMachine vm = new VirtualMachine(VirtualMachine.initialRunLimit, program, new ArrayList<>(Arrays.asList(c.args)));
            try {
                boolean ok = vm.run();
                Assert.assertEquals(expectOK, ok);
            } catch (VMRunTimeException e) {
                e.printStackTrace();
                VirtualMachine.traceOut.flush();
                System.out.write(buf.toByteArray(), 0, buf.toByteArray().length);
            }
        }
    }
}
