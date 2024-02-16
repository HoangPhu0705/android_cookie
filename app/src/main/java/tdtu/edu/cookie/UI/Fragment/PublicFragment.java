package tdtu.edu.cookie.UI.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Adapter.AdapterPublicTopics;
import tdtu.edu.cookie.databinding.FragmentPublicBinding;
import tdtu.edu.cookie.databinding.FragmentSettingBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentPublicBinding binding;

    AdapterPublicTopics adapter;

    RecyclerView recyclerView;

    List<Topic> topicList;



    public PublicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PublicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublicFragment newInstance(String param1, String param2) {
        PublicFragment fragment = new PublicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPublicBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);


        recyclerView = binding.recyclerView;
        topicList = new ArrayList<>();

        // Call getPublicTopics and update UI in the onComplete block
        getPublicTopics(topicList);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


    }


    private void getPublicTopics(List<Topic> topicList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicsRef = db.collection("Topics");

        Query query = topicsRef.whereEqualTo("is_public", true);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Topic topic = new Topic(Integer.parseInt(document.getId()), String.valueOf(document.get("title")), true, document.getString("user_id"), document.getString("email"));
                    topicList.add(topic);
                }

                adapter = new AdapterPublicTopics(getContext(), topicList);
                recyclerView.setAdapter(adapter);


            } else {
                // Handle the error case
                Log.e("PUBLIC FRAGMENT", "Error getting public topics: ", task.getException());
            }
        });
    }






}