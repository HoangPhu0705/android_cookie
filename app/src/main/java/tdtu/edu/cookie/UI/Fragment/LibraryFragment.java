package tdtu.edu.cookie.UI.Fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.Repository.Repository;
import tdtu.edu.cookie.UI.Activity.FlashCard;
import tdtu.edu.cookie.UI.Activity.SignIn;
import tdtu.edu.cookie.UI.Adapter.AdapterExportTopics;
import tdtu.edu.cookie.UI.Adapter.AdapterFavWords;
import tdtu.edu.cookie.UI.Activity.Quiz;
import tdtu.edu.cookie.UI.Adapter.AdapterOptionTopics;
import tdtu.edu.cookie.UI.Adapter.AdapterTopics;
import tdtu.edu.cookie.UI.Adapter.AdapterWords;
import tdtu.edu.cookie.UI.Adapter.CallBackFireStore;
import tdtu.edu.cookie.UI.Adapter.CallBackItemTouch;
import tdtu.edu.cookie.UI.Adapter.CallBackTopicTouch;
import tdtu.edu.cookie.UI.Adapter.MyItemTouchHelperCallBack;
import tdtu.edu.cookie.UI.Adapter.TopicTouchHelperCallBack;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;
import tdtu.edu.cookie.ViewModel.TopicVM;
import tdtu.edu.cookie.ViewModel.WordVM;
import tdtu.edu.cookie.databinding.FragmentLibraryBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment implements CallBackItemTouch, CallBackTopicTouch, AdapterExportTopics.ListOwner {
    TopicVM topicVM;
    WordVM wordVM;

    HashMap<Integer, List<Words>> hashMapWordInTopic = new HashMap<>();
    ArrayList<Words> words = new ArrayList<Words>();
    ArrayList<Topic> topics = new ArrayList<Topic>();
    ArrayList<Words> wordsFav = new ArrayList<Words>();
    AdapterWords adapter;
    AdapterFavWords adapterFavWords;
    Vibrator vibrator;
    TextInputEditText editTextVocabulary;
    EditText editTextDefinition;

    AdapterTopics adapterTopics;
    AdapterOptionTopics adapterOptionTopics;

    AdapterExportTopics adapterExportTopics;
    ImageView image;
    TextView textViewTopic;
    private FirebaseFirestore db;
    private LoadingDialog loadingDialog;
    public TextView numOfVocab;
    TextView numOfTopic;
    SignIn signIn;
    String user_id;

    Repository repository;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentLibraryBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LibraryFragment() {
        // Required empty public constructor
    }

    public LibraryFragment(String user_id) {
        // Required empty public constructor
        this.user_id = user_id;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(requireContext());
        RecyclerView recyclerView = binding.recyclerView;
        RecyclerView recyclerView1 = binding.recyclerView1;
        NestedScrollView scrollView = binding.scrollView;
        FrameLayout toolbar = binding.toolbar;
        TextView textViewTitle = binding.textViewTitle;
        ImageView createTopic = binding.createTopic;
        ImageView wordFavorite = binding.wordFavorite;

        ImageView createFolder = binding.createFolder;
        com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton createVocabulary = binding.createVocabulary;
        numOfVocab = binding.numOfVocab;
        numOfTopic = binding.numOfTopic;
        textViewTopic = binding.textViewTopic;
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        db = FirebaseFirestore.getInstance();
        adapterFavWords = new AdapterFavWords(wordsFav, getContext(), this, 0);

        if (wordVM == null) {
            wordVM = new ViewModelProvider(requireActivity()).get(WordVM.class);
        }
        if (topicVM == null) {
            topicVM = new ViewModelProvider(requireActivity()).get(TopicVM.class);
        }


//        repository = new Repository(getActivity().getApplication());
        createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(5);
                showBottomDialogCreateTopic();
            }
        });

        wordFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(5);
                showBottomDialogFavoriteWord();
            }
        });

        createVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(5);
                showBottomDialog();
            }
        });


//        words.clear();
        if (words.isEmpty()) {
            displayWordByUserId(new CallBackFireStore() {
                @Override
                public void onDataLoaded(List<?> data) {
                    if (!data.isEmpty() && data.get(0) instanceof Words) {
                        // Handle Words
                        Log.d(TAG, "Data loaded successfully!");
                        numOfVocab.setText(Integer.toString(words.size()));
                    }
                }

                @Override
                public void onDataLoadFailed(String errorMessage) {
                    // Handle the failure case
                    Log.e(TAG, "Data loading failed: " + errorMessage);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            numOfVocab.setText(Integer.toString(words.size()));

        }
        adapter = new AdapterWords(words, getContext(), this, 0, hashMapWordInTopic, user_id);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (isAdded())
                        {
                            if (scrollView.getScrollY() > 150) {
                                toolbar.setBackground(getResources().getDrawable(R.drawable.border_bottom_white_bg));
                                textViewTitle.setTextColor(getResources().getColor(R.color.cookie));
                                createFolder.setImageResource(R.drawable.baseline_create_new_folder_24_scrolled);
                                wordFavorite.setImageResource(R.drawable.heart_header_active);
                            } else {
                                toolbar.setBackgroundColor(getResources().getColor(R.color.background));
                                textViewTitle.setTextColor(getResources().getColor(R.color.coffee));
                                createFolder.setImageResource(R.drawable.baseline_create_new_folder_24);
                                wordFavorite.setImageResource(R.drawable.heart_header);
                            }
                        }
                        else {
                            Log.d(TAG, "onScrollChanged: Fragment not attached to activity");
                        }

                    }
                });
        LinearLayoutManager myLinearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView1.setAdapter(adapter);
        recyclerView1.setLayoutManager(myLinearLayoutManager);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallBack(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView1);


        binding.fileHandleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileOption();
            }
        });



        if (topics.isEmpty()) {
            loadingDialog.show();
            displayTopicByUserId( new CallBackFireStore() {
                @Override
                public void onDataLoaded(List<?> data) {
                    if (!data.isEmpty() && data.get(0) instanceof Topic) {
                        // Handle Topics
                        Log.d(TAG, "Data loaded successfully!");
                        numOfTopic.setText(Integer.toString(topics.size()));
                    }
                }
                private void displayTopicByUserId(CallBackFireStore callback) {

                    db.collection("Topics").whereEqualTo("user_id", user_id).get()
                            .addOnCompleteListener(task -> {

                                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                            int id = Integer.parseInt(documentSnapshot.get("id").toString());
                                            String title = documentSnapshot.get("title").toString();
                                            boolean is_public = Boolean.parseBoolean(documentSnapshot.get("is_public").toString());
                                            String user_id = documentSnapshot.get("user_id").toString();
                                            String email = documentSnapshot.get("email").toString();

                                            Topic topic = new Topic(id, title, is_public, user_id, email);
                                            topics.add(topic);
                                        }
                                        adapterTopics.notifyDataSetChanged();
                                        callback.onDataLoaded(topics);
                                        loadingDialog.cancel();
                                    }
                            ).addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                                    }
                            );
                }
                @Override
                public void onDataLoadFailed(String errorMessage) {
                    // Handle the failure case
                    Log.e(TAG, "Data loading failed: " + errorMessage);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            numOfTopic.setText(Integer.toString(topics.size()));
        }
        adapterTopics = new AdapterTopics(topics, getContext(), this, hashMapWordInTopic, user_id);
        adapter.setAdapterTopics(adapterTopics);
        recyclerView.setAdapter(adapterTopics);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        ItemTouchHelper.Callback callbackTopic = new TopicTouchHelperCallBack(this);
        ItemTouchHelper itemTouchHelperTopic = new ItemTouchHelper(callbackTopic);
        itemTouchHelperTopic.attachToRecyclerView(recyclerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;

    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_create_vocabulary);
        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        TextView add = dialog.findViewById(R.id.addTopic);
        TextInputLayout textInputLayoutDefinition = dialog.findViewById(R.id.textInputLayoutDefinition);

        editTextVocabulary = dialog.findViewById(R.id.editTextVocabulary);
        image = dialog.findViewById(R.id.image);
        editTextVocabulary.requestFocus();
        editTextDefinition = dialog.findViewById(R.id.editTextDefinition);
        MaterialButtonToggleGroup toggleButton = dialog.findViewById(R.id.toggleButton);
        final String[] wordForm = new String[1];


        textInputLayoutDefinition.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showBottomDialogImageOption();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        toggleButton.check(R.id.buttonVerb);
        wordForm[0] = "verb";
        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId == R.id.buttonVerb) {
                    wordForm[0] = "verb";
                } else if (checkedId == R.id.buttonNoun) {
                    wordForm[0] = "noun";
                } else if (checkedId == R.id.buttonAdj) {
                    wordForm[0] = "adjective";
                } else if (checkedId == R.id.buttonAdv) {
                    wordForm[0] = "adverb";
                }

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextVocabulary.getText().toString().equals("") || editTextDefinition.getText().toString().equals("")) {
                    Toast.makeText(requireContext(), "Từ vựng và định nghĩa không được bỏ trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadingDialog.show();

                String url = "https://api.dictionaryapi.dev/api/v2/entries/en/";

                String urlAudio = "https://translate.google.com/translate_tts?ie=UTF-8&q=";


                ImageView image = dialog.findViewById(R.id.image);
                image.buildDrawingCache();
                String vocabulary = editTextVocabulary.getText().toString();
                String definition = editTextDefinition.getText().toString();
//                String spelling = "/həˈloʊ/";
//                final String[] spelling = new String[1];
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDateTime ldt = LocalDateTime.now();
                String date = dtf.format(ldt);

                boolean isFav = false;
                int topicId = 0;
                String urlVocab = url + vocabulary;

                final String[] spelling = new String[1];
                RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlVocab, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (!jsonObject.isNull("phonetic")) {
                                spelling[0] = jsonObject.getString("phonetic");
                            } else {
                                spelling[0] = "/transcription/";
                            }
                            image.setDrawingCacheEnabled(true);
                            Bitmap photo = image.getDrawingCache();
                            String example = "";
                            JSONArray jsonArrayMeaning = jsonObject.getJSONArray("meanings");

                            for (int i = 0; i < jsonArrayMeaning.length(); i++) {
                                JSONObject jsonObjectMeaning = jsonArrayMeaning.getJSONObject(i);
                                String partOfSpeech = jsonObjectMeaning.getString("partOfSpeech");
                                if (partOfSpeech.equals(wordForm[0])) {
                                    JSONArray jsonArrayDefinition = jsonObjectMeaning.getJSONArray("definitions");
                                    for (int j = 0; j < jsonArrayDefinition.length(); j++) {
                                        JSONObject jsonObjectDefinition = jsonArrayDefinition.getJSONObject(j);

                                        if (!jsonObjectDefinition.isNull("example")) {
                                            example = jsonObjectDefinition.getString("example");
                                            break;
                                        } else {
                                            example = "There is no example for this word!";
                                        }
                                    }
                                }
                            }

                            String audio = urlAudio + vocabulary + "&tl=en&client=tw-ob";
                            int id = 1;
                            if (!words.isEmpty()) {
                                id = words.get(words.size() - 1).getId() + 1;
                            }
                            long currentTimestamp = System.currentTimeMillis();
                            int word_id = Math.abs((user_id + id + currentTimestamp).hashCode());
                            String status = "Chưa học";
                            Words word = new Words(word_id, vocabulary, definition, spelling[0], date, photo, wordForm[0], example, audio, isFav, topicId, status, user_id);
                            addWordToFireStore(word_id, vocabulary, definition, spelling[0], date, photo, wordForm[0], example, audio, isFav, topicId, status, user_id);
                            adapter.addWords(word);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("error API", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spelling[0] = "/transcription/";
                        image.setDrawingCacheEnabled(true);
                        Bitmap photo = image.getDrawingCache();
                        String example = "There is no example for this word!";
                        String audio = urlAudio + vocabulary + "&tl=en&client=tw-ob";
                        int id = 1;
                        if (!words.isEmpty()) {
                            id = words.get(words.size() - 1).getId() + 1;
                        }
                        long currentTimestamp = System.currentTimeMillis();
                        int word_id = Math.abs((user_id + id + currentTimestamp).hashCode());
                        String status = "Chưa học";
                        Words word = new Words(word_id, vocabulary, definition, spelling[0], date, photo, wordForm[0], example, audio, isFav, topicId, status, user_id);
                        addWordToFireStore(word_id, vocabulary, definition, spelling[0], date, photo, wordForm[0], example, audio, isFav, topicId, status, user_id);
                        adapter.addWords(word);

                    }
                });

                requestQueue.add(stringRequest);

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
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showBottomDialogCreateTopic() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_create_topic);
        TextView cancelButtonTopic = dialog.findViewById(R.id.cancelButtonTopic);
        TextView addButtonTopic = dialog.findViewById(R.id.addButtonTopic);
        EditText editTextTopic = dialog.findViewById(R.id.editTextTopic);

        addButtonTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextTopic.getText().toString().equals("")) {
                    Toast.makeText(requireContext(), "Tên chủ đề không được bỏ trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingDialog.show();
                int id = 1;
                if (!topics.isEmpty()) {
                    id = topics.get(topics.size() - 1).getId() + 1;
                }
                long currentTimestamp = System.currentTimeMillis();
                int topic_id = Math.abs((user_id + id + currentTimestamp).hashCode());
                String topicName = editTextTopic.getText().toString();
                boolean is_public = false;
                Topic topic = new Topic(topic_id, topicName, is_public, user_id, getEmail());
                addTopicToFireStore(topic_id, topicName, is_public, user_id, getEmail());
                adapterTopics.addTopic(topic);
//                topicVM.insert(topic);
                dialog.dismiss();
            }
        });

        cancelButtonTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

    @Override
    public void itemTouchOnMove(int oldPosition, int newPosition) {
//        words.add(newPosition, words.remove(oldPosition));
//        adapter.notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
        if (!words.isEmpty()) {
            String word = words.get(viewHolder.getAdapterPosition()).getEnglish_word();

            //backup of removed item for undo purpose
            final Words deletedItem = words.get(viewHolder.getAdapterPosition());
            int deletedIndex = viewHolder.getAdapterPosition();
            //remove the item from recyclerview
//        wordVM.delete(deletedItem);
            adapter.deleteWordFromFireStore(deletedIndex);
            adapter.removeItem(deletedIndex);
            numOfVocab.setText(Integer.toString(words.size()));

            //show snackbar with undo option
            Snackbar snackbar = Snackbar.make(binding.getRoot(), word + " bị xóa khỏi mục Từ vựng!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
//                    wordVM.insert(deletedItem);
                    addWordToFireStore(deletedItem.getId(), deletedItem.getEnglish_word(), deletedItem.getVietnamese_word(), deletedItem.getSpelling(), deletedItem.getDate(), deletedItem.getImage(), deletedItem.getWord_form(), deletedItem.getExample(), deletedItem.getAudio(), deletedItem.isIs_fav(), deletedItem.getTopic_id(), deletedItem.getStatus(), deletedItem.getUser_id());
                    adapter.restoreItem(deletedItem, deletedIndex);
                    numOfVocab.setText(Integer.toString(words.size()));
                }
            });
            snackbar.setActionTextColor(Color.parseColor("#AA5656"));


            snackbar.show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");   // get photo
            image.setImageBitmap(photo);
//            imageViewChangePhoto.setImageBitmap(photo);
        }
        if (requestCode == 124 && data != null) {
            Uri uri = data.getData();
            image.setImageURI(uri);
        }
        if (requestCode == 125 && data != null) {
            if (adapter.imageViewChangePhoto != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");   // get photo
                adapter.imageViewChangePhoto.setImageBitmap(photo);
            }
        }

        if (requestCode == 126 && data != null) {
            if (adapter.imageViewChangePhoto != null) {
                Uri uri = data.getData();
                adapter.imageViewChangePhoto.setImageURI(uri);
            }
        }


        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Words");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("English Word");
                headerRow.createCell(1).setCellValue("Vietnamese Word");
                headerRow.createCell(2).setCellValue("Spelling");
                headerRow.createCell(3).setCellValue("Word Form");
                headerRow.createCell(4).setCellValue("Example");


                for (int i = 0; i < words.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Words word = words.get(i);
                    row.createCell(0).setCellValue(word.english_word);
                    row.createCell(1).setCellValue(word.vietnamese_word);
                    row.createCell(2).setCellValue(word.spelling);
                    row.createCell(3).setCellValue(word.word_form);
                    row.createCell(4).setCellValue(word.example);
                }


                try (OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri)) {
                    FancyToast.makeText(getContext(), "Lưu file thành công", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    workbook.write(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                // Handle export failure

            }
        }


        if(requestCode == 11 && resultCode == Activity.RESULT_OK)  {
            if(data != null){
                Uri uri = data.getData();
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                    Workbook workbook = new XSSFWorkbook(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);



                    String urlAudio = "https://translate.google.com/translate_tts?ie=UTF-8&q=";

                    long currentTimestamp = System.currentTimeMillis();
                    int topic_id = Math.abs((user_id + 1 + currentTimestamp).hashCode());


                    String topicName = getFileName(uri);
                    String photoBase64 = "/9j/4AAQSkZJRgABAQAAAQABAAD/4gHYSUNDX1BST0ZJTEUAAQEAAAHIAAAAAAQwAABtbnRyUkdCIFhZWiAH4AABAAEAAAAAAABhY3NwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAA9tYAAQAAAADTLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAlkZXNjAAAA8AAAACRyWFlaAAABFAAAABRnWFlaAAABKAAAABRiWFlaAAABPAAAABR3dHB0AAABUAAAABRyVFJDAAABZAAAAChnVFJDAAABZAAAAChiVFJDAAABZAAAAChjcHJ0AAABjAAAADxtbHVjAAAAAAAAAAEAAAAMZW5VUwAAAAgAAAAcAHMAUgBHAEJYWVogAAAAAAAAb6IAADj1AAADkFhZWiAAAAAAAABimQAAt4UAABjaWFlaIAAAAAAAACSgAAAPhAAAts9YWVogAAAAAAAA9tYAAQAAAADTLXBhcmEAAAAAAAQAAAACZmYAAPKnAAANWQAAE9AAAApbAAAAAAAAAABtbHVjAAAAAAAAAAEAAAAMZW5VUwAAACAAAAAcAEcAbwBvAGcAbABlACAASQBuAGMALgAgADIAMAAxADb" +
                                            "/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAFoASwDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFAEBAAAAAAAAAAAAAAAAAAAAAP/EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhEDEQA/AJ/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                                            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/Z";
                    byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    Topic topic = new Topic(topic_id, topicName, false, user_id, getEmail());
                    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    List<Words> listOfWords = new ArrayList<>();
                    for(int i = 1; i <= sheet.getLastRowNum(); i++){
                        Row row = sheet.getRow(i);
                        String englishWord = row.getCell(0).getStringCellValue();
                        String vietnameseWord = row.getCell(1).getStringCellValue();
                        String spelling = row.getCell(2).getStringCellValue();
                        String wordForm = row.getCell(3).getStringCellValue();
                        String example = row.getCell(4).getStringCellValue();

                        String audio = urlAudio + englishWord + "&tl=en&client=tw-ob";
                        Words w = new Words(1, englishWord, vietnameseWord, spelling, date, decodedByte,
                                                wordForm, example, audio,
                                            false, topic_id, "Đang học", user_id);

                        listOfWords.add(w);


                    }



                    addTopicAndWordToFireStore(topic_id, topicName, false, user_id, getEmail(), listOfWords);
                    adapterTopics.addTopic(topic);



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri != null) {
            String path = uri.getPath();
            if (path != null) {
                int startIndex = path.lastIndexOf('/');
                if (startIndex != -1) {
                    fileName = path.substring(startIndex + 1);

                    // Remove file extension if present
                    int extensionIndex = fileName.lastIndexOf('.');
                    if (extensionIndex != -1) {
                        fileName = fileName.substring(0, extensionIndex);
                    }
                }
            }
        }
        return fileName;
    }

    public void showBottomDialogImageOption() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_image_option);

        LinearLayout linearLayoutTakePhoto = dialog.findViewById(R.id.linearLayoutTakePhoto);
        LinearLayout linearLayoutChoosePhoto = dialog.findViewById(R.id.linearLayoutChoosePhoto);

        linearLayoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent();
                    i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, 123);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        linearLayoutChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 124);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showFileOption() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_file_option);

        LinearLayout importBtn = dialog.findViewById(R.id.import_option);
        LinearLayout exportBtn = dialog.findViewById(R.id.export_option);


        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialogOptionTopic();
            }
        });

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                startActivityForResult(intent, 11);
            }
        });







        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    public void showBottomDialogImageOptionForChange() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_image_option);

        LinearLayout linearLayoutTakePhoto = dialog.findViewById(R.id.linearLayoutTakePhoto);
        LinearLayout linearLayoutChoosePhoto = dialog.findViewById(R.id.linearLayoutChoosePhoto);

        linearLayoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent();
                    i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, 125);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        linearLayoutChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 126);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void topicTouchOnMove(int oldPosition, int newPosition) {
//        topics.add(newPosition, topics.remove(oldPosition));
//        adapterTopics.notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onSwipedTopic(RecyclerView.ViewHolder viewHolder, int position) {

        String topic = topics.get(viewHolder.getAdapterPosition()).getTitle();

        //backup of removed item for undo purpose
        final Topic deletedTopic = topics.get(viewHolder.getAdapterPosition());
        final int deletedIndexTopic = viewHolder.getAdapterPosition();


        //remove the item from recyclerview
//        adapterTopics.deleteTopicFromFireStore(deletedIndexTopic);
//        adapterTopics.deleteTopicAndWordInTopicFromFireStore(deletedIndexTopic);
        adapterTopics.deleteTopic(deletedIndexTopic);
        adapterTopics.removeItem(viewHolder.getAdapterPosition());
        numOfTopic.setText(Integer.toString(topics.size()));

        //show snackbar with undo option
        Snackbar snackbar = Snackbar.make(binding.getRoot(), topic + " bị xóa khỏi mục Chủ đề!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                addTopicToFireStore(deletedTopic.getId(), deletedTopic.getTitle(), deletedTopic.getIs_public(), deletedTopic.getUser_id(), deletedTopic.getEmail());
                adapterTopics.restoreItem(deletedTopic, deletedIndexTopic);
                numOfTopic.setText(Integer.toString(topics.size()));
            }
        });
        snackbar.setActionTextColor(Color.parseColor("#AA5656"));
        snackbar.setBackgroundTint(Color.parseColor("#80000000"));
        snackbar.show();

    }

    //function to display data to recylerview
    public void displayData(CallBackFireStore callback) {
        loadingDialog.show();
        words.clear();
        db.collection("Words").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            int id = Integer.parseInt(documentSnapshot.get("id").toString());
                            String english_word = documentSnapshot.get("english_word").toString();
                            String vietnamese_word = documentSnapshot.get("vietnamese_word").toString();
                            String spelling = documentSnapshot.get("spelling").toString();
                            String date = documentSnapshot.get("date").toString();
                            String photoBase64 = documentSnapshot.get("photo").toString();
                            String word_form = documentSnapshot.get("word_form").toString();
                            String example = documentSnapshot.get("example").toString();
                            String audio = documentSnapshot.get("audio").toString();
                            boolean is_fav = Boolean.parseBoolean(documentSnapshot.get("is_fav").toString());
                            int topic_id = Integer.parseInt(documentSnapshot.get("topic_id").toString());
                            String status = documentSnapshot.get("status").toString();

                            byte[] photoData = Base64.decode(photoBase64, Base64.DEFAULT);
                            Bitmap photo = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);

                            Words word = new Words(id, english_word, vietnamese_word, spelling, date, photo, word_form, example, audio, is_fav, topic_id, status, user_id);
                            words.add(word);

                        }
                        adapter.notifyDataSetChanged();
                        callback.onDataLoaded(words);
                        loadingDialog.cancel();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        callback.onDataLoadFailed("Lỗi kết nối");
                    }
                });
    }

    public void displayWordByUserId(CallBackFireStore callback) {
        loadingDialog.show();
        words.clear();
        db.collection("Words").whereEqualTo("user_id", user_id).get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        int id = Integer.parseInt(documentSnapshot.get("id").toString());
                        String english_word = documentSnapshot.get("english_word").toString();
                        String vietnamese_word = documentSnapshot.get("vietnamese_word").toString();
                        String spelling = documentSnapshot.get("spelling").toString();
                        String date = documentSnapshot.get("date").toString();
                        String photoBase64 = documentSnapshot.get("photo").toString();
                        String word_form = documentSnapshot.get("word_form").toString();
                        String example = documentSnapshot.get("example").toString();
                        String audio = documentSnapshot.get("audio").toString();
                        boolean is_fav = Boolean.parseBoolean(documentSnapshot.get("is_fav").toString());
                        int topic_id = Integer.parseInt(documentSnapshot.get("topic_id").toString());
                        String status = documentSnapshot.get("status").toString();

                        byte[] photoData = Base64.decode(photoBase64, Base64.DEFAULT);
                        Bitmap photo = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);

                        Words word = new Words(id, english_word, vietnamese_word, spelling, date, photo, word_form, example, audio, is_fav, topic_id, status, user_id);
                        words.add(word);

                    }
                    adapter.notifyDataSetChanged();
                    callback.onDataLoaded(words);
                    loadingDialog.cancel();
                }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            callback.onDataLoadFailed("Lỗi kết nối");
        });
    }


    private void displayTopicByUserId(CallBackFireStore callback) {

        db.collection("Topics").whereEqualTo("user_id", user_id).get()
                .addOnCompleteListener(task -> {

                            for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                int id = Integer.parseInt(documentSnapshot.get("id").toString());
                                String title = documentSnapshot.get("title").toString();
                                boolean is_public = Boolean.parseBoolean(documentSnapshot.get("is_public").toString());
                                String user_id = documentSnapshot.get("user_id").toString();
                                String email = documentSnapshot.get("email").toString();

                                Topic topic = new Topic(id, title, is_public, user_id, email);
                                topics.add(topic);
                            }
                            adapterTopics.notifyDataSetChanged();
                            callback.onDataLoaded(topics);
                            loadingDialog.cancel();
                        }
                ).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                );
    }

    //function to display all isFav Word and Word in Topic
    public void displayFavWord() {
//        loadingDialog.show();
        wordsFav.clear();
        db.collection("Words").whereEqualTo("is_fav", true).get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        int id = Integer.parseInt(documentSnapshot.get("id").toString());
                        String english_word = documentSnapshot.get("english_word").toString();
                        String vietnamese_word = documentSnapshot.get("vietnamese_word").toString();
                        String spelling = documentSnapshot.get("spelling").toString();
                        String date = documentSnapshot.get("date").toString();
                        String photoBase64 = documentSnapshot.get("photo").toString();
                        String word_form = documentSnapshot.get("word_form").toString();
                        String example = documentSnapshot.get("example").toString();
                        String audio = documentSnapshot.get("audio").toString();
                        boolean is_fav = Boolean.parseBoolean(documentSnapshot.get("is_fav").toString());
                        int topic_id = Integer.parseInt(documentSnapshot.get("topic_id").toString());
                        String status = documentSnapshot.get("status").toString();

                        byte[] photoData = Base64.decode(photoBase64, Base64.DEFAULT);
                        Bitmap photo = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);

                        Words word = new Words(id, english_word, vietnamese_word, spelling, date, photo, word_form, example, audio, is_fav, topic_id, status, user_id);
                        wordsFav.add(word);

                    }
                    adapterFavWords.notifyDataSetChanged();

//                    loadingDialog.cancel();
                }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
        });
    }

    public void displayIsFavWordInTopic(Topic topic) {
        AtomicInteger size = new AtomicInteger(wordsFav.size());
        db.collection("Topics").document(String.valueOf(topic.getId())).collection("Words").whereEqualTo("is_fav", true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Words> wordsList = task.getResult().toObjects(Words.class);
                        wordsFav.addAll(wordsList);
                        size.set(wordsFav.size());
                        adapterFavWords.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }



    public void addWordToFireStore(int id, String english_word, String vietnamese_word, String spelling, String date, Bitmap photo, String word_form, String example, String audio, boolean is_fav, int topic_id, String status, String user_id) {
        if (!english_word.isEmpty()) {
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


            db.collection("Words").document(String.valueOf(id)).set(word).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
//                            Toast.makeText(requireContext(), "Thêm từ thành công", Toast.LENGTH_SHORT).show();
                            numOfVocab.setText(Integer.toString(words.size()));
                            loadingDialog.cancel();
                        }
                    }
            ).addOnFailureListener(
                    e -> {
                        Toast.makeText(requireContext(), "Thêm từ thất bại", Toast.LENGTH_SHORT).show();
                        Log.e("error", e.getMessage());
                    }
            );
        }

    }

    private void addTopicAndWordToFireStore(int id, String topicName, boolean is_public, String user_id, String email, List<Words> listOfWords) {
        HashMap<String, Object> topic = new HashMap<>();
        topic.put("id", id);
        topic.put("title", topicName);
        topic.put("is_public", is_public);
        topic.put("user_id", user_id);
        topic.put("email", email);

        db.collection("Topics").document(String.valueOf(id)).set(topic).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        addWordsToTopicSubcollection(id, listOfWords);
                        adapterTopics.notifyDataSetChanged();

                    }
                }
        ).addOnFailureListener(
                e -> {
                    Toast.makeText(requireContext(), "Thêm chủ đề thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("error", e.getMessage());
                }
        );
    }


    private void addWordsToTopicSubcollection(int topicId, List<Words> listOfWords) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicWordsCollection = db.collection("Topics").document(String.valueOf(topicId)).collection("Words");

        // Add each word to the sub-collection
        for (Words word : listOfWords) {
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
            wordMap.put("user_id", user_id);

            topicWordsCollection.document(String.valueOf(word.getId())).set(wordMap)
                    .addOnCompleteListener(subcollectionTask -> {
                        if (subcollectionTask.isSuccessful()) {
                            // Handle the success of adding a word to the sub-collection


                        }
                        FancyToast.makeText(getContext(), "Nhập topic thành công", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();


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


    private void addTopicToFireStore(int id, String topicName, boolean is_public, String user_id, String email) {
        HashMap<String, Object> topic = new HashMap<>();
        topic.put("id", id);
        topic.put("title", topicName);
        topic.put("is_public", is_public);
        topic.put("user_id", user_id);
        topic.put("email", email);

        db.collection("Topics").document(String.valueOf(id)).set(topic).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        numOfTopic.setText(Integer.toString(topics.size()));
                        loadingDialog.cancel();

                    }
                }
        ).addOnFailureListener(
                e -> {
                    Toast.makeText(requireContext(), "Thêm chủ đề thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("error", e.getMessage());
                }
        );
    }

    private void showBottomDialogFavoriteWord(){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_favorite);

        RecyclerView recyclerViewFavoriteVocabulary = dialog.findViewById(R.id.recyclerViewFavoriteVocabulary);
        ImageView backIcon = dialog.findViewById(R.id.backIcon);
        NestedScrollView nestedScrollViewFavoriteVocabulary = dialog.findViewById(R.id.nestedScrollViewFavoriteVocabulary);
        LinearLayoutManager myLinearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };


        displayFavWord();
        for (int i = 0; i < topics.size(); i++) {
            displayIsFavWordInTopic(topics.get(i));
        }
        if (wordsFav.size() <= 6)
        {
            nestedScrollViewFavoriteVocabulary.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2500));
        }
        else {
            nestedScrollViewFavoriteVocabulary.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        recyclerViewFavoriteVocabulary.setLayoutManager(myLinearLayoutManager);
        recyclerViewFavoriteVocabulary.setAdapter(adapterFavWords);
        adapterFavWords.setWords(wordsFav);
        adapterFavWords.notifyDataSetChanged();


        backIcon.setOnClickListener(new View.OnClickListener() {
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

    public String getEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getEmail();
        }
        return "";
    }


    public void showBottomDialogOptionTopic() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_export_topic);
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

        adapterExportTopics = new AdapterExportTopics(LibraryFragment.this, requireContext(), topics, LibraryFragment.this);


        recyclerViewTopic.setAdapter(adapterExportTopics);
        recyclerViewTopic.setLayoutManager(new LinearLayoutManager(requireContext()));

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

                            Topic topic = new Topic(id, title, is_public, user_id, getEmail());
                            topics.add(topic);
                        }

                        adapterExportTopics.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Load data failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void push(List<Words> wordList) {
        words.clear();
        words.addAll(wordList);
        adapter.notifyDataSetChanged();
    }
}