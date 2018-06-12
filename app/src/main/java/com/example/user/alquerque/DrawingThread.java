package com.example.user.alquerque;

/**
 * Created by user on 2016.06.05..
 */


        import android.graphics.Canvas;

public class DrawingThread extends Thread {
MainActivity parentActivity;
    GameSurfaceView myView;
    private volatile boolean running = false;


    public DrawingThread(GameSurfaceView view, MainActivity activity) {
        myView = view;
     parentActivity = activity;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        while(running){

            Canvas canvas = myView.getHolder().lockCanvas();

            if(canvas != null){
                synchronized (myView.getHolder()) {
                 //canvas.getDensity();
                   // myView.redraw(canvas);
                    parentActivity.redraw(canvas);
                }
                myView.getHolder().unlockCanvasAndPost(canvas);
            }



        }
    }

}