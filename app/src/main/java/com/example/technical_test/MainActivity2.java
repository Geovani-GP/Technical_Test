package com.example.technical_test;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.technical_test.Options.BasicAuthInterceptor;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * Esta Clase se utiliza para el consumo de un servicio RestFull de tipo "Post" para Insertar un dato, utilizando una autenticación Oauth 2.0 Bearer, con OkHttp.
 * Para este ejercicio se utiliza SharedPreferences para manejar datos como variables de sesion o local Storage, y mantener tanto informacióm del token como otros datos
 * se requieran en la aplicacion.
 * @author Geovani Gómez Pérez
 * @since version 1.1.51.
 */


public class MainActivity2 extends AppCompatActivity {
    public OkHttpClient client;
    private TextView ETacceso, ETproducto;

    public static final MediaType CONTENT_TYPE = MediaType.parse("application/json");
    String apURL = "https://apidev.buroidentidad.com:9425/bid/rest/v1/operations";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ETacceso = (TextView) findViewById(R.id.tv2);
        ETproducto = (TextView) findViewById(R.id.ET_producto);
        Button BTNenviar = (Button) findViewById(R.id.btn_enviar);
        ETproducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void afterTextChanged(Editable s) {
                BTNenviar.setEnabled(s.toString().length() > 0);
            }
        });

        acceso();
    }
    private void acceso() {
        SharedPreferences prefe = getSharedPreferences("data", Context.MODE_PRIVATE);
        String token_store = prefe.getString("token", "data");
        if (prefe.toString().equals("")) {
            ETacceso.setText("Denegado");
        } else {
            ETacceso.setText("Concedido");
            System.out.println("contenido: " + token_store);
        }
    }


    public void insertarDato(View v) {
        client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("USER_CLIENT_APP", "password"))
                .build();
        String producto = ETproducto.getText().toString();
        if (ETproducto.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Campo Vacio", Toast.LENGTH_SHORT).show();
        } else {
            String requestData = "{\n    \"data\": {\n        \"activityStatus\": \"PENDIENTE\",\n        \"activityValue\": \"\",\n        \"code\": \"" + producto + "\",\n        \"data\": \"\",\n        \"productId\": 1,\n        \"secuence\": 1,\n        \"workflowId\": 1\n    },\n    \"metadata\": {\n        \"accuracy\": 16.08,\n        \"deviceInfo\": \"Motorola moto g(6) plus\",\n        \"latitude\": 19.59450382,\n        \"longitude\": -99.02764833,\n        \"timeZoneId\": 1,\n        \"userId\": 1\n    }\n}";
            try {
                loginRequest(apURL, requestData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loginRequest(String apUrl, String requestData) throws IOException {
        SharedPreferences prefe = getSharedPreferences("data", Context.MODE_PRIVATE);
        String token_store = prefe.getString("token", "data");
        RequestBody body = RequestBody.create(CONTENT_TYPE, requestData);
        Request request = new Request.Builder()
                .url(apUrl)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token_store)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CallBackResponse", "onFailure() Request was: " + call.request());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.e("Response", "MensajeRes: " + responseData);
                JSONObject json;
                try {
                    json = new JSONObject(responseData);
                    final JSONObject data = json.getJSONObject("data");
                    Log.e("Mensaje: ", "Buscando :" + data);
                    String operationId = data.getString("operationId");
                    Log.e("Response", "Mensaje: " + response.toString());
                    Log.e("Mensaje: ", "Buscando :" + operationId);
                    SharedPreferences.Editor editor = prefe.edit();
                    editor.putString("operationId", operationId);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void next_screen(View v) {
        Intent intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
    }
}
