package tdtu.edu.cookie.UI.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import tdtu.edu.cookie.Database.Entity.Folder;
import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Fragment.HomeFragment;
import tdtu.edu.cookie.UI.Adapter.TopicInFolderTouchHelper;

public class AdapterFolder extends RecyclerView.Adapter<AdapterFolder.ViewHolder> {

    ArrayList<Folder> folders = new ArrayList<>();
    ArrayList<Topic> topics = new ArrayList<>();
    HashMap<Integer, List<Topic>> hashMapTopicInFolder;
    HashMap<Integer, List<Words>> hashMapWordInTopic = new HashMap<>();

    AdapterTopics adapterTopics;
    private Context context;
    FirebaseFirestore db;
    Vibrator vibrator;
    HomeFragment homeFragment;
    String user_id;
    EditText textViewTitleFolder;

    public HashMap<Integer, AdapterFolder.ViewHolder> viewHolderFolder = new HashMap<>();



    public AdapterFolder(Context context, ArrayList<Folder> folders, HashMap<Integer, List<Topic>> hashMapTopicInFolder, HomeFragment homeFragment, String user_id) {
        this.context = context;
        this.folders = folders;
        this.hashMapTopicInFolder = hashMapTopicInFolder;
        this.homeFragment = homeFragment;
        this.user_id = user_id;
    }

    public void setHashMapTopicInFolder(HashMap<Integer, List<Topic>> hashMapTopicInFolder) {
        this.hashMapTopicInFolder = hashMapTopicInFolder;
    }

    public void addFolder(Folder folder) {
        folders.add(folder);
        notifyDataSetChanged();
    }

    public void updateFolder(Folder folder) {
        for (int i = 0; i < folders.size(); i++) {
            if (folders.get(i).getId() == folder.getId()) {
                folders.set(i, folder);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public AdapterFolder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folders, parent, false);
        return new AdapterFolder.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFolder.ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        Folder folder = folders.get(position);
        holder.textViewFolderName.setText(folder.title);

        viewHolderFolder.put(folder.id, holder);

        if (!hashMapTopicInFolder.containsKey(folder.id)) {
            AtomicInteger count = new AtomicInteger();
            db.collection("Folders").document(String.valueOf(folder.getId())).collection("Topics").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Topic> topicList = task.getResult().toObjects(Topic.class);
                            hashMapTopicInFolder.put(folder.getId(), topicList);
                            count.set(topicList.size());
                            holder.textViewTopicCount.setText(hashMapTopicInFolder.get(folder.id).size() + " topic");
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            holder.textViewTopicCount.setText(hashMapTopicInFolder.get(folder.id).size() + " topic");
        }
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        holder.cardViewFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(5);
                displayTopicByUserIdInFolder(folder);
                showBottomDialogFolder(folder, holder.textViewTopicCount);
            }
        });


    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFolderName;
        TextView textViewTopicCount;
        CardView cardViewFolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFolderName = itemView.findViewById(R.id.textViewFolderName);
            textViewTopicCount = itemView.findViewById(R.id.textViewTopicCount);
            cardViewFolder = itemView.findViewById(R.id.cardViewFolder);
        }

    }


    public void deleteFolderFromFireStore(int position) {
        Folder folder = folders.get(position);
        db.collection("Folders").document(String.valueOf(folder.getId())).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        folders.remove(position);
                        notifyDataSetChanged();
                        homeFragment.numOfFolder.setText(String.valueOf(folders.size()));
                    }
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Error deleting document", Toast.LENGTH_SHORT).show();
                        }
                );
    }

    private void showBottomDialogFolder(Folder folder, TextView textViewTopicCount) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_folder);

        textViewTitleFolder = dialog.findViewById(R.id.textViewTitleFolder);
        TextView textViewEditFolderName = dialog.findViewById(R.id.textViewEditFolderName);
        ImageView backIcon = dialog.findViewById(R.id.backIcon);
        textViewTitleFolder.setText(folder.getTitle());
        RecyclerView recyclerViewTopicWithFolder = dialog.findViewById(R.id.recyclerViewTopicWithFolder);

        adapterTopics = new AdapterTopics(topics, context, homeFragment, hashMapWordInTopic, user_id, folders, this, folder.getId());
        recyclerViewTopicWithFolder.setAdapter(adapterTopics);
        recyclerViewTopicWithFolder.setLayoutManager(new LinearLayoutManager(context));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TopicInFolderTouchHelper(adapterTopics));
        itemTouchHelper.attachToRecyclerView(recyclerViewTopicWithFolder);
        textViewEditFolderName.setOnClickListener(v -> {
            if (textViewEditFolderName.getText().toString().equals("Sửa")) {
                textViewEditFolderName.setText("Lưu");
                textViewTitleFolder.setEnabled(true);
                textViewTitleFolder.requestFocus();
                showKeyboard();
            } else {
                textViewEditFolderName.setText("Sửa");
                textViewTitleFolder.setEnabled(false);
                String title = textViewTitleFolder.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(context, "Folder name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    db.collection("Folders").document(String.valueOf(folder.getId())).update("title", textViewTitleFolder.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
//                                    Toast.makeText(context, "Update success", Toast.LENGTH_SHORT).show();
                                }
                            });
                    folder.setTitle(title);
                    updateFolder(folder);
                    dialog.dismiss();
                }


            }
        });

        backIcon.setOnClickListener(v -> {
            dialog.dismiss();
            textViewTopicCount.setText(String.valueOf(topics.size()) + " topic");
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textViewTitleFolder, InputMethodManager.SHOW_IMPLICIT);
    }

    private void displayTopicByUserIdInFolder(Folder folder) {
        topics.clear();
        db.collection("Folders").document(String.valueOf(folder.getId())).collection("Topics").whereEqualTo("user_id", user_id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Topic> topicList = task.getResult().toObjects(Topic.class);
                        topics.addAll(topicList);
                        adapterTopics.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }


}
