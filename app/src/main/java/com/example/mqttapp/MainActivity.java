package com.example.mqttapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationmqtt.R;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MQTTApp";
    private Mqtt5AsyncClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editTextName = findViewById(R.id.editTextName);
        Button buttonSend = findViewById(R.id.buttonSend);

        if (isConnectedToInternet()) {
            mqttClient = MqttClient.builder()
                    .useMqttVersion5()
                    .serverHost("test.mosquitto.org")
                    .serverPort(1883)
                    .buildAsync();

            mqttClient.connect()
                    .whenComplete((ack, throwable) -> {
                        if (throwable == null) {
                            Log.d(TAG, "Connected to broker successfully");
                            runOnUiThread(() -> Toast.makeText(this, "Connected to broker", Toast.LENGTH_SHORT).show());
                        } else {
                            Log.e(TAG, "Failed to connect to broker", throwable);
                            runOnUiThread(() -> Toast.makeText(this, "Failed to connect to broker", Toast.LENGTH_SHORT).show());
                        }
                    });
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextName.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mqttClient != null && mqttClient.getState().isConnected()) {
                    mqttClient.publishWith()
                            .topic("test/topic")
                            .payload(message.getBytes())
                            .qos(MqttQos.AT_LEAST_ONCE)
                            .send()
                            .whenComplete((publishAck, throwable) -> {
                                if (throwable == null) {
                                    Log.d(TAG, "Message sent: " + message);
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Message sent: " + message, Toast.LENGTH_SHORT).show());
                                } else {
                                    Log.e(TAG, "Failed to send message", throwable);
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show());
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Client not connected. Check connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}





