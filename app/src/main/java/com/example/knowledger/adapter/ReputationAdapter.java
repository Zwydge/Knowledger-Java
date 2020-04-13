package com.example.knowledger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.knowledger.R;
import com.example.knowledger.entities.Reputation;

import java.util.List;

public class ReputationAdapter extends RecyclerView.Adapter<ReputationAdapter.ReputationViewHolder> {

    private List<Reputation> listeReputation;

    public static class ReputationViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView reputation;
        ReputationViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ans_content);
            reputation = itemView.findViewById(R.id.nb_vote);
        }
    }

    public ReputationAdapter(List<Reputation> listeReputation) {
        this.listeReputation = listeReputation;
    }

    @Override
    public ReputationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reputation_list, parent, false);
        ReputationViewHolder reputationViewHolder = new ReputationViewHolder(view);
        return reputationViewHolder;
    }

    @Override
    public void onBindViewHolder(ReputationViewHolder holder, int position) {
        Reputation rep = listeReputation.get(position);
        holder.name.setText(rep.getCategory());
        String rep_text = String.valueOf(rep.getValue());
        holder.reputation.setText(rep_text);
    }

    @Override
    public int getItemCount() {
        return listeReputation.size();
    }
}
