package com.example.sihuserapp.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sihuserapp.Objects.PingResponse;
import com.example.sihuserapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.NoticeViewHolder> {
    private ArrayList<PingResponse> list;

    public ListAdapter(ArrayList<PingResponse> body) {
        list = body;
    }

    @Override
    @NonNull
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.query_card, parent, false);
        return new NoticeViewHolder(v);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public void onBindViewHolder(@NonNull final NoticeViewHolder holder, final int position) {
        Date date = new Date(list.get(position).getTimestamp().getTime() * 1000);
        @SuppressLint("SimpleDateFormat") String dateString = new SimpleDateFormat("dd-MM-yy").format(date);
        holder.time.setText(dateString);
        holder.queryText.setText(list.get(position).getText());
        holder.status.setText(list.get(position).getStatusChoice());
    }

    class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView queryText;
        TextView time;
        TextView status;

        NoticeViewHolder(View parent) {
            super(parent);
            this.queryText = parent.findViewById(R.id.query_text);
            this.time = parent.findViewById(R.id.date);
            this.status = parent.findViewById(R.id.status);
        }
    }
}
