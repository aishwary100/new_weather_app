package com.example.sunshine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static com.example.sunshine.R.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecast;
    DayNightSwitch dswitch;
    View bg;
    ImageView imageView;
    TextView textDesc, textCity, textHUmidity, textVision, textFeelsLike, textTemp;
    SearchView searchView;
    ListView listView;
    String[] data;
    ArrayList arrayList;
    ArrayAdapter arrayAdapter;
    String city = "London,uk";
    String cityString;
    String key = "4059e087b45050464aef581d4d7acd5f";
    String Curl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + key;
    String[] citydata;
    InputStream inputStreamcounter;
    BufferedReader bufferedReadercounter;

    InputStream inputStreamloader;
    BufferedReader bufferedReaderloader;

    public class DownloadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpsURLConnection httpsURLConnection;
            InputStream inputStream;
            InputStreamReader inputStreamReader;
            String result = "";


            try {

                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                inputStream = httpsURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data != -1) {
                    result += (char) data;
                    data = inputStreamReader.read();
                }


            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    public class DownloadIcon extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            URL url;
            HttpsURLConnection httpsURLConnection;
            InputStream inputStream;
            InputStreamReader inputStreamReader;

            try {
                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                inputStream = httpsURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else
            Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();


        ///ID ASSIGNMENT
        textDesc = findViewById(id.desctext);
        textCity = findViewById(id.citytext);
        textHUmidity = findViewById(id.humidtext);
        textVision = findViewById(id.visiontext);
        textFeelsLike = findViewById(id.feeltext);
        textTemp = findViewById(id.temptext);
        searchView = findViewById(id.searchView);
        listView = findViewById(id.listv);
        imageView = findViewById(id.imageView2);


        ///ARRAY LIST
        int count =0 ;
        inputStreamcounter  = this.getResources().openRawResource(raw.citydata);
        bufferedReadercounter = new BufferedReader(new InputStreamReader(inputStreamcounter));

        inputStreamloader = this.getResources().openRawResource(raw.citydata);
        bufferedReaderloader = new BufferedReader(new InputStreamReader(inputStreamloader));

        try{
            while(bufferedReadercounter.readLine()!=null){
                count++;
            }

        }catch(Exception e){}

        citydata = new String[count];

        try{
            for( int  i =0 ;i <count; i++){
                citydata[i] = bufferedReaderloader.readLine();
            }

        }catch(Exception e){}

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, citydata);
        listView.setAdapter(arrayAdapter);
        //////////////


        //NEW

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              cityString =(listView.getItemAtPosition(position).toString());
                searchView.setQuery(cityString,false);
                DownloadJSON downloadJSON = new DownloadJSON();
                Curl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityString + "&units=metric&appid=" + key;
                try {

                    String result = downloadJSON.execute(Curl).get();
                    Log.i("JSON", result);
                    JSONObject jsonObject = new JSONObject(result);





                    String temp = jsonObject.getJSONObject("main").getString("temp");
                    String humidity = jsonObject.getJSONObject("main").getString("humidity");
                    String feel_like = jsonObject.getJSONObject("main").getString("feels_like");
                    String visibility = jsonObject.getString("visibility");
                    String min = jsonObject.getJSONObject("main").getString("temp_min");
                    String max = jsonObject.getJSONObject("main").getString("temp_max");
                    JSONObject jo = new JSONObject(result);
                    JSONArray jsonArray =jo.getJSONArray("weather");
                    String desc = jsonArray.getJSONObject(0).getString("main");

                    mForecast = "Today's temprature is"  + max + "/" + min  + " at" + cityString;

                    float temper = Float.parseFloat(temp);
                    int tmper2 = (int) temper;

                    textHUmidity.setText(humidity + "g/m3");
                    textVision.setText(max+"°c      "+min+"°c");
                    textFeelsLike.setText(feel_like + "°");
                    textTemp.setText(tmper2 + "°C");
                    textCity.setText(cityString);
                    textDesc.setText(desc);



                    switch (desc) {
                        case "Clear":
                            imageView.setBackgroundResource(drawable.sunny);
                            break;
                        case "Clouds":
                        case "Haze":
                            imageView.setImageResource(drawable.cloudy2);
                            break;
                        case "Shower rain":
                        case "Rain":
                            imageView.setImageResource(drawable.rainy);
                            break;
                        case "Mist":
                        case "Drizzle":
                            imageView.setImageResource(drawable.partial_rainy);
                            break;
                        case "Thunderstorm":
                            imageView.setImageResource(drawable.thunder);
                            break;
                        case "Snow":
                            imageView.setImageResource(drawable.snowfall);
                            break;
                        default:
                            imageView.setImageResource(drawable.sunny); break;
                        case "Smoke":
                        case "Dust":
                        case "Fog":
                        case "Sand":
                        case "Ash":
                        case "Squall" :
                        case "Tornado": imageView.setImageResource(drawable.wind); break;
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Data Unavailable", Toast.LENGTH_LONG).show();
                }listView.setVisibility(View.GONE);

            }
        });
        listView.setVisibility(View.GONE);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listView.setVisibility(View.GONE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (arrayList.contains(query)) {
                    arrayAdapter.getFilter().filter(query);
                } else {
                    Toast.makeText(MainActivity.this, "No Match found", Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listView.setVisibility(View.VISIBLE);
                arrayAdapter.getFilter().filter(newText);
                return false;
            }


        });

        /////////////////////////




////////////DARK MODE STUFF
        dswitch = (DayNightSwitch) findViewById(id.dayNight);
        bg = findViewById(id.backgroundView);
        dswitch.setDuration(450);
        dswitch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean is_night) {
                if (is_night) {
                    Toast.makeText(MainActivity.this, "Night Mode On", Toast.LENGTH_LONG).show();
                    bg.setAlpha(1f);
                } else {

                    Toast.makeText(MainActivity.this, "Day Mode On", Toast.LENGTH_LONG).show();
                    bg.setAlpha(0f);
                }
            }
        });


    }



    private void openLocationInMap() {
        // COMPLETED (9) Use preferred location rather than a default location to display in the map
        String addressString = cityString;
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.share_map, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecast + FORECAST_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }
}