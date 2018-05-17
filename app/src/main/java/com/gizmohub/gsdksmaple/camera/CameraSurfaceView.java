package com.gizmohub.gsdksmaple.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.gizmohub.sdk.logo.GLogoRecog;
import com.gizmohub.sdk.logo.LogoResultListener;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback {

    private static final String TAG = CameraSurfaceView.class.getSimpleName();

    private SurfaceHolder mSurfaceHolder;
    private boolean isSave = false;
    private long previewTime = 0;
    private GLogoRecog gLogoRecog;

    public CameraSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        gLogoRecog = new GLogoRecog(getContext());
//        gLogoRecog.setLogoResultListener(new LogoResultListener() {
//            @Override
//            public void onRecoging() {
//                Toast.makeText(getContext(), "识别中", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onRecogSuceess(LogoResult logoResult) {
//                Toast.makeText(getContext(), "cose:"+logoResult.cost+"\nname:" + logoResult.name + "\nid:" + logoResult.id, Toast.LENGTH_SHORT).show();
//            }
//        });
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtils.autoFocus();
            }
        });
    }

    public void setgLogoRecogListener(LogoResultListener logoRecogListener){
        gLogoRecog.setLogoResultListener(logoRecogListener);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraUtils.openBackCamera(CameraUtils.DESIRED_PREVIEW_FPS,this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraUtils.startPreviewDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraUtils.releaseCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(previewTime <= 0) {
            previewTime = SystemClock.uptimeMillis();
        }else {
            if(SystemClock.uptimeMillis() - previewTime > 500 ){
                Log.e("recogn","onPreviewFrame");

                isSave = true;
                previewTime = SystemClock.uptimeMillis();
                gLogoRecog.recognize(data, camera);
                Log.d("bitmap","cost time:"+String.valueOf(SystemClock.uptimeMillis()-previewTime));
            }
        }
    }
}
