package com.gizmohub.sdk.parameter.model;

import com.gizmohub.sdk.parameter.BaseParameter;

/**
 * Created by kl on 18-3-18.
 */

public class TestParameter extends BaseParameter {

    public TestParameter(){
    }

    @Override
    public String toOnLineURL() throws NullPointerException {
//        return "https://gizmohub.com/models/27ff8325c01a0bd2200795858b3531d03b376670/embed";
        return "https://gizmohub.com/models/e3660160d6963d5269cdabaf8c940526c4a3e940/embed#LD";
    }

}
