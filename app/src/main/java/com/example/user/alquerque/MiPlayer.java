package com.example.user.alquerque;

import android.graphics.Point;
import android.util.Log;

import java.util.List;

/**
 * Created by user on 2017.11.23..
 */

public class MiPlayer {
    GameBoard miBoard;
    int evaluatedVertices;

    MiPlayer() {
        miBoard = new GameBoard();

    }


    Vertice getBestNextVertice(Vertice fromVertice, int depth, int turn) { //rekursīvs mm algoritms
        evaluatedVertices++;
        Log.d("MiPlayer", "getNext, depth: " + depth+" vert "+evaluatedVertices);
        List<List<Point>> moves = miBoard.getAllPosibleMoves(turn, fromVertice);
        Vertice curBestVertice = null;
        for (int i = 0; i < moves.size(); i++) {
            List<Point> noUz = moves.get(i);

            Vertice v = miBoard.makeJumpOrNormalSimMove( fromVertice,noUz, turn);
          //  Log.d("MiPlayer", "simMove, nextVertice: " + v.getPositions());

            if (depth > 0) {

                Vertice to = getBestNextVertice(v, depth - 1, turn * -1);
                if (to != null)
                   v.value = to.value;

            }
            curBestVertice = bestVerticeOfTwo(curBestVertice, v, turn);

        }
        //vfromVertice.value=curBestVertice.value;
        //return fromVertice;
        return curBestVertice;

    }


    Vertice bestVerticeOfTwo(Vertice v1, Vertice v2, int turn) {
        if (v1 == null) {
            return v2;
        }
        if (turn == 1) {//max
            if (v1.value > v2.value)
                return v1;
            else
                return v2;
        } else {//min
            if (v1.value < v2.value)
                return v1;
            else
                return v2;
        }
    }

}
