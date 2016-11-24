package com.example.sidhartha.drone5id;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    RelativeLayout layout_joystick;
    RelativeLayout layout_slider;
    slider sl;
    TextView textView1, textView2, textView3, textView4, textView5,thrustView,connection_text;
    JoyStickClass js;
    BluetoothAdapter mBtAdapter=BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice BTdev;
    BluetoothSocket BtSocket;
    OutputStream outputStream;
    int oldthrust=0,olddirection=0;
    boolean firsttime=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView)findViewById(R.id.textView6);
        textView2 = (TextView)findViewById(R.id.textView10);
        textView3 = (TextView)findViewById(R.id.textView11);
        textView4 = (TextView)findViewById(R.id.textView12);
        textView5 = (TextView)findViewById(R.id.textView13);
        connection_text=(TextView)findViewById(R.id.textView);
        thrustView=(TextView)findViewById(R.id.textView16);
        final Switch conn=(Switch)findViewById(R.id.switch1);
        final Switch gps=(Switch)findViewById(R.id.switch2);
        final Switch thrst=(Switch)findViewById(R.id.switch3);


        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);
        layout_slider = (RelativeLayout)findViewById(R.id.slider);

        sl = new slider(getApplicationContext()
                , layout_slider, R.drawable.thrustcontrol);
        sl.setStickSize(150, 150);
        sl.setIndicatorSize(270, 48);
        sl.setLayoutSize(500, 500);
        sl.setLayoutAlpha(150);
        sl.setStickAlpha(100);
        sl.setOffset(90);
        sl.setThrust(0);

        layout_slider.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (thrst.isChecked()) {
                    sl.drawStick(arg1);
                    if (arg1.getAction() == MotionEvent.ACTION_DOWN
                            || arg1.getAction() == MotionEvent.ACTION_MOVE) {

                    }
                    thrustView.setText((int)(sl.thrust * 100) + " % : THRUST");
                    if(oldthrust!=(int)(sl.thrust*100)) {
                        sendData((int) (sl.thrust * 100)+11);
                        oldthrust=(int)(sl.thrust*100);
                    }
                }
                return true;
            }
        });

        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (thrst.isChecked()) {
                    js.drawStick(arg1);
                    if (arg1.getAction() == MotionEvent.ACTION_DOWN
                            || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                        textView1.setText(String.valueOf(js.position_x));
                        textView2.setText(String.valueOf(js.position_y));
                        textView3.setText(String.valueOf((int) js.getAngle()));
                        textView4.setText(String.valueOf((int) js.getDistance()));

                        int direction = js.get8Direction();
                        if (direction == JoyStickClass.STICK_UP) {
                            textView5.setText("Up ");
                        } else if (direction == JoyStickClass.STICK_UPRIGHT) {
                            textView5.setText("Up Right ");
                        } else if (direction == JoyStickClass.STICK_RIGHT) {
                            textView5.setText("Right ");
                        } else if (direction == JoyStickClass.STICK_DOWNRIGHT) {
                            textView5.setText("Down Right ");
                        } else if (direction == JoyStickClass.STICK_DOWN) {
                            textView5.setText("Down ");
                        } else if (direction == JoyStickClass.STICK_DOWNLEFT) {
                            textView5.setText("Down Left ");
                        } else if (direction == JoyStickClass.STICK_LEFT) {
                            textView5.setText("Left ");
                        } else if (direction == JoyStickClass.STICK_UPLEFT) {
                            textView5.setText("Up Left ");
                        } else if (direction == JoyStickClass.STICK_NONE) {
                            textView5.setText("Center ");
                        }
                        if (direction != olddirection) {
                            sendData(direction+2);
                            olddirection = direction;
                        }
                    } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                        textView1.setText("");
                        textView2.setText("");
                        textView3.setText("");
                        textView4.setText("");
                        textView5.setText("");
                        sendData(2);
                    }
                }
                    return true;
                }
        });

        conn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    connection_text.setText("LOADING...");
                    if (mBtAdapter == null) {
                        Toast.makeText(getBaseContext(), "Bluetooth Not supported", Toast.LENGTH_SHORT);
                        conn.setChecked(false);
                        connection_text.setText("NOT SUPPORTED");
                    } else {
                        if (!mBtAdapter.isEnabled()) {
                            Intent enableIntent = new Intent(mBtAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, 1);
                        }
                        BTdev = mBtAdapter.getRemoteDevice("98:D3:34:90:83:DE");
                        try {
                            BtSocket = BTdev.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                            BtSocket.connect();
                            //try {
                            //    BtSocket = (BluetoothSocket) BTdev.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(BTdev, 1);
                            //    if(!BtSocket.isConnected())
                            //        BtSocket.connect();
                            //}
                            //catch(Exception e)
                            //{

                            //}
                            outputStream = BtSocket.getOutputStream();
                            connection_text.setText("CONNECTED");
                            firsttime=false;
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "OOPS! Somethin went wrong ;P;", Toast.LENGTH_SHORT);
                            connection_text.setText("NOT CONNECTED");
                            conn.setChecked(false);
                        }
                    }
                } else {
                    connection_text.setText("LOADING...");
                    try {
                        BtSocket.close();
                        connection_text.setText("NOT CONNECTED");
                    } catch (IOException e) {
                        conn.setChecked(false);
                        connection_text.setText("SOMETHIING's WRONG ;P");
                    }
                }
                oldthrust=0;
            }
        });

        thrst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(!firsttime)
                        sendData(11);
                    else
                        thrst.setChecked(false);
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                    sendData(11);
                    sl.setThrust(0);
                    thrustView.setText(sl.thrust * 100 + " % : THRUST");
                }
            }
        });

        gps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(!firsttime)
                    sendData(1);
                    else
                        gps.setChecked(false);
                }
                else
                {
                    sendData(0);
                }
            }
        });
}
    private void sendData(int x)
    {
        try
        {
                    outputStream.write((byte)x);
        }
        catch (IOException e)
        {
            if(outputStream==null)
                Toast.makeText(getBaseContext(), "OutputStream NULL", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getBaseContext(), "Sendata error : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}