package com.gizmo.gsdksmaple;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.gizmo.gsdk.GSDK;
import com.gizmo.gsdk.activity.GActivity;
import com.gizmo.gsdk.logo.LogoRecog;
import com.gizmo.gsdk.parameter.TestParameter;

import java.io.File;

/**
 * Created by kl on 18-3-18.
 */

public class SampleActivity extends AppCompatActivity {
    LogoRecog logoRecog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);
        GSDK.init(getApplicationContext(),"test_api@gizmohub.com","16a37b5dddbeadd477c1c431f62df1d596f6450f");
        findViewById(R.id.display_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display();
            }
        });

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        logoRecog = new LogoRecog(this);
        findViewById(R.id.search2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recog("2.jpg");
            }
        });
        findViewById(R.id.search3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recog("3.jpg");
            }
        });
        findViewById(R.id.search4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recog("4.jpg");
            }
        });
    }

    private void display(){
        Intent intent = new Intent(SampleActivity.this, GActivity.class);
        Bundle bundle = new Bundle();
        TestParameter testParameter = new TestParameter();
        bundle.putString(GActivity.PARAMETER,testParameter.toURL());
        SampleActivity.this.startActivity(intent);
    }

    private void search(){
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
//
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();
//        int[] pix = new int[w * h];
//        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

//        Intent intent = new Intent(this,CameraActivity.class);
//        this.startActivity(intent);
        recog("1.jpg");
    }

    private void recog(String name){
        final long now = SystemClock.uptimeMillis();
        new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... strings) {
                if(strings.length == 0){
                    return -1001;
                }
                int result=logoRecog.recognize(strings[0]);
                return result;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                Toast.makeText(SampleActivity.this,"result:"+integer+" time:"+String.valueOf(SystemClock.uptimeMillis()-now),Toast.LENGTH_SHORT).show();
            }
        }.execute(this.getFilesDir()+ File.separator+name);
    }
}
