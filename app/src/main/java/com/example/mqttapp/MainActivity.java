package com.example.mqttapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttToken;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Declaración de objetos para la UI
    private MqttAndroidClient mqttAndroidClient;
    private TextView tvMessages;  // Para mostrar mensajes recibidos
    private EditText etMessage;   // Campo para escribir el mensaje
    private Button btnSend;       // Botón para enviar el mensaje

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Usa tu layout aquí

        // Inicializar los componentes UI
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Configuración del cliente MQTT
        String clientId = MqttClient.generateClientId(); // ID único para el cliente
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(),
                "tcp://test.mosquitto.org:1883", clientId);  // Usa tu broker MQTT aquí

        // Opciones de conexión (Clean session, etc.)
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);  // Conexión limpia

        // Intentar conectar al broker MQTT
        connectToBroker(options);

        // Configurar el callback del cliente MQTT para recibir mensajes
        setMqttCallback();

        // Configurar la acción para el botón de enviar
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);  // Enviar el mensaje
                    etMessage.setText("");  // Limpiar campo de mensaje
                } else {
                    Toast.makeText(MainActivity.this, "Por favor, ingrese un mensaje", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para conectar al broker MQTT
    private void connectToBroker(MqttConnectOptions options) {
        try {
            IMqttToken conectadoAlBrokerMqtt = mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }

                @Override
                public void onSuccess(MqttToken asyncActionToken) {
                    Log.d(TAG, "Conectado al broker MQTT");
                    subscribeToTopic();  // Suscribirse al topic después de la conexión exitosa
                }

                @Override
                public void onFailure(MqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Error al conectar al broker: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para suscribirse al topic
    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe("test/topic", 0);  // Suscríbete al topic que desees
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para configurar el callback que maneja los mensajes
    private void setMqttCallback() {
        mqttAndroidClient.setCallback(new org.eclipse.paho.client.mqttv3.MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Conexión perdida: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String receivedMessage = new String(message.getPayload());
                onMessageReceived(receivedMessage);  // Mostrar mensaje en el TextView
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

            @Override
            public void deliveryComplete(MqttToken token) {
                // Se puede usar para confirmar que el mensaje fue entregado (no necesario en este caso)
            }
        });
    }

    // Este método es llamado cuando se recibe un mensaje
    private void onMessageReceived(String message) {
        tvMessages.append("\n" + message);  // Mostrar el mensaje recibido en el TextView
    }

    // Método para enviar un mensaje
    private void sendMessage(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttAndroidClient.publish("test/topic", mqttMessage);  // Publicar el mensaje en el topic
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
