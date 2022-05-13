package com.example.technical_test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class MainActivity2 extends AppCompatActivity {

    private TextView textView2;

    public OkHttpClient client;
    //MediaType mediaType = MediaType.parse("text/plain");
    //RequestBody body = RequestBody.create(mediaType, "");
    public static final MediaType CONTENT_TYPE = MediaType.get("text/plain");
    String apURL= "https://apidev.buroidentidad.com:9425/uaa/oauth/token?grant_type=password&username=training&password=password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView2=(TextView) findViewById(R.id.tv2);






        client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("USER_CLIENT_APP", "password"))
                .build();
        String username="training";
        String password="password";
        String grant_type="password";
        String requestData= "grant_type="+grant_type+"&username="+username+"&password="+password;
        try{
            loginRequest(apURL, requestData);
        }catch (IOException e){
            e.printStackTrace();
        }

        acceso();
    }

    private void acceso() {
        SharedPreferences prefe=getSharedPreferences("datos",Context.MODE_PRIVATE);
        if (prefe.toString().equals("")){
            textView2.setText("Denegado");
        }else{
            textView2.setText("Concedido");
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

                    String responseData = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(responseData);
                    try {
                        final String owner = json.getString("access_token");

                        SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferencias.edit();
                        editor.putString("token", owner);
                        editor.commit();
                        finish();

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
