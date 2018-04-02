package com.gizmo.gsdksmaple;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gizmo.gsdk.logo.LogoResult;
import com.gizmo.gsdk.logo.LogoResultListener;
import com.gizmo.gsdk.parameter.ARParameters;
import com.gizmo.gsdk.parameter.ModelParameters;
import com.gizmo.gsdk.utils.ImageUtils;
import com.gizmo.gsdksmaple.camera.CameraSurfaceView;
import com.gizmo.gsdksmaple.camera.CameraUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TYPE = "type";


    private static final int REQUEST_CAMERA = 0x01;

    private CameraSurfaceView mCameraSurfaceView;
    private Button mBtnTake;
    private Button mBtnSwitch;

    private int mOrientation;

    // CameraSurfaceView 容器包装类
    private FrameLayout mAspectLayout;
    private boolean mCameraRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera);
        // Android 6.0相机动态权限检查
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initView();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_CAMERA);
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        mAspectLayout = (FrameLayout) findViewById(R.id.layout_aspect);;
        mCameraSurfaceView = new CameraSurfaceView(this);
        mAspectLayout.addView(mCameraSurfaceView);
        mOrientation = CameraUtils.calculateCameraPreviewOrientation(CameraActivity.this);
        mBtnTake = (Button) findViewById(R.id.btn_take);
        mBtnTake.setOnClickListener(this);
        mBtnSwitch = (Button) findViewById(R.id.btn_switch);
        mBtnSwitch.setOnClickListener(this);

        int type = getIntent().getIntExtra(TYPE,0);

        if(type == 0) {
            mCameraSurfaceView.setgLogoRecogListener(new LogoResultListener() {
                @Override
                public void onRecoging() {
                }

                @Override
                public void onRecogSuceess(LogoResult logoResult) {
                    Toast.makeText(CameraActivity.this, "name:" + logoResult.name + "\nid:" + logoResult.id, Toast.LENGTH_SHORT).show();
                }
            });
        }else if(type == 1) {
            mCameraSurfaceView.setgLogoRecogListener(new LogoResultListener() {
                @Override
                public void onRecoging() {

                }

                @Override
                public void onRecogSuceess(LogoResult logoResult) {
                    Intent intent = new Intent(CameraActivity.this, GActivity.class);
                    ModelParameters testParameter = new ModelParameters("b7fdfb6e28200aff3c4025b1fef8c1e475e2b5e0","1","1");
                    intent.putExtra(GActivity.PARAMETER,testParameter.toURL());
                    CameraActivity.this.startActivity(intent);
                }
            });
        }else if(type == 2) {
            mCameraSurfaceView.setgLogoRecogListener(new LogoResultListener() {
                @Override
                public void onRecoging() {

                }

                @Override
                public void onRecogSuceess(LogoResult logoResult) {
                    Intent intent = new Intent(CameraActivity.this, GActivity.class);
                    ARParameters testParameter = new ARParameters("b7fdfb6e28200aff3c4025b1fef8c1e475e2b5e0","1","1");
                    intent.putExtra(GActivity.PARAMETER,testParameter.toURL());
                    CameraActivity.this.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // 相机权限
            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraRequested = true;
                    initView();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraRequested) {
            CameraUtils.startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraUtils.stopPreview();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take:
                takePicture();
                break;

            case R.id.btn_switch:
                switchCamera();
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePicture() {
        CameraUtils.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraUtils.startPreview();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (bitmap != null) {
                    bitmap = ImageUtils.getRotatedBitmap(bitmap, mOrientation);
                    String path = Environment.getExternalStorageDirectory() + "/DCIM/Camera/"
                            + System.currentTimeMillis() + ".jpg";
                    try {
                        FileOutputStream fout = new FileOutputStream(path);
                        BufferedOutputStream bos = new BufferedOutputStream(fout);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                        fout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                CameraUtils.startPreview();
            }
        });
    }


    /**
     * 切换相机
     */
    private void switchCamera() {
        if (mCameraSurfaceView != null) {
            CameraUtils.switchCamera(1 - CameraUtils.getCameraID(), mCameraSurfaceView.getHolder(),mCameraSurfaceView);
            // 切换相机后需要重新计算旋转角度
            mOrientation = CameraUtils.calculateCameraPreviewOrientation(CameraActivity.this);
        }
    }

}
