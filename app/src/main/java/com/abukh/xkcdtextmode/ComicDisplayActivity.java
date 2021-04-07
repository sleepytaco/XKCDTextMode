package com.abukh.xkcdtextmode;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;

public class ComicDisplayActivity extends AppCompatActivity {

    private Button nextButton;
    private Button previousButton;
    private TextView lineTextView;
    private Button openComicButton;

    private XKCDComic comic;
    public static final String TAG = ComicDisplayActivity.class.getSimpleName();

    private String[] script; // lines of the comic
    private Stack<String> linesStack = new Stack<>(); // stack to navigate between the lines

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_display);

        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        lineTextView = findViewById(R.id.lineTextView);
        openComicButton = findViewById(R.id.openComicButton);

        Intent intent = getIntent();
        Integer comic_num = intent.getIntExtra("comic_num", -1);
        if (comic_num == -1) {
            comic = (XKCDComic) intent.getSerializableExtra("comic_details");
        } else {
            getComic(comic_num);
        }

        while (comic == null) {

        }

        script = comic.getTranscript();

        loadStoryInfo();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextLine();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousLine();
            }
        });

        openComicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(ComicDisplayActivity.this, ComicImageDisplay.class);
                    intent.putExtra("imageLink", comic.getImageLink());
                    startActivity(intent);
                }
            }
        });
    }

    private void previousLine() {
        if (!linesStack.isEmpty()) {
            linesStack.pop();

            if (linesStack.isEmpty()) {
                loadStoryInfo();
            } else {
                nextButton.setVisibility(View.VISIBLE);
                lineTextView.setText(linesStack.peek());
            }
        }

    }

    private void nextLine() {
        if (linesStack.isEmpty()) {

            if (script.length == 1) {
                nextButton.setVisibility(View.INVISIBLE);
            }

            linesStack.push(script[0]);
            lineTextView.setText(script[0]);
            previousButton.setVisibility(View.VISIBLE);

        } else {
            int currentLine = linesStack.size();

            if (currentLine >= script.length) {
                nextButton.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Done!", Toast.LENGTH_LONG);
                Log.v(TAG, "DONE");
            } else {
                if (currentLine == script.length - 1) {
                    nextButton.setVisibility(View.INVISIBLE);
                }
                linesStack.push(script[currentLine]);
                lineTextView.setText(script[currentLine]);
            }
        }
    }

    private void loadStoryInfo() {
        previousButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        lineTextView.setText("#" + comic.getComicNum() + "\n" + comic.getTitle() + "\n~ " + comic.getDate());
    }

    private void getComic(int comic_num) {

        comic = null;

        String url = "https://xkcd.com/" + comic_num + "/info.0.json";

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

                        comic = new XKCDComic(
                                details.getInt("num"),
                                details.getInt("month"),
                                details.getInt("year"),
                                details.getString("title"),
                                details.getString("transcript"),
                                details.getString("img"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //alertUserAboutError();
                    Toast.makeText(ComicDisplayActivity.this, "Uh-oh! Internet problems :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

}