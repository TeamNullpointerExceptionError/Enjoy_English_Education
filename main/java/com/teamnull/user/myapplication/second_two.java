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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

public class second_two extends AppCompatActivity {
    private Button homego, play,next;
    private ImageView[] select;

    private int select_ID, click_Num, index, random, end_length, cunt, quiz_index, seve_num;
    private int[] quiz_output, end_Quiz;

    private String table_Name;

    private TextToSpeech tts;

    private DBAccess dbAccess;
    private DB_Data[] db_data;
    private SQLiteDatabase sqlDB = null;

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("tap",1);
        startActivity(intent);
        finish();
    }                               //뒤로가기 이벤트 처리

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_two);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init();                             //초기화

        Start_DB();                         //open DB

        Start_Setting();                    //초기 셋팅

        EventHandler();                     //이벤트 처리기

    }

    private void Init(){
        homego      = (Button)findViewById(R.id.home);
        play        = (Button)findViewById(R.id.play);
        next        = (Button)findViewById(R.id.next);
        select      = new ImageView[4];

        select_ID   = R.id.select1;                       //id값 지정
        for(int i = 0;i < 4; i++)
            select[i] = (ImageView)findViewById(select_ID++);//id값 설정

        cunt            = 0;
        click_Num       = 0;                            //각 버튼의 번호
        index           = 0;                            //4개의 문제를 랜덤으로 넣어주기 위한 index
        end_length      = 10;                           //퀴즈의 갯수
        seve_num        = 0;                            //인덱스값 저장

        table_Name      = "word";                       //table 이름

        quiz_output     = new int[4];                   //문제당 선택지
        end_Quiz        = new int[end_length*4];        //문제의 갯수

        dbAccess        = new DBAccess(this);   //디비 생성

        //TTS초기화
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR){
                    tts.setSpeechRate(0.5f);                //언어 속도
                    tts.setLanguage(Locale.US);            //언어 선택(영국)
                }
            }
        });
    }               //초기화

    private void Start_DB(){
        sqlDB = dbAccess.getReadableDatabase();

        dbAccess.openDataBase();
        db_data = dbAccess.getData(table_Name);
    }               //DB Open

    private void Start_Setting(){
        //랜덤으로 60개의 난수 발생
        for(int i = 0; i < end_Quiz.length;i++){
            random = Random(db_data.length);
            quiz_index      = 0;
            while(i > quiz_index){
                if(end_Quiz[quiz_index] == random){
                    random = Random(db_data.length);
                    quiz_index  = 0;
                    continue;
                }
                quiz_index++;
            }
            end_Quiz[i]     = random;

        }
        index = 0;

        Quiz_Output();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR){
                    tts.setSpeechRate(0.7f);                //언어 속도
                    tts.setLanguage(Locale.US);            //언어 선택(미국)
                }
                TTS();
            }
        });
    }

    private void Quiz_Output(){

        for(int i = 0;i < select.length;i++){
            select[i].setImageBitmap(Byte_To_Bitmap(db_data[end_Quiz[index+i]].Get_Bitmap()));
        }
        //index -= 2;
        random = Random(4);
        seve_num = random + index;
        TTS();
        index += 4;
    }

    private int Random(int length){
        return (int)(Math.random()*length);
    }


    private void EventHandler(){
        homego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //홈키 버튼이벤트: 메인UI로 이동

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tap",1);
                startActivity(intent);
                finish();

            }
        });     //홈키 눌렀을때의 이벤트 발생

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index+3 < end_Quiz.length)
                    Quiz_Output();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS();
            }
        });     //play 버튼을 눌렀을때의 이벤트 발생


        //이미지 클릭시 이벤트 처리기
        select[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_Num = 0;
                Answer(click_Num,random);

            }
        });

        select[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_Num = 1;
                Answer(click_Num,random);

            }
        });

        select[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_Num = 2;
                Answer(click_Num,random);

            }
        });

        select[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_Num = 3;
                Answer(click_Num,random);

            }
        });
    }       //이벤트 처리기

    private void Answer(int click_Num, int random){
        if(click_Num == random && index+3 < end_Quiz.length){
            Toast.makeText(getApplicationContext(),"정답입니다!", Toast.LENGTH_LONG).show();
            Quiz_Output();

        }else{
            Toast.makeText(getApplicationContext(),"틀렸습니다.다시 클릭해주세요!", Toast.LENGTH_LONG).show();
        }
    }

    private void TTS(){
        try {
            Thread.sleep(10);               //초기 음성 0.01초 딜레이
        }catch (Exception e){ }

        tts.setSpeechRate(0.7f);                //언어 속도
        tts.speak(db_data[end_Quiz[seve_num]].Get_Text(), QUEUE_FLUSH,null);
    }                   //TTS(Text to Speech)

    private Bitmap Byte_To_Bitmap(byte[] byteBit){
        return BitmapFactory.decodeByteArray(byteBit,0,byteBit.length);
    }   //byte -> Bitmap

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TTS_End();

    }

    public void finish() {
        TTS_End();
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TTS();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TTS_End();

    }

    private void TTS_End(){
        if(tts.isSpeaking())    tts.stop();
        if(tts != null)         tts.shutdown();
    }
}