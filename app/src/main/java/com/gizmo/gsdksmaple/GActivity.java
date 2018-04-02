package com.gizmo.gsdksmaple;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.gizmo.gsdk.R;
import com.gizmo.gsdk.parameter.ModelParameters;
import com.gizmo.gsdk.parameter.TestParameter;
import com.gizmo.gsdk.view.GView;

/**
 * Created by kl on 18-3-18.
 */

public class GActivity extends AppCompatActivity {

    public static final String PARAMETER = "parameter";
    private GView gView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gactivity);
        findViewById(R.id.toggle_ar_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gView != null) {
                    gView.toggleAr();
                }
            }
        });
        gView = (GView) findViewById(R.id.gview);
        load3D();
    }

    private void load3D(){
        Intent intent = getIntent();
        if(intent == null || gView == null) return;
        //BaseParameter baseParameter = intent.get(PARAMETER);
        gView.load3D( intent.getStringExtra(PARAMETER));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && gView.goBack() ) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
