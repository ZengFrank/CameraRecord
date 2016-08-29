package com.ben.camerarecord;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class RecordActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView surfaceview;// 视频预览控件
    private LinearLayout lay; //愿揽控件的
    private SurfaceHolder surfaceHolder; //和surfaceView相关的

    /**
     * onCreate方法
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record);
        //初始化控件
        init();
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


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
        Log.i("process",Thread.currentThread().getName());
        //录像线程，当然也可以在别的地方启动，但是一定要在onCreate方法执行完成以及surfaceHolder被赋值以后启动
        RecordThread thread=new RecordThread(20000,surfaceview,surfaceHolder,this);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // surfaceDestroyed的时候同时对象设置为null
        surfaceview = null;
        surfaceHolder = null;
    }
}
