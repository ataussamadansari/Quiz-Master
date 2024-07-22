package com.example.quizmaster.Leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizmaster.Login_Signup.UserModel;
import com.example.quizmaster.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.viewHolder> {
    Context context;
    ArrayList<UserModel> userModels;

    public LeaderAdapter(Context context, ArrayList<UserModel> userModels) {
        this.context = context;
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public LeaderAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.learder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderAdapter.viewHolder holder, int position) {
        UserModel model = userModels.get(position);
        holder.userRank.setText(String.valueOf(model.getRank()));
        holder.userName.setText(model.getName());
        holder.userPoints.setText(String.valueOf(model.getPoints()));
        Glide.with(context).load(model.getProfile()).into(holder.userImg);
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }


    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView userRank, userName, userPoints;
        CircleImageView userImg;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImg);
            userName = itemView.findViewById(R.id.userName);
            userRank = itemView.findViewById(R.id.userRank);
            userPoints = itemView.findViewById(R.id.userPoints);
        }
    }
}
