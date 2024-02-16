package tdtu.edu.cookie.UI.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Activity.Bxh;
import tdtu.edu.cookie.UI.Activity.FlashCard;
import tdtu.edu.cookie.UI.Activity.Quiz;
import tdtu.edu.cookie.UI.Activity.inputText;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;

public class AdapterPublicTopics extends RecyclerView.Adapter<AdapterPublicTopics.ViewHolder>{

    private List<Topic> topics;
    private Context context;

    List<Words> wordsList;
    int englishToVietnameseCount;
    int vietnameseToEnglishCount;
    public AdapterPublicTopics(Context context,List<Topic> topics){
        this.context = context;
        this.topics = topics;
    }

    @NonNull
    @Override
    public AdapterPublicTopics.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_public_topics, parent, false);


        return new AdapterPublicTopics.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPublicTopics.ViewHolder holder, int position) {
        holder.setData(topics.get(position));

        holder.saveTopic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = getCurrentUserId();
                String userEmail = getCurrentUserEmail();

                long currentTimestamp = System.currentTimeMillis();
                int topic_id = Math.abs((userId + 1 + currentTimestamp).hashCode());
                Topic topic = new Topic(topic_id, topics.get(position).getTitle(), false, userId, userEmail);


                fetchWordsForTopic(topics.get(position).getId(), topic_id, topic);

            }
        });

        holder.btn_bxh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Bxh.class);
                intent.putExtra("idTopic",String.valueOf(topics.get(position).getId()));
                context.startActivity(intent);
            }
        });
        holder.btn_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            intent.putExtra("topicId",topics.get(position).getId());
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
                            CollectionReference wordsCollection = db.collection("Topics").document(String.valueOf(topics.get(position).getId())).collection("Words");
                            wordsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int count=0;
                                    for (DocumentSnapshot snapshot : task.getResult()) {
                                        count   ++;             }
                                    loadingDialog.dismiss();
                                    Toast.makeText(context, "Number of words: " + count, Toast.LENGTH_SHORT).show();
                                    showEditDialog(topics.get(position).getId(),0,count);
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
                        CollectionReference wordsCollection = db.collection("Topics").document(String.valueOf(topics.get(position).getId())).collection("Words");
                        wordsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int count=0;
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    count   ++;             }
                                loadingDialog.dismiss();
                                Toast.makeText(context, "Number of words: " + count, Toast.LENGTH_SHORT).show();
                                showEditDialog(topics.get(position).getId(),1,count);
                            }
                        });


                    }
                });
                dialog.show();
                Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
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
                        context.startActivity(intent);

                    }
                    else{
                        Intent intent = new Intent(context, inputText.class);
                        intent.putExtra("idTopic",idTopic);
                        intent.putExtra("vietQuestion",vietnameseToEnglishCount);
                        intent.putExtra("engQuestion",englishToVietnameseCount);
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

    private String getCurrentUserEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    private String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            return user.getUid();
        } else {
            // Handle the case where the user is not authenticated
            return null;
        }
    }


    private void addTopicAndWordsToFirestore(Topic topic, int topicId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add the topic to the "Topics" collection
        db.collection("Topics").document(String.valueOf(topicId)).set(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After successfully adding the topic, add the words to the sub-collection
                        addWordsToTopicSubcollection(topicId);
                        FancyToast.makeText(context, "Lưu chủ đề thành công", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Thêm chủ đề thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("error", e.getMessage());
                });
    }

    private void addWordsToTopicSubcollection(int topicId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicWordsCollection = db.collection("Topics").document(String.valueOf(topicId)).collection("Words");

        // Add each word to the sub-collection
        for (Words word : wordsList) {
            Bitmap imageBitmap = word.getImage();
            String base64Image = convertBitmapToBase64(imageBitmap);
            long currentTimestamp = System.currentTimeMillis();
            int word_id = Math.abs((word.getUser_id() + topicId + currentTimestamp).hashCode());
            word.setId(word_id);
            // Convert the word object to a map to add to Firestore
            Map<String, Object> wordMap = new HashMap<>();
            wordMap.put("id", word.getId());
            wordMap.put("english_word", word.getEnglish_word());
            wordMap.put("vietnamese_word", word.getVietnamese_word());
            wordMap.put("spelling", word.getSpelling());
            wordMap.put("date", word.getDate());
            wordMap.put("photo", base64Image); // Assuming the image is a Serializable object
            wordMap.put("word_form", word.getWord_form());
            wordMap.put("example", word.getExample());
            wordMap.put("audio", word.getAudio());
            wordMap.put("is_fav", false);
            wordMap.put("topic_id", topicId);
            wordMap.put("status", "Đang học");
            wordMap.put("user_id", getCurrentUserId());

            topicWordsCollection.document(String.valueOf(word.getId())).set(wordMap)
                    .addOnCompleteListener(subcollectionTask -> {
                        if (subcollectionTask.isSuccessful()) {
                            // Handle the success of adding a word to the sub-collection
                        }
                    })
                    .addOnFailureListener(subcollectionException -> {
                        // Handle errors when adding a word to the sub-collection
                        Log.e("Subcollection Error", subcollectionException.getMessage());
                    });
        }
    }

    private void fetchWordsForTopic(int topicId, int newTopicId, Topic newTopic) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Lay word tu topic ban dau
        CollectionReference topicWordsCollection = db.collection("Topics").document(String.valueOf(topicId)).collection("Words");


        topicWordsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    wordsList = new ArrayList<>();  // Initialize wordsList
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String photoBase64 = document.get("photo").toString();
                        byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        Words word = new Words(Integer.parseInt(document.getId()), document.getString("english_word"), document.getString("vietnamese_word"),
                                            document.getString("spelling"), document.getString("date"), decodedByte, document.getString("word_form"),
                                            document.getString("example"), document.getString("audio"), false, newTopicId, "Đang học", getCurrentUserId());


                        wordsList.add(word);
                    }
                    //them word do vao topic moi
                    addTopicAndWordsToFirestore(newTopic, newTopicId);
                }
            } else {
                // Handle errors
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("Fetch Words Error", exception.getMessage());
                }
            }
        });
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    private String convertBitmapToBase64(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return null;
    }


    @Override
    public int getItemCount() {
        return topics.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView topicName;
        TextView ownerName;

        TextView learnerNumbers;

        ImageView saveTopic_btn;
        Button btn_bxh;
        Button btn_study;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            topicName = itemView.findViewById(R.id.topicName);
            ownerName = itemView.findViewById(R.id.ownerName);
            learnerNumbers = itemView.findViewById(R.id.learningNumbers);
            saveTopic_btn = itemView.findViewById(R.id.saveTopic_btn);
            btn_bxh = itemView.findViewById(R.id.btn_bxh);
            btn_study = itemView.findViewById(R.id.learnTopic_btn);
        }

        void setData(Topic topic){
            topicName.setText(topic.getTitle());
            ownerName.setText(topic.getEmail());
        }
    }

}
