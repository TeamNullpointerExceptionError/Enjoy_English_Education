package com.teamnull.user.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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

import java.util.Random;

public class third_one extends AppCompatActivity {
    //일반 변수
    Button homego;
    Random rnd = new Random();
    int game_number = rnd.nextInt(3);
    EditText editText;
    TextView[] textViews;
    Button[] hint = new Button[9];
    ImageView buttonimg;
    String input_save[];
    String[] answer;
    int count = 0;
    int id_value=0;
    //
    // 데이터베이스 변수
    private String table_Name;
    private DBAccess dbAccess;
    private DB_Data db_data[];
    //

    @Override
    public void onBackPressed() { //뒤로가기 버튼
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("tap",2);
        startActivity(intent);
        finish();
    }           //뒤로가기 버튼 이벤트 처리

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_one + game_number);
        editText = (EditText) findViewById(R.id.editText);
        buttonimg = (ImageView) findViewById(R.id.buttonimg);
        //db 오픈
        table_Name = "word"; // 게임 테이블 명
        Start_DB();
        //
        for (int i = 0; i < hint.length; i++) // 힌트 크기 만큼 반복하여
        {
            id_value = getResources().getIdentifier("hint" + (i + 1), "id", this.getPackageName()); // 해당 id값을 정수형으로 받은후
            hint[i] = (Button) findViewById(id_value); // 버튼으로 ID값을 보낸후
            hint[i].setOnClickListener(new View.OnClickListener() // 클릭 이벤트 처리
            {
                @Override
                public void onClick(View v)
                {
                    buttonimg.setImageResource(0); // 클릭 될때 기존 이미지를 지우고
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    if(game_number == 0) // 첫번째 게임일때
                    {
                        switch(v.getId()) {
                            case R.id.hint1: buttonimg.setImageBitmap(//배
                                    Byte_To_Bitmap(db_data[36].Get_Bitmap()));break; // 저장된 데이터 베이스의 이미지를 불러온다.
                            case R.id.hint2: buttonimg.setImageBitmap(//개구리
                                    Byte_To_Bitmap(db_data[14].Get_Bitmap()));break;
                            case R.id.hint3: buttonimg.setImageBitmap(//포도
                                    Byte_To_Bitmap(db_data[16].Get_Bitmap()));break;
                            case R.id.hint4: buttonimg.setImageBitmap(//레몬
                                    Byte_To_Bitmap(db_data[27].Get_Bitmap()));break;
                            case R.id.hint5: buttonimg.setImageBitmap(//낙타
                                    Byte_To_Bitmap(db_data[5].Get_Bitmap()));break;
                            case R.id.hint6: buttonimg.setImageBitmap(//고양이
                                    Byte_To_Bitmap(db_data[6].Get_Bitmap()));break;
                            case R.id.hint7: buttonimg.setImageBitmap(//박쥐
                                    Byte_To_Bitmap(db_data[2].Get_Bitmap()));break;
                            case R.id.hint8: buttonimg.setImageBitmap(//사과
                                    Byte_To_Bitmap(db_data[0].Get_Bitmap()));break;
                            case R.id.hint9: buttonimg.setImageBitmap(//오렌지
                                    Byte_To_Bitmap(db_data[34].Get_Bitmap()));break;
                        }
                    }
                    else if(game_number == 1) // 두번째 게임일때
                    {
                        switch(v.getId()) {
                            case R.id.hint1: buttonimg.setImageBitmap(//호두
                                    Byte_To_Bitmap(db_data[52].Get_Bitmap()));break; // 위와 동일
                            case R.id.hint2: buttonimg.setImageBitmap(//바나나
                                    Byte_To_Bitmap(db_data[4].Get_Bitmap()));break;
                            case R.id.hint3: buttonimg.setImageBitmap(//고릴라
                                    Byte_To_Bitmap(db_data[17].Get_Bitmap()));break;
                            case R.id.hint4: buttonimg.setImageBitmap(//소
                                    Byte_To_Bitmap(db_data[7].Get_Bitmap()));break;
                            case R.id.hint5: buttonimg.setImageBitmap(//새
                                    Byte_To_Bitmap(db_data[3].Get_Bitmap()));break;
                            case R.id.hint6: buttonimg.setImageBitmap(//자두
                                    Byte_To_Bitmap(db_data[37].Get_Bitmap()));break;
                            case R.id.hint7: buttonimg.setImageBitmap(//복숭아
                                    Byte_To_Bitmap(db_data[38].Get_Bitmap()));break;
                            case R.id.hint8: buttonimg.setImageBitmap(//키위
                                    Byte_To_Bitmap(db_data[24].Get_Bitmap()));break;
                            case R.id.hint9: buttonimg.setImageBitmap(//오리
                                    Byte_To_Bitmap(db_data[8].Get_Bitmap()));break;
                        }
                    }
                    else // 세번째 게임일때
                    {
                        switch(v.getId()) {
                            case R.id.hint1: buttonimg.setImageBitmap(//바이올린
                                    Byte_To_Bitmap(db_data[50].Get_Bitmap()));break; // 위와 동일
                            case R.id.hint2: buttonimg.setImageBitmap(//장난감
                                    Byte_To_Bitmap(db_data[45].Get_Bitmap()));break;
                            case R.id.hint3: buttonimg.setImageBitmap(//입
                                    Byte_To_Bitmap(db_data[29].Get_Bitmap()));break;
                            case R.id.hint4: buttonimg.setImageBitmap(//나무
                                    Byte_To_Bitmap(db_data[46].Get_Bitmap()));break;
                            case R.id.hint5: buttonimg.setImageBitmap(//달
                                    Byte_To_Bitmap(db_data[30].Get_Bitmap()));break;
                            case R.id.hint6: buttonimg.setImageBitmap(//원숭이
                                    Byte_To_Bitmap(db_data[31].Get_Bitmap()));break;
                            case R.id.hint7: buttonimg.setImageBitmap(//무지개
                                    Byte_To_Bitmap(db_data[40].Get_Bitmap()));break;
                            case R.id.hint8: buttonimg.setImageBitmap(//호랑이
                                    Byte_To_Bitmap(db_data[47].Get_Bitmap()));break;
                            case R.id.hint9: buttonimg.setImageBitmap(//목
                                    Byte_To_Bitmap(db_data[32].Get_Bitmap()));break;
                        }
                    }
                }
            });
        }
        //입력하는 부분처리
        if(game_number == 0) // 첫번째 게임
        {
            answer = new String[]{"p","e","a","r","f","o","g","r","a","p","e","l","e","m","o","n","c","m","l","c","a","t","b","a","t","p","p","l","r","n","g","e"};
            textViews = new TextView[32];
            input_save = new String[32];

        }
        else if(game_number == 1) // 두번째 게임
        {
            answer = new String[]{"w","a","l","n","u","t","b","n","a","n","a","g","o","r","i","l","l","a","c","w","b","i","r","d","p","l","m","e","a","c","h","k","w","u","k"};
            textViews = new TextView[35];
            input_save = new String[35];
        }
        else // 세번째 게임
        {
            answer = new String[]{"v","i","o","l","i","n","t","y","m","o","u","t","h","t","r","e","e","m","o","o","n","m","o","n","k","e","y","a","n","b","w","i","g","r","e","c"};
            textViews = new TextView[36];
            input_save = new String[36];
        }
        for(int i = 0; i < textViews.length; i++) // 위에 지정된 크기만큼 반복하여
        {
            id_value = getResources().getIdentifier("textView"+(i+1),"id",this.getPackageName()); // 해당 id값을 정수형으로 받은후
            textViews[i] = (TextView) findViewById(id_value); // 텍스트뷰로 보낸다
        }
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    input_save[count] = editText.getText().toString();
                    if(count == textViews.length)
                    {
                        Toast.makeText(getApplicationContext(),"퀴즈를 완성했습니다.",Toast.LENGTH_LONG).show();
                        editText.setEnabled(false);
                    }
                    if(input_save[count].equalsIgnoreCase(answer[count]) || input_save[count].equalsIgnoreCase(answer[count]+32)) // 각 자리에 입력된 값이 동일한 경우
                    {
                        textViews[count].setText(editText.getText()); // 해당 알파벳을 자리에 입력하고
                        count++; // 인덱스를 증가시킨다.
                        Toast.makeText(getApplicationContext(),"정답입니다!", Toast.LENGTH_SHORT).show();
                        editText.setText(null);
                    }
                    else // 틀린경우
                    {
                        if(count == 0) // 첫번째인 경우 인덱스 값을 0으로 고정
                        {
                            count=0;
                            Toast.makeText(getApplicationContext(),"틀렸습니다.다시 입력해주세요!", Toast.LENGTH_SHORT).show();
                            editText.setText(null);
                        }
                        else // 0이 아닌경우
                        {
                            count=count; // 값을 유지해서 자리를 고정
                            Toast.makeText(getApplicationContext(),"틀렸습니다.다시 입력해주세요!", Toast.LENGTH_SHORT).show();
                            editText.setText(null);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        homego = (Button)findViewById(R.id.home);
        homego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //홈키 버튼이벤트: 메인UI로 이동

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tap",2);
                startActivity(intent);
                finish();
            }
        });
    }
    private void Start_DB(){
        dbAccess = new DBAccess(third_one.this);
        dbAccess.openDataBase();
        db_data = dbAccess.getData(table_Name);
    }//DB Open

    private Bitmap Byte_To_Bitmap(byte[] byteBit){
        return BitmapFactory.decodeByteArray(byteBit,0,byteBit.length);
    }   //byte -> Bitmap
}