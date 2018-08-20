package com.teamnull.user.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button one,two,three;
    private Button second_one,second_two,second_three;
    private Button third_one,third_two,third_three;

    private TabHost tabHost;

    private Intent intent;

    private int tap_Index = 0;

    private MediaPlayer background;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        init();

        Start_Setting();

        Event_Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(background != null) background.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(background != null){
            background.pause();
            if(isFinishing()){
                background.stop();
                background.release();
            }
        }
    }

    private void init(){
        tabHost = (TabHost)findViewById(R.id.tab_host);

        intent = getIntent();

        one = (Button)findViewById(R.id.one);
        two = (Button)findViewById(R.id.two);
        three = (Button)findViewById(R.id.three);

        second_one = (Button)findViewById(R.id.second_one);
        second_two = (Button)findViewById(R.id.second_two);
        second_three = (Button)findViewById(R.id.second_three);

        third_one = (Button)findViewById(R.id.third_one);
        third_two = (Button)findViewById(R.id.third_two);

        background = new MediaPlayer();
    }

    private void Start_Setting(){
        tabHost.setup();

        tap_Index = intent.getExtras().getInt("tap");

        Tap_Init();

        Tap_Setting();

        Background_Sound();
    }

    private void Background_Sound(){
        try{
            AssetFileDescriptor descriptor = getAssets().openFd("background.mp3");
            background.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            background.prepare();;
            background.setLooping(true);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error: "+ e.toString(),Toast.LENGTH_SHORT).show();
            background = null;
        }
    }

    private void Tap_Init(){
        // Tab1 Setting
        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("Tab1");
        tabSpec1.setIndicator("Phonics"); // Tab Subject
        tabSpec1.setContent(R.id.tab_view1); // Tab Content
        tabHost.addTab(tabSpec1);



        // Tab2 Setting
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("Tab2");
        tabSpec2.setIndicator("Word"); // Tab Subject
        tabSpec2.setContent(R.id.tab_view2); // Tab Content
        tabHost.addTab(tabSpec2);



        // Tab3 Setting
        TabHost.TabSpec tabSpec3 = tabHost.newTabSpec("Tab3");
        tabSpec3.setIndicator("Game"); // Tab Subject
        tabSpec3.setContent(R.id.tab_view3); // Tab Content
        tabHost.addTab(tabSpec3);
    }

    private void Tap_Setting(){
        for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setBackground(getDrawable(R.drawable.round2));
        }

        // show First Tab Content: 메인화면 디폴트 탭 설정
        tabHost.setCurrentTab(tap_Index);
        tabHost.getTabWidget().getChildAt(tap_Index).setBackground(getDrawable(R.drawable.round3));


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) { //탭 버튼 누른 후 색상 변환
                for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    tabHost.getTabWidget().getChildAt(i).setBackground(getDrawable(R.drawable.round2));
                }

                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackground(getDrawable(R.drawable.round3));
            }
        });
    }

    private void Event_Handler(){
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), one.class);
                startActivity(intent);
                finish();
            }
        });


        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), two.class);
                startActivity(intent);
                finish();
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), three.class);
                startActivity(intent);
                isFinishing();
            }
        });


        second_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), second_one.class);
                startActivity(intent);
                finish();
            }
        });


        second_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), second_three.class);
                startActivity(intent);
                finish();
            }
        });


        second_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), second_two.class);
                startActivity(intent);
                finish();
            }
        });


        third_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), third_one.class);
                startActivity(intent);
                finish();
            }
        });


        third_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), third_two.class);
                startActivity(intent);
                finish();
            }
        });
    }
}