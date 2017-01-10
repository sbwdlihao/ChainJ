package chainj.protocol.vm;

import chainj.protocol.bc.*;
import chainj.protocol.bc.txinput.IssuanceInput;
import chainj.protocol.bc.txinput.SpendInput;
import com.google.common.primitives.Bytes;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sbwdlihao on 08/01/2017.
 */
public class IntrospectionTest {

    @Test
    public void testNextProgram() {
        Block block = new Block(new BlockHeader(new byte[]{1, 2, 3}));
        byte[] program = Assemble.assemble("NEXTPROGRAM 0x010203 EQUAL");
        VirtualMachine vm = new VirtualMachine(50000, block, program);
        boolean ok = vm.run();
        Assert.assertTrue(ok);

        program = Assemble.assemble("NEXTPROGRAM 0x0102 EQUAL");
        vm = new VirtualMachine(50000, block, program);
        ok = vm.run();
        Assert.assertFalse(ok);
    }

    @Test
    public void testBlockTime() {
        Block block = new Block(new BlockHeader(3263827));
        byte[] program = Assemble.assemble("BLOCKTIME 3263827 NUMEQUAL");
        VirtualMachine vm = new VirtualMachine(50000, block, program);
        boolean ok = vm.run();
        Assert.assertTrue(ok);

        program = Assemble.assemble("BLOCKTIME 3263826 NUMEQUAL");
        vm = new VirtualMachine(50000, block, program);
        ok = vm.run();
        Assert.assertFalse(ok);
    }

    @Test
    public void testOutpointAndNonceOp() {
        Hash zeroHash = new Hash();
        byte[] nonce = new byte[]{36, 37, 38};
        Transaction tx = new Transaction(new TxData(new TxInput[]{
                new SpendInput(zeroHash, 0, new byte[][]{}, new AssetID((byte)1), 5, "spendprog".getBytes(), "ref".getBytes()),
                new IssuanceInput(nonce, new AssetID(), 6, new byte[]{}, zeroHash, "issueprog".getBytes(), new byte[][]{})
        }));
        VirtualMachine vm = new VirtualMachine(50000, tx, 0, new byte[]{OP.OP_OUTPOINT});
        vm.step();

        List<byte[]> expectedStack = new ArrayList<>(Arrays.asList(
                zeroHash.getValue(), new byte[]{}
        ));
        Assert.assertTrue(Arrays.deepEquals(expectedStack.toArray(), vm.getDataStack().toArray()));

        vm = new VirtualMachine(50000, tx, 1, new byte[]{OP.OP_OUTPOINT});
        try {
            vm.step();
        } catch (VMRunTimeException e) {
            Assert.assertEquals(Errors.ErrContext, e.getMessage());
        }

        vm = new VirtualMachine(50000, tx, 0, new byte[]{OP.OP_NONCE});
        try {
            vm.step();
        } catch (VMRunTimeException e) {
            Assert.assertEquals(Errors.ErrContext, e.getMessage());
        }

        vm = new VirtualMachine(50000, tx, 1, new byte[]{OP.OP_NONCE});
        vm.step();
        expectedStack = new ArrayList<>(Arrays.asList(
                nonce
        ));
        Assert.assertTrue(Arrays.deepEquals(expectedStack.toArray(), vm.getDataStack().toArray()));
    }

    @Test
    public void testIntrospectionTest() {
        Transaction tx = new Transaction(new TxData(
                0,
                new TxInput[]{
                        new SpendInput(new Hash(), 0, new byte[][]{}, new AssetID((byte)1), 5, "spendprog".getBytes(), "ref".getBytes()),
                        new IssuanceInput(new byte[]{}, new AssetID(), 6, new byte[]{}, new Hash(), "issueprog".getBytes(), new byte[][]{})
                },
                new TxOutput[]{
                        new TxOutput(new AssetID((byte)3), 8, "wrongprog".getBytes(), new byte[]{}),
                        new TxOutput(new AssetID((byte)3), 8, "controlprog".getBytes(), new byte[]{}),
                        new TxOutput(new AssetID((byte)2), 8, "controlprog".getBytes(), new byte[]{}),
                        new TxOutput(new AssetID((byte)2), 7, "controlprog".getBytes(), new byte[]{}),
                        new TxOutput(new AssetID((byte)2), 7, "controlprog".getBytes(), "outref".getBytes()),
                },
                0,
                20,
                "txref".getBytes()
        ));

        List<VMCase> cases = new ArrayList<>(Arrays.asList(
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{4},
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        new VirtualMachine(50101, -117, tx, new ArrayList<>(Arrays.asList(new byte[]{1})))
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{3},
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        new VirtualMachine(50102, -118, tx, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{0},
                                new byte[]{},
                                new byte[]{1},
                                Bytes.concat(new byte[]{9}, new byte[31]),
                                new byte[]{1},
                                "missingprog".getBytes()
                        ))),
                        new VirtualMachine(50070, -86, tx, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, new ArrayList<>(Arrays.asList(
                                new byte[]{0},
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrContext
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{4},
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                Types.int64Bytes(-1),
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{4},
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                Types.int64Bytes(-1),
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                Types.int64Bytes(-1),
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{5},
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKOUTPUT,
                        new VirtualMachine(0, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{4},
                                Hex.decode("1f2a05f881ed9fa0c9068a84823677409f863891a2196eb55dbfbb677a566374"),
                                new byte[]{7},
                                Bytes.concat(new byte[]{2}, new byte[31]),
                                new byte[]{1},
                                "controlprog".getBytes()
                        ))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_ASSET,
                        new VirtualMachine(0, tx),
                        new VirtualMachine(49959, 40, tx, new ArrayList<>(Arrays.asList(
                                Bytes.concat(new byte[]{1}, new byte[31])
                        )))
                ),
                new VMCase(OP.OP_AMOUNT,
                        new VirtualMachine(0, tx),
                        new VirtualMachine(49990, 9, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{5}
                        )))
                ),
                new VMCase(OP.OP_PROGRAM,
                        new VirtualMachine(0, tx, "spendprog".getBytes()),
                        new VirtualMachine(49982, 17, tx, new ArrayList<>(Arrays.asList(
                                "spendprog".getBytes()
                        )))
                ),
                new VMCase(OP.OP_PROGRAM,
                        new VirtualMachine(50000, tx, "issueprog".getBytes(), 1),
                        new VirtualMachine(49982, 17, tx, new ArrayList<>(Arrays.asList(
                                "issueprog".getBytes()
                        )), 1)
                ),
                new VMCase(OP.OP_MINTIME,
                        new VirtualMachine(0, tx),
                        new VirtualMachine(49991, 8, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{}
                        )))
                ),
                new VMCase(OP.OP_MAXTIME,
                        new VirtualMachine(0, tx),
                        new VirtualMachine(49990, 9, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{20}
                        )))
                ),
                new VMCase(OP.OP_TXREFDATAHASH,
                        new VirtualMachine(0, tx),
                        new VirtualMachine(49959, 40, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{
                                        62, 81, (byte)144, (byte)242, 105, 30, 109, 69, 28, 80, (byte)237, (byte)249, (byte)169, (byte)166, 106, 122,
                                        103, 121, (byte)199, (byte)135, 103, 100, 82, (byte)129, 13, (byte)191, 79, 110, 64, 83, 104, 44,
                                }
                        )))
                ),
                new VMCase(OP.OP_INDEX,
                        new VirtualMachine(0, tx),
                        new VirtualMachine(49991, 8, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{}
                        )))
                )
        ));

        for (byte b : new byte[]{OP.OP_CHECKOUTPUT, OP.OP_ASSET, OP.OP_AMOUNT, OP.OP_PROGRAM,
                OP.OP_MINTIME, OP.OP_MAXTIME, OP.OP_TXREFDATAHASH, OP.OP_REFDATAHASH, OP.OP_INDEX, OP.OP_OUTPOINT}) {
            cases.add(new VMCase(b, new VirtualMachine(0, tx), Errors.ErrRunLimitExceeded));
            cases.add(new VMCase(b, new VirtualMachine(0), Errors.ErrContext));
        }

        VMCase.runCaseForIntrospection(cases);
    }
}
