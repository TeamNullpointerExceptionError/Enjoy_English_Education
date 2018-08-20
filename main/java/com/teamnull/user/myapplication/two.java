package com.teamnull.user.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class two extends AppCompatActivity {

    private Button homego, next, play, previous;
    private VideoView videoView;
    private LinearLayout surface;


    private int count = 0;
    private ImageView img;
    private TwoDrowing twoDrowing;

    private TextToSpeech tts;

    private File video = null;

    private DBAccess dbAccess;
    private DB_Data[] db_data ;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);  // Intent 선언
        intent.putExtra("tap",0);
        startActivity(intent);   // Intent 시작
        finish();
    }       //뒤로가기 버튼 이벤트 처리

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init();                             //초기화

        Start_DB();

        Video_Play();

        Drow_Surface();

        EventHandler();                     //각종 Event처리기

    }

    private void Init(){
        homego = (Button)findViewById(R.id.home);
        next = (Button)findViewById(R.id.next);
        previous = (Button)findViewById(R.id.previous);
        previous.setVisibility(View.INVISIBLE);
        play = (Button)findViewById(R.id.play);
        img = (ImageView)findViewById(R.id.img);
        videoView = (VideoView)findViewById(R.id.video);



        dbAccess = new DBAccess(this);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status != ERROR){
                    tts.setLanguage(Locale.UK);            //언어 선택
                }
                TTS();
            }
        });

    }               //초기화

    private void Start_DB(){
        //sqlDB = dbAccess.getReadableDatabase();

        dbAccess.openDataBase();

        db_data = dbAccess.getData("spelling");


        //videoView.pause();

    }


    private void Video_Play(){
        videoView.setVideoPath(DB_To_Video().getPath());
        videoView.seekTo(0);
        videoView.start();
    }

    private File DB_To_Video(){
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(dbAccess.Get_Video(25-count)); //DB에 영상 실수로 역순으로 넣어서 이렇게 짬
            video = getApplicationContext().getDatabasePath("video.db");
            OutputStream output = new FileOutputStream(video);
            byte[] buffer = new byte[dbAccess.Get_Video(25-count).length];
            int len = 0;

            while ((len = input.read(buffer))>0){
                output.write(buffer,0,len);
            }
            output.close();
            input.close();


        }catch (IOException e){

        }

        return video;
    }

    private void Drow_Surface(){
        // suface를 LinearLayout안에서만 작동하게 지정
        //surfaceview를 실행
        surface = (LinearLayout)findViewById(R.id.DrowSurface);

        twoDrowing = new TwoDrowing(this);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);                                   //화면의 크기 받아오기
        twoDrowing.init(size.x,size.y,this,db_data);                                         //twoDrowing 초기화

        surface.addView(twoDrowing);

    }       //LinearLayout에 Surface(그림)실행

    private void EventHandler(){
        homego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //홈키 버튼이벤트: 메인UI로 이동

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tap",0);
                startActivity(intent);
                finish();

            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
                videoView.start();
            }               //동영상이 종료되었을때 다시 실행
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous.setVisibility(View.VISIBLE);
                if(count < 25) {
                    count++;
                    Video_Play();
                    twoDrowing.Put_index(count, true);
                    TTS();

                }if(count ==25 ) {
                    Toast.makeText(getApplicationContext(),"a부터 z까지 다 적어봤습니다.\n홈으로 이동해주세요.",Toast.LENGTH_LONG).show();
                    next.setVisibility(View.INVISIBLE);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setVisibility(View.VISIBLE);
                if(count > 0) {
                    count--;
                    Video_Play();
                    twoDrowing.Put_index(count, true);
                    TTS();

                }if(count ==0 ) {
                    Toast.makeText(getApplicationContext(),"첫번째 알파벳입니다.\n다음 알파벳을 그려보세요.",Toast.LENGTH_LONG).show();
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
    }       //각종 Event처리기

    private Bitmap Byte_To_Bitmap(byte[] byteBit){
        return BitmapFactory.decodeByteArray(byteBit,0,byteBit.length);
    }   //byte -> Bitmap


    private void TTS(){

        try {
            Thread.sleep(10);               //초기 음성 0.01초 딜레이
        }catch (Exception e){ }

        if(count == 0)  tts.speak("ei",TextToSpeech.QUEUE_FLUSH,null);  //읽기
        else        tts.speak(db_data[count].Get_Text().split("/")[0],TextToSpeech.QUEUE_FLUSH,null);

    }                //TTS(Text to Speech)

    private void TTS_End(){
        if(tts.isSpeaking())    tts.stop();
        if(tts != null)         tts.shutdown();
    }

    @Override
    protected void onDestroy() {
        TTS_End();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        TTS_End();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
        TTS();
    }

    @Override
    public void finish() {
        TTS_End();
        super.finish();
    }

}