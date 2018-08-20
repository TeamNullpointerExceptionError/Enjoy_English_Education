package com.teamnull.user.myapplication;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class SurfaceThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private TwoDrowing Drowing;
    private boolean running = false;

    public SurfaceThread(SurfaceHolder surfaceHolder, TwoDrowing Drowing) {
        this.surfaceHolder = surfaceHolder;
        this.Drowing = Drowing;
    }

    public SurfaceHolder getSurfaceHolder(){
        return surfaceHolder;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {
        try{
            Canvas canvas;
            while(running) {
                canvas = null;

                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        try {
                            Thread.sleep(50);
                            Drowing.draw(canvas);
                        } catch (Exception ex) {

                        }
                    }
                } finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }catch (Exception e){

        }
    }
}