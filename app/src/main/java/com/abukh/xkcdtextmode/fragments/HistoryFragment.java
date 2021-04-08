package com.abukh.xkcdtextmode.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abukh.xkcdtextmode.R;
import com.abukh.xkcdtextmode.XKCD;
import com.abukh.xkcdtextmode.adapters.ComicsHistoryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView textView;
    protected ComicsHistoryAdapter adapter;
    private FloatingActionButton fab;
    private FloatingActionButton fabConfirm;

    private boolean noHistory = true;

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
        textView = view.findViewById(R.id.textView);
        fab = view.findViewById(R.id.fab);
        fabConfirm = view.findViewById(R.id.fabConfirm);

        allComics = new ArrayList<>();
        adapter = new ComicsHistoryAdapter(getContext(), allComics, 0);

        rvComicsHistory.setAdapter(adapter);
        rvComicsHistory.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rvComicsHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        queryComicsHistory();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fab.setVisibility(View.GONE);
                fabConfirm.setVisibility(View.VISIBLE);
                Snackbar.make(view, "Are you sure? Your progress will be permanently deleted.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                new CountDownTimer(5000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // do something after 1s
                    }

                    @Override
                    public void onFinish() {
                        // do something end times 5s
                        if (noHistory == false) {
                            fab.setVisibility(View.GONE);
                            fabConfirm.setVisibility(View.GONE);
                            return;
                        }
                        fab.setVisibility(View.VISIBLE);
                        fabConfirm.setVisibility(View.GONE);
                    }

                }.start();

            }
        });

        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteComicsHistoryFromParse(view);
            }
        });

    }

    private void deleteComicsHistoryFromParse(View view) {

        ParseQuery<XKCD> query = ParseQuery.getQuery("XKCD");
        query.whereEqualTo(XKCD.KEY_USER, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<XKCD>() {

            @Override
            public void done(List<XKCD> comics, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                if (comics.isEmpty()) {
                    //Toast.makeText(getContext(), "No history yet!", Toast.LENGTH_SHORT).show();
                    noHistory = true;
                    textView.setVisibility(View.VISIBLE);
                    rvComicsHistory.setVisibility(View.INVISIBLE);
                } else {
                    noHistory = false;
                    textView.setVisibility(View.INVISIBLE);
                    rvComicsHistory.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < comics.size(); i ++) {
                    if (i == comics.size() - 1) {
                        comics.get(i).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                rvComicsHistory.setVisibility(View.INVISIBLE);
                                fab.setVisibility(View.GONE);
                                fabConfirm.setVisibility(View.GONE);
                                textView.setVisibility(View.VISIBLE);
                                Snackbar.make(view, "Your progress was permanently deleted.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                        break;
                    }
                    comics.get(i).deleteInBackground();
                }

                adapter.clear();
                //swipeContainer.setRefreshing(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        queryComicsHistory();
    }

    protected void queryComicsHistory() {
        ParseQuery<XKCD> query = ParseQuery.getQuery("XKCD");
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
                    //Toast.makeText(getContext(), "No history yet!", Toast.LENGTH_SHORT).show();
                    textView.setVisibility(View.VISIBLE);
                    rvComicsHistory.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                    rvComicsHistory.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
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