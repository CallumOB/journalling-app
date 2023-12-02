package com.example.journallingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*  class written following instructions from Android documentation and with reference to
    room demo from week 6
    - https://developer.android.com/develop/ui/views/layout/recyclerview#java
    - https://brightspace.tudublin.ie/d2l/le/content/286386/viewContent/2166893/View */
public class EntryRecyclerAdapter extends RecyclerView.Adapter<EntryRecyclerAdapter.ViewHolder> {

    /* Information about OnClickListener referenced from StackOverflow.
    * https://stackoverflow.com/questions/24471109/recyclerview-onclick?page=1&tab=scoredesc#tab-top
    * */
    interface OnClickListener {
        void onClick(Entry entry);
    }

    private Context context;
    private List<Entry> entryList;
    private OnClickListener listener;

    public void addOnClickListener(OnClickListener listener) {this.listener = listener;}

    public EntryRecyclerAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entryList = entries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        private TextView entryTitle;
        private TextView entryLocation;
        private TextView entryDate;

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
