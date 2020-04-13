package com.example.knowledger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.knowledger.R;
import com.example.knowledger.entities.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    List<Category> listeCategory;

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView member;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ans_content);
            member = itemView.findViewById(R.id.nb_vote);
        }
    }

    public CategoryAdapter(List<Category> listeCategory) {
        this.listeCategory = listeCategory;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(view);
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category ingredient = listeCategory.get(position);
        holder.name.setText(ingredient.getName());
        holder.member.setText(ingredient.getMember());
    }

    @Override
    public int getItemCount() {
        return listeCategory.size();
    }
}
