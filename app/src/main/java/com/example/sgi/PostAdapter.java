package com.example.sgi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {


    private Context mContext ;
    private List<Post> mData ;
    private boolean mprocesslike = false;
    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new MyViewHolder(row);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.postUserName.setText(mData.get(position).getUsername());
        holder.postDescription.setText(mData.get(position).getDescription());
        holder.timeStamp.setText(""+ mData.get(position).getTimeStamp());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.postImage);
        //Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.userimg);
        StorageReference profileRef = storageReference.child("users/"+mData.get(position).getUserId()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.userimg);
            }
        });
        final String postId = mData.get(position).getPostKey();

        final DatabaseReference mdatabaselike = FirebaseDatabase.getInstance().getReference().child("likes");
        mdatabaselike.keepSynced(true);
        setLikeBtn(postId,userId,mdatabaselike,holder);

        if (Objects.equals(FirebaseAuth.getInstance().getUid(), mData.get(position).getUserId())){
            holder.delete.setVisibility(View.VISIBLE);

        }

        final String postUrl = mData.get(position).getPicture();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(v.getContext(),postId,mdatabaselike,postUrl);

            }
        });








        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprocesslike = true;

                mdatabaselike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mprocesslike){
                            if (dataSnapshot.child(postId).hasChild(userId)){
                                mdatabaselike.child(postId).child(userId).removeValue();
                                holder.likeImage.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                holder.likes.setText(dataSnapshot.child(postId).getChildrenCount()+ " Likes");
                                mprocesslike = false;
                            }
                            else {
                                mdatabaselike.child(postId).child(userId).setValue("liked");
                                holder.likeImage.setImageResource(R.drawable.ic_favorite_red_24dp);
                                holder.likes.setText(dataSnapshot.child(postId).getChildrenCount()+ " Likes");
                                mprocesslike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }



        });



    }

    private void showAlertDialog(final Context context, final String postId, final DatabaseReference mdatabaselike, final String postUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to delete this post?");
        builder.setTitle("Delete Post");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
                reference.child(postId).removeValue();
                mdatabaselike.child(postId).removeValue();
                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if (dataSnapshot.child(postId).child("picture").exists()){

                                  StorageReference postImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(postUrl);
                                  postImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          Toast.makeText(context,"Post Deleted",Toast.LENGTH_SHORT).show();



                                      }
                                  }).addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                      }
                                  });


                              }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }



    private void setLikeBtn(final String postId, final String userId, DatabaseReference mdatabaselike, final MyViewHolder holder) {

        mdatabaselike.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(userId)){
                    holder.likeImage.setImageResource(R.drawable.ic_favorite_red_24dp);

                }
                else {
                    holder.likeImage.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                }
                if (dataSnapshot.child(postId).getChildrenCount() > 0){
                    holder.likes.setText(dataSnapshot.child(postId).getChildrenCount()+ " Likes");

                }
                else {
                    holder.likes.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView postUserName;
        TextView postDescription;
        TextView timeStamp;
        TextView likes;
        TextView delete;
        ImageView postImage,likeImage;
        CircleImageView userimg;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.post_item_userimg);
            postUserName = itemView.findViewById(R.id.post_item_username);
            postDescription = itemView.findViewById(R.id.post_item_description);
            postImage = itemView.findViewById(R.id.post_item_image);
            timeStamp = itemView.findViewById(R.id.post_date_time);
            likes = itemView.findViewById(R.id.like_count);
            likeImage = itemView.findViewById(R.id.likeImage);
            delete = itemView.findViewById(R.id.delete_btn);




        }
    }
}
