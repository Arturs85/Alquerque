package com.example.user.alquerque;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 2017.11.12..
 */

public class GameBoard {
    Bitmap bitmap;
    final static int Y_OFFSET = 100;
    boolean isJumpMove = false;
    //Canvas canvas;
    Paint paint;
    int[] occupancy = new int[26];
    boolean blackLost = false;
    boolean whiteLost = false;
    List<List<Point>> tempJumpPaths = new ArrayList<>();
    List<List<Point>> tempJumpPathsMi = new ArrayList<>();

    GameBoard(Resources res) {
        //this.canvas = canvas;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmap = BitmapFactory.decodeResource(res, R.drawable.board, o);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    }

    GameBoard() {
        //tikai mi apreekiniem
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, Y_OFFSET, null);

    }

    List<List<Point>> getPly2JumpPositionsMulti(int pos, int caller) {
        //List<List<Point>> validTargetSequences = new ArrayList<>(8);
        int[] occupancy = Arrays.copyOf(this.occupancy, this.occupancy.length);
        tempJumpPaths = new ArrayList<>();
        getPly2JumpPositionsRecu(pos, caller, occupancy, new ArrayList<Point>());
        return tempJumpPaths;
    }

    void getPly2JumpPositionsRecu(int pos, int caller, int[] occupancy, List<Point> pathSoFar) {//rekursijai
        // List<Point> validTargetPos = new ArrayList<>(8);

        int pectecuSkaits = 0;
        for (int i = 0; i < 8; i++) {//pārbauda brīvas 2. līmeņa blakuspozīcijas
            if (ply2Pos[pos - 1][i] != 0 && occupancy[ply2Pos[pos - 1][i]] == 0 && occupancy[blakusPos[pos - 1][i]] == caller * -1) {//ja blakusPozīcija ir brīva
                //validTargetPos.add(new Point(pos, ply2Pos[pos - 1][i]));//x no, y- uz
                pectecuSkaits++;
                int[] occupancyMod = Arrays.copyOf(occupancy, occupancy.length);
                occupancyMod[blakusPos[pos - 1][i]] = 0;//izdzeess nosisto akmeni
                int newTargetPos = ply2Pos[pos - 1][i];
                List<Point> soFar = new ArrayList<>(pathSoFar.size() + 1);
                soFar.addAll(pathSoFar);
                //Collections.copy(soFar,pathSoFar);
                soFar.add(new Point(pos, ply2Pos[pos - 1][i]));

                getPly2JumpPositionsRecu(newTargetPos, caller, occupancyMod, soFar);

//šo ciklu var saīsināt - pārbaudīt tikai tās pozīcijas, pirms kurām ir pretinieks
                //  Log.d("GameBoord", "RecuMoveJump: " + pos + " - " + ply2Pos[pos - 1][i]);

            }

        }//ja nevar turpinaat sissanu

        if (pathSoFar.size() > 0 && pectecuSkaits == 0) { // ja sis ir zemaakais liimenis-galapunkts
            isJumpMove = true;
            Log.d("++++++++++GameBoard", "moveJumpPath: " + pathSoFar.get(0).x + " - " + pathSoFar.get(pathSoFar.size() - 1).y+"size: "+pathSoFar.size());

            tempJumpPaths.add(pathSoFar);
        }

    }


    List<Point> getPly2JumpPositions(int pos, int caller) {
        List<Point> validTargetPos = new ArrayList<>(8);


        for (int i = 0; i < 8; i++) {//pārbauda brīvas 2. līmeņa blakuspozīcijas
            if (ply2Pos[pos - 1][i] != 0 && occupancy[ply2Pos[pos - 1][i]] == 0 && occupancy[blakusPos[pos - 1][i]] == caller * -1) {//ja blakusPozīcija ir brīva
                validTargetPos.add(new Point(pos, ply2Pos[pos - 1][i]));//x no, y- uz
//šo ciklu var saīsināt - pārbaudīt tikai tās pozīcijas, pirms kurām ir pretinieks
                //  Log.d("GameBoord", "moveJump: " + pos + " - " + ply2Pos[pos - 1][i]);

            }

        }
        if (validTargetPos.size() > 0) { // ja iespējams lēciens, citus gājienus nepārbauda
            isJumpMove = true;
        }
        return validTargetPos;

    }


    List<List<Point>> getFreeAdjPositions(int pos, int caller) {
        List<List<Point>> validTargetPos = new ArrayList<>(8);

        for (int i = 0; i < 8; i++) {//pārbauda brīvas blakuspozīcijas
            if (blakusPos[pos - 1][i] != 0 && occupancy[blakusPos[pos - 1][i]] == 0) {//ja blakusPozīcija ir brīva
                List<Point> path = new ArrayList<Point>(1);
                path.add(new Point(pos, blakusPos[pos - 1][i]));
                validTargetPos.add(path);
                //Log.d("GameBoord", "move: " + pos + " - " + blakusPos[pos - 1][i]);
            }

        }
        isJumpMove = false;
        return validTargetPos;
    }


    // forMi
    List<List<Point>> getFreeAdjPositionsMi(Vertice from, int pos, int turn) {
        List<List<Point>> validTargetPos = new ArrayList<>(8);
        int occuBin = from.c | from.h;//visas aizņemtaas poz

        for (int i = 0; i < 8; i++) {//pārbauda brīvas blakuspozīcijas
            if (blakusPos[pos - 1][i] != 0 && ((occuBin >> (blakusPos[pos - 1][i] - 1)) & 1) == 0) {//ja blakusPozīcija ir brīva
                List<Point> path = new ArrayList<Point>(1);
                path.add(new Point(pos, blakusPos[pos - 1][i]));
                validTargetPos.add(path);

                // Log.d("GameBoord", "moveMi: " + pos + " - " + blakusPos[pos - 1][i]);
            }

        }
        isJumpMove = false;
        return validTargetPos;
    }

    //mi
    List<List<Point>> getPly2JumpPositionsMiMulti(int pos, int caller, Vertice from) {
        List<List<Point>> validTargetSequences = new ArrayList<>(8);
        tempJumpPathsMi = new ArrayList<>();
        Vertice start = new Vertice(from.c, from.h);
        getPly2JumpPositionsMi(start, pos, caller, new ArrayList<Point>());
        return tempJumpPathsMi;
    }


    void getPly2JumpPositionsMi(Vertice from, int pos, int caller, List<Point> pathSoFar) {
        // List<Point> validTargetPos = new ArrayList<>(8);
        int occuBin = from.c | from.h;//visas aizņemtaas poz
        int enemyStonesBin;
        if (caller == 1) {//mi gaajiens
            enemyStonesBin = from.h;
        } else
            enemyStonesBin = from.c;
        int pectecuSkaits = 0;


        for (int i = 0; i < 8; i++) {//pārbauda brīvas 2. līmeņa blakuspozīcijas
            if (ply2Pos[pos - 1][i] != 0 && ((occuBin >> (ply2Pos[pos - 1][i] - 1)) & 1) == 0 && ((enemyStonesBin >> (blakusPos[pos - 1][i] - 1)) & 1) == 1) {//ja blakusPozīcija ir brīva
                pectecuSkaits++;

                //occupancy[blakusPos[pos - 1][i]]=0;//izdzeess nosisto akmeni
                int enemyStonesBinMod = enemyStonesBin & ~(1 << (blakusPos[pos - 1][i] - 1));
                Vertice fromMod;
                if (caller == 1) {//izdzeess nososto akmeni
                    fromMod = new Vertice(from.c, enemyStonesBinMod);
                } else {
                    fromMod = new Vertice(enemyStonesBinMod, from.h);


                }

                int newTargetPos = ply2Pos[pos - 1][i];


                List<Point> soFar = new ArrayList<>(pathSoFar.size() + 1);
                soFar.addAll(pathSoFar);
                soFar.add(new Point(pos, newTargetPos));
                getPly2JumpPositionsMi(fromMod, newTargetPos, caller, soFar);


//šo ciklu var saīsināt - pārbaudīt tikai tās pozīcijas, pirms kurām ir pretinieks
                // Log.d("GameBoard", "moveJumpMiMult: " + pos + " - " + ply2Pos[pos - 1][i]);

            }

        }
        if (pathSoFar.size() > 0 && pectecuSkaits == 0) { // ja sis ir zemaakais liimenis-galapunkts
            isJumpMove = true;
            Log.d("-------------GameBoard", "moveJumpMiMultPath: " + pathSoFar.get(0).x + " - " + pathSoFar.get(pathSoFar.size() - 1).y+"size: "+pathSoFar.size());

            tempJumpPathsMi.add(pathSoFar);
        }

    }

    int getJumpDirection(int pos1, int pos2) {
        for (int i = 0; i < 8; i++) {//
            if (ply2Pos[pos1 - 1][i] == pos2) {
                return i;

            }
        }
        return -1;// nav tāda virziena

    }

    int getJumpOverPosition(int pos1, int pos2) {
        int dir = getJumpDirection(pos1, pos2);
        // Log.d("GameBoord", "jumpDir" + dir);
        if (dir >= 0)
            return blakusPos[pos1 - 1][dir];
        else
            return 0;// ja nav leeciens
    }

    Vertice occupancyArrayToVertice() {//pārvērš akmeņu izvietojumu kompaktā formā- 2 int
        int c = 0;
        int h = 0;
        for (int oc = 1; oc < occupancy.length; oc++) {
            if (occupancy[oc] == -1) {// found computer player stone
                c = c | (1 << (oc - 1));
            } else if (occupancy[oc] == 1) {//human player stone
                h = h | (1 << (oc - 1));
            }

        }
        return new Vertice(c, h);
    }

    // for mi
    List<List<Point>> getAllPosibleMoves(int caller, Vertice curLayout) {//caller- owner,
        List<List<Point>> validTargetPos = new ArrayList<>(20);
        int stoneInt = 0;
        if (caller == 1) {
            stoneInt = curLayout.c;

        } else
            stoneInt = curLayout.h;

        int stoneInt2 = stoneInt;
        for (int i = 1; i < Integer.SIZE - 6; i++) {
            if ((stoneInt & 1) > 0) //ja ir akmens sajaa poziicijaa
                // if(stones[i].status!=0&&stones[i].owner==caller)
                validTargetPos.addAll(getPly2JumpPositionsMiMulti(i, caller, curLayout));
            stoneInt = stoneInt >> 1;
        }
        // Log.d("sur", "jumpsMi:  " + validTargetPos.size());
        isJumpMove = true;
        if (validTargetPos.isEmpty()) {//ja nav iespējams lēciens
            for (int i = 1; i < Integer.SIZE - 6; i++) {
                if ((stoneInt2 & 1) > 0) //ja ir akmens sajaa poziicijaa
                    validTargetPos.addAll(getFreeAdjPositionsMi(curLayout, i, caller));
                stoneInt2 = stoneInt2 >> 1;
            }
            isJumpMove = false;
        }
        //   Log.d("sur", "totalMovesMi :  " + validTargetPos.size());

        return validTargetPos;
    }


    //mi
    Vertice makeJumpOrNormalSimMove(Vertice from, List<Point> path, int turn) {

        Vertice start = new Vertice(from.c, from.h);
        for (Point p : path) {
            start = makeSimMove(p, turn, start);
        }
        return start;

    }

//Vertice makeJumpStep(Point noUz){

//}

    // mi only
    Vertice makeSimMove(Point noUz, int turn, Vertice fromVertice) {//izmaina occupancy masiivu atbilstoši gājiemnam
        int c = fromVertice.c;
        int h = fromVertice.h;
        int remove = getJumpOverPosition(noUz.x, noUz.y);//ja ir jāizpilda lēciens, tad noskaidro kuram akmenim tas būs pāri
        //Log.d("sur", "selectedStonePos: " + selectedStone.getPos() + " selPos: " + selectedPos + " removePos: " + remove);
        if (remove > 0) {
            c = c & (~(1 << (remove - 1)));
            h = h & (~(1 << (remove - 1)));

            //   occupancy[remove] = 0;
        }
        if (turn == 1) {
            c = c | (1 << (noUz.y - 1));
            c = c & (~(1 << (noUz.x - 1)));
        } else {
            h = h | (1 << (noUz.y - 1));
            h = h & (~(1 << (noUz.x - 1)));
        }

        // occupancy[noUz.y] = turn;//atzīmē jauno vietu kā aizņemtu --
        //occupancy[noUz.x] = 0;// veco kā brīvu
        Vertice res = new Vertice(c, h);
        return res;
    }


    // blakuspozīciju saraksts
    static int[][] blakusPos = new int[][]{
            new int[]{0, 0, 2, 7, 6, 0, 0, 0},
            new int[]{0, 0, 3, 0, 7, 0, 1, 0},
            new int[]{0, 0, 4, 9, 8, 7, 2, 0},
            new int[]{0, 0, 5, 0, 9, 0, 3, 0},
            new int[]{0, 0, 0, 0, 10, 9, 4, 0},//5 rinda(pozīcija)
            new int[]{1, 0, 7, 0, 11, 0, 0, 0},
            new int[]{2, 3, 8, 13, 12, 11, 6, 1},
            new int[]{3, 0, 9, 0, 13, 0, 7, 0},
            new int[]{4, 5, 10, 15, 14, 13, 8, 3},
            new int[]{5, 0, 0, 0, 15, 0, 9, 0},//10
            new int[]{6, 7, 12, 17, 16, 0, 0, 0},
            new int[]{7, 0, 13, 0, 17, 0, 11, 0},
            new int[]{8, 9, 14, 19, 18, 17, 12, 7},
            new int[]{9, 0, 15, 0, 19, 0, 13, 0},
            new int[]{10, 0, 0, 0, 20, 19, 14, 9},//15
            new int[]{11, 0, 17, 0, 21, 0, 0, 0},
            new int[]{12, 13, 18, 23, 22, 21, 16, 11},
            new int[]{13, 0, 19, 0, 23, 0, 17, 0},
            new int[]{14, 15, 20, 25, 24, 23, 18, 13},
            new int[]{15, 0, 0, 0, 25, 0, 19, 0},//20
            new int[]{16, 17, 22, 0, 0, 0, 0, 0},
            new int[]{17, 0, 23, 0, 0, 0, 21, 0},
            new int[]{18, 19, 24, 0, 0, 0, 22, 17},
            new int[]{19, 0, 25, 0, 0, 0, 23, 0},
            new int[]{20, 0, 0, 0, 0, 0, 24, 19}};//25

    // otrā līmeņa saistīto pozīciju saraksts
    static int[][] ply2Pos = new int[][]{
            new int[]{0, 0, 3, 13, 11, 0, 0, 0,},
            new int[]{0, 0, 4, 0, 12, 0, 0, 0},
            new int[]{0, 0, 5, 15, 13, 11, 1, 0},
            new int[]{0, 0, 0, 0, 14, 0, 2, 0},
            new int[]{0, 0, 0, 0, 15, 13, 3, 0},//5
            new int[]{0, 0, 8, 0, 16, 0, 0, 0,},
            new int[]{0, 0, 9, 19, 17, 0, 0, 0},
            new int[]{0, 0, 10, 0, 18, 0, 6, 0},
            new int[]{0, 0, 0, 0, 19, 17, 7, 0},
            new int[]{0, 0, 0, 0, 20, 0, 8, 0},//10
            new int[]{1, 3, 13, 23, 21, 0, 0, 0},
            new int[]{2, 0, 14, 0, 22, 0, 0, 0},
            new int[]{3, 5, 15, 25, 23, 21, 11, 1},
            new int[]{4, 0, 0, 0, 24, 0, 12, 0},
            new int[]{5, 0, 0, 0, 25, 23, 13, 3},//15
            new int[]{6, 0, 18, 0, 0, 0, 0, 0},
            new int[]{7, 9, 19, 0, 0, 0, 0, 0},
            new int[]{8, 0, 20, 0, 0, 0, 16, 0},
            new int[]{9, 0, 0, 0, 0, 0, 17, 7},
            new int[]{10, 0, 0, 0, 0, 0, 18, 0},//20
            new int[]{11, 13, 23, 0, 0, 0, 0, 0},
            new int[]{12, 0, 24, 0, 0, 0, 0, 0},
            new int[]{13, 15, 25, 0, 0, 0, 21, 11},
            new int[]{14, 0, 0, 0, 0, 0, 22, 0},
            new int[]{15, 0, 0, 0, 0, 0, 23, 13}};//25
}