package com.example.user.alquerque;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by user on 2017.11.13..
 */

public class Stone {
    final static int FIELDS_DST = 110;
    final static int BOARD_EDGE_DST =20;
    Bitmap bitmap;
    final static int Y_OFFSET = 100;
int posX =0;
    int posY = 0;
    int id=0;
    boolean isWhite = false;
    boolean ownerComputer = false;
    AnimationThread animationThread;
    int owner = -1;   // -1 - black, 1 - white
    boolean selected = false;
   int status = 1; // 0, ja noņemts no spēles
    //Canvas canvas;
    Paint paint;
    Stone(Resources res,int posX,int posY,boolean isWhite,boolean ownerComputer ,int id){
        //this.canvas = canvas;
       this.id = id;
        this.ownerComputer = ownerComputer;
        this.isWhite = isWhite;
        this.posY = posY;
        this.posX = posX;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        if(!isWhite) {

            bitmap = BitmapFactory.decodeResource(res, R.drawable.stoneb, o);
        }
            else {
            bitmap = BitmapFactory.decodeResource(res, R.drawable.white_stone, o);
            owner = 1;
            }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
paint.setColor(Color.RED);

    }
    void animateMove(int x,int y){
        animationThread = new AnimationThread(x, y);
        animationThread.start();
       while (animationThread.isAlive());//gaida
    }

int getPos(){

    int row = (posY-100)/FIELDS_DST+1;// 100- Board Y offset
    int col = (posX)/FIELDS_DST+1;
    return (row-1)*5+col;
}
    void draw(Canvas canvas){
        if(status>0)
        canvas.drawBitmap(bitmap,posX,posY,null);
       // canvas.drawText("own "+owner,posX,posY+30,paint);
        //canvas.drawText("id "+id,posX,posY+50,paint);


       // if(selected)
    //canvas.drawText("pos "+getPos(),posX,posY+10,paint);

    }
class AnimationThread extends Thread{
    int x;
    int y;

    AnimationThread(int x, int y){
        this.x = x;
        this.y= y;

    }
    @Override
    public void run() {
        moveTo(x,y);

        super.run();
    }
    //synchronized void setStatus

    boolean moveTo(int x,int y){

        int deltaX = (int)Math.signum(x-posX);
        int deltaY = (int)Math.signum(y - posY);
        while(posY!=y||posX!=x){
            if(posX!=x)
                posX+=deltaX;

            if(posY!=y);
            posY+= deltaY;

            try {
    sleep(2);
}
catch (Exception e){}

        }
return true;
    }

}


}
