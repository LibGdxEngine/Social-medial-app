package com.libgdx.ahmed_fathy.socialapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.libgdx.ahmed_fathy.socialapp.Models.Comment;
import com.libgdx.ahmed_fathy.socialapp.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ComentViewHolder> {

    Context mContext;
    List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ComentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_comment_item , viewGroup ,false);
        return new ComentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentViewHolder VH, int i) {
        VH.userName.setText(mData.get(i).getUserName());
        VH.commentContent.setText(mData.get(i).getContent());
        VH.commentDate.setText(timeStampToString((long) mData.get(i).getTiemstamp()));
        Glide.with(mContext).load(mData.get(i).getUserImage()).into(VH.userImage);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ComentViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName , commentContent , commentDate;

        public ComentViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.comment_user_image);
            userName = itemView.findViewById(R.id.comment_user_name);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentDate = itemView.findViewById(R.id.comment_date);
        }
    }

    private String timeStampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy" , calendar).toString();
        return date;
    }

}
