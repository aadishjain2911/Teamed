package com.example.myblog;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment {

    private RecyclerView bookmarksListView ;
    private List<BookmarksPost> bookmarks_list ;

    private FirebaseFirestore firebaseFirestore ;
    private BookmarksRecyclerAdapter bookmarksRecyclerAdapter ;

    private FirebaseAuth firebaseAuth ;

    private DocumentSnapshot lastVisible ;

    private Context context ;

    private Boolean isFirstPageFirstLoad = true ;
    public BookmarksFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context ;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity()!=null) {

            bookmarks_list = new ArrayList<>() ;

            bookmarksListView = view.findViewById(R.id.bookmarks_list_view) ;

            bookmarksRecyclerAdapter = new BookmarksRecyclerAdapter(bookmarks_list,context) ;
            bookmarksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            bookmarksListView.setAdapter(bookmarksRecyclerAdapter);
            firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.getCurrentUser() != null) {

                firebaseFirestore = FirebaseFirestore.getInstance();

                final String currentUserId = firebaseAuth.getCurrentUser().getUid() ;

                bookmarksListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        Boolean reachedBottom = !recyclerView.canScrollVertically(1) ;

                        if (reachedBottom) {

                            loadMorePosts(currentUserId);

                        }
                    }
                });



                Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(10) ;

                if (firstQuery != null) {
                    firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (e==null) {

                                if (isFirstPageFirstLoad) {

                                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                                }
                                if (queryDocumentSnapshots != null) {

                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                        if (doc.getType() == DocumentChange.Type.ADDED) {

                                            String bookmarksPostId = doc.getDocument().getId();
                                            final BookmarksPost bookmarksPost = doc.getDocument().toObject(BookmarksPost.class).withId(bookmarksPostId);

                                            firebaseFirestore.collection("Users/" + currentUserId + "/Bookmarks").document(bookmarksPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    if (task.isSuccessful()) {
                                                        if (task.getResult().exists()) {

                                                            if (isFirstPageFirstLoad) {

                                                                bookmarks_list.add(bookmarksPost);

                                                            } else {

                                                                bookmarks_list.add(0, bookmarksPost);

                                                            }

                                                            bookmarksRecyclerAdapter.notifyDataSetChanged();
                                                        }
                                                    } else {

                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, "Error : " + error, Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    }
                                }

                                isFirstPageFirstLoad = false;
                            }
                        }
                    });
                }
            }

        }

    }

    public void loadMorePosts (final String currentUserId) {

        Query nextQuery = firebaseFirestore.collection("Posts").
                orderBy("timestamp", Query.Direction.DESCENDING).
                startAfter(lastVisible).
                limit(10) ;
        if (nextQuery != null) {
            nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String bookmarksPostId = doc.getDocument().getId() ;
                                final BookmarksPost bookmarksPost = doc.getDocument().toObject(BookmarksPost.class).withId(bookmarksPostId) ;

                                firebaseFirestore.collection("Users/"+currentUserId+"/Bookmarks").document(bookmarksPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.getResult().exists()) {

                                            if (isFirstPageFirstLoad) {

                                                bookmarks_list.add(bookmarksPost);

                                            } else {

                                                bookmarks_list.add(0, bookmarksPost);

                                            }

                                            bookmarksRecyclerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

}
