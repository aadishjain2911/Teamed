package com.example.myblog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchingActivity extends AppCompatActivity {

    public List<Users> usersList ;

    private RecyclerView search_result ;

    private CircleImageView search_button ;

    private EditText search_text ;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth ;

    private FirebaseRecyclerOptions<Users> firebaseRecyclerOptions ;

    private FirebaseRecyclerAdapter adapter ;

    private SearchRecyclerAdapter searchRecyclerAdapter ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searching) ;

        usersList = new ArrayList<>() ;

        search_button = (CircleImageView) findViewById(R.id.search_button) ;
        search_text = (EditText) findViewById(R.id.search_field) ;
        search_result = (RecyclerView) findViewById(R.id.search_recycler) ;

        firebaseFirestore = FirebaseFirestore.getInstance() ;
        firebaseAuth = FirebaseAuth.getInstance() ;

        searchRecyclerAdapter = new SearchRecyclerAdapter(usersList,getApplicationContext()) ;

        search_result.setHasFixedSize(true);
        search_result.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        search_result.setAdapter(searchRecyclerAdapter) ;

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usersList.clear();
                searchRecyclerAdapter.notifyDataSetChanged();

                String searchText = search_text.getText().toString() ;

                Query searchQuery ;

                searchQuery = firebaseFirestore.collection("Users").orderBy("name").startAt(searchText).endAt(searchText+"\uf8ff");

                if (searchQuery != null) {

                    searchQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (!queryDocumentSnapshots.isEmpty()) {

                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        Users users = doc.getDocument().toObject(Users.class) ;

                                        usersList.add(users) ;

                                        searchRecyclerAdapter.notifyDataSetChanged();
                                    }
                                }

                            }

                            else {

                                Toast.makeText(SearchingActivity.this, "No results found.", Toast.LENGTH_SHORT).show();
                                
                            }
                        }
                    });

                }
            }
        });
    }
}
