package com.gizmohub.gsdksmaple;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gizmohub.sdk.GHSDK;
import com.gizmohub.sdk.parameter.model.ARParameters;
import com.gizmohub.sdk.parameter.model.TestParameter;

/**
 * Created by kl on 18-3-18.
 */

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);
        GHSDK.init(getApplicationContext(),"KTxCe4KGCc","tgxhxjwyhehzbtnfgtatt9jfydlvhcpjnxuymcbd");

        findViewById(R.id.display_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display();
            }
        });

        findViewById(R.id.ar_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ar();
            }
        });

        findViewById(R.id.ar_scan_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera(0);
            }
        });

        findViewById(R.id.ar_3d_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera(1);
            }
        });

        findViewById(R.id.ar_ar_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera(2);
            }
        });



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 10001);
        }
    }

    private void display(){
        Intent intent = new Intent(SampleActivity.this, GActivity.class);
        //ModelParameters testParameter = new ModelParameters("b7fdfb6e28200aff3c4025b1fef8c1e475e2b5e0",false,true);
        TestParameter testParameter = new TestParameter();
        intent.putExtra(GActivity.PARAMETER,testParameter.toOnLineURL());
        SampleActivity.this.startActivity(intent);
    }

    private void ar(){
        Intent intent = new Intent(SampleActivity.this, GActivity.class);
        ARParameters testParameter = new ARParameters("b7fdfb6e28200aff3c4025b1fef8c1e475e2b5e0",true,true);
        intent.putExtra(GActivity.PARAMETER,testParameter.toOnLineURL());
        SampleActivity.this.startActivity(intent);
    }

    private void startCamera(int type){
        Intent intent = new Intent(SampleActivity.this,CameraActivity.class);
        intent.putExtra(CameraActivity.TYPE,type);
        SampleActivity.this.startActivity(intent);
    }


}
