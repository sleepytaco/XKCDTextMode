package com.abukh.xkcdtextmode;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button getRandomComic;
    private Button previousComicButton;

    private volatile int total_comics = -1;
    private int previousComicNum = -1;
    private ArrayList<Integer> alreadyRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("mewo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        getRandomComic = findViewById(R.id.getRandomComicButton);
        previousComicButton = findViewById(R.id.previousComicButton);

        previousComicButton.setVisibility(View.INVISIBLE);

        alreadyRead = new ArrayList<>();

        getTotalComicsCount(); // set total_comics to the last available number

        getRandomComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {
                    while (total_comics == -1) {
                        // waits till  total_comics var is updated with current number of comics
                    }

                    int random_comic = generateUnreadComicNum();

                    getComic(random_comic);
                }

            }
        });

        previousComicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    getComic(previousComicNum);
                }
            }
        });
    }

    private int generateUnreadComicNum() { // generates a unique number in the comic range
        Random rand = new Random();
        int random_comic = 1 + rand.nextInt(total_comics);

        while (alreadyRead.contains(random_comic)) {
            random_comic = 1 + rand.nextInt(total_comics);
        }

        return random_comic;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        previousComicButton.setVisibility(View.VISIBLE);
    }

    private void getTotalComicsCount() {
        String url = "https://xkcd.com/info.0.json"; // url to retrieve most recent comic

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonData = response.body().string();

                    if (response.isSuccessful()) {
                        try {
                            JSONObject details = new JSONObject(jsonData);
                            total_comics = details.getInt("num"); // retrieve most recent comic number
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        alertUserAboutError();
                    }
                }
            });
        }
    }

    private void getComic(int comic_num) {

        String url = "https://xkcd.com/" + comic_num + "/info.0.json";

        Log.v(TAG, Integer.toString(comic_num));

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonData = response.body().string();

                    if (response.isSuccessful()) {

                        try {
                            JSONObject details = new JSONObject(jsonData);
                            int comic_num = details.getInt("num");

                            alreadyRead.add(comic_num); // add to list of already read comics
                            previousComicNum = comic_num; // also set it as previously read comic num

                            XKCDComic comic = new XKCDComic(
                                    details.getInt("num"),
                                    details.getInt("month"),
                                    details.getInt("year"),
                                    details.getString("title"),
                                    details.getString("transcript"),
                                    details.getString("img"));

                            Intent intent = new Intent(MainActivity.this, ComicDisplayActivity.class);
                            intent.putExtra("comic_details", (Serializable) comic);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        alertUserAboutError();
                    }
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        // Use the android class ConnectivityManager to work with network issues
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        // check if theres a network and if its connected to the internet
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else { // let the user know theres no network
            Toast.makeText(this, "Sorry, the network is unavailable.", Toast.LENGTH_LONG).show();
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

}