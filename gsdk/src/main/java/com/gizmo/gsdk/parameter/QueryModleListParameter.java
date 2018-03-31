package com.gizmo.gsdk.parameter;

import com.gizmo.gsdk.GSDK;
import com.gizmo.gsdk.utils.Predication;
import com.gizmo.gsdk.utils.SignatureUtil;

/**
 * Created by kl on 18-3-18.
 */

public class QueryModleListParameter extends BaseParameter {
    //模型浏览接口
    private static final String QUERY_URL = "https://staging.gizmohub.com/api/v2/contents?";

    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private String timestamp;
    private int limit;
    private int offset;

    public QueryModleListParameter(String timestamp,int limit,int offset){
        this.timestamp = timestamp;
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public String toURL() throws NullPointerException {
        Predication.checkNotNullString(EMAIL, GSDK.sEmail);
        Predication.checkNotNullString(TIMESTAMP,timestamp);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(QUERY_URL)
                .append(EMAIL).append("=").append(GSDK.sEmail)
                .append("&").append(TIMESTAMP).append("=").append(timestamp)
                .append("&").append(LIMIT).append("=").append(limit)
                .append("&").append(OFFSET).append("=").append(offset)
                .append("&").append(SIGNATURE).append("=").append(SignatureUtil.signature(GSDK.sEmail,GSDK.sToken,timestamp));
        return stringBuffer.toString();
    }
}
