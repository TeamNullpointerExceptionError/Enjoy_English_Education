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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class three extends AppCompatActivity {

    private Handler handler = new Handler();
    private DBAccess dbAccess;
    private Button homego, next, play;
    private EditText editText;                          //답을 적을 edit text
    private TextView textView;
    private TextView quizText;                          //Quiz 문제에 이용할 TextView
    private ImageView display, quiz_Image;


    private int count=0;
    private int random;
    private int result = 100;
    private int randomCnt= 0;

    private String str[] = new String[26];
    private String answer = null;
    private String table_Name;
    private String table_Quiz;
    private String speak;

    private ArrayList<String> word_Array ;

    private boolean flag[] = new boolean[str.length];

    private DB_Data db_data[];
    private DB_Data db_word[];

    private TextToSpeech tts;

    private Thread thread1, thread2;

    private Bitmap bit;

    @Override
    public void onBackPressed() { //뒤로가기 버튼
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("tap",0);
        startActivity(intent);
        finish();
    }                               //뒤로가기 이벤트 처리

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dbAccess = new DBAccess(this);
        //sqlDB = dbAccess.getReadableDatabase();

        Init();                                                     //초기회

        Thread_Scheduling();                                        //스레드 스케쥴링
    }

    private void Init(){
        next = (Button)findViewById(R.id.next);

        editText = (EditText)findViewById(R.id.phonicEditText);

        quizText = (TextView)findViewById(R.id.phonicquiz);
        textView = (TextView)findViewById(R.id.tv);

        display = (ImageView)findViewById((R.id.img));
        quiz_Image = (ImageView)findViewById(R.id.wordimg);


        homego = (Button)findViewById(R.id.home);
        next = (Button)findViewById(R.id.next);
        play = (Button)findViewById(R.id.play);

        table_Name = "spelling";
        table_Quiz = "word";
    }                                       //초기화

    public void Start_DB(){

        dbAccess.openDataBase();

        db_data = dbAccess.getData(table_Name);
        db_word = dbAccess.getData(table_Quiz);
        //dbAccess.DB_Close();

        for(int i=0;i<db_data.length;i++){
            str[i] = db_data[i].Get_Text();
        }
    }                                   //DB Open

    private void Start_Setting(){

        for(int i = 0; i < str.length;i++)  flag[i] = false;

        random=(int)(Math.random()*25);
        textView.setText(str[random]);
        display.setImageBitmap(Byte_To_Bitmap(db_data[random].Get_Bitmap4()));

        answer = str[random].split("/ ")[1];


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status != ERROR){
                    tts.setLanguage(Locale.UK);            //언어 선택
                }
                TTS();
            }
        });
        quizText.setText(Quiz_Word(answer));
        quiz_Image.setImageBitmap(Byte_To_Bitmap(db_word[randomCnt].Get_Bitmap()));

        flag[random]=true;
    }                               //초기 셋팅

    private void EventHandler(){
        editText.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){

                    if(QuizTest(answer, editText.getText().toString())){
                        Toast.makeText(getApplicationContext(),"정답입니다!", Toast.LENGTH_LONG).show();

                        ScreenUpdate();                                 //화면 업데이트
                    }else{
                        Toast.makeText(getApplicationContext(),"틀렸습니다.다시 입력해주세요!", Toast.LENGTH_LONG).show();
                        editText.setText(null);
                    }
                    return true;
                }

                return false;
            }
        });


        homego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //홈키 버튼이벤트: 메인UI로 이동

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tap",0);
                startActivity(intent);
                finish();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //다음버튼이벤트: 다음 문제 넘어감
                editText.setText(null);

                Next_Quiz();                                    //다음 퀴즈로 넘어가는 루틴
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }catch (Exception e){}
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS();
            }
        });
    }                               //이벤트 처리기

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

    private void Next_Quiz(){
        if (count < 25) {
            while (true) {
                random = (int) (Math.random() * 26);
                if (!flag[random]) {
                    textView.setText(str[random]);
                    display.setImageBitmap(Byte_To_Bitmap(db_data[random].Get_Bitmap4()));

                    answer = str[random].split("/ ")[1];
                    quizText.setText(Quiz_Word(answer));
                    quiz_Image.setImageBitmap(Byte_To_Bitmap(db_word[randomCnt].Get_Bitmap()));
                    count++;
                    TTS();

                    flag[random] = true;

                    break;
                }
            }
        }if(count ==25 ) {
            next.setVisibility(textView.INVISIBLE);
            Toast.makeText(getApplicationContext()," 퀴즈가 끝났습니다",Toast.LENGTH_LONG).show();
        }
    }                                 //다음 퀴즈 넘어가는 루틴

    private void ScreenUpdate(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    tts.speak(speak,TextToSpeech.QUEUE_FLUSH,null);
                    Thread.sleep(1000);         //1초간 대기
                }catch (Exception e){}

                handler.post(new Runnable() {         //run함수를 이용하기 해
                    @Override
                    public void run() {

                        Next_Quiz();
                        editText.setText(null);        //editText초기화
                        //quizText.setText(Quiz_Word(answer));      //다음 문제 출제
                    }
                });

            }
        });

        thread.start();
    }                              //다음 이미지 넘어가는 루틴

    private boolean QuizTest(String answer, String input){
        return (answer.equals(input))? true: false;
    }   //문제 정답인지 아닌지 확인

    private Bitmap Byte_To_Bitmap(byte[] byteBit){
        return BitmapFactory.decodeByteArray(byteBit,0,byteBit.length);
    }   //byte -> Bitmap

    private String Quiz_Word(String spelling){
        int count = 0;
        int cnt_Random;

        word_Array = new ArrayList<String>();
        for(int i = 0; i < db_word.length; i++){
            if(QuizTest(spelling,db_word[i].Get_Text().substring(0,1))) {
                word_Array.add(db_word[i].Get_Text());
                randomCnt = i;
                count++;
            }
            // return db_word[i].Get_Text().substring(1);
        }
        randomCnt -= count;
        cnt_Random = (int) (Math.random() * count);
        speak = word_Array.get(cnt_Random);
        randomCnt += cnt_Random+1;
        return word_Array.get(cnt_Random).substring(1);
    }               //퀴즈 문제

    private void TTS(){
        try {
            Thread.sleep(10);               //초기 음성 0.01초 딜레이
        }catch (Exception e){ }

        if(random == 0)  tts.speak("ei",TextToSpeech.QUEUE_FLUSH,null);  //읽기
        else        tts.speak(str[random].split("/")[0],TextToSpeech.QUEUE_FLUSH,null);

    }                                       //TTS(Text to Speech)
    private void TTS_End(){
        if(tts.isSpeaking())    tts.stop();
        if(tts != null)         tts.shutdown();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Byte_To_Bitmap(db_data[random].Get_Bitmap()).recycle();
        TTS_End();
    }
    public void finish() {
        super.finish();
        TTS_End();
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
}