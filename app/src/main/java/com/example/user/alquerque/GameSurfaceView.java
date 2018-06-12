package com.example.user.alquerque;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017.11.12..
 */

public class GameSurfaceView extends SurfaceView {
    private SurfaceHolder surfaceHolder;
    GameBoard gameBoard;
    Stone[] stones;
    int toucX = 0;
    int toucY = 0;
    int lost = 0;
    Stone selectedStone;
    final int TOUCH_PRECISION = 40;
    int selectedPos = 0;
    List<Point> freeAdjPos;
    List<List<Point>> possiblePaths = new ArrayList<>();
    Paint paint;
    Paint cMovesPaint;
    static final Integer lock = 1;
    int startMove = 1;
    int turn = 1;//balti, -1 melnie
    int heir = 0;
    MiPlayer miPlayer;

    public GameSurfaceView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        //  surfaceHolder.setFixedSize(480,800);

        surfaceHolder.addCallback(callback);
    }

    public GameSurfaceView(Context context,
                           AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        //   surfaceHolder.setFixedSize(480,800);
        surfaceHolder.addCallback(callback);
    }

    public GameSurfaceView(Context context,
                           AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        surfaceHolder = getHolder();
        //      surfaceHolder.setFixedSize(480,800);

        surfaceHolder.addCallback(callback);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(480, 800);
            setLayoutParams(params);
            loadBitmaps(1);

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d("SurfaceView", "format :" + format + " w: " + width + " h: " + height);

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        toucX = (int) event.getX();
        toucY = (int) event.getY();

        selectedPos = getSelectedPosition(toucX, toucY);
        if (selectedPos != 0) {
            int i = getStoneAtLocation(selectedPos);
            if (selectedStone != null) {//ir ieziimeets akmens

                if (i == 0 && lost == 0) {//mēģināt veikt gaajienu

                    Point desiredMove = new Point(selectedStone.getPos(), selectedPos);
                    List<Point> path = getPossiblePathFromPoint(desiredMove);
                    if (path != null) {
                        makeJumpOrNormalMove(path);
                        //makeMove(desiredMove,turn);
                        Vertice currentVertice = gameBoard.occupancyArrayToVertice();
                        heir = currentVertice.value;
                        makeComputerMove();
                    }
                }
            }
            if (i != 0 && stones[i].owner == turn)//vai nav touch tukšā lauciņā
            {
                selectStone(i);// test

            }
        }

        return super.onTouchEvent(event);

    }

    List<Point> getPossiblePathFromPoint(Point noUz) {
        for (List<Point> path : possiblePaths) {
            if (path.get(0).x == noUz.x && path.get(path.size() - 1).y == noUz.y)
                return path;
        }
        return null;
    }

    void redraw(Canvas canvas) {


    }

    void createStones() {
        stones = new Stone[25];
        int a = 1;
        for (int row = 5; row >= 1; row--) {
            for (int col = 5; col >= 1; col--) {
                int posX = Stone.FIELDS_DST * (col - 1) + Stone.BOARD_EDGE_DST;
                int posY = Stone.FIELDS_DST * (row - 1) + Stone.BOARD_EDGE_DST + gameBoard.Y_OFFSET;
                Log.d("sur", "a: " + a);

                stones[a] = new Stone(getResources(), posX - Stone.BOARD_EDGE_DST, posY - Stone.BOARD_EDGE_DST, false, true, a);
                gameBoard.occupancy[stones[a].getPos()] = -1;//c
                a++;

                if (a == 13) {
                    col = 0;
                    row = 0;
                }
            }
        }

        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 5; col++) {
                int posX = Stone.FIELDS_DST * (col - 1) + Stone.BOARD_EDGE_DST;
                int posY = Stone.FIELDS_DST * (row - 1) + Stone.BOARD_EDGE_DST + gameBoard.Y_OFFSET;
                Log.d("sur", "ah: " + a);

                stones[a] = new Stone(getResources(), posX - Stone.BOARD_EDGE_DST, posY - Stone.BOARD_EDGE_DST, true, false, a);
                gameBoard.occupancy[stones[a].getPos()] = 1;//h
                a++;
                if (a == 25) {
                    col = 6;
                    row = 6;
                }//brake all

            }
        }
    }

    void drawStones(Canvas canvas) {
        // if(stones!=null)// imp
        drawValidPaths(canvas);

        for (int x = 1; x <= 24; x++) {

            // Log.d("sur","x: "+x );
            // if(stones[x].status>0)//neziimeet izdzestos
            stones[x].draw(canvas);
        }
        if (lost == 1)
            canvas.drawText("Melnie uzvarēja", 40, 300, cMovesPaint);
        else if (lost == -1)
            canvas.drawText("Baltie uzvarēja", 40, 300, paint);

    }

    void drawValidPaths(Canvas canvas) {
        //if(freeAdjPos!=null) {
        if (possiblePaths != null) {
            synchronized (lock) {
                for (int i = 0; i < possiblePaths.size(); i++) {
                    for (Point pos : possiblePaths.get(i)) {
                        Point p = getPointFromPosition(pos.y);
                        Point p2 = getPointFromPosition(pos.x);
                        if (turn == 1)
                            canvas.drawLine(p2.x, p2.y, p.x, p.y, paint);
                        else
                            canvas.drawLine(p2.x, p2.y, p.x, p.y, cMovesPaint);

                    }
                }
            }
        }

    }

    Point getPointFromPosition(int pos) {
        int row = (pos - 1) / 5;
        int col = (pos - 1) % 5;

        int posX = Stone.FIELDS_DST * (col) + Stone.BOARD_EDGE_DST;
        int posY = Stone.FIELDS_DST * (row) + Stone.BOARD_EDGE_DST + gameBoard.Y_OFFSET;
        return new Point(posX, posY);
    }

    int getSelectedPosition(int x, int y) {
        int a = 0;
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 5; col++) {
                int posX = Stone.FIELDS_DST * (col - 1) + Stone.BOARD_EDGE_DST;
                int posY = Stone.FIELDS_DST * (row - 1) + Stone.BOARD_EDGE_DST + gameBoard.Y_OFFSET;
                if (x + TOUCH_PRECISION > posX && x - TOUCH_PRECISION < posX && y + TOUCH_PRECISION > posY && y - TOUCH_PRECISION < posY)
                    return 5 * (row - 1) + col;
                //  stones[a].posY = posY-BOARD_EDGE_DST;
                // stones[a++].posX = posX-BOARD_EDGE_DST;

//Log.d("Surface",posX+" "+posY);

            }
        }
        return 0;//no match
    }

    int getStoneAtLocation(int pos) {

        for (int i = 1; i < stones.length; i++) {
            if (stones[i].getPos() == pos && stones[i].status != 0) {
                return i;
            }

        }
        return 0;
    }


    void deselectStones() {
        for (int i = 1; i < stones.length; i++) {
            stones[i].selected = false;
        }
        synchronized (lock) {
            selectedStone = null;
        }
    }

    void selectStone(int index) {
        deselectStones();
        stones[index].selected = true;
        selectedStone = stones[index];
    }

    List<List<Point>> getAllPosibleMoves(int caller) {//caller- owner

        List<Point> validTargetPos = new ArrayList<>(20);
        List<List<Point>> jumpPaths = new ArrayList<>();
        int callerStonesCount = 0;
        for (int i = 1; i < stones.length; i++) {
            if (stones[i].status != 0 && stones[i].owner == caller) {
                validTargetPos.addAll(gameBoard.getPly2JumpPositions(stones[i].getPos(), caller));
                jumpPaths.addAll(gameBoard.getPly2JumpPositionsMulti(stones[i].getPos(), caller));
                callerStonesCount++;
            }
        }
     //   Log.d("sur", "jumps:  " + validTargetPos.size());

        if (validTargetPos.isEmpty()) {//ja nav iespējams lēciens
            for (int i = 1; i < stones.length; i++) {
                if (stones[i].status != 0 && stones[i].owner == caller)
                    //          validTargetPos.addAll(gameBoard.getFreeAdjPositions(stones[i].getPos(), caller));
                    jumpPaths.addAll(gameBoard.getFreeAdjPositions(stones[i].getPos(), caller));
            }
        }
      //  Log.d("sur", "totalMoves :  " + validTargetPos.size());
        if (callerStonesCount > 1)
            //return validTargetPos;
            return jumpPaths;
        else return null;
    }

    void makeJumpOrNormalMove(List<Point> path) {
        if (gameBoard.isJumpMove) {
            for (Point p : path) {
                makeJumpStep(p);
            }
        } else {
            makeNormalMove(path.get(0));
        }
        deselectStones();
        possiblePaths = null;
        // freeAdjPos = null;
        turn *= -1; // mainaas gājiens
        //freeAdjPos = getAllPosibleMoves(turn);
        possiblePaths = getAllPosibleMoves(turn);
        //if(freeAdjPos==null){
        if (possiblePaths == null) {
            lost = turn;
        }

    }

    void makeJumpStep(Point noUz) {

        //if (freeAdjPos.contains(noUz)) {//paarbauda vai c/h izveletais deriigs gajiens

        Point dest = getPointFromPosition(noUz.y);
        if (gameBoard.isJumpMove) {
            int remove = gameBoard.getJumpOverPosition(noUz.x, noUz.y);//ja ir jāizpilda lēciens, tad noskaidro kuram akmenim tas būs pāri
            //Log.d("sur", "selectedStonePos: " + selectedStone.getPos() + " selPos: " + selectedPos + " removePos: " + remove);
            Log.d("sur", " stones index: " + getStoneAtLocation(remove) + " rmove: " + remove);
            stones[getStoneAtLocation(remove)].status = 0;//izdzēš akmeni
            //stones[getStoneAtLocation(remove)].posX = 1000;// pārvieto prom no lauka
            gameBoard.occupancy[remove] = 0;
        }
        gameBoard.occupancy[noUz.y] = selectedStone.owner;//atzīmē jauno vietu kā aizņemtu --
        gameBoard.occupancy[selectedStone.getPos()] = 0;// veco kā brīvu
        // selectedStone.posX = dest.x - Stone.BOARD_EDGE_DST; // lai zīmētu pareizās koordinātēs, bet šeit kods nevietā
        //selectedStone.posY = dest.y - Stone.BOARD_EDGE_DST;
        selectedStone.animateMove(dest.x - Stone.BOARD_EDGE_DST, dest.y - Stone.BOARD_EDGE_DST);

    }

    void makeNormalMove(Point noUz) {
        Point dest = getPointFromPosition(noUz.y);

        gameBoard.occupancy[noUz.y] = selectedStone.owner;//atzīmē jauno vietu kā aizņemtu --
        gameBoard.occupancy[selectedStone.getPos()] = 0;// veco kā brīvu
        // selectedStone.posX = dest.x - Stone.BOARD_EDGE_DST; // lai zīmētu pareizās koordinātēs, bet šeit kods nevietā
        //selectedStone.posY = dest.y - Stone.BOARD_EDGE_DST;
        selectedStone.animateMove(dest.x - Stone.BOARD_EDGE_DST, dest.y - Stone.BOARD_EDGE_DST);

    }

    void makeMove(Point noUz, int owner) {

        //if (freeAdjPos.contains(noUz)) {//paarbauda vai c/h izveletais deriigs gajiens

        Point dest = getPointFromPosition(noUz.y);
        if (gameBoard.isJumpMove) {
            int remove = gameBoard.getJumpOverPosition(noUz.x, noUz.y);//ja ir jāizpilda lēciens, tad noskaidro kuram akmenim tas būs pāri
            //Log.d("sur", "selectedStonePos: " + selectedStone.getPos() + " selPos: " + selectedPos + " removePos: " + remove);
            Log.d("sur", " stones index: " + getStoneAtLocation(remove) + " rmove: " + remove);
            stones[getStoneAtLocation(remove)].status = 0;//izdzēš akmeni
            //stones[getStoneAtLocation(remove)].posX = 1000;// pārvieto prom no lauka
            gameBoard.occupancy[remove] = 0;
        }
        gameBoard.occupancy[noUz.y] = selectedStone.owner;//atzīmē jauno vietu kā aizņemtu --
        gameBoard.occupancy[selectedStone.getPos()] = 0;// veco kā brīvu
        // selectedStone.posX = dest.x - Stone.BOARD_EDGE_DST; // lai zīmētu pareizās koordinātēs, bet šeit kods nevietā
        //selectedStone.posY = dest.y - Stone.BOARD_EDGE_DST;
        selectedStone.animateMove(dest.x - Stone.BOARD_EDGE_DST, dest.y - Stone.BOARD_EDGE_DST);
        deselectStones();
        possiblePaths = null;
        // freeAdjPos = null;
        turn *= -1; // mainaas gājiens
        //freeAdjPos = getAllPosibleMoves(turn);
        possiblePaths = getAllPosibleMoves(turn);
        //if(freeAdjPos==null){
        if (possiblePaths == null) {
            lost = turn;
        }
        //  }


    }

    void makeComputerMove() {
        if (possiblePaths != null && !possiblePaths.isEmpty()) {//ja ir iespējami gājieni

//        if(freeAdjPos!=null&&!freeAdjPos.isEmpty()){//ja ir iespējami gājieni
            Vertice v = gameBoard.occupancyArrayToVertice();
            Log.d("sur", "mi vertice1 " + v.getPositions());

            miPlayer.evaluatedVertices = 0;
            Vertice next = miPlayer.getBestNextVertice(v, 3, 1);//preteeja turn noziime
            if (next == null)
                return;
            Log.d("sur", "mi vertice2 " + next.getPositions());


            //Point p = freeAdjPos.get((int)(Math.random()*freeAdjPos.size()));
            Point p = v.getMove(v, next);
            List<Point> path = getPossiblePathFromPoint(p);
            Log.d("sur", "Computer Move from " + p.x + " uz " + p.y);
            if (path != null) {
                selectStone(getStoneAtLocation(p.x));
//makeMove(p,turn);
                makeJumpOrNormalMove(path);
            }
        }
    }

    void loadBitmaps(int turn) {
        Log.d("sur", "load: ");
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.DKGRAY);
        paint.setAlpha(150);
        paint.setStrokeWidth(7);
        paint.setTextSize(60);
        paint.setFakeBoldText(true);
        cMovesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cMovesPaint.setColor(Color.RED);
        cMovesPaint.setAlpha(150);
        cMovesPaint.setStrokeWidth(7);
        cMovesPaint.setTextSize(60);
        cMovesPaint.setFakeBoldText(true);
        startMove = turn;
        this.turn = turn;
        gameBoard = new GameBoard(getResources());
        createStones();
        lost = 0;
        //     freeAdjPos=getAllPosibleMoves(turn);
        possiblePaths = getAllPosibleMoves(turn);
        miPlayer = new MiPlayer();

    }


}
