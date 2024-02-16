package tdtu.edu.cookie.UI.Adapter;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;

public class AdapterExportTopics extends RecyclerView.Adapter<AdapterExportTopics.ViewHolder> {

    private ListOwner listOwner;
    List<Topic> topics;
    List<Words> wordsList = new ArrayList<>();

    private Context context;
    private Fragment parentFragment;


    public AdapterExportTopics(Fragment parentFragment, Context context, List<Topic> topics, ListOwner listOwner ) {
        this.parentFragment = parentFragment;
        this.context = context;
        this.topics = topics;
        this.listOwner = listOwner;
    }

    @NonNull
    @Override
    public AdapterExportTopics.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_topic_export, parent, false);


        return new AdapterExportTopics.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterExportTopics.ViewHolder holder, int position) {
        holder.setData(topics.get(position));


        holder.btnTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchWordsForTopic(topics.get(position).getId(), new WordsFetchCallback() {

                    @Override
                    public void onWordsFetched(List<Words> wordsList) {

                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        intent.putExtra(Intent.EXTRA_TITLE, topics.get(position).getTitle());
                        listOwner.push(wordsList);
                        parentFragment.startActivityForResult(intent, 10);

                    }
                });



            };
        });


    }

    public interface ListOwner {
        public void push(List<Words> wordList);
    }

    @Override
    public int getItemCount() {
        return topics.size();
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

    public interface WordsFetchCallback {
        void onWordsFetched(List<Words> wordsList);
    }
    private void fetchWordsForTopic(int topicId, WordsFetchCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Lay word tu topic ban dau
        CollectionReference topicWordsCollection = db.collection("Topics").document(String.valueOf(topicId)).collection("Words");


        topicWordsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {// Initialize wordsList
                    wordsList.clear();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String photoBase64 = document.get("photo").toString();
                        byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        Words word = new Words(Integer.parseInt(document.getId()), document.getString("english_word"), document.getString("vietnamese_word"),
                                document.getString("spelling"), document.getString("date"), decodedByte, document.getString("word_form"),
                                document.getString("example"), document.getString("audio"), false, topicId, "Đang học", getCurrentUserId());


                        wordsList.add(word);
                    }

                    callback.onWordsFetched(wordsList);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTopicName;

        CardView btnTopic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTopicName = itemView.findViewById(R.id.textViewTopicName);
            btnTopic = itemView.findViewById(R.id.cardViewTopic);

        }


        void setData(Topic topic){
            textViewTopicName.setText(topic.getTitle());
        }
    }
}
