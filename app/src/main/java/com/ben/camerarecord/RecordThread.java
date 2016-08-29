package com.ben.camerarecord;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RecordThread extends Thread {

    private MediaRecorder mediarecorder;// 录制视频的类
    private SurfaceHolder surfaceHolder;
    private long recordTime;
    private SurfaceView surfaceview;// 显示视频的控件
    private Camera mCamera;
    private Context context;

    public RecordThread(long recordTime, SurfaceView surfaceview,
                        SurfaceHolder surfaceHolder, Context context) {
        this.recordTime = recordTime;
        this.surfaceview = surfaceview;
        this.surfaceHolder = surfaceHolder;
        this.context = context;
    }

    @Override
    public void run() {

        /**
         * 开始录像
         */
        startRecord();

        /**
         * 启动定时器，到规定时间recordTime后执行停止录像任务
         */
        if (recordTime != 0) {
            Timer timer = new Timer();
            timer.schedule(new TimerThread(), recordTime);
        }

    }


    /**
     * 获取摄像头实例对象
     *
     * @return
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // 打开摄像头错误
            Log.i("info", "打开摄像头错误");
        }
        return c;
    }

    //获取文件夹
    private File getDir() {
        //得到SD卡根目录
        File dir = new File( Environment.getExternalStorageDirectory().getPath(),"CameraRecord");
        if (dir.exists()) {
            return dir;
        } else {
            dir.mkdirs();
            return dir;
        }
    }

    /**
     * 开始录像
     */
    public void startRecord() {
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(180);
        // 解锁camera
        mCamera.unlock();
        mediarecorder.setCamera(mCamera);

        // 设置录制视频源为Camera(相机)
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 设置录制文件质量，格式，分辨率之类，这个全部包括了
        mediarecorder.setProfile(CamcorderProfile
                .get(CamcorderProfile.QUALITY_720P));

        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        File pictureFile = new File(getDir(), "CR_"+df.format(new Date()) + ".3gp");
        // 设置视频文件输出的路径
        mediarecorder.setOutputFile(pictureFile.getPath());

        try {
            // 准备录制
            mediarecorder.prepare();
            // 开始录制
            mediarecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mediarecorder != null) {
            // 停止录制
            mediarecorder.stop();
            // 释放资源
            mediarecorder.release();
            mediarecorder = null;

            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }

        }
    }

    /**
     * 定时器
     *
     * @author bcaiw
     */
    class TimerThread extends TimerTask {

        /**
         * 停止录像
         */
        @Override
        public void run() {
            stopRecord();
            this.cancel();
        }
    }
}