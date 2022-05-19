package com.example.technical_test;

import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.EditText;
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
 * Esta Clase se utiliza para el consumo de un servicio RestFull de tipo "Post" para Generar token, utilizando una autenticación Oauth 2.0 Bearer, con OkHttp.
 * @author Geovani Gómez Pérez
 * @since version 1.1.51.
 */
public class MainActivity extends AppCompatActivity {
    public OkHttpClient client;

    public static final MediaType CONTENT_TYPE = MediaType.get("text/plain");
    String apURL = "https://apidev.buroidentidad.com:9425/uaa/oauth/token?grant_type=password&username=training&password=password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText et_nombre = findViewById(R.id.et_Name);
        Button btnSiguiente = findViewById(R.id.btn_Siguiente);
        et_nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable s) {
                btnSiguiente.setEnabled(s.toString().length() > 0);
            }
        });
    }


    public void next_screen(View v) {
        client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("USER_CLIENT_APP", "password"))
                .build();
        String username = "training";
        String password = "password";
        String grant_type = "password";
        String requestData = "grant_type=" + grant_type + "&username=" + username + "&password=" + password;
        try {
            loginRequest(apURL, requestData);
            Toast.makeText(this,"Token creado",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity2.class);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginRequest(String apUrl, String requestData) throws IOException {
        RequestBody body = RequestBody.create(CONTENT_TYPE, requestData);
        Request request = new Request.Builder()
                .url(apUrl)
                .method("POST", body)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Authorization", "Basic dXNlcmFwcDpwYXNzd29yZA==")
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
                SharedPreferences preferencias = getSharedPreferences("data", Context.MODE_PRIVATE);
                String responseData = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(responseData);
                    try {
                        final String owner = json.getString("access_token");
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("token", owner);
                        editor.commit();
                        System.out.println(owner);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

}