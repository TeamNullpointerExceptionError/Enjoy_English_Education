package com.teamnull.user.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

public class TwoDrowing extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceThread mainThread;
    private two twoDrowing;
    private ImageView img2;

    private int index_Next = 0;

    private Paint paint;                                            //페인트 선언
    private Path path ;                                             //그려지는 궤적

    private float old_x;
    private float old_y;
    private float circle_X;
    private float circle_Y;
    private float per_x, per_y;

    private int canvasW,canvasH;

    private DB_Data[] getImage;

    private boolean drawing = false;
    private boolean drag = false;
    private boolean next = false;

    private Bitmap image_Bit;

    Context context;
    public TwoDrowing(Context context) {
        super(context);
        this.context = context;

        mainThread = new SurfaceThread(getHolder(),this);
        path = new Path();

        getHolder().addCallback(this);                              //surface 의 callback메서드 호출
        setFocusable(true);                                         //해당퓨 포커스

    }


    public void init(int w, int h, two twoDrowing, DB_Data[] getImage){
        this.twoDrowing = twoDrowing;
        this.getImage = getImage;
        drawing = true;                                             //drow를 할지 안할지 결정
    }

    private Bitmap Byte_To_Bitmap(byte[] byteBit){
        return BitmapFactory.decodeByteArray(byteBit,0,byteBit.length);
    }   //byte -> Bitmap

    public void Put_index(int index_Next, boolean next){
        this.index_Next = index_Next;
        this.next = next;
    }

    //그리는 메소드
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (drawing == false) return;

        paint = new Paint();

        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.rgb(255, 187, 0));       //선의 색을 rgb로 지정
        paint.setStrokeWidth(100);                                   //선의 두깨는 100으로 지정
        paint.setAntiAlias(true);                                   //부드러운 선으로 antialias
        paint.setStyle(Paint.Style.STROKE);                         //선의 타입을 stroke으로 지정
        canvas.drawPath(path, paint);                               //터치하는 궤도에 따라 그리기

        canvasH = canvas.getHeight();
        canvasW = canvas.getWidth();
        image_Bit = Bitmap.createScaledBitmap(                      //이미지 로드
                Byte_To_Bitmap(getImage[index_Next].Get_Bitmap3()),
                canvasW,canvasH,true);
        canvas.drawBitmap(image_Bit,0,0,paint);


        if (drag) {                                                 //누르고 있을때 실행
            paint.setColor(Color.argb(200, 80, 214, 255));
            canvas.drawCircle(circle_X, circle_Y, 20, paint);//커서용 원 만들기
        }

        if(next){
            path.reset();
            next = !next;
        }
    }

    @Override
    public SurfaceHolder getHolder() {
        return super.getHolder();
    }

    //터지에 대한 이벤트 처리
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int act = event.getActionMasked();                          //어떠한 터치 방식으로 받았는지 int 형으로 입력
        int x , y;

        x = (int)event.getX();                                      //터치한 좌표의 x값으로 초기회
        y = (int)event.getY();                                      //터치한 좌표의 y값으로 초기화


        switch (act){
            case MotionEvent.ACTION_DOWN:                           //터치를 누르고 있을때의 이벤트
                path.moveTo(x,y);                                   //경로 이동
                Persent(x,y);

                circle_X = old_x = x;                               //현재의 x값을 원의 중심점과 이전 좌표값에저장
                circle_Y = old_y = y;                               //현재의 y값을 원의 중심점과 이전 좌표값에저장

                drag = true;
                break;
            case MotionEvent.ACTION_MOVE:                           //터치를 누르고 이동하고 있을때의 이벤트

                float dx = Math.abs(x - old_x);                     //x의 현재의 좌표값과 이전의 좌표값을 계산후 절대값으로 변환
                float dy = Math.abs(y - old_y);                     //y의 현재의 좌표값과 이전의 좌표값을 계산후 절대값으로 변환

                if(dx >= 4 || dy >= 4){                             //이동한 값이 4픽섹로다 클경우에만 그리기


                    path.quadTo(old_x,old_y,x,y);                   //부드러운 선을 만들기

                    circle_X = old_x = x;                           //현재의 x값을 원의 중심점과 이전 좌표값에저장
                    circle_Y = old_y = y;                           //현재의 y값을 원의 중심점과 이전 좌표값에저장

                    drag = true;
                }

                invalidate();

                break;

            default:
                drag = false;
        }


        return true;
    }


    //surface가 생성이 되었을때 실행
    //Thread 실행
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mainThread.setRunning(true);
        try{
            if(mainThread.getState() == Thread.State.TERMINATED){
                mainThread = new SurfaceThread(getHolder(),this);
                mainThread.setRunning(true);
                setFocusable(true);
            }
            mainThread.start();
        }catch (Exception e) {
        }
    }


    //surface가 바뀔때 실행
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }


    //surface가 종료되었을 때 실행
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        mainThread.setRunning(false);
        while (retry){
            try{
                mainThread.join();
                retry = false;
            }catch (Exception e){

            }
        }
    }

    private void Persent(int x, int y){
        per_x = ((float)x/(float)canvasW)*100.0f;
        per_y = ((float)y/(float) canvasH)*100.0f;

    }
}