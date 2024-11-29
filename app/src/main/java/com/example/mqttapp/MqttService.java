package com.example.mqttapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class MqttService {

    private static final String TAG = "MqttService";
    private MqttAndroidClient mqttAndroidClient;
    private String brokerUrl = "tcp://broker.hivemq.com:1883"; // Broker público
    private String clientId = "android-client-id";
    private String topic = "mqtt/test/topic"; // Topic al que nos suscribimos
    private Context context;

    public MqttService(Context context) {
        this.context = context;
        mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, clientId);
    }

    public void connectToBroker() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        try {
            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(context, "Conectado al broker", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Conectado al broker");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "Fallo la conexión", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Conexión fallida: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(topic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Suscrito al topic: " + topic);
                    Toast.makeText(context, "Suscrito al topic", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Error al suscribirse: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttAndroidClient.publish(topic, mqttMessage);
            Log.d(TAG, "Mensaje enviado: " + message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setCallback(MqttCallback callback) {
        mqttAndroidClient.setCallback(callback);
    }
}
