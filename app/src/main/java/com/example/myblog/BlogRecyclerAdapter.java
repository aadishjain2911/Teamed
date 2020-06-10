package com.example.myblog;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list ;

    private FirebaseFirestore firebaseFirestore ;
    private FirebaseAuth firebaseAuth ;

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
        firebaseAuth = FirebaseAuth.getInstance() ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false) ;

        final String blogPostId = blog_list.get(position).BlogPostId ;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid() ;

        holder.setView();

        String desc_data = blog_list.get(position).getDescription() ;
        holder.setDescText(desc_data) ;

        String name_data = blog_list.get(position).getName() ;
        holder.setNameText(name_data);

        final String user_id = blog_list.get(position).getUser_id() ;


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
        String dateString = DateFormat.format("dd/mm/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString) ;

        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> contactsMap = new HashMap<>() ;
                contactsMap.put("timestamp", FieldValue.serverTimestamp()) ;

                firebaseFirestore.collection("Posts/"+blogPostId+"/contacts").document(currentUserId).set(contactsMap) ;

                holder.con_text.setText("CONTACTED");
                holder.con_text.setTextColor(0xFFFFFF) ;

                firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Map<String, Object> notifMap = new HashMap<>() ;
                            String name = task.getResult().getString("name") ;
                            String image = task.getResult().getString("image");
                            notifMap.put("sender_image",image);
                            notifMap.put("blogPostId", blogPostId) ;
                            notifMap.put("sender_name", name) ;
                            notifMap.put("notif_type","contacted") ;
                            firebaseFirestore.collection("Users/"+user_id+"/Contacts_Invites").document(currentUserId).set(notifMap) ;
                        }
                    }
                }) ;
            }
        });

        holder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> invitesMap = new HashMap<>() ;
                invitesMap.put("timestamp", FieldValue.serverTimestamp()) ;

                firebaseFirestore.collection("Posts/"+blogPostId+"/contacts").document(currentUserId).set(invitesMap) ;

                holder.inv_text.setText("INVITED");
                holder.inv_text.setTextColor(0xFFFFFF);

                firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Map<String, Object> notifMap = new HashMap<>() ;
                            String name = task.getResult().getString("name") ;
                            String image = task.getResult().getString("image");
                            notifMap.put("sender_image",image);
                            notifMap.put("blogPostId", blogPostId) ;
                            notifMap.put("sender_name", name) ;
                            notifMap.put("notif_type","invited") ;
                            firebaseFirestore.collection("Users/"+user_id+"/Contacts_Invites").document(currentUserId).set(notifMap) ;
                        }
                    }
                }) ;
            }
        });
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

        private Button contact, invite ;

        private TextView con_text, inv_text ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView ;

            contact = mView.findViewById(R.id.button_contact) ;
            invite = mView.findViewById(R.id.button_invite) ;
            con_text = mView.findViewById(R.id.contact_text) ;
            inv_text = mView.findViewById(R.id.invite_text) ;

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

        public void setView() {

            con_text.setText("CONTACT");
            inv_text.setText("INVITE") ;

        }
    }

}
