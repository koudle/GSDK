package com.gizmohub.sdk.parameter;

/**
 * Created by kl on 18-3-18.
 */

public abstract class BaseParameter {
    public static final String CONTENT_ID = "content_id";
    public static final String ACCESSKEY = "accesskey";
    public static final String TIMESTAMP = "timestamp";

    protected static final String SIGNATURE = "signature";


    public abstract String toOnLineURL() throws NullPointerException;

}
