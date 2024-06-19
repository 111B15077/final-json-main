package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName() + "My";
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catchData();
    }

    private void catchData() {
        String catchData = "https://api.jsonserve.com/y-kTWs";
        ProgressDialog dialog = ProgressDialog.show(this, "讀取中", "請稍候", true);
        new Thread(() -> {
            try {
                URL url = new URL(catchData);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    json.append(line);
                }

                JSONObject jsonObject = new JSONObject(json.toString());
                JSONObject records = jsonObject.getJSONObject("records");
                JSONArray stations = records.getJSONArray("Station");

                for (int i = 0; i < stations.length(); i++) {
                    JSONObject station = stations.getJSONObject(i);
                    String countyName = station.getJSONObject("GeoInfo").getString("CountyName");
                    String townName = station.getJSONObject("GeoInfo").getString("TownName");
                    String dateTime = station.getJSONObject("ObsTime").getString("DateTime");
                    String precipitation = station.getJSONObject("RainfallElement").getJSONObject("Now").getString("Precipitation");

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("CountyName", countyName);
                    hashMap.put("TownName", townName);
                    hashMap.put("DateTime", dateTime);
                    hashMap.put("Precipitation", precipitation);

                    arrayList.add(hashMap);
                }

                Log.d(TAG, "catchData: " + arrayList);

                runOnUiThread(() -> {
                    dialog.dismiss();
                    RecyclerView recyclerView;
                    MyAdapter myAdapter;
                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                    myAdapter = new MyAdapter();
                    recyclerView.setAdapter(myAdapter);
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCounty, tvTown, tvDateTime, tvPrecipitation;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCounty = itemView.findViewById(R.id.tvCountyName);
                tvTown = itemView.findViewById(R.id.tvTownName);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                tvPrecipitation = itemView.findViewById(R.id.tvPrecipitation);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_data_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, String> item = arrayList.get(position);
            holder.tvCounty.setText("縣市：" + item.get("CountyName"));
            holder.tvTown.setText("鄉鎮：" + item.get("TownName"));
            holder.tvDateTime.setText("觀測時間：" + item.get("DateTime"));
            holder.tvPrecipitation.setText("降水量：" + item.get("Precipitation"));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), more_info.class);
                    intent.putExtra("CountyName", item.get("CountyName"));
                    intent.putExtra("TownName", item.get("TownName"));
                    intent.putExtra("DateTime", item.get("DateTime"));
                    intent.putExtra("Precipitation", item.get("Precipitation"));
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
}
