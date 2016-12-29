package chainj.log;

import chainj.net.http.reqid.ReqId;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.util.Strings;

/**
 * Created by sbwdlihao on 12/12/2016.
 */
public class Log {

    public static String contextPrefix(HttpContext ctx) {
        String out;
        String reqId = ReqId.fromContext(ctx);
        out = String.format("reqId=%s", reqId);
        String coreId = ReqId.coreIDFromContext(ctx);
        if (!Strings.isEmpty(coreId)) {
            out += " " + String.format("coreId=%s", coreId);
        }
        String subReqId = ReqId.fromSubContext(ctx);
        if (!Strings.isEmpty(subReqId) && !subReqId.equals(ReqId.Unknown)) {
            out += " " + String.format("subReqId=%s", subReqId);
        }
        return out;
    }
}
