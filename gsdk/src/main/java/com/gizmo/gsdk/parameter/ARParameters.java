package com.gizmo.gsdk.parameter;

public class ARParameters extends ModelParameters {

    public ARParameters(String content_id, String autostart, String autoload) {
        super(content_id, autostart, autoload);
    }

    @Override
    public String toURL() throws NullPointerException {
        String url = super.toURL();
        url += "&ar=1";
        return url;
    }
}
