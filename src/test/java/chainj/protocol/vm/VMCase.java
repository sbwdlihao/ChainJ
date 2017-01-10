package chainj.protocol.vm;

import org.junit.Assert;

import java.util.List;

/**
 * Created by sbwdlihao on 04/01/2017.
 */
class VMCase {
    private byte op;
    private VirtualMachine startVM;
    private String wantErr;
    private VirtualMachine wantVM;

    VMCase(byte op, VirtualMachine startVM, VirtualMachine wantVM) {
        this.op = op;
        this.startVM = startVM;
        this.wantVM = wantVM;
    }

    VMCase(byte op, VirtualMachine startVM, String wantErr) {
        this.op = op;
        this.startVM = startVM;
        this.wantErr = wantErr;
    }

    static void runCase(List<VMCase> cases) {
        cases.forEach(c -> {
            try {
                OPInfo.getOPInfo(c.op).getFn().apply(c.startVM);
                Assert.assertEquals(c.wantVM, c.startVM);
            } catch (VMRunTimeException e) {
                Assert.assertEquals(c.wantErr, e.getMessage());
            }
        });
    }

    static void runCaseForCrypto(List<VMCase> cases) {
        cases.forEach(c -> {
            try {
                OPInfo.getOPInfo(c.op).getFn().apply(c.startVM);
                c.wantVM.setSigHasher(c.startVM.getSigHasher());
                Assert.assertEquals(c.wantVM, c.startVM);
            } catch (VMRunTimeException e) {
                Assert.assertEquals(c.wantErr, e.getMessage());
            }
        });
    }

    static void runCaseForIntrospection(List<VMCase> cases) {
        cases.forEach(c -> {
            byte[] program = new byte[]{c.op};
            VirtualMachine vm = c.startVM;
            if (c.wantErr == null || !c.wantErr.equals(Errors.ErrRunLimitExceeded)) {
                vm.setRunLimit(50000);
            }
            if (vm.getMainProgram().length == 0) {
                vm.setMainProgram(program);
            }
            vm.setProgram(program);
            try {
                vm.run();
                c.wantVM.setMainProgram(vm.getMainProgram());
                c.wantVM.setProgram(program);
                c.wantVM.setProgramIndex(1);
                c.wantVM.setNextProgramIndex(1);
                c.wantVM.setSigHasher(c.startVM.getSigHasher());
                Assert.assertEquals(c.wantVM, c.startVM);
            } catch (VMRunTimeException e) {
                Assert.assertEquals(c.wantErr, e.getMessage());
            }
        });
    }
}
