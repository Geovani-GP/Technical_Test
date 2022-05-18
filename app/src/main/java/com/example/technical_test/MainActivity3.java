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

public class MainActivity3 extends AppCompatActivity {
    public OkHttpClient client;
    TextView TVactualizar;
    Button BtnActualizar;

    public static final MediaType CONTENT_TYPE = MediaType.parse("application/json");
    String apURL = "https://apidev.buroidentidad.com:9425/bid/rest/v1/operations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        TVactualizar= (TextView) findViewById(R.id.TVactualizar);
        BtnActualizar=(Button) findViewById(R.id.BtnActualizar);
        TVactualizar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable s) {
                BtnActualizar.setEnabled(s.toString().length() > 0);
            }
        });
    }

    public void actualizarDato(View v) {

        SharedPreferences prefe = getSharedPreferences("data", Context.MODE_PRIVATE);
        String operationId = prefe.getString("operationId","data");
        Log.e("Mensaje: ","operationId :"+operationId);
        client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("USER_CLIENT_APP", "password"))
                .build();
        String activityValue = TVactualizar.getText().toString();
        if (TVactualizar.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Campo Vacio", Toast.LENGTH_SHORT).show();
        }else {
            String requestData = "{\n    \"operationId\": "+operationId+",\n    \"data\": {\n        \"activityStatus\": \"FINALIZADO\",\n        \"activityValue\": \""+activityValue+"\",\n        \"code\": \"VALIDATED\",\n        \"data\": \"\",\n        \"productId\": 1,\n        \"secuence\": 15,\n        \"workflowId\": 1\n    },\n    \"metadata\": {\n        \"accuracy\": 90.048004,\n        \"deviceInfo\": \"Motorola moto g(6) plus\",\n        \"latitude\": 19.59443968,\n        \"longitude\": -99.02765932,\n        \"timeZoneId\": 1,\n        \"userId\": 3\n    }\n}";
            try {
                loginRequest(apURL, requestData);
                Intent intent = new Intent(this, MainActivity2.class);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void loginRequest(String apUrl, String requestData) throws IOException {
        SharedPreferences prefe = getSharedPreferences("data", Context.MODE_PRIVATE);
        String token_store = prefe.getString("token","data");
        RequestBody body = RequestBody.create(CONTENT_TYPE, requestData);
        Request request = new Request.Builder()
                .url(apUrl)
                .method("PUT", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer "+token_store)
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
                Log.e("Response","MensajeRes: "+responseData);
                JSONObject json;
                try {
                    json = new JSONObject(responseData);
                    final JSONObject data = json.getJSONObject("data");
                    Log.e("Mensaje: ","Buscando :"+data);
                   // String operationId=data.getString("operationId");
                   // Log.e("Response","Mensaje: "+response.toString());


                   // Log.e("Mensaje: ","Buscando :"+operationId);



                   /* SharedPreferences preferencias = getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putString("operationId", operationId);
                    editor.commit();*/
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}