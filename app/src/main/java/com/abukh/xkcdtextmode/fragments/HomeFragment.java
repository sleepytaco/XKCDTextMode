package com.abukh.xkcdtextmode.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abukh.xkcdtextmode.ComicDisplayActivity;
import com.abukh.xkcdtextmode.LoginActivity;
import com.abukh.xkcdtextmode.R;
import com.abukh.xkcdtextmode.XKCD;
import com.abukh.xkcdtextmode.XKCDComic;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public static final String TAG = HomeFragment.class.getSimpleName();

    private Button getRandomComic;
    private Button previousComicButton;
    private Button btnLogout;
    private TextView tvLoggedInAs;
    private TextView tvProgress;
    private ProgressBar progBar;
    private TextView tvProgPercent;

    private volatile int total_comics = -1;
    private int previousComicNum = -1;
    private int addedToParse = -1;
    private ArrayList<Integer> alreadyRead;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getRandomComic = view.findViewById(R.id.getRandomComicButton);
        previousComicButton = view.findViewById(R.id.previousComicButton);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvLoggedInAs = view.findViewById(R.id.tvLoggedInAs);
        tvProgress = view.findViewById(R.id.tvProgress);
        tvProgPercent = view.findViewById(R.id.tvProgPercent);

        progBar = view.findViewById(R.id.progBar);

        previousComicButton.setVisibility(View.INVISIBLE);

        tvLoggedInAs.setText(Html.fromHtml("Logged in as <b>" + ParseUser.getCurrentUser().getUsername() + "</b>"));

        alreadyRead = new ArrayList<>();

        getTotalComicsCount(); // set total_comics to the last available number
        setProgressBar();

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
                    getPreviousComicFromParse();                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

                if (currentUser != null) {
                    Toast.makeText(getContext(), "Error logging you out!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getContext(), LoginActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        });

    }


    private void setProgressBar() {

        while (total_comics == -1) {

        }

        //progBar.setMax(total_comics);

        ParseQuery<XKCD> query = ParseQuery.getQuery("XKCD");
        query.whereEqualTo(XKCD.KEY_USER, ParseUser.getCurrentUser());

        DecimalFormat df = new DecimalFormat("#.##");

        // count number of query objects
        try {
            int count  =  query.count();

            String progress = df.format((count/(float) total_comics) * 100);

            progBar.setProgress((int) ((count/(float) total_comics) * 100));
            tvProgress.setText(count + "/" + total_comics + " Comics Found");
            tvProgPercent.setText(progress + "%");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*
        query.findInBackground(new FindCallback<XKCD>() {

            @Override
            public void done(List<XKCD> comics, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                if (comics.isEmpty()) {
                    //Toast.makeText(getContext(), "No progress made yet!", Toast.LENGTH_SHORT).show();
                    progBar.setProgress(0);
                    tvProgress.setText(0 + "/" + total_comics + " Comics Found");
                } else {
                    progBar.setProgress(comics.size());
                    tvProgress.setText(comics.size() + "/" + total_comics + " Comics Found");

                }
            }
        });
        */
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
    public void onResume() {
        super.onResume();
        previousComicButton.setVisibility(View.VISIBLE);
        setProgressBar();
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
                        //alertUserAboutError();
                        Toast.makeText(getContext(), "Uh-oh! Internet problems :(", Toast.LENGTH_SHORT).show();
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

                            addComicToParse(comic);

                            while (addedToParse == -1) {

                            }

                            addedToParse = -1;

                            Intent intent = new Intent(getContext(), ComicDisplayActivity.class);
                            intent.putExtra("comic_num", comic.getComicNum());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        //alertUserAboutError();
                        Toast.makeText(getContext(), "Uh-oh! Internet problems :(", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getPreviousComicFromParse() {

        ParseQuery<XKCD> query = ParseQuery.getQuery("XKCD");
        query.whereEqualTo(XKCD.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt"); // order the objects according to createdAt column value
        query.setLimit(1);

        query.findInBackground(new FindCallback<XKCD>() {

            @Override
            public void done(List<XKCD> comics, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                if (comics.isEmpty()) {
                    Toast.makeText(getContext(), "No previous comic found!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), ComicDisplayActivity.class);
                    intent.putExtra("comic_num", comics.get(0).getComicNum());
                    startActivity(intent);
                }
            }
        });
    }

    private void addComicToParse(XKCDComic comic) {

        ParseQuery<XKCD> query = ParseQuery.getQuery("XKCD");
        query.whereEqualTo(XKCD.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(XKCD.KEY_COMIC_NUM, comic.getComicNum());

        query.findInBackground(new FindCallback<XKCD>() {

            @Override
            public void done(List<XKCD> comics, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                Log.v(TAG, "Checking if " + comic.getComicNum() + " is in the DB");
                if (comics.isEmpty()) {
                    Log.v(TAG, "No it is not in the DB");
                    XKCD newComic = new XKCD();
                    newComic.setUser(ParseUser.getCurrentUser());
                    newComic.setComicTitle("#" + comic.getComicNum() + " " + comic.getTitle());
                    newComic.setComicNum(comic.getComicNum());

                    newComic.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while saving", e);
                                Toast.makeText(getContext(), "Error while marking this comic as read!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            addedToParse = 1;
                        }
                    });
                } else {
                    addedToParse = 1;
                }

            }
        });
    }

    private boolean isNetworkAvailable() {
        // Use the android class ConnectivityManager to work with network issues
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        // check if theres a network and if its connected to the internet
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else { // let the user know theres no network
            Toast.makeText(getContext(), "Sorry, the network is unavailable.", Toast.LENGTH_LONG).show();
        }

        return isAvailable;
    }

}