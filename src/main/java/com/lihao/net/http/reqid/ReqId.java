package com.lihao.net.http.reqid;

import org.apache.http.protocol.HttpContext;

/**
 * Created by sbwdlihao on 12/12/2016.
 */
public class ReqId {

    private static final String ctxKeyPrefix = "ReqId.";

    // reqIDKey is the key for request IDs in Contexts.  It is
    // un exported; clients use NewContext and FromContext
    // instead of using this key directly.
    private static final String reqIDKey = ctxKeyPrefix+"ReqId";

    // subReqIDKey is the key for sub-request IDs in Contexts.  It is
    // unexported; clients use NewSubContext and FromSubContext
    // instead of using this key directly.
    private static final String subReqIDKey = ctxKeyPrefix+"SubReqId";

    // coreIDKey is the key for Chain-Core-ID request header field values.
    // It is only for statistics; don't use it for authorization.
    private static final String coreIDKey = ctxKeyPrefix+"CoreId";

    public static final String Unknown = "unknownReqId";

    public static String fromContext(HttpContext ctx) {
        String reqId = (String) ctx.getAttribute(reqIDKey);
        if (reqId == null) {
            reqId = Unknown;
        }
        return reqId;
    }

    public static String fromSubContext(HttpContext ctx) {
        String reqId = (String) ctx.getAttribute(subReqIDKey);
        if (reqId == null) {
            reqId = Unknown;
        }
        return reqId;
    }

    public static String coreIDFromContext(HttpContext ctx) {
        return (String) ctx.getAttribute(coreIDKey);
    }
}
