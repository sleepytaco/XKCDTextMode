package com.abukh.xkcdtextmode.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abukh.xkcdtextmode.ComicDisplayActivity;
import com.abukh.xkcdtextmode.R;
import com.abukh.xkcdtextmode.XKCD;
import com.abukh.xkcdtextmode.XKCDComic;
import com.abukh.xkcdtextmode.fragments.HistoryFragment;
import com.abukh.xkcdtextmode.fragments.HomeFragment;
import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ComicsHistoryAdapter extends RecyclerView.Adapter<ComicsHistoryAdapter.ViewHolder>{

    private Context context;
    private List<XKCD> comics_history;

    public ComicsHistoryAdapter(Context context, List<XKCD> comics_history) {
        this.context = context;
        this.comics_history = comics_history;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comic_history_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the post located at the position
        XKCD comic = comics_history.get(position);

        // bind the data from post onto the viewholder
        holder.bind(comic);
    }

    @Override
    public int getItemCount() {
        return comics_history.size();
    }

    // Clean all elements of the recycler

    public void clear() {
        Log.v("Adapter", "Cleared");
        comics_history.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<XKCD> list) {
        comics_history.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView row;
        private RelativeLayout container;
        private XKCDComic comic = null;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.tvRow);
            container = itemView.findViewById(R.id.history_container);
        }

        // Binds the data to the view elements
        public void bind(XKCD comic) {
            row.setText(comic.getComicTitle());

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ComicDisplayActivity.class);
                    intent.putExtra("comic_num", comic.getComicNum());
                    context.startActivity(intent);
                }
            });
        }
    }

}
