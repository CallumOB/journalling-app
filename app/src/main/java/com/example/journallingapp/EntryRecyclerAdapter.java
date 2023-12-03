package com.example.journallingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*  class written following instructions from Android documentation and with reference to
    room demo from week 6
    - https://developer.android.com/develop/ui/views/layout/recyclerview#java
    - https://brightspace.tudublin.ie/d2l/le/content/286386/viewContent/2166893/View */
public class EntryRecyclerAdapter extends RecyclerView.Adapter<EntryRecyclerAdapter.ViewHolder> {

    interface OnClickListener {
        void onClick(Entry entry);
    }

    private final Context context;
    private List<Entry> entryList;
    private OnClickListener listener;

    public EntryRecyclerAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entryList = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    void updateData(List<Entry> entries) {
        this.entryList = entries;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView entryTitle;
        private final TextView entryLocation;
        private final TextView entryDate;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener((View.OnClickListener) this);
            entryTitle = itemView.findViewById(R.id.entryTitle);
            entryLocation = itemView.findViewById(R.id.entryLocation);
            entryDate = itemView.findViewById(R.id.entryDate);
        }

        void bindData(int position) {
            Entry entry = entryList.get(position);
            entryTitle.setText(entry.getName());
            entryLocation.setText(entry.getLocation());
            entryDate.setText(entry.getDate());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (listener != null) {
                Entry entry = entryList.get(position);
                listener.onClick(entry);
            }
        }
    }

    void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }
}
