package com.teamnull.user.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by USER on 2018-05-10.
 */

class DBAccess extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "englishdata.db"; //DB파일명
    private static final String PACKAGE_DIR = "/data/data/com.teamnull.user.myapplication/databases"; //DB파일을 휴대폰 sdcard에 저장할 경로 지정

    private int index = 0;

    private String str[];

    private byte[][] byteArray, byteArray2, byteArray3, byteArray4; //바이트 배열. -> 이미지파일을 blob데이터로 받아올 배열


    private  SQLiteDatabase myDataBase;

    private DB_Data db_data[];


    public DBAccess(Context context) {
        super(context, PACKAGE_DIR + "/" + DATABASE_NAME, null, 4); //경로 , 파일명, 가져올 항목의 수
        initialize(context);
    }

    private void initialize(Context ctx) {
        File folder = new File(PACKAGE_DIR); //지정한 경로의 파일을 생성
        folder.mkdirs();

        File outfile = new File(PACKAGE_DIR + "/" + DATABASE_NAME);

        if (outfile.length() <= 0) {
            AssetManager assetManager = ctx.getResources().getAssets();
            try {
                InputStream is = assetManager.open(DATABASE_NAME, AssetManager.ACCESS_BUFFER);
                long filesize = is.available();
                byte[] tempdata = new byte[(int) filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                FileOutputStream fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void openDataBase(){
        String myPath = PACKAGE_DIR + "/"+ DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }


    public DB_Data[] getData(String table_Name){
        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery("select * from "+table_Name,null);

        index = 0;
        str = new String[cursor.getCount()];
        byteArray = new byte[cursor.getCount()][];
        db_data = new DB_Data[cursor.getCount()];
        try{
            while (cursor.moveToNext()) {

                str[index] = cursor.getString(0);
                byteArray[index] = cursor.getBlob(1);

                if(table_Name == "spelling") {
                    byteArray2 = new byte[cursor.getCount()][];
                    byteArray3 = new byte[cursor.getCount()][];
                    byteArray4 = new byte[cursor.getCount()][];

                    byteArray2[index] = cursor.getBlob(2);
                    byteArray3[index] = cursor.getBlob(3);
                    byteArray4[index] = cursor.getBlob(4);

                    db_data[index] = new DB_Data(cursor.getString(0),
                            byteArray[index],
                            byteArray2[index],
                            byteArray3[index],
                            byteArray4[index]);

                }else if(table_Name == "word"){
                    db_data[index] = new DB_Data(cursor.getString(0),
                            byteArray[index]);
                }
                index++;
            }
        }catch (SQLiteException e){
            getData(table_Name);
        }
        return db_data;
    }

    public byte[] Get_Video(int index){
        Cursor cursor = myDataBase.rawQuery("select * from video",null);
        cursor.moveToPosition(index);
        return cursor.getBlob(0);
    }

//    public DB_Data[] Get_Video(int index){
//        Cursor cursor = myDataBase.rawQuery("select * from video",null);
//        index = 0;
//        //cursor.move(0);
//        db_data = new DB_Data[cursor.getCount()];
//        while (cursor.moveToNext()){
//            db_data[index] = new DB_Data(cursor.getBlob(0));
//            index++;
//        }
//        return db_data;
//    }
}