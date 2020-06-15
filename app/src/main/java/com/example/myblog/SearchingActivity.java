package com.example.myblog;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchingActivity extends AppCompatActivity {

    public List<Users> usersList ;

    private RecyclerView search_result ;

    private CircleImageView search_button ;

    private EditText search_text ;

    private DatabaseReference databaseReference ;
    private FirebaseAuth firebaseAuth ;

    private FirebaseRecyclerOptions<Users> firebaseRecyclerOptions ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searching) ;

        usersList = new ArrayList<>() ;

        search_button = (CircleImageView) findViewById(R.id.search_button) ;
        search_text = (EditText) findViewById(R.id.search_field) ;
        search_result = (RecyclerView) findViewById(R.id.search_recycler) ;

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance() ;
        search_result.setHasFixedSize(true);
        search_result.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchText = search_text.getText().toString() ;

                UserSearch(searchText) ;

            }
        });
    }

    private void UserSearch(String searchText) {

        Query searchQuery = databaseReference.orderByChild("name").startAt(searchText).endAt(searchText+"\uf8ff");

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Users>().setQuery(searchQuery,Users.class).build() ;

        FirebaseRecyclerAdapter <Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {

                if (firebaseAuth.getCurrentUser()!=null) {

                    holder.setDetails(model.getName(), model.getBranch(), model.getYear(), model.getImage());
                }
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return null;
            }
        };

        search_result.setAdapter(firebaseRecyclerAdapter) ;

    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView ;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView ;

        }

        public void setDetails(String name,String branch,String year,String image) {

            TextView search_name = (TextView) mView.findViewById(R.id.search_name) ;
            TextView search_year = (TextView) mView.findViewById(R.id.search_year) ;
            TextView search_branch = (TextView) mView.findViewById(R.id.search_branch) ;
            CircleImageView search_image = (CircleImageView) mView.findViewById(R.id.search_profile_image) ;

            search_name.setText(name) ;
            search_branch.setText(branch) ;
            search_year.setText(year) ;

            RequestOptions placeholderoption = new RequestOptions() ;
            placeholderoption.placeholder(R.drawable.kindpng_4517876) ;

            Glide.with(getApplicationContext()).applyDefaultRequestOptions(placeholderoption).load(image).into(search_image);

        }
    }
}
