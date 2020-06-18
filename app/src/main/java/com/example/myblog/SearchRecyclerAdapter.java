package com.example.myblog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.UsersViewHolder> {

    public List<Users> usersList ;

    private FirebaseFirestore firebaseFirestore ;
    private FirebaseAuth firebaseAuth ;

    private Context context ;

    public SearchRecyclerAdapter(List<Users> users_list,Context context) {

        this.usersList = users_list ;
        this.context = context;

    }

    @NonNull
    @Override
    public SearchRecyclerAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_layout, parent, false);

        firebaseAuth = FirebaseAuth.getInstance() ;

        return new UsersViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerAdapter.UsersViewHolder holder, int position) {

        if (firebaseAuth.getCurrentUser()!=null) {

            holder.setDetails(
                    usersList.get(position).getName(),
                    usersList.get(position).getBranch(),
                    usersList.get(position).getYear(),
                    usersList.get(position).getImage()
            );
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size() ;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        private View mView ;

        private TextView search_name,search_year,search_branch ;

        private CircleImageView search_image ;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView ;

        }

        public void setDetails(String name,String branch,String year,String image) {

            search_name = (TextView) mView.findViewById(R.id.search_name) ;
            search_year = (TextView) mView.findViewById(R.id.search_year) ;
            search_branch = (TextView) mView.findViewById(R.id.search_branch) ;
            search_image = (CircleImageView) mView.findViewById(R.id.search_profile_image) ;

            search_name.setText(name) ;
            search_branch.setText(branch) ;
            search_year.setText(year+" Year") ;

            RequestOptions placeholderoption = new RequestOptions() ;
            placeholderoption.placeholder(R.drawable.kindpng_4517876) ;

            Glide.with(context).applyDefaultRequestOptions(placeholderoption).load(image).into(search_image);

        }
    }
}
