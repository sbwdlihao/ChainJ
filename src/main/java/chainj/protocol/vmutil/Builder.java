package chainj.protocol.vmutil;

import chainj.protocol.vm.PushData;
import com.google.common.primitives.Bytes;

/**
 * Created by sbwdlihao on 31/12/2016.
 */
class Builder {
    private byte[] program = new byte[0];

    byte[] getProgram() {
        return program;
    }

    Builder addInt64(long n) {
        program = Bytes.concat(program, PushData.pushDataInt64(n));
        return this;
    }

    Builder addData(byte[] data) {
        program = Bytes.concat(program, PushData.pushDataBytes(data));
        return this;
    }

    Builder addRawBytes(byte[] data) {
        program = Bytes.concat(program, data);
        return this;
    }

    Builder addOP(byte op) {
        program = Bytes.concat(program, new byte[]{op});
        return this;
    }
}
