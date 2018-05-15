package com.gizmo.gsdksmaple;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.gizmo.gsdk.R;
import com.gizmo.gsdk.parameter.car.CarParameter;
import com.gizmo.gsdk.parameter.car.CarStateInfo;
import com.gizmo.gsdk.parameter.car.CarStateModel;
import com.gizmo.gsdk.view.GView;

/**
 * Created by kl on 18-3-18.
 */

public class GActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PARAMETER = "parameter";
    private GView gView;
    private boolean carLight = false;
    private boolean car3DButton = false;
    private boolean carDoor = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gactivity);

        gView = (GView) findViewById(R.id.gview);
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
        if(intent == null || gView == null) return;
        //BaseParameter baseParameter = intent.get(PARAMETER);
        CarStateInfo stateInfo = new CarStateInfo();
        stateInfo.CarLight = carLight;
        stateInfo.CarExterior = CarStateModel.build().setName("F00002-6");
        //stateInfo.CarView = CarStateModel.build().setName("look");
        stateInfo.CarDoors = new Boolean[]{false,false};
        stateInfo.CarWheel = CarStateModel.build().setName("F00003-5");
        stateInfo.Car3DButtons =car3DButton;
        CarParameter carParameter = new CarParameter("",stateInfo,false);
        gView.loadModel(carParameter, null);
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
            gView.modifyCarExterior(CarStateModel.build().setName("F00002-7"));
        }else if(id == R.id.blue){
            gView.modifyCarExterior(CarStateModel.build().setName("F00002-8"));
        }else if(id == R.id.black){
            gView.modifyCarExterior(CarStateModel.build().setName("F00002-6"));
        }else if(id == R.id.gray){
            gView.modifyCarExterior(CarStateModel.build().setName("F00002-9"));
        }else if(id == R.id.door){
            carDoor = !carDoor;
            if(carDoor){
                gView.modifyCarDoor(new Boolean[]{true,true});
            }else {
                gView.modifyCarDoor(new Boolean[]{false,false});
            }
        }else if(id == R.id.light){
            carLight = !carLight;
            gView.modifyCarLight(carLight);
        }else if(id == R.id.dButton){
            car3DButton = !car3DButton;
            gView.modifyCar3DButtons(car3DButton);
        }else if(id == R.id.outview){
            gView.modifyCarView(CarStateModel.build().setName("orbit"));
        }else if(id == R.id.inview){
            gView.modifyCarView(CarStateModel.build().setName("look"));
        }
    }
}
