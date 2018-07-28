package com.gizmohub.sdk.parameter.model;

public class ARParameters extends ModelParameters {

    public ARParameters(String content_id, boolean autostart, boolean autoload) {
        super(content_id, autostart, autoload);
    }

    @Override
    public String toOnLineURL() throws NullPointerException {
        String url = super.toOnLineURL();
        url += "&ar=1";
        return url;
    }
}
