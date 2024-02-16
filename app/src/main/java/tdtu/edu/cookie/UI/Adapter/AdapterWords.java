package tdtu.edu.cookie.UI.Adapter;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.varunest.sparkbutton.SparkEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Fragment.LibraryFragment;
import tdtu.edu.cookie.ViewModel.TopicVM;
import tdtu.edu.cookie.ViewModel.WordVM;

public class AdapterWords extends RecyclerView.Adapter<AdapterWords.ViewHolder> {
    WordVM wordVM;
    TopicVM topicVM;
    HashMap<Integer, List<Words>> hashMapWordInTopic;

    private ArrayList<Words> words;
    private ArrayList<Topic> topics = new ArrayList<>();

    AdapterOptionTopics adapterOptionTopics;
    AdapterTopics adapterTopics;

    public LibraryFragment fragment;
    public ImageView imageViewChangePhoto = null;
    Vibrator vibrator;
    public FirebaseFirestore db;
    private Context context;
    int topic_id;
    int folder_id;
    String user_id;

    public int getPositionWord(int id) {
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public interface OnDeleteCompleteListener {
        void onDeleteComplete(int deletedItemId);
    }

    private OnDeleteCompleteListener onDeleteCompleteListener;

    public void setOnDeleteCompleteListener(OnDeleteCompleteListener listener) {
        this.onDeleteCompleteListener = listener;
    }

    public AdapterWords(ArrayList<Words> words, Context context, LibraryFragment fragment, HashMap<Integer, List<Words>> hashMapWordInTopic) {
        this.words = words;
        this.context = context;
        this.fragment = fragment;
        this.hashMapWordInTopic = hashMapWordInTopic;
    }

    public AdapterWords(ArrayList<Words> words, Context context, LibraryFragment fragment, int topic_id, HashMap<Integer, List<Words>> hashMapWordInTopic) {
        this.words = words;
        this.context = context;
        this.fragment = fragment;
        this.topic_id = topic_id;
        this.hashMapWordInTopic = hashMapWordInTopic;
    }

    public AdapterWords(ArrayList<Words> words, Context context, LibraryFragment fragment, int topic_id, HashMap<Integer, List<Words>> hashMapWordInTopic, int folder_id) {
        this.words = words;
        this.context = context;
        this.fragment = fragment;
        this.topic_id = topic_id;
        this.hashMapWordInTopic = hashMapWordInTopic;
        this.folder_id = folder_id;
    }

    public AdapterWords(ArrayList<Words> words, Context context, LibraryFragment fragment, int topic_id, HashMap<Integer, List<Words>> hashMapWordInTopic, String user_id) {
        this.words = words;
        this.context = context;
        this.fragment = fragment;
        this.topic_id = topic_id;
        this.hashMapWordInTopic = hashMapWordInTopic;
        this.user_id = user_id;
    }


    public AdapterWords(ArrayList<Words> words, Context context) {
        this.words = words;
        this.context = context;
    }


    public void setAdapterTopics(AdapterTopics adapterTopics) {
        this.adapterTopics = adapterTopics;
    }

    @NonNull
    @Override
    public AdapterWords.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vocabularies, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterWords.ViewHolder holder, int position) {
        Words word = words.get(position);
        int positionWord = position;
        db = FirebaseFirestore.getInstance();

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        holder.textViewWord.setText(word.getEnglish_word());
        holder.textViewWordForm.setText(word.getWord_form());
        holder.textViewSpelling.setText(word.getSpelling());
        holder.textViewMeaning.setText(word.getVietnamese_word());
        holder.textViewDate.setText(word.getDate());

        holder.cardViewVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(4);
                showBottomDialogEditVocabulary(word);
            }
        });
        holder.imageViewSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(5);

                playAudio(word.getAudio());
            }
        });

        holder.heartButton.setChecked(word.isIs_fav());

        holder.heartButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (wordVM == null) {
                    wordVM = new ViewModelProvider(
                            fragment.requireActivity()
                    ).get(WordVM.class);
                }
                if (buttonState) {
                    word.setIs_fav(true);
                    if (topic_id == 0) {
                        updateFavWordToFireStore(word.getId(), word.isIs_fav());
                    } else {
                        updateFavWordByTopicToFireStore(word.getId(), word.isIs_fav());
                    }

                } else {
                    word.setIs_fav(false);
                    if (topic_id == 0) {
                        updateFavWordToFireStore(word.getId(), word.isIs_fav());
                    } else {
                        updateFavWordByTopicToFireStore(word.getId(), word.isIs_fav());
                    }
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });


        if (word.getTopic_id() != 0) {
            holder.topicButton.setImageResource(R.drawable.baseline_yellow_bookmark_24);
        } else {
            holder.topicButton.setImageResource(R.drawable.baseline_bookmark_24);
        }
        Drawable drawable1 = holder.topicButton.getDrawable();
        Drawable drawable2 = ContextCompat.getDrawable(context, R.drawable.baseline_bookmark_24);

        if (drawable1 != null && drawable2 != null) {
            // Convert drawables to bitmaps
            Bitmap bitmap1 = getBitmapFromDrawable(drawable1);
            Bitmap bitmap2 = getBitmapFromDrawable(drawable2);

            if (bitmap1 != null && bitmap2 != null && bitmap1.sameAs(bitmap2)) {
                // The drawables are the same
                holder.topicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibrator.vibrate(5);
                        showBottomDialogOptionTopic(word, positionWord, words);
                    }
                });
            } else {
                // The drawables are different
                holder.topicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibrator.vibrate(5);

                        getBitmapImageByWordInTopicById(word.getId(), db, new BitmapCallback() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap) {
                                fragment.addWordToFireStore(word.getId(), word.getEnglish_word(), word.getVietnamese_word(), word.getSpelling(), word.getDate(), bitmap, word.getWord_form(), word.getExample(), word.getAudio(), word.isIs_fav(), 0, "Chưa học", word.getUser_id());
                                deleteWordByTopicFromFireStore(word.getId(), db);
                                hashMapWordInTopic.get(topic_id).remove(positionWord);
                                removeWord(positionWord);
                                notifyItemRangeChanged(positionWord, words.size());

                            }
                        });

                    }
                });
            }
        }

        if (word.status.equals("Chưa học")) {
            holder.textViewStatus.setText("Chưa học");
            holder.progressBarStatus.setProgress(0);
        } else if (word.status.equals("Đang học")) {
            holder.textViewStatus.setText("Đang học");
            holder.progressBarStatus.setProgress(50);
        } else if (word.status.equals("Đã học ")) {
            holder.textViewStatus.setText("Đã học ");
            holder.progressBarStatus.setProgress(100);
        }

    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            // If the drawable is already a BitmapDrawable, get the bitmap
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            // If the drawable is a VectorDrawable, convert it to a Bitmap
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
            bitmap = Bitmap.createBitmap(
                    vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }

        return bitmap;
    }


    private void playAudio(String url) {

        //  Play audio from url but not use intent
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(context, "There is no sound of this word", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public void setWords(ArrayList<Words> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    public void addWords(Words words) {
        this.words.add(words);
        notifyDataSetChanged();
    }

    public void addWord(Words words) {
        this.words.add(words);
    }

    public void updateWords(Words words) {
        for (int i = 0; i < this.words.size(); i++) {
            if (this.words.get(i).getId() == words.getId()) {
                this.words.set(i, words);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWord;
        TextView textViewWordForm;
        TextView textViewSpelling;
        TextView textViewMeaning;
        TextView textViewDate;
        RelativeLayout viewBackground;
        LinearLayout viewForeground;
        CardView cardViewVocabulary;
        ImageView topicButton;
        com.varunest.sparkbutton.SparkButton heartButton;
        ImageView imageViewSpeaker;

        ProgressBar progressBarStatus;
        TextView textViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWord = itemView.findViewById(R.id.textViewWord);
            textViewWordForm = itemView.findViewById(R.id.textViewWordForm);
            textViewSpelling = itemView.findViewById(R.id.textViewSpelling);
            textViewMeaning = itemView.findViewById(R.id.textViewMeaning);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            viewBackground = itemView.findViewById(R.id.viewBackground);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            heartButton = itemView.findViewById(R.id.heartButton);
            cardViewVocabulary = itemView.findViewById(R.id.cardViewVocabulary);
            imageViewSpeaker = itemView.findViewById(R.id.imageViewSpeaker);
            topicButton = itemView.findViewById(R.id.topicButton);
            progressBarStatus = itemView.findViewById(R.id.progressBarStatus);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);

        }
    }

    public void removeItem(int position) {
        words.remove(position);
        notifyItemRemoved(position);
    }

    //remove word by id
    public void removeWord(int position) {
        words.remove(position);
        notifyItemRemoved(position);
    }


    public void restoreItem(Words item, int position) {
        words.add(position, item);
        notifyItemInserted(position);
    }

    private void showBottomDialogEditVocabulary(Words word) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_edit_vocabulary);
        TextView textViewChangePhoto = dialog.findViewById(R.id.textViewChangePhoto);
        CardView cardViewEdit = dialog.findViewById(R.id.cardViewEdit);
        EditText editTextDefinition = dialog.findViewById(R.id.editTextDefinition);
        EditText editTextWord = dialog.findViewById(R.id.editTextWord);
        EditText editTextSpelling = dialog.findViewById(R.id.editTextSpelling);
        EditText editTextExample = dialog.findViewById(R.id.editTextExample);
        ImageView imageViewEdit = dialog.findViewById(R.id.imageViewEdit);
        ImageView imageViewEditOff = dialog.findViewById(R.id.imageViewEditOff);
        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        TextView editVocabulary = dialog.findViewById(R.id.editVocabulary);
        imageViewChangePhoto = dialog.findViewById(R.id.imageViewChangePhoto);




        editTextSpelling.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editTextSpelling.getText().toString().equals("")) {
                    editTextSpelling.setText("/transcription/");
                }
            }
        });


        MaterialButtonToggleGroup toggleButtonEdit = dialog.findViewById(R.id.toggleButtonEdit);
        String wordForm = word.getWord_form();
        if (wordForm.equals("verb")) {
            toggleButtonEdit.check(R.id.buttonVerbEdit);
        } else if (wordForm.equals("noun")) {
            toggleButtonEdit.check(R.id.buttonNounEdit);
        } else if (wordForm.equals("adjective")) {
            toggleButtonEdit.check(R.id.buttonAdjEdit);
        } else if (wordForm.equals("adverb")) {
            toggleButtonEdit.check(R.id.buttonAdvEdit);
        }

        editTextWord.setText(word.getEnglish_word());
        editTextSpelling.setText(word.getSpelling());
        editTextDefinition.setText(word.getVietnamese_word());
        editTextExample.setText(word.getExample());

        List<Words> wordList = hashMapWordInTopic.get(topic_id);
        if (wordList != null) {
            getImageByWordInTopicById(word.getId(), db);
        } else {
            imageViewChangePhoto.setImageBitmap(word.getImage());
        }

        textViewChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showBottomDialogImageOptionForChange();
            }
        });
        imageViewChangePhoto.buildDrawingCache();
        imageViewChangePhoto.setDrawingCacheEnabled(true);


        cardViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageViewEditOff.getVisibility() == View.INVISIBLE) {
                    imageViewEditOff.setVisibility(View.VISIBLE);
                    imageViewEdit.setVisibility(View.INVISIBLE);
                    editTextDefinition.setFocusableInTouchMode(true);
                    editTextDefinition.requestFocus();
                } else {
                    imageViewEditOff.setVisibility(View.INVISIBLE);
                    imageViewEdit.setVisibility(View.VISIBLE);
                    editTextDefinition.setFocusable(false);

                }

            }
        });

        if (fragment != null) {
            textViewChangePhoto.setVisibility(View.VISIBLE);
        } else {
            textViewChangePhoto.setVisibility(View.GONE);
        }

        editVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wordForm = "";
                if (toggleButtonEdit.getCheckedButtonId() == R.id.buttonVerbEdit) {
                    wordForm = "verb";
                } else if (toggleButtonEdit.getCheckedButtonId() == R.id.buttonNounEdit) {
                    wordForm = "noun";
                } else if (toggleButtonEdit.getCheckedButtonId() == R.id.buttonAdjEdit) {
                    wordForm = "adj";
                } else if (toggleButtonEdit.getCheckedButtonId() == R.id.buttonAdvEdit) {
                    wordForm = "adv";
                }


                word.setEnglish_word(editTextWord.getText().toString());
                word.setSpelling(editTextSpelling.getText().toString());
                word.setVietnamese_word(editTextDefinition.getText().toString());
                word.setExample(editTextExample.getText().toString());
                word.setWord_form(wordForm);
                String urlAudio = "https://translate.google.com/translate_tts?ie=UTF-8&q=";
                String audio = urlAudio + word.getEnglish_word() + "&tl=en&client=tw-ob";
                word.setAudio(audio);

                Bitmap photo = imageViewChangePhoto.getDrawingCache();
                // Check if the Bitmap is not null and not recycled
                if (photo != null && !photo.isRecycled()) {
                    // Create a new Bitmap to avoid using a recycled one
                    Bitmap newBitmap = Bitmap.createBitmap(photo);
                    word.setImage(newBitmap);

//                    wordVM.update(word);
                    if (fragment != null) {
                        if (topic_id == 0) {
                            updateWordToFireStore(word.getId(), word.getEnglish_word(), word.getVietnamese_word(), word.getSpelling(), word.getDate(), word.getImage(), word.getWord_form(), word.getExample(), word.getAudio(), word.isIs_fav(), word.getTopic_id(), word.status, word.user_id);
                        } else {
                            updateWordByTopicToFireStore(word.getId(), word.getEnglish_word(), word.getVietnamese_word(), word.getSpelling(), word.getDate(), word.getImage(), word.getWord_form(), word.getExample(), word.getAudio(), word.isIs_fav(), word.getTopic_id(), word.status, word.user_id);
                        }
                        updateWords(word);
                    }


                    // Recycle the original Bitmap
                    photo.recycle();
                }
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
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


    public void showBottomDialogOptionTopic(Words word, int positionWord, ArrayList<Words> words) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_topic_option);
        RecyclerView recyclerViewTopic = dialog.findViewById(R.id.recyclerViewTopic);
        TextView cancelButton = dialog.findViewById(R.id.cancelButton);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        topics.clear();
        displayTopicByUserIdData();

        adapterOptionTopics = new AdapterOptionTopics(topics, context, word, dialog, db, positionWord, words, fragment, this, hashMapWordInTopic, adapterTopics, user_id);

        recyclerViewTopic.setAdapter(adapterOptionTopics);
        recyclerViewTopic.setLayoutManager(new LinearLayoutManager(context));

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



    private void displayTopicByUserIdData() {

        db.collection("Topics").whereEqualTo("user_id", user_id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        topics.clear();
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            int id = Integer.parseInt(documentSnapshot.get("id").toString());
                            String title = documentSnapshot.get("title").toString();
                            boolean is_public = Boolean.parseBoolean(documentSnapshot.get("is_public").toString());

                            Topic topic = new Topic(id, title, is_public, user_id, fragment.getEmail());
                            topics.add(topic);
                        }
                        adapterOptionTopics.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Load data failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateWordToFireStore(int id, String english_word, String vietnamese_word, String spelling, String date, Bitmap photo, String word_form, String example, String audio, boolean is_fav, int topic_id, String status, String user_id) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();
        String photoBase64 = Base64.encodeToString(photoData, Base64.DEFAULT);
        db.collection("Words").document(String.valueOf(id)).update("english_word", english_word, "vietnamese_word", vietnamese_word, "spelling", spelling, "date", date, "photo", photoBase64, "word_form", word_form, "example", example, "audio", audio, "is_fav", is_fav, "topic_id", topic_id, "status", status, "user_id", user_id)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Cập nhật từ thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cập nhật từ thất bại", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Cập nhật từ thất bại", Toast.LENGTH_SHORT).show();
                        });
    }

    public void updateFavWordToFireStore(int id, boolean is_fav) {
        db.collection("Words").document(String.valueOf(id)).update("is_fav", is_fav)
                .addOnCompleteListener(task -> {
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Cập nhật từ thất bại", Toast.LENGTH_SHORT).show();
                        });
    }

    public void updateWordByTopicToFireStore(int id, String english_word, String vietnamese_word, String spelling, String date, Bitmap photo, String word_form, String example, String audio, boolean is_fav, int topic_id, String status, String user_id) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();
        String photoBase64 = Base64.encodeToString(photoData, Base64.DEFAULT);
        db.collection("Topics").document(String.valueOf(topic_id)).collection("Words").document(String.valueOf(id)).update("english_word", english_word, "vietnamese_word", vietnamese_word, "spelling", spelling, "date", date, "photo", photoBase64, "word_form", word_form, "example", example, "audio", audio, "is_fav", is_fav, "topic_id", topic_id, "status", status, "user_id", user_id)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Cập nhật từ thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cập nhật từ thất bại", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Cập nhật từ thất bại", Toast.LENGTH_SHORT).show();
                        });
    }

    private void updateFavWordByTopicToFireStore(int id, boolean is_fav) {
        db.collection("Topics").document(String.valueOf(topic_id)).collection("Words").document(String.valueOf(id)).update("is_fav", is_fav)
                .addOnCompleteListener(task -> {

                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Cập nhật từ thất bại", Toast.LENGTH_SHORT).show();
                        });
//        topic_id = 0;
    }


    public void deleteWordFromFireStore(int position) {
        Words word = words.get(position);
        db.collection("Words").document(String.valueOf(word.getId())).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                        });
    }

    public void deleteWordByIdFromFireStore(int id, FirebaseFirestore db) {
        db.collection("Words").document(String.valueOf(id)).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        if (onDeleteCompleteListener != null) {
//                            onDeleteCompleteListener.onDeleteComplete(id);
//                        }
                    } else {
                        Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                        });
    }

    private void deleteWordByTopicFromFireStore(int id, FirebaseFirestore db) {
        db.collection("Topics").document(String.valueOf(topic_id)).collection("Words").document(String.valueOf(id)).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                        });
    }

    private void getImageByWordInTopicById(int id, FirebaseFirestore db) {
        db.collection("Topics").document(String.valueOf(topic_id)).collection("Words").document(String.valueOf(id)).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String photoBase64 = task.getResult().get("photo").toString();
                        byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageViewChangePhoto.setImageBitmap(decodedByte);
                    } else {
                        Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                        });
    }

    public interface BitmapCallback {
        void onBitmapLoaded(Bitmap bitmap);
    }


    public void getBitmapImageByWordInTopicById(int id, FirebaseFirestore db, BitmapCallback callback) {
        db.collection("Topics").document(String.valueOf(topic_id)).collection("Words").document(String.valueOf(id)).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String photoBase64 = task.getResult().get("photo").toString();
                        byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        callback.onBitmapLoaded(decodedByte);
                    } else {
                        Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Xóa từ thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void UpdateWordInTopicInFolderToFireStore(int id, String english_word, String vietnamese_word, String spelling, String date, Bitmap photo, String word_form, String example, String audio, boolean is_fav, int topic_id, String status, String user_id, int idFolder) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();
        String photoBase64 = Base64.encodeToString(photoData, Base64.DEFAULT);
        db.collection("Folders").document(String.valueOf(idFolder)).collection("Topics").document(String.valueOf(topic_id)).collection("Words").document(String.valueOf(id)).update("english_word", english_word, "vietnamese_word", vietnamese_word, "spelling", spelling, "date", date, "photo", photoBase64, "word_form", word_form, "example", example, "audio", audio, "is_fav", is_fav, "topic_id", topic_id, "status", status, "user_id", user_id)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Cập nhật từ thành công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                        e -> {
                            Toast.makeText(context, "Cập nhật từ thất bại", Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "onFailure: " + e.getMessage());
                        });
    }







}
