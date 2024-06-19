package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class more_info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String countyName = extras.getString("CountyName");
            String townName = extras.getString("TownName");
            String dateTime = extras.getString("DateTime");
            String precipitation = extras.getString("Precipitation");

            TextView tvCountyName = findViewById(R.id.tvCountyName);
            TextView tvTownName = findViewById(R.id.tvTownName);
            TextView tvDateTime = findViewById(R.id.tvDateTime);
            TextView tvPrecipitation = findViewById(R.id.tvPrecipitation);

            tvCountyName.setText("縣市: " + countyName);
            tvTownName.setText("鄉鎮: " + townName);
            tvDateTime.setText("觀測時間: " + dateTime);
            tvPrecipitation.setText("降水量: " + precipitation);
        }
    }

    public void goBack(View view) {
        finish();
    }
}
