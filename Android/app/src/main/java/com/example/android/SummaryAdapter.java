package com.example.android;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter {

    ArrayList<String> summaryModels;
    Context context;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    private int prePosition = -1;

    public SummaryAdapter(Context context, ArrayList<String> summaryModels){
        this.summaryModels = summaryModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_summary,parent,false);
        SummaryViewHolder viewHolder = new SummaryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SummaryViewHolder summaryViewHolder = (SummaryViewHolder) holder;

        summaryViewHolder.summaryTitle_text.setText(summaryModels.get(position));
        summaryViewHolder.summaryContent_text.setText(SummaryFragment.setSummaryContext(summaryModels.get(position)));

        summaryViewHolder.expand_button.setImageResource(selectedItems.get(position)?R.drawable.ic_expand_less : R.drawable.ic_expand_more);
        summaryViewHolder.summaryContextView.setVisibility(selectedItems.get(position)? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return summaryModels.size();
    }

    public class SummaryViewHolder extends RecyclerView.ViewHolder{

        ImageButton expand_button;

        LinearLayout summaryContextView;

        TextView summaryTitle_text;
        TextView summaryContent_text;

        ImageButton trash_button;


        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);

            expand_button = itemView.findViewById(R.id.expand_button);

            summaryContextView = itemView.findViewById(R.id.summaryContextView);

            summaryTitle_text = itemView.findViewById(R.id.summaryTitle_text);
            summaryContent_text = itemView.findViewById(R.id.summaryContent_text);

            trash_button = itemView.findViewById(R.id.trash_button);

            expand_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if(selectedItems.get(pos)) {
                        selectedItems.delete(pos);
                        //("here", "del");
                        //endtimeText.setText(String.valueOf(mediaPlayer.getDuration()));

                    } else {
                        selectedItems.delete(prePosition);
                        selectedItems.put(pos, true);
                        //Log.d("here","put");
                        //endtimeText.setText(String.valueOf(mediaPlayer.getDuration()));

                    }
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(pos);

                    prePosition = pos;
                }
            });

        }
    }
}
