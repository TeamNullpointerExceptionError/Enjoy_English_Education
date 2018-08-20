package com.teamnull.user.myapplication;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URL;

public class DB_Data {
    private String text;

    private byte video[];
    private byte image[];
    private byte image2[];
    private byte image3[];
    private byte image4[];

    public DB_Data(String text, byte image[], byte image2[], byte image3[], byte image4[]){
        this.text = text;
        this.image = image;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
    }

    public DB_Data(String text, byte image[]){
        this.text = text;
        this.image = image;
    }

    public String Get_Text(){
        return text;
    }

    public byte[] Get_Bitmap(){
        return  image;
    }

    public byte[] Get_Bitmap2(){ return image2;}

    public byte[] Get_Bitmap3(){return image3;}

    public byte[] Get_Bitmap4(){return image4;}

}