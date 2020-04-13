package com.example.knowledger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.knowledger.R;
import com.example.knowledger.entities.Answer;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private List<Answer> listeAnswer;

    public static class AnswerViewHolder extends RecyclerView.ViewHolder{
        TextView answer;
        TextView vote;
        Button like;
        Button dislike;

        AnswerViewHolder(View itemView) {
            super(itemView);
            answer = itemView.findViewById(R.id.ans_content);
            vote = itemView.findViewById(R.id.nb_vote);
            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
        }
    }

    public AnswerAdapter(List<Answer> listeAnswer) {
        this.listeAnswer = listeAnswer;
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_list, parent, false);
        AnswerViewHolder answerViewHolder = new AnswerViewHolder(view);
        return answerViewHolder;
    }

    @Override
    public void onBindViewHolder(AnswerViewHolder holder, int position) {
        Answer ans = listeAnswer.get(position);
        holder.answer.setText(ans.getContent());
        String nbvote = String.valueOf(ans.getVote());
        holder.vote.setText(nbvote);

        String nblike = String.valueOf(ans.getLike());
        holder.like.setText(nblike);

        String nbdislike = String.valueOf(ans.getDislike());
        holder.dislike.setText(nbdislike);
    }

    public Answer getAnswer(int position){
        return this.listeAnswer.get(position);
    }

    @Override
    public int getItemCount() {
        return listeAnswer.size();
    }
}
