package com.example.myblog;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifFragment extends Fragment {

    private RecyclerView notifListView ;
    private List<NotifPost> notif_list ;

    private FirebaseFirestore firebaseFirestore ;
    private NotifRecyclerAdapter notifRecyclerAdapter ;

    private FirebaseAuth firebaseAuth ;

    private DocumentSnapshot lastVisible ;

    private Context context ;

    private Boolean isFirstPageFirstLoad = true ;

    private String currentUserId ;

    public NotifFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notif, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity()!=null) {

            notif_list = new ArrayList<>() ;

            notifListView = view.findViewById(R.id.notif_list_view) ;

            notifRecyclerAdapter = new NotifRecyclerAdapter(notif_list,context) ;
            notifListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            notifListView.setAdapter(notifRecyclerAdapter);

            firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.getCurrentUser() != null) {

                firebaseFirestore = FirebaseFirestore.getInstance();

                currentUserId = firebaseAuth.getCurrentUser().getUid() ;

                notifListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        Boolean reachedBottom = !recyclerView.canScrollVertically(1) ;

                        if (reachedBottom) {

                            loadMoreNotifs();

                        }
                    }
                });



                Query firstQuery = firebaseFirestore.collection("Users/"+currentUserId+"/ContactsInvites").orderBy("timestamp", Query.Direction.DESCENDING).limit(10) ;

                if (firstQuery != null) {
                    firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (isFirstPageFirstLoad && queryDocumentSnapshots.size()!=0) {

                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                            }
                            if (queryDocumentSnapshots != null) {

                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String notifPostId = doc.getDocument().getId() ;

                                        NotifPost notifPost = doc.getDocument().toObject(NotifPost.class).withId(notifPostId) ;

                                        if (isFirstPageFirstLoad) {

                                            notif_list.add(notifPost);

                                        } else {

                                            notif_list.add(0, notifPost);

                                        }

                                        notifRecyclerAdapter.notifyDataSetChanged();
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

    public void loadMoreNotifs () {

        Query nextQuery = firebaseFirestore.collection("Users/"+currentUserId+"/ContactsInvites")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(5) ;
        if (nextQuery != null) {
            nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String notifPostId = doc.getDocument().getId() ;

                                NotifPost notifPost = doc.getDocument().toObject(NotifPost.class).withId(notifPostId) ;
                                notif_list.add(notifPost);

                                notifRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }

}
