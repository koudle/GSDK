package com.gizmohub.gsdksmaple;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.gizmohub.sdk.R;
import com.gizmohub.sdk.parameter.car.CarParameter;
import com.gizmohub.sdk.parameter.car.CarStateInfo;
import com.gizmohub.sdk.parameter.car.CarStateModel;
import com.gizmohub.sdk.view.GHCallback;
import com.gizmohub.sdk.view.GHView;

/**
 * Created by kl on 18-3-18.
 */

public class GActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PARAMETER = "parameter";
    private GHView GHView;
    private boolean carLight = false;
    private boolean car3DButton = false;
    private boolean carDoor = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gactivity);

        GHView = (GHView) findViewById(R.id.gview);
        findViewById(R.id.white).setOnClickListener(this);
        findViewById(R.id.blue).setOnClickListener(this);
        findViewById(R.id.black).setOnClickListener(this);
        findViewById(R.id.gray).setOnClickListener(this);
        findViewById(R.id.door).setOnClickListener(this);
        findViewById(R.id.light).setOnClickListener(this);
        findViewById(R.id.dButton).setOnClickListener(this);
        findViewById(R.id.outview).setOnClickListener(this);
        findViewById(R.id.inview).setOnClickListener(this);

        load3D();
    }

    private void load3D(){
        Intent intent = getIntent();
        if(intent == null || GHView == null) return;
        //BaseParameter baseParameter = intent.get(PARAMETER);
        CarStateInfo stateInfo = new CarStateInfo();
        stateInfo.headLight = carLight;
        stateInfo.exterior = CarStateModel.build().setName("F00002-6");
        stateInfo.CarDoors = new Boolean[]{false,false};
        stateInfo.wheel = CarStateModel.build().setName("F00003-5");
        stateInfo.annotations =car3DButton;
        CarParameter carParameter = new CarParameter("e0408eb7c275385aadee0af29f407af2f40be717",stateInfo);
        GHView.addEventListener("gizmohub:preload:progress", new GHCallback() {
            @Override
            public void callback(String value) {
                Toast.makeText(GActivity.this,value,Toast.LENGTH_SHORT).show();
            }
        });
        GHView.loadOnLineModel(carParameter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && gView.goBack() ) {
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.white){
            GHView.modifyExterior(CarStateModel.build().setName("F00002-7"));
        }else if(id == R.id.blue){
            GHView.modifyExterior(CarStateModel.build().setName("F00002-8"));
        }else if(id == R.id.black){
            GHView.modifyExterior(CarStateModel.build().setName("F00002-6"));
        }else if(id == R.id.gray){
            GHView.modifyExterior(CarStateModel.build().setName("F00002-9"));
        }else if(id == R.id.door){
            carDoor = !carDoor;
            if(carDoor){
                GHView.modifyCarDoor(new Boolean[]{true,true});
            }else {
                GHView.modifyCarDoor(new Boolean[]{false,false});
            }
        }else if(id == R.id.light){
            carLight = !carLight;
            GHView.modifyCarLight(carLight);
        }else if(id == R.id.dButton){
            car3DButton = !car3DButton;
            GHView.modifyAnnotations(car3DButton);
        }else if(id == R.id.outview){
            GHView.modifySkyBox(CarStateModel.build().setName("orbit"));
        }else if(id == R.id.inview){
            GHView.modifySkyBox(CarStateModel.build().setName("look"));
        }
    }
}
