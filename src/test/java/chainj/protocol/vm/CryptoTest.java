package chainj.protocol.vm;

import chainj.protocol.bc.*;
import chainj.protocol.bc.txinput.SpendInput;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sbwdlihao on 08/01/2017.
 */
public class CryptoTest {

    @Test
    public void  testCheckSig() {
        class Case {
            private String program;
            private boolean ok;
            private String err;

            private Case(String program, boolean ok, String  err) {
                this.program = program;
                this.ok = ok;
                this.err = err;
            }
        }
        Case[] cases = new Case[] {
                // This one's OK
                new Case(
                        "0x26ced30b1942b89ef5332a9f22f1a61e5a6a3f8a5bc33b2fc58b1daf78c81bf1d5c8add19cea050adeb37da3a7bf8f813c6a6922b42934a6441fa6bb1c7fc208 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20 0xdbca6fb13badb7cfdf76510070ffad15b85f9934224a9e11202f5e8f86b584a6 CHECKSIG",
                        true, ""
                ),
                // This one has a wrong-length signature
                new Case(
                        "0x26ced30b1942b89ef5332a9f22f1a61e5a6a3f8a5bc33b2fc58b1daf78c81bf1d5c8add19cea050adeb37da3a7bf8f813c6a6922b42934a6441fa6bb1c7fc2 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20 0xdbca6fb13badb7cfdf76510070ffad15b85f9934224a9e11202f5e8f86b584a6 CHECKSIG",
                        false, "java.security.SignatureException: signature length is wrong"
                ),
                // This one has a wrong-length message
                new Case(
                        "0x26ced30b1942b89ef5332a9f22f1a61e5a6a3f8a5bc33b2fc58b1daf78c81bf1d5c8add19cea050adeb37da3a7bf8f813c6a6922b42934a6441fa6bb1c7fc208 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f 0xdbca6fb13badb7cfdf76510070ffad15b85f9934224a9e11202f5e8f86b584a6 CHECKSIG",
                        false, Errors.ErrBadValue
                ),
                // This one has a wrong-length pubKey
                new Case(
                        "0x26ced30b1942b89ef5332a9f22f1a61e5a6a3f8a5bc33b2fc58b1daf78c81bf1d5c8add19cea050adeb37da3a7bf8f813c6a6922b42934a6441fa6bb1c7fc208 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20 0xdbca6fb13badb7cfdf76510070ffad15b85f9934224a9e11202f5e8f86b584 CHECKSIG",
                        false, ""
                ),
                // This one has a wrong byte in the signature
                new Case(
                        "0x26ced30b1942b89ef5332a9f22f1a61e5a6a3f8a5bc33b2fc58b1daf78c81bf1d5c8add19cea050adeb37da3a7bf8f813c6a6922b42934a6441fa6bb1c7fc208 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20 0xdbca6fb13badb7cfdf76510070ffad15b85f9934224a9e11202f5e8f86b584 CHECKSIG",
                        false, ""
                ),
                // This one has a wrong byte in the message
                new Case(
                        "0x26ced30b1942b89ef5332a9f22f1a61e5a6a3f8a5bc33b2fc58b1daf78c81bf1d5c8add19cea050adeb37da3a7bf8f813c6a6922b42934a6441fa6bb1c7fc208 0x0002030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20 0xdbca6fb13badb7cfdf76510070ffad15b85f9934224a9e11202f5e8f86b584a6 CHECKSIG",
                        false, ""
                ),
                // This one has a wrong byte in the pubKey
                new Case(
                        "0x26ced30b1942b89ef5332a9f22f1a61e5a6a3f8a5bc33b2fc58b1daf78c81bf1d5c8add19cea050adeb37da3a7bf8f813c6a6922b42934a6441fa6bb1c7fc208 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20 0x00ca6fb13badb7cfdf76510070ffad15b85f9934224a9e11202f5e8f86b584a6 CHECKSIG",
                        false, ""
                ),
                new Case(
                        "0x010203 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20 0x040506 1 1 CHECKMULTISIG",
                        false, ""
                ),
                new Case(
                        "0x010203 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f 0x040506 1 1 CHECKMULTISIG",
                        false, Errors.ErrBadValue
                ),
                new Case(
                        "0x26ced30b1942b89ef5332a9f22f1a61e5a6a3f8a5bc33b2fc58b1daf78c81bf1d5c8add19cea050adeb37da3a7bf8f813c6a6922b42934a6441fa6bb1c7fc208 0x0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20 0xdbca6fb13badb7cfdf76510070ffad15b85f9934224a9e11202f5e8f86b584a6 1 1 CHECKMULTISIG",
                        true, ""
                ),
        };

        for (Case c : cases) {
            byte[] program = Assemble.assemble(c.program);
            VirtualMachine vm = new VirtualMachine(50000, program);
            Exception err = null;
            boolean ok = false;
            try {
                ok = vm.run();
            } catch (Exception e) {
                err = e;
            }
            if (!c.err.equals("")) {
                Assert.assertNotNull(err);
                Assert.assertEquals(c.err, err.getMessage());
            } else {
                Assert.assertEquals(c.ok, ok);
            }
        }
    }

    @Test
    public void  testCryptoOps() {
        Transaction tx = new Transaction(new TxData(
                new TxInput[]{
                        new SpendInput(new Hash(), 0, new byte[][]{}, new AssetID(), 5, new byte[]{}, new byte[]{})
                },
                new TxOutput[]{}
        ));

        List<VMCase> cases = new ArrayList<>(Arrays.asList(
                new VMCase(OP.OP_RIPEMD160,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{1}))),
                        new VirtualMachine(49917, new ArrayList<>(Arrays.asList(new byte[]{
                                (byte)242, (byte)145, (byte)186, 80, 21, (byte)223, 52, (byte)140, (byte)128, (byte)133,
                                63, (byte)165, (byte)187, 15, 121, 70, (byte)245, (byte)201, (byte)225, (byte)179,
                        })))
                ),
                new VMCase(OP.OP_RIPEMD160,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[65]))),
                        new VirtualMachine(49980, new ArrayList<>(Arrays.asList(new byte[]{
                                (byte)171, 60, 102, (byte)205, 10, 63, 18, (byte)180, (byte)244, (byte)250,
                                (byte)235, 84, (byte)138, 85, 22, 7, (byte)148, (byte)250, (byte)215, 6,
                        })))
                ),
                new VMCase(OP.OP_SHA1,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{1}))),
                        new VirtualMachine(49917, new ArrayList<>(Arrays.asList(new byte[]{
                                (byte)191, (byte)139, 69, 48, (byte)216, (byte)210, 70, (byte)221, 116, (byte)172,
                                83, (byte)161, 52, 113, (byte)187, (byte)161, 121, 65, (byte)223, (byte)247,
                        })))
                ),
                new VMCase(OP.OP_SHA1,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[65]))),
                        new VirtualMachine(49980, new ArrayList<>(Arrays.asList(new byte[]{
                                (byte)240, (byte)250, 69, (byte)144, 107, (byte)208, (byte)244, (byte)195, 102, (byte)143,
                                (byte)205, 13, (byte)143, 104, (byte)212, (byte)178, (byte)152, (byte)179, 14, 91,
                        })))
                ),
                new VMCase(OP.OP_SHA256,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{1}))),
                        new VirtualMachine(49905, new ArrayList<>(Arrays.asList(new byte[]{
                                75, (byte)245, 18, 47, 52, 69, 84, (byte)197, 59, (byte)222, 46, (byte)187, (byte)140, (byte)210, (byte)183, (byte)227,
                                (byte)209, 96, 10, (byte)214, 49, (byte)195, (byte)133, (byte)165, (byte)215, (byte)204, (byte)226, 60, 119, (byte)133, 69, (byte)154,
                        })))
                ),
                new VMCase(OP.OP_SHA256,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[65]))),
                        new VirtualMachine(49968, new ArrayList<>(Arrays.asList(new byte[]{
                                (byte)152, (byte)206, 66, (byte)222, (byte)239, 81, (byte)212, 2, 105, (byte)213, 66, (byte)245, 49, 75, (byte)239, 44,
                                116, 104, (byte)212, 1, (byte)173, 93, (byte)133, 22, (byte)139, (byte)250, (byte)180, (byte)192, 16, (byte)143, 117, (byte)247,
                        })))
                ),
                new VMCase(OP.OP_SHA3,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[]{1}))),
                        new VirtualMachine(49905, new ArrayList<>(Arrays.asList(new byte[]{
                                39, 103, (byte)241, 92, (byte)138, (byte)242, (byte)242, (byte)199, 34, 93, 82, 115, (byte)253, (byte)214, (byte)131, (byte)237,
                                (byte)199, 20, 17, 10, (byte)152, 125, 16, 84, 105, 124, 52, (byte)138, (byte)237, 78, 108, (byte)199,
                        })))
                ),
                new VMCase(OP.OP_SHA3,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(new byte[65]))),
                        new VirtualMachine(49968, new ArrayList<>(Arrays.asList(new byte[]{
                                65, 106, (byte)167, (byte)181, (byte)192, (byte)224, 101, 48, 102, (byte)167, (byte)198, 77, (byte)189, (byte)208, 0, (byte)157,
                                (byte)190, (byte)132, 56, 97, 81, (byte)254, 3, (byte)159, (byte)217, 66, (byte)250, (byte)162, (byte)219, 97, 114, (byte)235,
                        })))
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866")
                        ))),
                        new VirtualMachine(48976, -143, new ArrayList<>(Arrays.asList(new byte[]{1})))
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("badda7a7a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866")
                        ))),
                        new VirtualMachine(48976, -144, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("bad220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866")
                        ))),
                        new VirtualMachine(48976, -144, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("badabdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866")
                        ))),
                        new VirtualMachine(48976, -144, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(50000, new ArrayList<>()),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866")
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866")
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("badbad"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866")
                                ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKSIG,
                        new VirtualMachine(0),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866"),
                                new byte[]{1}, new byte[]{1}
                        ))),
                        new VirtualMachine(48976, -161, new ArrayList<>(Arrays.asList(new byte[]{1})))
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("badabdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866"),
                                new byte[]{1}, new byte[]{1}
                        ))),
                        new VirtualMachine(48976, -162, new ArrayList<>(Arrays.asList(new byte[]{})))
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>()),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                new byte[]{1}, new byte[]{1}
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                new byte[]{1}, new byte[]{1}
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                new byte[]{1}, new byte[]{1}
                        ))),
                        Errors.ErrDataStackUnderflow
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("badbad"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866"),
                                new byte[]{1}, new byte[]{1}
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866"),
                                new byte[]{1}, new byte[]{0}
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866"),
                                new byte[]{0}, new byte[]{1}
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(50000, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866"),
                                new byte[]{2}, new byte[]{1}
                        ))),
                        Errors.ErrBadValue
                ),
                new VMCase(OP.OP_CHECKMULTISIG,
                        new VirtualMachine(0, new ArrayList<>(Arrays.asList(
                                Hex.decode("af5abdf4bbb34f4a089efc298234f84fd909def662a8df03b4d7d40372728851fbd3bf59920af5a7c361a4851967714271d1727e3be417a60053c30969d8860c"),
                                Hex.decode("916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9"),
                                Hex.decode("ab3220d065dc875c6a5b4ecc39809b5f24eb0a605e9eef5190457edbf1e3b866"),
                                new byte[]{1}, new byte[]{1}
                        ))),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_TXSIGHASH,
                        new VirtualMachine(50000, tx, new SigHasher(tx.getTxData())),
                        new VirtualMachine(49704, tx, new ArrayList<>(Arrays.asList(
                                new byte[]{
                                        (byte)249, 70, (byte)194, 24, 124, 118, (byte)190, (byte)163, 46, (byte)222, 120, (byte)132, 95, (byte)216, (byte)244, (byte)228,
                                        (byte)142, 83, (byte)200, 43, 54, (byte)241, (byte)189, 38, 7, 28, (byte)211, 123, (byte)145, 16, (byte)186, (byte)133,
                                }
                        )))
                ),
                new VMCase(OP.OP_TXSIGHASH,
                        new VirtualMachine(0, tx, new SigHasher(tx.getTxData())),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_TXSIGHASH,
                        new VirtualMachine(50000, null, new SigHasher(tx.getTxData())),
                        Errors.ErrContext
                ),
                new VMCase(OP.OP_BLOCKSIGHASH,
                        new VirtualMachine(50000, new Block()),
                        new VirtualMachine(49832, new Block(), new ArrayList<>(Arrays.asList(
                                new byte[]{
                                        46, 87, (byte)204, (byte)195, 74, 20, 1, 41, (byte)253, (byte)183, 90, 121, 57, 8, (byte)151, 70,
                                        (byte)184, 65, 6, (byte)185, 30, (byte)180, 112, 95, (byte)211, 21, 21, 49, (byte)218, 27, (byte)166, 88,
                                }
                        )))
                ),
                new VMCase(OP.OP_BLOCKSIGHASH,
                        new VirtualMachine(0, new Block()),
                        Errors.ErrRunLimitExceeded
                ),
                new VMCase(OP.OP_BLOCKSIGHASH,
                        new VirtualMachine(50000),
                        Errors.ErrContext
                )
        ));

        for (byte b : new byte[]{OP.OP_RIPEMD160, OP.OP_SHA1, OP.OP_SHA256, OP.OP_SHA3}) {
            cases.add(new VMCase(b, new VirtualMachine(0, new ArrayList<>(Arrays.asList(new byte[]{1}))), Errors.ErrRunLimitExceeded));
            cases.add(new VMCase(b, new VirtualMachine(50000, new ArrayList<>()), Errors.ErrDataStackUnderflow));
        }

        VMCase.runCaseForCrypto(cases);
    }
}
