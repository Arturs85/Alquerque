package com.example.user.alquerque;

import android.graphics.Point;

/**
 * Created by user on 2017.11.23..
 */

public class Vertice {// stāvoklis spēlē
    int h;  //human stones positions
    int c;  //mi stones positions
    int  value =0;

    Vertice(int c,int h){
        this.c = c;
        this.h = h;
value = evaluate();
    }
   int evaluate(){

        return (countStones(c)-countStones(h));
    }

int countStones( int layout){
    byte counter =0;
    for (byte i =0;i<Integer.SIZE-6;i++){
        byte isStone = (byte)( layout&1);
        counter+=isStone;
        layout=layout>>1;
    }
return counter;
}

Point getMove(Vertice from, Vertice to){
  int xor = from.c^to.c;
    int fromBin = from.c&xor;
    int toBin = to.c&xor;
int fromInt=0;
    int toInt = 0;
    for (byte i =1;i<Integer.SIZE-6;i++) {

        if((fromBin&1)==1){
        fromInt =i;
        break;
        }
        fromBin=fromBin>>1;
    }
    for (byte i =1;i<Integer.SIZE-6;i++) {

        if((toBin&1)==1){
            toInt =i;
            break;
        }
        toBin=toBin>>1;
    }
return new Point(fromInt,toInt);
}
String getPositions(){
    String res="co : "+c;
    int co =c;
    int hu =h;

    for (byte i =1;i<Integer.SIZE-6;i++) {

        if((co&1)==1){
           res = res.concat("  "+i);
        }
    co = co>>1;
    }
   res = res.concat(" hum: ");
    for (byte i =1;i<Integer.SIZE-6;i++) {

        if((hu&1)==1){
          res =   res.concat("  "+i);
        }
    hu = hu>>1;
    }
return res;
}

}
