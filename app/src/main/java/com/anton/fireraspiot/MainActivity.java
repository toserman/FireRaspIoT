package com.anton.fireraspiot;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

//import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLivingRoom;
    Intent lvroomIntent;
    private final String TAG = "MY";//;//MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLivingRoom = findViewById(R.id.activityLivingRoom);
        btnLivingRoom.setOnClickListener(this);
        lvroomIntent = new Intent(this, LivingRoomActivity.class);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activityLivingRoom:
                Log.e(TAG, "Button: Start Living Room Activity");
                startActivity(lvroomIntent); //Only for test
                break;
        }
    }

    //    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.e("TAG", "STATE onResume");
        super.onResume();
    }
}