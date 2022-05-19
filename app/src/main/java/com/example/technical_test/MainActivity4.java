package com.example.technical_test;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 *
 * Esta Clase se utiliza para el consumo de un servicio RestFull de tipo "Get" para Consultar los datos, utilizando una autenticación Oauth 2.0 Bearer, con OkHttp.
 * Para este ejercicio seguiremos utilizando SharedPreferences.
 * @author Geovani Gómez Pérez
 * @since version 1.1.51.
 */

public class MainActivity4 extends AppCompatActivity {
    public OkHttpClient client;
    TextView TvConsulta, TvTemp;
    public static final MediaType CONTENT_TYPE = MediaType.parse("text/plain");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        TvConsulta = (TextView) findViewById(R.id.TvConsulta);
        TvTemp = (TextView) findViewById(R.id.TvTemp);
        DataStorage();
    }

    public void consultarDato(View v) {
        SharedPreferences prefe = getSharedPreferences("data", Context.MODE_PRIVATE);
        String operationId = prefe.getString("operationId", "data");
        String apURL = "https://apidev.buroidentidad.com:9425/bid/rest/v1/operation?operationId=" + operationId;
        client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("USER_CLIENT_APP", "password"))
                .build();
        String requestData = "";
        try {
            loginRequest(apURL, requestData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginRequest(String apUrl, String requestData) throws IOException {
        SharedPreferences prefe = getSharedPreferences("data", Context.MODE_PRIVATE);
        String token_store = prefe.getString("token", "data");
        RequestBody body = RequestBody.create(CONTENT_TYPE, requestData);
        Request request = new Request.Builder()
                .url(apUrl)
                .method("GET", null)
                .addHeader("Content-Type", "text/plain")
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
                    JSONObject data = json.getJSONObject("data");
                    SharedPreferences.Editor editor = prefe.edit();
                    editor.putString("datosFinales", String.valueOf(data));
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void next_screen(View v) {
        SharedPreferences prefe = getSharedPreferences("data", Context.MODE_PRIVATE);
        prefe.edit().clear().apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void DataStorage() {
        SharedPreferences prefe = getSharedPreferences("data", Context.MODE_PRIVATE);
        String operationId = prefe.getString("operationId", "data");
        String token = prefe.getString("token", "data");
        String DatosFinales = prefe.getString("datosFinales", "data");
        TvTemp.setText("DataStorage: \n token: " + token + " \n OperationId: " + operationId);
        TvConsulta.setText(DatosFinales);
    }
}