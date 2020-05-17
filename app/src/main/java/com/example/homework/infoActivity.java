package com.example.homework;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

//import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;

public class infoActivity extends AppCompatActivity {
    private TextView mDate;
    private TextView mStatus;
    private Button mBack;
    private String mLong;
    private String mLat;
    public String tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mBack = (Button) findViewById(R.id.iBack);

        Intent intent = getIntent();

//        final String tmp2 = intent.getStringExtra(CustomerMapActivity.EXTRA_TEXT);

        if (intent.getStringExtra(DeliveryMapActivity.EXTRA_TEXT) != null) {
            tmp = intent.getStringExtra(DeliveryMapActivity.EXTRA_TEXT);
            mLat = intent.getStringExtra(DeliveryMapActivity.EXTRA_TEXT1);
            mLong = intent.getStringExtra(DeliveryMapActivity.EXTRA_TEXT2);
        } else {
            tmp = intent.getStringExtra(CustomerMapActivity.EXTRA_TEXT);
            mLat = intent.getStringExtra(CustomerMapActivity.EXTRA_TEXT1);
            mLong = intent.getStringExtra(CustomerMapActivity.EXTRA_TEXT2);
        }

//        mDate = (TextView) findViewById(R.id.iDate);
//        mDate.setText(tmp + mLat + mLong + "");

        find_weather(mLat, mLong);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tmp != null && tmp.equals("dm")) {
                    Intent intent = new Intent(infoActivity.this, DeliveryMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } else {
                    Intent intent = new Intent(infoActivity.this, CustomerMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        });
    }

    public void find_weather(String mLat, String mLong) {

        mDate = (TextView) findViewById(R.id.iDate);

        final String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + mLat +
                "&lon=" + mLong + "&appid=848784a9122d7f302f233d1d6be11c7c&units=metric";
        Log.d("url1", url);


//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String myResp = response.body().string();
//                    Log.d("TAG", myResp);
//                }
//            }
//        });

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            String tmp;

            @Override
            public void onResponse(JSONObject response) {
                Log.d("url2.4", String.valueOf(response));
                try {
                    String city = response.getString("name");
                    // temp
                    JSONObject main_object = response.getJSONObject("main");
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    // weather
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String description = object.getString("description");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat time = new SimpleDateFormat("EEEE MM-dd");
                    String formatted_date = time.format(calendar.getTime());

                    handleStatus(main_object.getDouble("temp"));

                    tmp = formatted_date + "\n" +
                            "City(According to GPS):" + city + "\n"
                            + "Weather: " + description + "\n"
                            + "Temp (F): " + temp;

                    Log.d("url2.5", String.valueOf(response));
                    mDate.setText(tmp);

                } catch (JSONException e) {
//                    Log.d("TAG", String.valueOf(response));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );

        Log.d("url2", url);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }


    public void handleStatus(double temp) {
        mStatus = (TextView) findViewById(R.id.iStatus);

        if (temp > 30) {
            mStatus.setText("Very hot, you can buy some ice drink. The delivery speed may go to slow a bit.");
        } else if (temp > 20) {
            mStatus.setText("Nice weather. Buy more food and share to your friend.");
        } else {
            mStatus.setText("Very Cold, you can buy some hot drink. The delivery speed may go to slow a bit.");
        }
    }
}
