package tdtu.edu.cookie.UI.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import tdtu.edu.cookie.Database.Entity.Folder;
import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;

public class AdapterOptionFolders extends RecyclerView.Adapter<AdapterOptionFolders.ViewHolder>{

    ArrayList<Folder> folders;
    private Context context;
    Topic topic;
    HashMap<Integer, List<Topic>> hashMapTopicInFolder;
    FirebaseFirestore db;
    private Dialog dialog;
    List<Words> wordsList;


    public AdapterOptionFolders(ArrayList<Folder> folders, HashMap<Integer, List<Topic>> hashMapTopicInFolder, Context context, Topic topic, Dialog dialog) {
        this.folders = folders;
        this.context = context;
        this.hashMapTopicInFolder = hashMapTopicInFolder;
        this.topic = topic;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public AdapterOptionFolders.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folders, parent, false);
        return new AdapterOptionFolders.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOptionFolders.ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        Folder folder = folders.get(position);
        holder.textViewFolderName.setText(folder.title);
//        holder.textViewTopicCount.setText("0 topics");
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
        holder.cardViewFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchWordsForTopic(topic.getId(), topic.getId(), topic, folder.getId());
//                addTopicAndWordsToFolder(topic, topic.getId(), String.valueOf(folder.getId()));
//               addTopicByFolderToFireStore(topic.getId(), topic.getTitle(), topic.getIs_public(), topic.getUser_id(), topic.getEmail(), String.valueOf(folder.getId()));
               if (!checkExistTopicInFolder(topic.getId(), folder.getId())) {
                   hashMapTopicInFolder.get(folder.getId()).add(topic);
                   hashMapTopicInFolder.put(folder.getId(), hashMapTopicInFolder.get(folder.getId()));
                   Toast.makeText(context, "Thêm topic vào folder thành công", Toast.LENGTH_SHORT).show();
                   if (dialog != null && dialog.isShowing()) {
                       dialog.dismiss();
                   }
               } else {
                   Toast.makeText(context, "Topic đã tồn tại trong folder", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //function to check exist topic in folder
    public boolean checkExistTopicInFolder(int topic_id, int folder_id) {
        for (Topic topic : hashMapTopicInFolder.get(folder_id)) {
            if (topic.getId() == topic_id) {
                return true;
            }
        }
        return false;
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

    private void addTopicByFolderToFireStore(int id, String topicName, boolean is_public, String user_id, String email, String folder_id) {
        HashMap<String, Object> topic = new HashMap<>();
        topic.put("id", id);
        topic.put("title", topicName);
        topic.put("is_public", is_public);
        topic.put("user_id", user_id);
        topic.put("email", email);
        db.collection("Folders").document(String.valueOf(folder_id)).collection("Topics").document(String.valueOf(id)).set(topic)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                );
    }

    private void addWordsToTopicSubcollection(int folder_id,int topicId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicWordsCollection = db.collection("Folders").document(String.valueOf(folder_id)).collection("Topics").document(String.valueOf(topicId)).collection("Words");

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

    private String convertBitmapToBase64(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return null;
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

   private void addTopicAndWordsToFolder(Topic topic, int topicId, int folder_id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add the topic to the "Topics" collection
        db.collection("Folders").document(String.valueOf(folder_id)).collection("Topics").document(String.valueOf(topicId)).set(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After successfully adding the topic, add the words to the sub-collection
                        addWordsToTopicSubcollection(folder_id,topicId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Thêm chủ đề thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("error", e.getMessage());
                });
    }

    private void fetchWordsForTopic(int topicId, int newTopicId, Topic newTopic, int folder_id) {
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
                    addTopicAndWordsToFolder(newTopic, newTopicId, folder_id);
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


}
