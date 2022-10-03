package com.example.dig4634.accelerometerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DiveGame extends AppCompatActivity implements SensorEventListener, SurfaceHolder.Callback {

    Paint red_fill;
    Paint white_stroke;
    Paint white_text;
    Bitmap diver;
    Bitmap sun;

    int points = 0;
    String message = "Points: " + points;

    SurfaceHolder holder=null;

    Animator my_animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive);

        red_fill=new Paint();
        red_fill.setColor(Color.RED);
        red_fill.setStyle(Paint.Style.FILL);

        white_stroke=new Paint();
        white_stroke.setColor(Color.WHITE);
        white_stroke.setStyle(Paint.Style.STROKE);
        white_stroke.setStrokeWidth(10);

        white_text=new Paint();
        white_text.setColor(Color.WHITE);
        white_text.setTextSize(100);

        diver=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.diver),500,500,false);
        sun=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.planet2),200,200,false);


        SensorManager manager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer=manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer!=null){
            manager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL,SensorManager.SENSOR_DELAY_UI);
        }

        SurfaceView my_surface=findViewById(R.id.surfaceView);
        my_surface.getHolder().addCallback(this);


        my_animator=new Animator(this);
        my_animator.start();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        acc_x=sensorEvent.values[0];
        // acc_y=sensorEvent.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    float acc_x=0;
    float acc_y=0;
    int diver_x_position=310;
    int diver_y_position=1320;

    int sun_x_position=500;
    int sun_y_position=600;

    public void update(int width, int height){

        sun_y_position+=5;

        diver_x_position-=acc_x*2;
        diver_y_position+=acc_y*2;

        if(diver_x_position<0)diver_x_position=0;
        else if(diver_x_position>width-200)diver_x_position=width-200;

        if (Math.abs(diver_x_position-sun_x_position)<200 && Math.abs(diver_y_position-sun_y_position)<200){
            points++;
            message="Points: " + points;
            sun_x_position = 0;
            sun_y_position = 0;
        }

    }

    public void draw(){

        if(holder==null)return;




        Canvas c=holder.lockCanvas();

        update(c.getWidth(),c.getHeight());

        c.drawColor(Color.rgb(145,186,214));

        //c.drawRect(0,0, 200,200,red_fill);

        //c.drawCircle(c.getWidth()/2,c.getHeight()/2,100,white_stroke);



        c.drawBitmap(sun,sun_x_position,sun_y_position,null);

        c.drawBitmap(diver,diver_x_position,diver_y_position,null);

        c.drawText(message, 50, 100, white_text);

        holder.unlockCanvasAndPost(c);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d("Example","Surface is created");
        holder=surfaceHolder;

        draw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d("Example","Surface changed");
        holder=surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        holder=null;
    }

    @Override
    public void onDestroy(){

        my_animator.finish();
        SensorManager manager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        manager.unregisterListener(this,manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

        super.onDestroy();
    }
}
