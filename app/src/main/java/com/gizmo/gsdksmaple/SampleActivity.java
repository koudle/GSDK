package com.gizmo.gsdksmaple;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.gizmo.gsdk.GSDK;
import com.gizmo.gsdk.activity.GActivity;
import com.gizmo.gsdk.opencv.OpenCVTest;
import com.gizmo.gsdk.parameter.DisplayParameters;
import com.gizmo.gsdk.parameter.TestParameter;

/**
 * Created by kl on 18-3-18.
 */

public class SampleActivity extends AppCompatActivity {
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
    }

    private void display(){
        Intent intent = new Intent(SampleActivity.this, GActivity.class);
        Bundle bundle = new Bundle();
        TestParameter testParameter = new TestParameter();
        bundle.putString(GActivity.PARAMETER,testParameter.toURL());
        SampleActivity.this.startActivity(intent);
    }

    private void search(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int[] resultPixels = OpenCVTest.gray(pix, w, h);
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        result.setPixels(resultPixels,0,w,0,0,w,h);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(result);
    }
}
