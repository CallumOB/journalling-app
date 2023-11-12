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
        void onClick(View view);
    }

    private Context context;
    private List<Entry> entry_list;
    private OnClickListener listener;

    public void addOnClickListener(OnClickListener listener) {this.listener = listener;}

    public EntryRecyclerAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entry_list = entries;
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
        return entry_list.size();
    }

    void updateData(List<Entry> entries) {
        this.entry_list = entries;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView entry_title;
        private TextView entry_location;
        private TextView entry_date;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener((View.OnClickListener) this);
            entry_title = itemView.findViewById(R.id.entryTitle);
            entry_location = itemView.findViewById(R.id.entryLocation);
            entry_date = itemView.findViewById(R.id.entryDate);
        }

        void bindData(int position) {
            Entry entry = entry_list.get(position);
            entry_title.setText(entry.getName());
            entry_location.setText(entry.getLocation());
            entry_date.setText(entry.getDate().toString());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (listener != null) {
            // add functionality here for new intent and extras to view the given entry
            }
        }
    }
}
