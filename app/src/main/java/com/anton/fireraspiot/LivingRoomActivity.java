package com.anton.fireraspiot;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;


public class LivingRoomActivity extends AppCompatActivity implements View.OnClickListener, MqttCallback {
    TextView tview_log;
    Button btnPubMsgOff, btnPubMsgCancelOff, btn_clear_textview;
    TimePickerFragment timeDelayFragment;


    private final String TAG = "MY";//;//MainActivity.class.getName();
    static final int QOS = 2;
    final int SECINMIN = 60; //Seconds per minute
    final int SECINHOUR = SECINMIN*SECINMIN; //Seconds per hour
    public Handler hdThread; //Handler for receiving msg from Server Thread
    //private final String BROKER_ADDRESS = "tcp://iot.eclipse.org:1883";
    //private final String BROKER_ADDRESS = "tcp://test.mosquitto.org:1883";
    private final String BROKER_ADDRESS = "tcp://192.168.0.106:1883";
    private final String TOPIC = "home/livingroom/pc";
    private final String TURN_ON = "ON";
    private final String TURN_OFF = "OFF";
    private final String TURN_OFF_CANCEL = "CANCEL";
    private final String CHECK_STATUS = "STATUS";
    private final MemoryPersistence persistence = new MemoryPersistence();
    public MqttAndroidClient mqttAndroidClient;

    int resDelay; //Final delay in second for Power OFF PC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_living_room);
        btnPubMsgOff = findViewById(R.id.pub_msg_off);
        btnPubMsgCancelOff = findViewById(R.id.pub_msg_cancelOff);
        btn_clear_textview = findViewById(R.id.clear_textview);
        tview_log = findViewById(R.id.output);
        tview_log.setMovementMethod(new ScrollingMovementMethod());


        btnPubMsgOff.setOnClickListener(this);
        btnPubMsgCancelOff.setOnClickListener(this);
        btn_clear_textview.setOnClickListener(this);

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), BROKER_ADDRESS, "AndroidThingSub", persistence);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false); //false for receiving missed messages
        mqttConnectOptions.setConnectionTimeout(3);
        mqttConnectOptions.setAutomaticReconnect(true);


        Log.e("TAG","New Connection:" + BROKER_ADDRESS);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("TAG","Connection Success!");
                    tview_log.append("Connection Success " + BROKER_ADDRESS + " ! \n");
                    try {
                        mqttAndroidClient.subscribe(TOPIC, 1, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.e(TAG,"Subscribed!");
                                tview_log.append("Client Subscribed to " + TOPIC + "\n");
                            }
                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.e(TAG, "Subscribed fail!");
                                tview_log.append("Subscribed fail!  " + TOPIC + "\n");
                            }
                        });
                    } catch (MqttException ex) {
                        Log.e(TAG, "Exceptionst subscribing");
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG,"Connection Failure!");
                    Log.e(TAG,"throwable: " + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("TAG","Connection was lost!");
                tview_log.append("Connection with " + BROKER_ADDRESS + " lost " + "\n");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e("TAG","Message Arrived!: " + topic + ": " + new String(message.getPayload()));
                String date = new SimpleDateFormat("MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                String payload = new String(message.getPayload());
                tview_log.append(date + " MSG Arrived: " + payload + "\n");
                switch (payload) {
                    case "PC_ON":
                        tview_log.append("MSG Arrived PC_ON OK !!!\n");
                        publishMessage(TURN_OFF + ":" + resDelay);
                        break;
//                    case "OFF":
//                        Log.d(TAG, "LED OFF");
//                        ledPin.setValue(false);
//                        break;
                    default:
                        Log.d(TAG, "Message not supported " + payload );
                        break;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.e("TAG","Delivery Complete!");
                try {
                    MqttMessage message = token.getMessage();
                    message.getPayload();
                    Log.e("TAG","Delivery Message = " + message.toString());
                    tview_log.append("MSG Delivered: " + message.toString() + "\n");
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void showTimePickerDialog(View v) {
        timeDelayFragment  = new TimePickerFragment();
        timeDelayFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void publishMessage(String msg) {
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(QOS);
        message.setRetained(false);
        try {
            mqttAndroidClient.publish(TOPIC, message);
            Log.e("TAG", "MY NEW Publish : " + TOPIC);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //TODO: Need Investigate
//mqttAndroidClient.publish("$SYS/broker/connection/AndroidThingSub/state", message);

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pub_msg_off:
                Log.e(TAG, "Button: Publish Message OFF PC");
                //publishMessage();

                if(timeDelayFragment != null) {
                    Log.e(TAG, "Button: TEST hour = " + Integer.toString(timeDelayFragment.getHour()));
                    Log.e(TAG, "Button: TEST minute = " + Integer.toString(timeDelayFragment.getMinute()));
                    resDelay = timeDelayFragment.getHour() * SECINHOUR + timeDelayFragment.getMinute() * SECINMIN;
                }
//                Log.e(TAG, "Button: TEST resDelay = " + Integer.toString(resDelay));
//                //publishMessage(TURN_OFF + ":" + resDelay);
                publishMessage(CHECK_STATUS);
               //TODO: Reset myMinute
                break;
            case R.id.pub_msg_cancelOff:
                Log.e(TAG, "Button: Publish Message CANCEL OFF PC");
                publishMessage(TURN_OFF_CANCEL);
                break;
            case R.id.clear_textview:
                Log.e(TAG, "Button: CLEAR TextView");
                tview_log.setText("");
                break;
        }
    }



    @Override
    public void connectionLost(Throwable cause) {
        Log.e(TAG, "MY connectionLost....");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.e(TAG, "MY " + payload);
        tview_log.setText("Recieve: " + payload);
//        switch (payload) {
//            case "ON":
//                Log.d(TAG, "LED ON");
//                ledPin.setValue(true);
//                break;
//            case "OFF":
//                Log.d(TAG, "LED OFF");
//                ledPin.setValue(false);
//                break;
//            default:
//                Log.d(TAG, "Message not supported!");
//                break;
//        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.e(TAG, "MY deliveryComplete....");
    }

    //    @Override
    protected void onStart() {
//        serverThread = new ServerUDPthread(UDP_PORT,MainActivity.this,hdThread);
//        serverThread.setRunning(true);
//        serverThread.start();
//        tview_log.setText("SERVER STARTED IP:" + getIpAddress() + " PORT: " + UDP_PORT + "\n");
        super.onStart();
    }

    @Override
    protected void onStop() {
//        if(serverThread != null){
//            serverThread.setRunning(false);
//            serverThread = null;
//        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.e("TAG", "STATE onResume");
        super.onResume();
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

}
