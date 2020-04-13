package com.example.knowledger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.knowledger.R;
import com.example.knowledger.entities.Question;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> listeQuestion;

    public static class QuestionViewHolder extends RecyclerView.ViewHolder{
        TextView category;
        TextView content;
        TextView id;
        QuestionViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.nb_vote);
            content = itemView.findViewById(R.id.ans_content);
            id = itemView.findViewById(R.id.id_quest);
        }
    }

    public QuestionAdapter(List<Question> listeQuestion) {
        this.listeQuestion = listeQuestion;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_list, parent, false);
        QuestionViewHolder questionViewHolder = new QuestionViewHolder(view);
        return questionViewHolder;
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        Question quest = listeQuestion.get(position);
        holder.category.setText(quest.getCatName());
        holder.content.setText(quest.getContent());
        String the_id;
        the_id = String.valueOf(quest.getId());
        holder.id.setText(the_id);
    }

    public Question getQuestion(int position){
        return this.listeQuestion.get(position);
    }

    @Override
    public int getItemCount() {
        return listeQuestion.size();
    }
}
