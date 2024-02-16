package tdtu.edu.cookie.UI.Adapter;


import static com.google.android.material.internal.ViewUtils.showKeyboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import tdtu.edu.cookie.Database.Entity.Folder;
import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.Database.Entity.wordsQuizz;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Activity.FlashCard;
import tdtu.edu.cookie.UI.Activity.Quiz;
import tdtu.edu.cookie.UI.Activity.inputText;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;
import tdtu.edu.cookie.UI.Fragment.HomeFragment;
import tdtu.edu.cookie.UI.Fragment.LibraryFragment;
import tdtu.edu.cookie.ViewModel.WordVM;

public class AdapterTopics extends RecyclerView.Adapter<AdapterTopics.ViewHolder> {

    private ArrayList<Topic> topics;
    WordVM wordVM;
    ArrayList<Words> words = new ArrayList<Words>();
    AdapterWords adapter;
    AdapterOptionFolders adapterOptionFolders;
    private Context context;
    Vibrator vibrator;
    public LibraryFragment fragment;
    private FirebaseFirestore db;
    EditText textViewTitleTopic;
    NestedScrollView nestedScrollViewTopic;
    HashMap<Integer, List<Words>> hashMapWordInTopic;
    HashMap<Integer, AdapterTopics.ViewHolder> hashMapViewHolder = new HashMap<>();
    int englishToVietnameseCount;
    int vietnameseToEnglishCount;
    String user_id;
    public HomeFragment homeFragment;
    private ArrayList<Folder> folders;
    private ArrayList<Folder> foldersOption = new ArrayList<>();
    HashMap<Integer, List<Topic>> hashMapTopicInFolder = new HashMap<>();
    AdapterFolder adapterFolder;
    int folder_id;


    public AdapterTopics(ArrayList<Topic> topics, Context context, LibraryFragment fragment, HashMap<Integer, List<Words>> hashMapWordInTopic, String user_id) {
        this.topics = topics;
        this.context = context;
        this.fragment = fragment;
        this.hashMapWordInTopic = hashMapWordInTopic;
        this.user_id = user_id;
    }

    public AdapterTopics(ArrayList<Topic> topics, Context context, HomeFragment fragment, HashMap<Integer, List<Words>> hashMapWordInTopic, String user_id, ArrayList<Folder> folders, AdapterFolder adapterFolder, int folder_id) {
        this.topics = topics;
        this.context = context;
        this.homeFragment = fragment;
        this.hashMapWordInTopic = hashMapWordInTopic;
        this.user_id = user_id;
        this.folders = folders;
        this.adapterFolder = adapterFolder;
        this.folder_id = folder_id;
    }



    @NonNull
    @Override
    public AdapterTopics.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topics, parent, false);
        return new AdapterTopics.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTopics.ViewHolder holder, int position) {
        if (wordVM == null) {
            wordVM = new ViewModelProvider((ViewModelStoreOwner) context).get(WordVM.class);
        }
        db = FirebaseFirestore.getInstance();
        Topic topic = topics.get(position);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        hashMapViewHolder.put(topic.getId(), holder);

        holder.textViewTopicName.setText(topic.getTitle());
        if (fragment != null) {
            holder.cardViewTopic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.fragment_practice_option);

                    LinearLayout flashCardButton = dialog.findViewById(R.id.linearLayoutFlashCard);
                    LinearLayout quizButton = dialog.findViewById(R.id.linearLayoutQuiz);
                    LinearLayout textButton = dialog.findViewById(R.id.linearLayoutText);

                    flashCardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(context, FlashCard.class);
                                intent.putExtra("topicId", topic.getId());
                                context.startActivity(intent);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    quizButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                LoadingDialog loadingDialog = new LoadingDialog(context);

                                loadingDialog.show();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                CollectionReference wordsCollection = db.collection("Topics").document(String.valueOf(topic.getId())).collection("Words");
                                wordsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        int count=0;
                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                            count   ++;             }
                                        loadingDialog.dismiss();
                                        showEditDialog(topic.getId(),0,count);
                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    textButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoadingDialog loadingDialog = new LoadingDialog(context);

                            loadingDialog.show();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference wordsCollection = db.collection("Topics").document(String.valueOf(topic.getId())).collection("Words");
                            wordsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int count=0;
                                    for (DocumentSnapshot snapshot : task.getResult()) {
                                        count   ++;             }
                                    loadingDialog.dismiss();
                                    showEditDialog(topic.getId(),1,count);
                                }
                            });


                        }
                    });
                    dialog.show();
                    Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                    return false;
                }
            });

        }

        if (!hashMapWordInTopic.containsKey(topic.getId())) {
            AtomicInteger count = new AtomicInteger();
            db.collection("Topics").document(String.valueOf(topic.getId())).collection("Words").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Words> wordsList = task.getResult().toObjects(Words.class);
                            hashMapWordInTopic.put(topic.getId(), wordsList);
                            count.set(wordsList.size());
                            holder.textViewWordCount.setText(String.valueOf(count) + " từ");
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    });
        }else {
            holder.textViewWordCount.setText(String.valueOf(hashMapWordInTopic.get(topic.getId()).size()) + " từ");
        }
        holder.cardViewTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(5);
                displayWordByUserIdInTopic(topic);
                showBottomDialogTopic(topic, holder.textViewWordCount);
            }
        });
    }
    private void showEditDialog(int idTopic,int quizz,int count) {
        // Tạo một Dialog mới
        final Dialog editDialog = new Dialog(context);
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.edit_quizz);

        // Tìm các thành phần trong layout của Dialog
        EditText editVietnamese = editDialog.findViewById(R.id.vnWordText);
        EditText editEnglish = editDialog.findViewById(R.id.engWordText);
        Button btnApply = editDialog.findViewById(R.id.applyQuizz);



        englishToVietnameseCount = Integer.parseInt(editVietnamese.getText().toString());
        vietnameseToEnglishCount = Integer.parseInt(editEnglish.getText().toString());

        // Tính toán tổng số câu hỏi
        int totalQuestions = englishToVietnameseCount + vietnameseToEnglishCount;


        // Thêm các xử lý sự kiện cho nút "Apply"
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị từ EditTexts
                englishToVietnameseCount = Integer.parseInt(editVietnamese.getText().toString());
                vietnameseToEnglishCount = Integer.parseInt(editEnglish.getText().toString());

                // Tính toán tổng số câu hỏi
                int totalQuestions = englishToVietnameseCount + vietnameseToEnglishCount;

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference wordsCollection = db.collection("Topics").document(String.valueOf(idTopic)).collection("Words");
                final int[] numberOfWords = new int[1];


                int number = count; // Thay thế giá trị này bằng số lượng thực tế từ

                // Kiểm tra điều kiện và hiển thị Toast nếu cần
                if (totalQuestions <= number) {
                    // Thực hiện các hành động cập nhật dữ liệu ở đây
                    Log.d("YourActivity", "Total Questions: " + totalQuestions);

                    // Ví dụ: Gọi hàm xử lý cập nhật dữ liệu
                    // Đóng dialog sau khi xử lý xong
                    if (quizz == 0) {
                        Intent intent = new Intent(context, Quiz.class);
                        intent.putExtra("idTopic", idTopic);
                        intent.putExtra("vietQuestion", vietnameseToEnglishCount);
                        intent.putExtra("engQuestion", englishToVietnameseCount);
                        intent.putExtra("is_public", 0);

                        context.startActivity(intent);

                    }
                else{
                        Intent intent = new Intent(context, inputText.class);
                        intent.putExtra("idTopic",idTopic);
                        intent.putExtra("vietQuestion",vietnameseToEnglishCount);
                        intent.putExtra("engQuestion",englishToVietnameseCount);
                        intent.putExtra("is_public", 0);

                        context.startActivity(intent);
                    }


                } else {
                    Toast.makeText(context, "Total questions cannot exceed the number of words", Toast.LENGTH_SHORT).show();

                }
            }
        });

        // Thêm xử lý sự kiện cho nút "Cancel"


        // Hiển thị Dialog
        editDialog.show();
        Objects.requireNonNull(editDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTopicName;
        TextView textViewWordCount;
        RelativeLayout viewBackgroundTopic;
        LinearLayout viewForegroundTopic;
        CardView cardViewTopic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTopicName = itemView.findViewById(R.id.textViewTopicName);
            textViewWordCount = itemView.findViewById(R.id.textViewWordCount);
            viewBackgroundTopic = itemView.findViewById(R.id.viewBackgroundTopic);
            viewForegroundTopic = itemView.findViewById(R.id.viewForegroundTopic);
            cardViewTopic = itemView.findViewById(R.id.cardViewTopic);

        }
    }

    public void removeItem(int position) {
        topics.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Topic item, int position) {
        topics.add(position, item);
        notifyItemInserted(position);
    }

    private void showBottomDialogTopic(Topic topic, TextView textViewWordCount) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_topic);

        ImageView imageViewAddToFolder = dialog.findViewById(R.id.imageViewAddToFolder);
        Switch switchPublic = dialog.findViewById(R.id.switchPublic);
        RecyclerView recyclerViewVocabularyWithTopic = dialog.findViewById(R.id.recyclerViewVocabularyWithTopic);
        textViewTitleTopic = dialog.findViewById(R.id.textViewTitleTopic);
        ImageView backIcon = dialog.findViewById(R.id.backIcon);
        TextView textViewEditTopicName = dialog.findViewById(R.id.textViewEditTopicName);
        textViewTitleTopic.setText(topic.getTitle());
        nestedScrollViewTopic = dialog.findViewById(R.id.nestedScrollViewTopic);
        textViewEditTopicName.setText("Sửa");

        adapter = new AdapterWords(words, context, fragment, topic.getId(), hashMapWordInTopic, folder_id);
        recyclerViewVocabularyWithTopic.setAdapter(adapter);
        recyclerViewVocabularyWithTopic.setLayoutManager(new LinearLayoutManager(context));

        if (homeFragment != null) {
            imageViewAddToFolder.setVisibility(View.GONE);
            textViewEditTopicName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textViewEditTopicName.getText().equals("Sửa")) {
                        textViewEditTopicName.setText("Lưu");
                        textViewTitleTopic.setEnabled(true);
                        textViewTitleTopic.requestFocus(); // Set focus to textViewTitleTopic
                        showKeyboard();
                    } else {
                        textViewEditTopicName.setText("Sửa");
                        textViewTitleTopic.setEnabled(false);
                        String title = textViewTitleTopic.getText().toString();
                        if (title.isEmpty()) {
                            Toast.makeText(context, "Tên Topic không được bỏ trống!", Toast.LENGTH_SHORT).show();
                        } else {
                            updateTopicInFolderToFireStore(folder_id, topic.getId(), title, topic.getIs_public());
                            topic.setTitle(title);
                            updateTopics(topic);
                            dialog.dismiss();
                        }
                    }

                }
            });

            } else {
            imageViewAddToFolder.setVisibility(View.VISIBLE);
            textViewEditTopicName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textViewEditTopicName.getText().equals("Sửa")) {
                        textViewEditTopicName.setText("Lưu");
                        textViewTitleTopic.setEnabled(true);
                        textViewTitleTopic.requestFocus(); // Set focus to textViewTitleTopic
                        showKeyboard();
                    } else {
                        textViewEditTopicName.setText("Sửa");
                        textViewTitleTopic.setEnabled(false);
                        String title = textViewTitleTopic.getText().toString();
                        if (title.isEmpty()) {
                            Toast.makeText(context, "Tên Topic không được bỏ trống!", Toast.LENGTH_SHORT).show();
                        } else {
                            updateTopicToFireStore(topic.getId(), title, topic.getIs_public());
                            topic.setTitle(title);
                            updateTopics(topic);
                            dialog.dismiss();
                        }
                    }

                }
            });
        }

        imageViewAddToFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(5);
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.fragment_folder_option);

                TextView cancelButton = dialog.findViewById(R.id.cancelButtonFolderOption);
                displayFolderByUserId(new CallBackFireStore() {
                    @Override
                    public void onDataLoaded(List<?> data) {
//                        numOfFolder.setText(String.valueOf(folders.size()));
                    }

                    @Override
                    public void onDataLoadFailed(String errorMessage) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

                RecyclerView recyclerViewOptionFolders = dialog.findViewById(R.id.recyclerViewFolder);
                adapterOptionFolders = new AdapterOptionFolders(foldersOption, hashMapTopicInFolder, context, topic, dialog);
                recyclerViewOptionFolders.setAdapter(adapterOptionFolders);
                recyclerViewOptionFolders.setLayoutManager(new LinearLayoutManager(context));

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });






        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                textViewWordCount.setText(String.valueOf(hashMapWordInTopic.get(topic.getId()).size()) + " từ");
                if (fragment!=null)
                {
                    fragment.displayWordByUserId(new CallBackFireStore() {


                        @Override
                        public void onDataLoaded(List<?> data) {
                            fragment.numOfVocab.setText(String.valueOf(data.size()));
                        }

                        @Override
                        public void onDataLoadFailed(String errorMessage) {

                        }
                    });
                }

            }
        });

        switchPublic.setChecked(topic.getIs_public());
        switchPublic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            topic.setIs_public(isChecked);
            updateTopicToFireStore(topic.getId(), topic.getTitle(), isChecked);
            updateTopics(topic);
        });


        dialog.show();


        Objects.requireNonNull(dialog.getWindow()).

                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().

                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().

                getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().

                setGravity(Gravity.BOTTOM);

    }

    //function to check Fragment


    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textViewTitleTopic, InputMethodManager.SHOW_IMPLICIT);
    }

    private void displayData(Topic topic) {
        words.clear();
        db.collection("Topics").document(String.valueOf(topic.getId())).collection("Words").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Words> wordsList = task.getResult().toObjects(Words.class);
                        words.addAll(wordsList);
                        if (words.size() <= 7) {
                            nestedScrollViewTopic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4000));
                        } else {
                            nestedScrollViewTopic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                        adapter.notifyDataSetChanged();


                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void displayWordByUserIdInTopic(Topic topic) {
        words.clear();
        db.collection("Topics").document(String.valueOf(topic.getId())).collection("Words").whereEqualTo("user_id", user_id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Words> wordsList = task.getResult().toObjects(Words.class);
                        words.addAll(wordsList);
                        if (words.size() <= 7) {
                            nestedScrollViewTopic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4000));
                        } else {
                            nestedScrollViewTopic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                        adapter.notifyDataSetChanged();


                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateTopicToFireStore(int id, String title, boolean is_public) {
        db.collection("Topics").document(String.valueOf(id)).update("title", title, "is_public", is_public)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        Toast.makeText(context, "Update topic successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Update topic failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                        e -> Toast.makeText(context, "Update topic failed", Toast.LENGTH_SHORT).show()
                );
    }


    private void updateTopicInFolderToFireStore (int idFolder, int idTopic, String title, boolean is_public) {
        db.collection("Folders").document(String.valueOf(idFolder)).collection("Topics").document(String.valueOf(idTopic)).update("title", title, "is_public", is_public)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    }
                }).addOnFailureListener(
                        e -> Toast.makeText(context, "Update topic failed", Toast.LENGTH_SHORT).show()
                );
    }

    public void updateTopics(Topic topic) {
        for (int i = 0; i < topics.size(); i++) {
            if (topics.get(i).getId() == topic.getId()) {
                topics.set(i, topic);
                notifyItemChanged(i);
                break;
            }
        }
    }

    //function to display isFav Word in Topic
    public void displayIsFavWordInTopic(Topic topic) {
        words.clear();
        db.collection("Topics").document(String.valueOf(topic.getId())).collection("Words").whereEqualTo("is_fav", true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Words> wordsList = task.getResult().toObjects(Words.class);
                        words.addAll(wordsList);
                        if (words.size() <= 7) {
                            nestedScrollViewTopic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3000));
                        } else {
                            nestedScrollViewTopic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }


    //function to delete Topic from firestore
    public void deleteTopic(int position) {
        db.collection("Topics").document(String.valueOf(topics.get(position).getId())).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Delete topic failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(context, "Delete topic failed", Toast.LENGTH_SHORT).show());
    }

    private void displayFolderByUserId(CallBackFireStore callBackFireStore) {
//        loadingDialog.show();
        foldersOption.clear();
        db.collection("Folders")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                        int id = Integer.parseInt(snapshot.get("id").toString());
                        String title = snapshot.get("title").toString();
                        String user_id = snapshot.get("user_id").toString();
                        foldersOption.add(new Folder(id, title, user_id));
                    }
                    callBackFireStore.onDataLoaded(foldersOption);
                    adapterOptionFolders.notifyDataSetChanged();
//                    loadingDialog.dismiss();
                }).addOnFailureListener(
                        e -> {
//                            loadingDialog.dismiss();
                            callBackFireStore.onDataLoadFailed(e.getMessage());
                            Toast.makeText(context, "Load folder fail", Toast.LENGTH_SHORT).show();
                        }
                );
    }

    public void deleteTopicInFolder(int position) {
        db.collection("Folders").document(String.valueOf(folders.get(position).getId())).collection("Topics").document(String.valueOf(topics.get(position).getId())).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        topics.remove(position);
                        notifyDataSetChanged();
                        List<Topic> topics = hashMapTopicInFolder.get(folders.get(position).getId());
                        if (topics != null) {
                            adapterFolder.viewHolderFolder.get(folders.get(position).getId()).textViewTopicCount.setText(String.valueOf(topics.size()) + " topic");
                        }
                        else {
                            adapterFolder.viewHolderFolder.get(folders.get(position).getId()).textViewTopicCount.setText("0 topic");
                        }
                    } else {
                        Toast.makeText(context, "Delete topic failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(context, "Delete topic failed", Toast.LENGTH_SHORT).show());
    }


}
