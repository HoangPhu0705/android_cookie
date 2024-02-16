package tdtu.edu.cookie.UI.Fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import tdtu.edu.cookie.Database.Entity.Folder;
import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Adapter.AdapterFolder;
import tdtu.edu.cookie.UI.Adapter.CallBackFireStore;
import tdtu.edu.cookie.UI.Adapter.FolderTouchHelper;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;
import tdtu.edu.cookie.databinding.FragmentHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    HashMap<Integer, List<Topic>> hashMapTopicInFolder = new HashMap<>();
    FragmentHomeBinding binding;
    ArrayList<Folder> folders = new ArrayList<>();
    private FirebaseFirestore db;
    private LoadingDialog loadingDialog;
    AdapterFolder adapterFolder;

    public TextView numOfFolder;
    String user_id;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(String user_id) {
        // Required empty public constructor
        this.user_id = user_id;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExtendedFloatingActionButton buttonCreateFolder = view.findViewById(R.id.buttonCreateFolder);
        RecyclerView recyclerViewFolder = view.findViewById(R.id.recyclerViewFolder);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        db = FirebaseFirestore.getInstance();
        loadingDialog = new LoadingDialog(requireContext());
        numOfFolder = view.findViewById(R.id.numOfFolder);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayFolderByUserId(new CallBackFireStore() {
                    @Override
                    public void onDataLoaded(List<?> data) {
                        numOfFolder.setText(String.valueOf(folders.size()));
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onDataLoadFailed(String errorMessage) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        if (folders.isEmpty()) {
            displayFolderByUserId(new CallBackFireStore() {
                @Override
                public void onDataLoaded(List<?> data) {
                    numOfFolder.setText(String.valueOf(folders.size()));
                }

                @Override
                public void onDataLoadFailed(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            numOfFolder.setText(String.valueOf(folders.size()));
        }

        adapterFolder = new AdapterFolder(getContext(), folders, hashMapTopicInFolder, this, user_id);

        recyclerViewFolder.setAdapter(adapterFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new FolderTouchHelper(adapterFolder));
        itemTouchHelper.attachToRecyclerView(recyclerViewFolder);

        buttonCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialogCreateFolder();
            }
        });
    }

    private void showBottomDialogCreateFolder() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_creat_folder);

        TextView addButtonFolder = dialog.findViewById(R.id.addButtonFolder);
        TextView cancelButtonFolder = dialog.findViewById(R.id.cancelButtonFolder);
        EditText editTextFolder = dialog.findViewById(R.id.editTextFolder);
        addButtonFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextFolder.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter folder name", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingDialog.show();
                int id = 1;
                if (!folders.isEmpty()) {
                    id = folders.get(folders.size() - 1).id + 1;
                }
                long currentTimestamp = System.currentTimeMillis();
                int folder_id = Math.abs((user_id + id + currentTimestamp).hashCode());

                addFolderToFireStore(folder_id, editTextFolder.getText().toString(), user_id);
                adapterFolder.addFolder(new Folder(folder_id, editTextFolder.getText().toString(), user_id));
                dialog.dismiss();
            }
        });


        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void addFolderToFireStore(int id, String title, String user_id) {
        HashMap<String, Object> folder = new HashMap<>();
        folder.put("id", id);
        folder.put("title", title);
        folder.put("user_id", user_id);
        db.collection("Folders")
                .document(String.valueOf(id))
                .set(folder)
                .addOnSuccessListener(aVoid -> {
                    numOfFolder.setText(String.valueOf(folders.size()));
                    loadingDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(requireContext(), "Add folder fail", Toast.LENGTH_SHORT).show();
                });
    }
    private void displayFolderByUserId(CallBackFireStore callBackFireStore) {
        loadingDialog.show();
        folders.clear();
        db.collection("Folders")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                        int id = Integer.parseInt(snapshot.get("id").toString());
                        String title = snapshot.get("title").toString();
                        String user_id = snapshot.get("user_id").toString();
                        folders.add(new Folder(id, title, user_id));
//                        adapterFolder.viewHolderFolder.get(id).textViewTopicCount.setText("0");
                    }
                    callBackFireStore.onDataLoaded(folders);
                    adapterFolder.notifyDataSetChanged();
                    loadingDialog.dismiss();
                }).addOnFailureListener(
                e -> {
                    loadingDialog.dismiss();
                    callBackFireStore.onDataLoadFailed(e.getMessage());
                    Toast.makeText(requireContext(), "Load folder fail", Toast.LENGTH_SHORT).show();
                }
        );
    }

}