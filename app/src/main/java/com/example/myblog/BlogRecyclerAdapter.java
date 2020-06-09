package com.example.myblog;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list ;

    private FirebaseFirestore firebaseFirestore ;

    private Context context;

    public BlogRecyclerAdapter(Context context){
        this.context=context;
    }

    public BlogRecyclerAdapter(List<BlogPost> blog_list) {

        this.blog_list = blog_list ;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        firebaseFirestore = FirebaseFirestore.getInstance() ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String desc_data = blog_list.get(position).getDescription() ;
        holder.setDescText(desc_data) ;

        String name_data = blog_list.get(position).getName() ;
        holder.setNameText(name_data);

        String user_id = blog_list.get(position).getUser_id() ;


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (context != null) {
                        String username = task.getResult().getString("name");
                        holder.setUsername(username);
                        String image = task.getResult().getString("image");
                        holder.setUserImage(image);
                    }
                }
                else {
                    String error = task.getException().getMessage() ;
                    Toast.makeText(context, " Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        long milliseconds = blog_list.get(position).getTimestamp().getTime() ;
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString) ;

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView desc_view ;

        private TextView username_view ;

        private TextView name_view ;

        private TextView date_view ;

        private CircleImageView blogUserImage ;

        private View mView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView ;
        }

        public void setDescText(String text) {

            desc_view = mView.findViewById(R.id.description) ;
            desc_view.setText(text) ;

        }

        public void setUsername(String username) {

            username_view = mView.findViewById(R.id.username) ;
            username_view.setText(username) ;

        }

        public void setNameText(String event_name) {

            name_view = mView.findViewById(R.id.event_name) ;
            name_view.setText(event_name) ;

        }

        public void setTime(String date) {

            date_view = mView.findViewById(R.id.date) ;
            date_view.setText(date) ;

        }

        public void setUserImage(String image) {

            blogUserImage = mView.findViewById(R.id.profile_image) ;

            RequestOptions placeholderoption = new RequestOptions() ;
            placeholderoption.placeholder(R.drawable.kindpng_4517876) ;
            Glide.with(context).applyDefaultRequestOptions(placeholderoption).load(image).into(blogUserImage) ;

        }
    }

}
