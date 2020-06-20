package com.example.myblog;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.LifecycleListener;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list ;

    private FirebaseFirestore firebaseFirestore ;
    private FirebaseAuth firebaseAuth ;

    private Context context ;

    private CircleImageView blogUserImage ;

    public BlogRecyclerAdapter(List<BlogPost> blog_list,Context context) {

        this.blog_list = blog_list ;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        firebaseFirestore = FirebaseFirestore.getInstance() ;
        firebaseAuth = FirebaseAuth.getInstance() ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false) ;

        final String blogPostId = blog_list.get(position).BlogPostId ;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid() ;

        String desc_data = blog_list.get(position).getDescription() ;
        holder.setDescText(desc_data) ;

        final String name_data = blog_list.get(position).getName() ;
        holder.setNameText(name_data);

        final String user_id = blog_list.get(position).getUser_id() ;


        if (currentUserId != null) {

            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        String username = task.getResult().getString("name");
                        holder.setUsername(username);
                        String image = task.getResult().getString("image");
                        holder.setUserImage(image) ;

                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(context, " Error : " + error, Toast.LENGTH_SHORT).show();

                    }
                }
            });

            long milliseconds = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
            holder.setTime(dateString);

            holder.contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (currentUserId == user_id) { Toast.makeText(context,"This post has been added by you.",Toast.LENGTH_SHORT).show(); }

                    else {

                        holder.contact.setText("CONTACTED");
                        holder.contact.setBackgroundColor(Color.LTGRAY);

                        firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    Map<String, Object> notifMap = new HashMap<>();
                                    String name = task.getResult().getString("name");
                                    String image = task.getResult().getString("image");
                                    notifMap.put("sender_image", image);
                                    notifMap.put("blogPostId", blogPostId);
                                    notifMap.put("sender_name", name);
                                    notifMap.put("notif_type", "contacted");
                                    notifMap.put("timestamp", FieldValue.serverTimestamp());
                                    notifMap.put("event_name", name_data);
                                    firebaseFirestore.collection("Users/" + user_id + "/ContactsInvites").add(notifMap);
                                }
                            }
                        });
                    }
                }
            });

            holder.invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (currentUserId == user_id) { Toast.makeText(context,"This post has been added by you.",Toast.LENGTH_SHORT).show(); }

                    else {

                        Map<String, Object> invitesMap = new HashMap<>();
                        invitesMap.put("timestamp", FieldValue.serverTimestamp());

                        holder.invite.setText("INVITED");
                        holder.invite.setBackgroundColor(Color.LTGRAY);

                        firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    Map<String, Object> notifMap = new HashMap<>();
                                    String name = task.getResult().getString("name");
                                    String image = task.getResult().getString("image");
                                    notifMap.put("sender_image", image);
                                    notifMap.put("blogPostId", blogPostId);
                                    notifMap.put("sender_name", name);
                                    notifMap.put("notif_type", "invited");
                                    notifMap.put("timestamp", FieldValue.serverTimestamp());
                                    notifMap.put("event_name", name_data);
                                    firebaseFirestore.collection("Users/" + user_id + "/ContactsInvites").add(notifMap);
                                }
                            }
                        });
                    }
                }
            });

            firebaseFirestore.collection("Users").document(currentUserId).collection("Bookmarks").document(blogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {

                            holder.bookmark_image.setImageResource(R.mipmap.action_bookmark_filled);

                        } else {

                            holder.bookmark_image.setImageResource(R.mipmap.action_bookmark_border);
                        }
                    }
                    else {

                        String error = task.getException().getMessage() ;
                        Toast.makeText(context,"Error : " +error,Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (context!=null) {

                holder.bookmark_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        firebaseFirestore.collection("Users/" + currentUserId + "/Bookmarks").document(blogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (!task.getResult().exists()) {

                                    Map<String, Object> bookmarksMap = new HashMap<>();

                                    bookmarksMap.put("timestamp", FieldValue.serverTimestamp());
                                    firebaseFirestore.collection("Users/" + currentUserId + "/Bookmarks").document(blogPostId).set(bookmarksMap);

                                    holder.bookmark_image.setImageResource(R.mipmap.action_bookmark_filled);

                                    Toast.makeText(context, "Added to your bookmarks.", Toast.LENGTH_SHORT).show();

                                } else {

                                    holder.bookmark_image.setImageResource(R.mipmap.action_bookmark_border);

                                    firebaseFirestore.collection("Users/" + currentUserId + "/Bookmarks").document(blogPostId).delete();
                                    Toast.makeText(context, "Removed from your bookmarks.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    }
                });
            }
        }
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



        private View mView ;

        private Button contact, invite ;

        private ImageView bookmark_image ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView ;

            blogUserImage = mView.findViewById(R.id.profile_image) ;
            contact = mView.findViewById(R.id.button_contact) ;
            invite = mView.findViewById(R.id.button_invite) ;
            bookmark_image = mView.findViewById(R.id.image_bookmark) ;

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

            RequestOptions placeholderoption = new RequestOptions() ;
            placeholderoption.placeholder(R.drawable.kindpng_4517876) ;

            if (context!=null) Glide.with(context).applyDefaultRequestOptions(placeholderoption).load(image).into(blogUserImage);

        }
    }

}
