package com.libgdx.ahmed_fathy.socialapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.libgdx.ahmed_fathy.socialapp.Activities.PostDetails;
import com.libgdx.ahmed_fathy.socialapp.Models.Post;
import com.libgdx.ahmed_fathy.socialapp.R;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item , viewGroup , false);
        MyViewHolder myViewHolder = new MyViewHolder(row);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.postTitle.setText(mData.get(i).getTitle());
        Glide.with(mContext).load(mData.get(i).getUserPhoto()).into(holder.postUserImage);
        Glide.with(mContext).load(mData.get(i).getPicture()).into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView postTitle;
        ImageView postImage , postUserImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            postUserImage = itemView.findViewById(R.id.post_user_image);
            postTitle = itemView.findViewById(R.id.post_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext , PostDetails.class);
                    int position = getAdapterPosition();

                    intent.putExtra("title" , mData.get(position).getTitle());
                    intent.putExtra("description" , mData.get(position).getDescription());
                    intent.putExtra("postImage" , mData.get(position).getPicture());
                    intent.putExtra("postKey" , mData.get(position).getPostKey());
                    intent.putExtra("userPhoto" , mData.get(position).getUserPhoto());
                    //intent.putExtra("userName" , mData.get(position).getUserName());
                    long timeStamp = (long) mData.get(position).getTimeStamp();
                    intent.putExtra("postDate" , timeStamp);
                    mContext.startActivity(intent);


                }
            });

        }
    }
}
