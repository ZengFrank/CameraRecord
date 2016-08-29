package com.ben.camerarecord;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    Button btnRecord;

    private SurfaceView surfaceview;// 视频预览控件
    private LinearLayout lay; //愿揽控件的
    private SurfaceHolder surfaceHolder; //和surfaceView相关的
    static RecordThread  thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRecord = (Button)findViewById(R.id.btn_record);
        init();
        //for android M request permission
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_REQUEST_CODE);
            return;
        }
    }
    private static final int MY_REQUEST_CODE = 101;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                onResume();
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
                finish();
            }
        }
    }

    /**
     * 初始化控件以及回调
     */
    private void init() {

        surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
        lay=(LinearLayout)this.findViewById(R.id.lay);
        lay.setVisibility(LinearLayout.INVISIBLE);
        SurfaceHolder holder = this.surfaceview.getHolder();// 取得holder
        holder.addCallback(this); // holder加入回调接口
        // 设置setType
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public  void btnStartClick(View view){
        thread=new RecordThread(0,surfaceview,surfaceHolder,this);
        thread.start();
        btnRecord.setEnabled(false);
    }


    public  void btnStopClick(View view){
        //Log.d("","thread="+thread.isAlive());
        if (thread != null) {
            thread.stopRecord();
        }
        btnRecord.setEnabled(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {
    // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
// surfaceDestroyed的时候同时对象设置为null
        surfaceview = null;
        surfaceHolder = null;
    }
}
