package tdtu.edu.cookie.UI.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;
import tdtu.edu.cookie.UI.Fragment.LibraryFragment;
import tdtu.edu.cookie.ViewModel.WordVM;

public class AdapterOptionTopics extends RecyclerView.Adapter<AdapterOptionTopics.ViewHolder> {
    private ArrayList<Topic> topics;
    private Context context;
    WordVM wordVM;
    Words word;
    ArrayList<Words> words;
    private Dialog dialog;

    Vibrator vibrator;

    private AdapterWords adapterWords;
    private AdapterTopics adapterTopics;
    private FirebaseFirestore db;
    int positionWord;
    private LibraryFragment libraryFragment;
    TextView numOfVocab;
    HashMap<Integer, List<Words>> hashMapWordInTopic;
    String user_id;


    public AdapterOptionTopics (ArrayList<Topic> topics, Context context, Words word, Dialog dialog, FirebaseFirestore db, int positionWord, ArrayList<Words> words, LibraryFragment libraryFragment, AdapterWords adapterWords, HashMap<Integer, List<Words>> hashMapWordInTopic, AdapterTopics adapterTopics, String user_id) {
        this.topics = topics;
        this.context = context;
        this.word = word;
        this.dialog = dialog;
        this.db = db;
        this.positionWord = positionWord;
        this.words = words;
        this.libraryFragment = libraryFragment;
        this.adapterWords = adapterWords;
        this.hashMapWordInTopic = hashMapWordInTopic;
        this.adapterTopics = adapterTopics;
        this.user_id = user_id;

    }




    @NonNull
    @Override
    public AdapterOptionTopics.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topics, parent, false);
        return new AdapterOptionTopics.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOptionTopics.ViewHolder holder, int position) {
        if (wordVM == null) {
            wordVM = new ViewModelProvider(
                    (ViewModelStoreOwner) context
            ).get(WordVM.class);
        }
//        adapterTopics = new AdapterTopics(topics, context, libraryFragment);
        numOfVocab = libraryFragment.getActivity().findViewById(R.id.numOfVocab);
        db = FirebaseFirestore.getInstance();
        Topic topic = topics.get(position);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        holder.textViewTopicName.setText(topic.getTitle());
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
                addWordByTopicToFireStore(word.getId(), word.getEnglish_word(), word.getVietnamese_word(), word.getSpelling(), word.getDate(), word.getImage(), word.getWord_form(), word.getExample(), word.getAudio(), word.isIs_fav(), topic.getId(), "Đang học", user_id);

                adapterWords.deleteWordFromFireStore(positionWord);
                adapterWords.removeWord(positionWord);
                numOfVocab.setText(String.valueOf(adapterWords.getItemCount()));
                hashMapWordInTopic.get(topic.getId()).add(word);
                holder.textViewWordCount.setText(String.valueOf(hashMapWordInTopic.get(topic.getId()).size()) + " từ");
                adapterTopics.hashMapViewHolder.get(topic.getId()).textViewWordCount.setText(String.valueOf(hashMapWordInTopic.get(topic.getId()).size()) + " từ");
                libraryFragment.numOfVocab.setText(String.valueOf(adapterWords.getItemCount()));
                notifyItemRangeChanged(0, getItemCount());
                Toast.makeText(context, "Added to " + topic.getTitle(), Toast.LENGTH_SHORT).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return topics.size();
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

    private void addWordByTopicToFireStore(int id, String english_word, String vietnamese_word, String spelling, String date, Bitmap photo, String word_form, String example, String audio, boolean is_fav, int topic_id, String status, String user_id)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();
        String photoBase64 = Base64.encodeToString(photoData, Base64.DEFAULT);

        HashMap<String, Object> word = new HashMap<>();
        word.put("id", id);
        word.put("english_word", english_word);
        word.put("vietnamese_word", vietnamese_word);
        word.put("spelling", spelling);
        word.put("date", date);
        word.put("photo", photoBase64);
        word.put("word_form", word_form);
        word.put("example", example);
        word.put("audio", audio);
        word.put("is_fav", is_fav);
        word.put("topic_id", topic_id);
        word.put("status", status);
        word.put("user_id", user_id);

        db.collection("Topics").document(String.valueOf(topic_id)).collection("Words").document(String.valueOf(id)).set(word).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
//                        Toast.makeText(context, "Add word successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Add word failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(
                e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

    }
}
