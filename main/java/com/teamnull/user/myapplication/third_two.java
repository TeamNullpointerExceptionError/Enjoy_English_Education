package com.teamnull.user.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import static java.lang.Math.*;

public class third_two extends AppCompatActivity implements View.OnClickListener {
    Button homego;
    //카드게임 변수
    private static final int TOTAL_CARD_NUM = 18;
    private int[] cardId = {R.id.card01, R.id.card02, R.id.card03, R.id.card04, R.id.card05, R.id.card06, R.id.card07,
            R.id.card08, R.id.card09,R.id.card10, R.id.card11, R.id.card12, R.id.card13, R.id.card14,
            R.id.card15, R.id.card16, R.id.card17, R.id.card18};
    // private int[] cardid2 = {R.id.card10, R.id.card11, R.id.card12, R.id.card13, R.id.card14,
    // R.id.card15, R.id.card16, R.id.card17, R.id.card18};

    private Card[] cardArray = new Card[TOTAL_CARD_NUM];

    private int CLICK_CNT = 0; // 클릭 카운트
    private Card first, second; // 첫번째 누른 버튼과 두번재 누른 버튼
    private int SUCCESS_CNT = 0; // 짝 맞추기 성공 카운트
    private boolean INPLAY = false; // 카드를 클릭할 수 있는지 여부

    //private Button hint;
    //
    // 데이터베이스 변수
    private String table_Name;
    private DBAccess dbAccess;
    private DB_Data db_data[];
    //

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("tap",2);
        startActivity(intent);
        finish();
    }                               //뒤로가기 이벤트 처리

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_two);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        table_Name = "word"; // 게임 테이블 명
        Start_DB();
        homego = (Button) findViewById(R.id.home);
        homego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //홈키 버튼이벤트: 메인UI로 이동

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tap", 2);
                startActivity(intent);
                finish();

            }
        });
        // 카드 생성부분
        for (int i = 0; i < TOTAL_CARD_NUM; i++) {
            cardArray[i] = new Card(i); // 카드 생성
            findViewById(cardId[i]).setOnClickListener(this); // 카드 클릭 리스너 설정
            cardArray[i].card = (ImageButton) findViewById(cardId[i]); // 카드 할당
            cardArray[i].onBack(); // 카드 뒤집어 놓음
        }
        startGame();

    }

    protected void endDIalog() {
        android.app.AlertDialog.Builder end = new android.app.AlertDialog.Builder(this);
        end.setMessage("모든 카드 짝을 맞추셨습니다. 축하합니다.")
                .setCancelable(false)
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("tap", 2);
                        startActivity(intent);
                        finish();
                    }
                });
        end.setCancelable(false)
                .setNeutralButton("다시시작", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startGame();
                    }
                });
        android.app.AlertDialog alt2 = end.create();
        alt2.setTitle("짝 맞추기 완료");
        alt2.show();
    }

    public void onClick(View v) {
        if (INPLAY) {
            switch (CLICK_CNT) {
                case 0: // 카드 하나만 뒤집었을 경우
                    for (int i = 0; i < TOTAL_CARD_NUM; i++) {
                        if (cardArray[i].card == (ImageButton) v) {
                            first = cardArray[i];
                            break;
                        }
                    }
                    if (first.isBack) { // 이미 뒤집힌 카드는 처리 안함
                        first.onFront();
                        CLICK_CNT = 1;
                    }
                    break;
                case 1: // 카드 두개 뒤집었을 경우
                    for (int i = 0; i < TOTAL_CARD_NUM; i++) {
                        if (cardArray[i].card == (ImageButton) v) {
                            second = cardArray[i];
                            break;
                        }
                    }
                    if (second.isBack) { // 뒷면이 보이는 카드일 경우만 처리
                        second.onFront();

                        if ( ((first.value+9) == (second.value)) || (((first.value) == (second.value+9))) ) { // 짝이 맞은 경우
                            SUCCESS_CNT++;
                            Toast.makeText(getApplicationContext(),"짝을 맞추셨습니다",Toast.LENGTH_SHORT).show();

                            if (SUCCESS_CNT == TOTAL_CARD_NUM / 2) { // 모든 카드의 짝을 다 맞추었을 경우
                                endDIalog();
                            }
                        } else { // 짝이 틀릴 경우
                            Timer t = new Timer(0);
                            Toast.makeText(getApplicationContext(),"틀렸습니다. 다시 선택해주세요.",Toast.LENGTH_SHORT).show();
                            t.start();

                        }
                        CLICK_CNT = 0;
                    }
                    break;
            }
        }
    }


    void startGame() { // 게임시작 메소드
        int[] random = new int[TOTAL_CARD_NUM]; // 카드 숫자만 랜덤 생성
        int x;

        for (int i = 0; i < TOTAL_CARD_NUM; i++) { // 모든 카드가 뒷면이 보이게함
            if (!cardArray[i].isBack)
                cardArray[i].onBack();
        }

        boolean check;
        for (int i = 0; i < TOTAL_CARD_NUM; i++) { // 0~17까지 랜덤한 순서로 random배열에 저장
            while (true) {
                check = false;
                x = (int) (Math.random() * TOTAL_CARD_NUM);
                for (int j = 0; j < i; j++) {
                    if (random[j] == x) {
                        check = true;
                        break;
                    }
                }
                if (!check) break;
            }
            random[i] = x;
        }
        for (int i = 0; i < TOTAL_CARD_NUM; i++) {
            cardArray[i].card = (ImageButton) findViewById(cardId[random[i]]); //이미지 버튼의 id를 넣는다.
            cardArray[i].onFront(); // 카드를 앞으로 돌린다.
        }
        Timer t = new Timer(1);
        t.start();
        SUCCESS_CNT = 0;
        CLICK_CNT = 0;
        INPLAY = true;
    }

    class Timer extends Thread {
        int kind;

        Timer (int kind) {
            super();
            this.kind = kind;
        }
        @Override
        public void run() {
            INPLAY = false;
            // TODO Auto-generated method stub
            try {
                if (kind == 0) {
                    Thread.sleep(1000);
                    mHandler.sendEmptyMessage(0);
                }
                else if (kind == 1) {
                    Thread.sleep(3000);
                    mHandler.sendEmptyMessage(1);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            INPLAY = true;
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                first.onBack();
                second.onBack();
                first.isBack = true;
                second.isBack = true;
            }
            else if (msg.what == 1) {
                for (int i=0; i<TOTAL_CARD_NUM; i++) {
                    cardArray[i].onBack();
                }
            }
        }
    };
    private void Start_DB(){
        dbAccess = new DBAccess(third_two.this);
        dbAccess.openDataBase();
        db_data = dbAccess.getData(table_Name);
    }//DB Open

    private Bitmap Byte_To_Bitmap(byte[] byteBit){
        return BitmapFactory.decodeByteArray(byteBit,0,byteBit.length);
    }   //byte -> Bitmap

    static class Card { // start of Card class
        private final static int backImageID = R.drawable.card_background;

        private final static int[] frontImageID = {R.drawable.card1,R.drawable.card2,R.drawable.card3,R.drawable.card4,R.drawable.card5,
                R.drawable.card6,R.drawable.card7,R.drawable.card8,R.drawable.card9,R.drawable.card10,
                R.drawable.card11,R.drawable.card12,R.drawable.card13,R.drawable.card14,R.drawable.card15,
                R.drawable.card16,R.drawable.card17,R.drawable.card18};

        int value;
        boolean isBack;
        ImageButton card;


        Card(int value) {
            this.value = value;
        }

        public void onBack() { // 카드 뒷면이 보이게 뒤집음
            if (!isBack) {
                card.setBackgroundResource(backImageID);
                isBack = true;
            }
        }

        public void flip() { // 카드를 뒤집음
            if (!isBack) {
                card.setBackgroundResource(backImageID);
                isBack = true;
            } else {
                card.setBackgroundResource(frontImageID[value]);
                isBack = false;
            }
        }

        public void onFront() { // 카드 그림면을 보여줌
            if (isBack) {
                card.setBackgroundResource(frontImageID[value]);
                isBack = false;
            }
        }


    }

}