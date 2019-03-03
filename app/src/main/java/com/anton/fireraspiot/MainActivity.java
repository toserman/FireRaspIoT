package com.anton.fireraspiot;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
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
import java.util.Enumeration;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MqttCallback {

    TextView tview_log;
    Button btn_pubmsg, btn_stop, btn_clear_textview;
    //    ServerUDPthread serverThread;
    private final String TAG = "MY";//;//MainActivity.class.getName();
    static final int UDP_PORT = 48656;
    static final int CLIENT_SRV_PORT = 48656;
    static final int QOS = 2;
    public Handler hdThread; //Handler for receiving msg from Server Thread
   // private final String BROKER_ADDRESS = "tcp://iot.eclipse.org:1883";
    private final String BROKER_ADDRESS = "tcp://test.mosquitto.org:1883";
    private final String TOPIC = "home/livingroom/pc";
    private final String TURN_ON = "ON";
    private final String TURN_OFF = "OFF";
    private final String TURN_OFF_CANCEL = "CANCEL";
    private final MemoryPersistence persistence = new MemoryPersistence();
    public MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_pubmsg = findViewById(R.id.pub_msg);
        btn_stop = findViewById(R.id.srv_stop);
        btn_clear_textview = findViewById(R.id.clear_textview);
        tview_log = findViewById(R.id.output);
        tview_log.setMovementMethod(new ScrollingMovementMethod());

        btn_pubmsg.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_clear_textview.setOnClickListener(this);

        // FROM INTERNET NEEEED APPLY !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        ///https://stackoverflow.com/questions/43038597/android-studio-mqtt-not-connecting

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), BROKER_ADDRESS, "AndroidThingSub", persistence);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("TAG","Connection was lost!");
                tview_log.append("Connection with " + BROKER_ADDRESS + " lost " + "\n");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e("TAG","Message Arrived!: " + topic + ": " + new String(message.getPayload()));
                tview_log.append("MSG Received: " + message.toString() + "\n");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.e("TAG","Delivery Complete!");

//                MqttMessage message = new MqttMessage("Hello, I am Android Mqtt Client.".getBytes());
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

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setAutomaticReconnect(true);


        Log.e("TAG","New Connection:" + BROKER_ADDRESS);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
//                    try {
                          Log.e("TAG","Connection Success!");
                          tview_log.append("Connection Success " + BROKER_ADDRESS + " ! \n");
                       //  mqttAndroidClient.subscribe(TOPIC, 1);
                       // Log.e("TAG","Subscribe !!!");
//                        System.out.println("Subscribed to /test");
//                        System.out.println("Publishing message..");
//                        mqttAndroidClient.publish("/test", new MqttMessage("Hello world testing..!".getBytes()));
                    //} catch (MqttException e) {
//                      e.printStackTrace();
                    //}

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

////        private void subscribeToTopic() {
//            try {
//                mqttAndroidClient.subscribe(TOPIC, 1, null, new IMqttActionListener() {
//                    @Override
//                    public void onSuccess(IMqttToken asyncActionToken) {
//                        Log.w("Mqtt","Subscribed!");
//                    }
//
//                    @Override
//                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                        Log.w("Mqtt", "Subscribed fail!");
//                    }
//                });
//
//            } catch (MqttException ex) {
//                System.err.println("Exceptionst subscribing");
//                ex.printStackTrace();
//            }
//        }}
       // mqttAndroidClient.subscribe(TOPIC, 1);
        //Log.e("TAG","Subscribe !!!");


//OLD
//        Log.e(TAG,"**************************************************");
//        Log.e("TAG","OLD Connection:" + BROKER_ADDRESS);
//        try {
//            MqttClient client = new MqttClient(BROKER_ADDRESS, "AndroidThingSub", new MemoryPersistence());
//           //  MqttClient client = new MqttClient("tcp://192.168.0.104:1883", "AndroidThingSub", new MemoryPersistence());
//            //MqttClient client = new MqttClient("tcp://test.mosquitto.org:1883", "AndroidThingSub", new MemoryPersistence());
//            client.setCallback(this);
//            client.connect();
//            //String topic = "MQTT Examples";
//            Log.e("TAG", "MY START MQtt Subscribe:" + TOPIC);
//            tview_log.setText("MY START MQtt Subscribe:" + TOPIC);
//            client.subscribe(TOPIC);
//
//
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }

    }

    public void newPublishMessage(String msg) {
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

    public void publishMessage () {
        try {
            MqttClient client = new MqttClient(BROKER_ADDRESS, "AndroidThingSub", new MemoryPersistence());
            //MqttClient client = new MqttClient("tcp://192.168.0.104:1883", "AndroidThingSub", new MemoryPersistence());
            //MqttClient client = new MqttClient("tcp://test.mosquitto.org:1883", "AndroidThingSub", new MemoryPersistence());

            //String topic = "MQTT Examples";
            Log.e("TAG", "MY MQtt Publish :");
           // tview_log.append("MY MQtt Publish Message");

            MqttMessage message = new MqttMessage("Hello, I am Android Mqtt Client.".getBytes());
            message.setQos(QOS);
            message.setRetained(false);


            client.publish(TOPIC, message);
            //client.getPendingDeliveryTokens();
//            IMqttDeliveryToken [] test = client.getPendingDeliveryTokens();


            //client.publish("MQTT Examples","Hello, I am Android Mqtt Client.".getBytes(),1,true);
            tview_log.append("Message published\n");

//                    MQTTClient_message pubmsg = MQTTClient_message_initializer;
                    //MQTTClient_deliveryToken token;
//
//                    MQTTClient_create(&client, ADDRESS, CLIENTID, MQTTCLIENT_PERSISTENCE_NONE, NULL);
//                     pubmsg.payload = PAYLOAD;
//                    pubmsg.payloadlen = strlen(PAYLOAD);
//                    pubmsg.qos = QOS;
//                    pubmsg.retained = 0;
//                    MQTTClient_publishMessage(client, TOPIC, &pubmsg, &token);
//                    MqttMessage msg = new MqttMessage();
//                    msg.setPayload("Hello IoT");
//                    client.publish(topic,"HELLO IoT",1,true);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pub_msg:
                Log.e(TAG, "Button: Publish Message");
                //publishMessage();
                newPublishMessage(TURN_OFF);
//                tview_log.setText("SERVER STARTED IP:" + getIpAddress());
//                if (serverThread == null) {
//                    serverThread = new ServerUDPthread(UDP_PORT, MainActivity.this,hdThread);
//                    serverThread.setRunning(true);
//                    serverThread.start();
//                    tview_log.setText("SERVER STARTED");
//                }
                //tview_log.setText(getIpAddress());
                break;
            case R.id.srv_stop:
                //TODO: Need debug for good STOP
                Log.e(TAG, "Button: SERVER STOP");
                newPublishMessage(TURN_OFF_CANCEL);
//                if(serverThread != null){
//                    serverThread.setRunning(false);
//                    serverThread.interrupt();
//                    serverThread = null;
//                    tview_log.setText("SERVER STOPPED");
//                }
                break;
            case R.id.clear_textview:
                Log.e(TAG, "Button: CLEAR TextView");
                tview_log.setText("");
                break;
        }
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

}