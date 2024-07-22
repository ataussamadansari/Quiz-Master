package com.example.quizmaster.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.quizmaster.Quiz.QuizActivity;
import com.example.quizmaster.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewHolder> {

    Context context;
    ArrayList<CategoryModel> categoryModels = new ArrayList<>();

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.cat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.viewHolder holder, int position) {
        CategoryModel model = categoryModels.get(position);
        holder.catName.setText(model.getCategoryName());
        Glide.with(context).load(model.getCategoryImage()).into(holder.catImage);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("categoryId", model.getCategoryId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        TextView catName, catQue;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.catImg);
            catName = itemView.findViewById(R.id.catName);
            catQue = itemView.findViewById(R.id.catQue);
        }
    }
}
