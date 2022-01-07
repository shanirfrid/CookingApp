package com.example.shanir.cookingappofshanir.classs;

/**
 * Created by Shanir on 21/04/2018.
 */

public class CheakTime {
    private int num;

    public CheakTime(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
    public int getsec()
    {
        return 0;
    }
    public int getmin()
    {
        return  num-(gethour()*60);
    }
    public int gethour()
    {
        return num/60;
    }


    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString()
    {

        int min=num;
        int hour=num/60;


        return hour+":"+min;
    }
}
