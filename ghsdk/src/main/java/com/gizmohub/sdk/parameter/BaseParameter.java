package com.gizmohub.sdk.parameter;

/**
 * Created by kl on 18-3-18.
 */

public abstract class BaseParameter {
    public static final String CONTENT_ID = "content_id";
    public static final String EMAIL = "email";
    public static final String TIMESTAMP = "timestamp";

    protected static final String SIGNATURE = "signature";


    public abstract String toURL() throws NullPointerException;


}
