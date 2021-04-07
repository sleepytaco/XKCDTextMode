package com.abukh.xkcdtextmode.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abukh.xkcdtextmode.R;
import com.abukh.xkcdtextmode.XKCD;
import com.abukh.xkcdtextmode.XKCDComic;
import com.abukh.xkcdtextmode.adapters.ComicsHistoryAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HistoryFragment";

    private RecyclerView rvComicsHistory;
    protected ComicsHistoryAdapter adapter;

    protected List<XKCD> allComics;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvComicsHistory = view.findViewById(R.id.rvComicsHistory);

        allComics = new ArrayList<>();
        adapter = new ComicsHistoryAdapter(getContext(), allComics);

        rvComicsHistory.setAdapter(adapter);
        rvComicsHistory.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rvComicsHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        queryComicsHistory();

    }

    @Override
    public void onResume() {
        super.onResume();
        queryComicsHistory();
    }

    protected void queryComicsHistory() {
        ParseQuery<XKCD> query = ParseQuery.getQuery("XKCD");
        //query.setLimit(10);
        //query.include(XKCD.KEY_USER); // includes user data as well
        query.addDescendingOrder("createdAt"); // order the objects according to createdAt column value
        query.whereEqualTo(XKCD.KEY_USER, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<XKCD>() {

            @Override
            public void done(List<XKCD> comics, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                if (comics.isEmpty()) {
                    Toast.makeText(getContext(), "No history yet!", Toast.LENGTH_SHORT).show();
                }
                for (XKCD comic : comics) {
                    Log.v(TAG, comic.getComicTitle() + " " + comic.getComicNum());
                }

                adapter.clear();
                adapter.addAll(comics);

                //swipeContainer.setRefreshing(false);
            }
        });
    }


}