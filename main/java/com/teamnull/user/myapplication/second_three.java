package com.teamnull.user.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class second_three extends AppCompatActivity {
    private Handler handler = new Handler();
    private Button homego, next, play;
    private ImageView leftView;
    private EditText input_Text;

    private DBAccess dbAccess;
    SQLiteDatabase sqlDB = null;

    private String table_Name;

    private int random;
    private int count;

    private boolean[] flag;

    private TextToSpeech tts;

    private DB_Data[] db_data;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("tap",1);
        startActivity(intent);
        finish();
    }                               //뒤로가기 이벤트 처리

    @Override
    protected void onDestroy() {
        TTS_End();
        Byte_To_Bitmap(db_data[random].Get_Bitmap()).recycle();
        super.onDestroy();
    }

    @Override
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_three);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dbAccess = new DBAccess(this);
        sqlDB = dbAccess.getReadableDatabase();



        Start_DB();

        init();

        Start_Setting();

        EventHandler();
    }

    private void init(){
        homego = (Button)findViewById(R.id.home);
        next = (Button)findViewById(R.id.next);
        play = (Button)findViewById(R.id.play);
        input_Text = (EditText)findViewById(R.id.editText);
        leftView = (ImageView)findViewById(R.id.img);

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

        count = 0;
        flag = new boolean[db_data.length];
        random = Random();

    }               //초기회

    private void Start_DB(){

        table_Name = "word";
        dbAccess.openDataBase();
        db_data = dbAccess.getData(table_Name);

    }               //DB Open

    private void Start_Setting(){
        leftView.setImageBitmap(Byte_To_Bitmap(db_data[random].Get_Bitmap()));
        flag[random] = true;

    }               //초기 셋팅

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

        input_Text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){

                    if(QuizTest(input_Text.getText().toString(),db_data[random].Get_Text())){
                        Toast.makeText(getApplicationContext(),"정답입니다!", Toast.LENGTH_LONG).show();

                        ScreenUpdate();                                 //화면 업데이트
                    }else{
                        Toast.makeText(getApplicationContext(),"틀렸습니다.다시 입력해주세요!", Toast.LENGTH_LONG).show();
                        input_Text.setText(null);
                    }
                    return true;
                }
                return false;
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenUpdate();                                 //화면 업데이트
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input_Text.getWindowToken(), 0);
                }catch (Exception e){}
            }
        });
    }               //이벤트처리기

    private boolean QuizTest(String answer, String input){
        return (answer.equals(input))? true: false;
    }   //문제 정답인지 아닌지 확인

    private Bitmap Byte_To_Bitmap(byte[] byteBit){
        return BitmapFactory.decodeByteArray(byteBit,0,byteBit.length);
    }   //byte -> Bitmap

    private int Random(){
        return (int)(Math.random()*db_data.length);
    }       //랜덤 돌리기

    private void ScreenUpdate(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                handler.post(new Runnable() {         //run함수를 이용하기 해
                    @Override
                    public void run() {

                        Next_Quiz();
                        input_Text.setText(null);        //editText초기화
                        //quizText.setText(Quiz_Word(answer));      //다음 문제 출제
                    }
                });

            }
        });

        thread.start();
    }                              //다음 이미지 넘어가는 루틴

    private void Next_Quiz(){
        if (count < db_data.length-1) {
            while (true) {
                random = Random();
                if (!flag[random]) {
                    leftView.setImageBitmap(Byte_To_Bitmap(db_data[random].Get_Bitmap()));
                    count++;
                    TTS();

                    flag[random] = true;

                    break;
                }
            }
        }else {
            Toast.makeText(getApplicationContext()," 퀴즈가 끝났습니다",Toast.LENGTH_LONG).show();
        }
    }

    private void TTS(){
        try {
            Thread.sleep(10);               //초기 음성 0.01초 딜레이
        }catch (Exception e){ }

        tts.setSpeechRate(0.7f);                //언어 속도
        tts.speak(db_data[random].Get_Text(),TextToSpeech.QUEUE_FLUSH,null);

    }                                       //TTS(Text to Speech)

    private void TTS_End(){
        if(tts.isSpeaking())    tts.stop();
        if(tts != null)         tts.shutdown();
    }
}