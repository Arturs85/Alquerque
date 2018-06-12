package com.example.user.alquerque;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    Canvas ofsc;
    final Bitmap bitmap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
    GameSurfaceView gameSurfaceView;
   // GameBoard gameBoard;
    DrawingThread drawingThread;
Paint textPaint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
gameSurfaceView = (GameSurfaceView) findViewById(R.id.surface);
        //ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.rootView);
        //layout.addView(gameSurfaceView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        drawingThread = new DrawingThread(gameSurfaceView, this);
        drawingThread.setRunning(true);
        drawingThread.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        drawingThread.setRunning(false);
    }

    void init() {
        ofsc = new Canvas(bitmap);
     //   gameBoard = new GameBoard(ofsc, getResources());
       // gameSurfaceView = new GameSurfaceView(this);
textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(18);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_restart:
                drawingThread.setRunning(false);
                drawingThread.interrupt();
                while (drawingThread.isAlive());
                gameSurfaceView.loadBitmaps(gameSurfaceView.startMove*-1);

                //gameSurfaceView.turn=gameSurfaceView.startMove*-1;
                drawingThread=new DrawingThread(gameSurfaceView,this);
                drawingThread.setRunning(true);

                drawingThread.start();
                if(gameSurfaceView.startMove==-1)
                gameSurfaceView.makeComputerMove();

                return true;

        default:
        return super.onOptionsItemSelected(item);
    }}
    void redraw(Canvas canvas) {
canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        gameSurfaceView.gameBoard.draw(canvas);
        canvas.drawText(" h(n)= "+
                gameSurfaceView.heir+" Pārbaudītie stāvokļi: "+gameSurfaceView.miPlayer.evaluatedVertices,10,30,textPaint);
gameSurfaceView.drawStones(canvas);
      //  Log.d("Draw","canvas h: "+ canvas.getHeight()+" w: "+canvas.getWidth()+ " dens: "+canvas.getDensity());
//canvas.drawBitmap(bitmap,0,0,gameBoard.paint);
    }


}
