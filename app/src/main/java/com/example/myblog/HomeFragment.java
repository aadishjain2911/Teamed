package com.example.myblog;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blogListView ;
    private List<BlogPost> blog_list ;

    private FirebaseFirestore firebaseFirestore ;
    private BlogRecyclerAdapter blogRecyclerAdapter ;

    private FirebaseAuth firebaseAuth ;

    private DocumentSnapshot lastVisible ;

    private Boolean isFirstPageFirstLoad = true ;

    private Context context ;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity()!=null) {

            blog_list = new ArrayList<>() ;

            blogListView = view.findViewById(R.id.blog_list_view) ;

            blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list,context) ;
            blogListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            blogListView.setAdapter(blogRecyclerAdapter);
            firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.getCurrentUser() != null) {

                firebaseFirestore = FirebaseFirestore.getInstance();

                blogListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        Boolean reachedBottom = !recyclerView.canScrollVertically(1) ;

                        if (reachedBottom) {

                            loadMorePosts();

                        }
                    }
                });


                Query firstQuery ;
                firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3) ;

                if (firstQuery != null) {

                    firstQuery.addSnapshotListener(
                                    new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (isFirstPageFirstLoad) {

                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                            }
                            if (queryDocumentSnapshots != null) {

                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String blogPostId = doc.getDocument().getId() ;

                                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId) ;

                                        if (isFirstPageFirstLoad) {

                                            blog_list.add(blogPost);

                                        } else {

                                            blog_list.add(0, blogPost);

                                        }

                                        blogRecyclerAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            isFirstPageFirstLoad = false ;

                        }

                    });
                }
            }

        }

    }

    public void loadMorePosts () {

        Query nextQuery = firebaseFirestore.collection("Posts").
                orderBy("timestamp", Query.Direction.DESCENDING).
                startAfter(lastVisible).
                limit(3) ;
        if (nextQuery != null) {
            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId() ;

                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId) ;
                                blog_list.add(blogPost);

                                blogRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }
}
