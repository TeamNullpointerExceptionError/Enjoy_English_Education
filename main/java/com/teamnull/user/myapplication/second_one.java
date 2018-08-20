package com.teamnull.user.myapplication;


import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;


public class second_one extends AppCompatActivity {

    private Button homego, next, play, previous;
    private DBAccess dbAccess;
    SQLiteDatabase sqlDB = null;
    private TextView textView;

    private ImageView display;
    private ImageView display_right;

    private int i=0;

    private String str[] = new String[60];
    private String table_Name;

    private Bitmap bitmap;

    private DB_Data db_data[];

    private TextToSpeech tts;       //tts 변수 선언

    private Thread thread1,thread2;

    @Override
    public void onBackPressed() { //뒤로가기 버튼
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("tap",1);
        startActivity(intent);
        finish();
    }           //뒤로가기 버튼 이벤트 처리

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_one);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dbAccess = new DBAccess(second_one.this);
        sqlDB = dbAccess.getReadableDatabase();

        Init();                                 //초기화

        Thread_Scheduling();                    //Thread Scheduling


    }

    private void Thread_Scheduling(){
        thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Start_DB();                                                 //DB Open

            }
        });

        thread2 = new Thread(new Runnable() {
            @Override
            public void run() {

                Start_Setting();                                            //초기 셋팅
                EventHandler();
            }
        });

        thread1.setPriority(1);
        thread2.setPriority(2);

        try {
            thread1.start();
            thread1.join();
            thread2.start();
            thread2.join();
        }catch (InterruptedException e){}
    }


    private void Start_Setting(){
        textView.setText(str[i]);

        bitmap = BitmapFactory.decodeByteArray(db_data[i].Get_Bitmap(), 0, db_data[i].Get_Bitmap().length);
        display.setImageBitmap(bitmap);


        //TTS초기화
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR){
                    tts.setSpeechRate(0.7f);                //언어 속도
                    tts.setLanguage(Locale.US);            //언어 선택(영국)
                }
                TTS();
            }
        });
    }           //초기 셋팅

    private void Init(){
        next = (Button)findViewById(R.id.next);
        previous = (Button)findViewById(R.id.previous);
        homego = (Button)findViewById(R.id.home);
        textView = (TextView)findViewById(R.id.tv);
        display = (ImageView)findViewById((R.id.img));
        display_right = (ImageView)findViewById(R.id.img2);
        play = (Button)findViewById(R.id.play);
        previous.setVisibility(View.INVISIBLE);
        table_Name = "word";
    }                   //초기화

    private void Start_DB(){


        dbAccess.openDataBase();
        db_data = dbAccess.getData(table_Name);
        //dbAccess.DB_Close();

        for(int i=0;i<str.length;i++){
            str[i] = db_data[i].Get_Text();
        }
    }               //DB Open

    private void EventHandler(){
        homego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //홈키 버튼이벤트: 메인UI로 이동

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tap",1);
                startActivity(intent);
                finish();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //홈키 버튼이벤트: 메인UI로 이동
                previous.setVisibility(View.VISIBLE);
                if(i<59) {
                    i++;
                    TTS();

                    textView.setText(str[i]);

                    bitmap = Byte_To_Bitmap(db_data[i].Get_Bitmap());
                    display.setImageBitmap(bitmap);

                }if(i==59) {
                    Toast.makeText(getApplicationContext(), "마지막 단어입니다.\n홈버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                    next.setVisibility(View.INVISIBLE);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //홈키 버튼이벤트: 메인UI로 이동
                next.setVisibility(View.VISIBLE);
                if (i > 0) {
                    i--;
                    TTS();

                    textView.setText(str[i]);

                    bitmap = Byte_To_Bitmap(db_data[i].Get_Bitmap());
                    display.setImageBitmap(bitmap);

                } if(i==0) {
                    Toast.makeText(getApplicationContext(), "첫번째 단어입니다.\n다음버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                    previous.setVisibility(View.INVISIBLE);
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS();
            }
        });
    }           //Event 처리기

    private void TTS(){
        try {
            Thread.sleep(10);               //초기 음성 0.01초 딜레이
        }catch (Exception e){ }
        //ei
        tts.setSpeechRate(0.7f);                //언어 속도
        tts.speak(db_data[i].Get_Text(),TextToSpeech.QUEUE_FLUSH,null);

    }                   //TTS(Text to Speech)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Byte_To_Bitmap(db_data[i].Get_Bitmap()).recycle();
        TTS_End();
        bitmap.recycle();
    }

    public void finish() {
        TTS_End();
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TTS_End();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TTS();
    }

    private void TTS_End(){
        if(tts.isSpeaking())    tts.stop();
        if(tts != null)         tts.shutdown();
    }


    private Bitmap Byte_To_Bitmap(byte[] byteBit){
        return BitmapFactory.decodeByteArray(byteBit,0,byteBit.length);
    }   //byte -> Bitmap
}