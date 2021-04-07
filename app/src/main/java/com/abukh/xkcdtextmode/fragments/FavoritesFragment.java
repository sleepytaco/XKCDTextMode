package com.abukh.xkcdtextmode.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abukh.xkcdtextmode.R;
import com.abukh.xkcdtextmode.XKCD;
import com.abukh.xkcdtextmode.adapters.ComicsHistoryAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "FavoritesFragment";

    private RecyclerView rvComicsHistory;
    protected ComicsHistoryAdapter adapter;

    protected List<XKCD> allComics;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        return inflater.inflate(R.layout.fragment_favorites, container, false);
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
        queryComicsFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        queryComicsFavorites();
    }

    private void queryComicsFavorites() {

        ParseQuery<XKCD> query = ParseQuery.getQuery("XKCD");
        query.addDescendingOrder("createdAt"); // order the objects according to createdAt column value
        query.whereEqualTo(XKCD.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(XKCD.KEY_IS_FAVORITE, "1");

        query.findInBackground(new FindCallback<XKCD>() {

            @Override
            public void done(List<XKCD> comics, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                if (comics.isEmpty()) {
                    Toast.makeText(getContext(), "No favorites yet!", Toast.LENGTH_SHORT).show();
                }
                for (XKCD comic : comics) {
                    Log.v(TAG, comic.getComicTitle() + " " + comic.getComicNum());
                }

                adapter.clear();
                adapter.addAll(comics);
            }
        });
    }
}