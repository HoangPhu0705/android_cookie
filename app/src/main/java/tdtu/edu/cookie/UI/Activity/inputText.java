package tdtu.edu.cookie.UI.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import tdtu.edu.cookie.Database.Entity.Question;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.Database.Entity.wordsQuizz;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;
import tdtu.edu.cookie.databinding.ActivityInputTextBinding;
import tdtu.edu.cookie.databinding.ActivityQuizBinding;

public class inputText extends AppCompatActivity {
    ActivityInputTextBinding binding;
    FirebaseFirestore db;
    int correctCount=0;
    int wrongCount=0;
    int index =0;
    int Timevalue ;
    ArrayList<Question> questionList = new ArrayList<>();
    Question currentQuestion;

    ArrayList<Words> listWords = new ArrayList<>();
    ArrayList<wordsQuizz> wrongWords = new ArrayList<>();
    ArrayList<wordsQuizz> correctWords = new ArrayList<>();
    ArrayList<wordsQuizz> resultList = new ArrayList<>();

    String idTopic;
    LoadingDialog loadingDialog;
    int engQuestion;
    int vietQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_text);
        binding = ActivityInputTextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        index =0;
        idTopic =String.valueOf(getIntent().getIntExtra("idTopic",0)) ;
        engQuestion = getIntent().getIntExtra("engQuestion",0);
        vietQuestion = getIntent().getIntExtra("vietQuestion",0);
        getWordFromFirebase();
        binding.imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }



    public void getWordFromFirebase(){
        db.collection("Topics").document(idTopic).collection("Words").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    int id = Integer.parseInt(snapshot.get("id").toString());
                    String english_word = snapshot.get("english_word").toString();
                    String vietnamese_word = snapshot.get("vietnamese_word").toString();
                    String spelling = snapshot.get("spelling").toString();
                    String date = snapshot.get("date").toString();
                    String word_form = snapshot.get("word_form").toString();
                    String example = snapshot.get("example").toString();
                    String audio = snapshot.get("audio").toString();
                    boolean is_fav = Boolean.parseBoolean(snapshot.get("is_fav").toString());
                    int topic_id = Integer.parseInt(snapshot.get("topic_id").toString());
                    String photoBase64 = snapshot.get("photo").toString();

                    byte[] photoData = Base64.decode(photoBase64, Base64.DEFAULT);
                    Bitmap photo = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);

                    Words word = new Words(id, english_word, vietnamese_word, spelling, date, photo, word_form, example, audio, is_fav, topic_id);
//                    wordsQuizz words = new wordsQuizz( Integer.parseInt(snapshot.getId()),snapshot.getString("english_word"),snapshot.getString("vietnamese_word"));
                    listWords.add(word);
                }
//                loadingDialog.dismiss();
                CreateQuestion();

            }
        });

    }
    public void CreateQuestion(){

        Random random = new Random();
        Toast.makeText(inputText.this, ""+listWords.size(), Toast.LENGTH_SHORT).show();


        Collections.shuffle(listWords);

        for(int i=0; i<engQuestion;i++){
            ArrayList<String> options = new ArrayList<>();

//            options.add(listWords.get(randomInRange1).getVietnamese_word());

            questionList.add(new Question("Từ tiếng việt của: "+listWords.get(i).getEnglish_word(),"",listWords.get(i).getEnglish_word(),listWords.get(i).image));

        }

        for(int i=engQuestion; i<vietQuestion+engQuestion;i++){
            ArrayList<String> options = new ArrayList<>();

//            options.add(listWords.get(randomInRange1).getEnglish_word());

            questionList.add(new Question("Từ tiếng anh của: "+listWords.get(i).getEnglish_word(),"",listWords.get(i).getEnglish_word(),listWords.get(i).image));

        }
        convertToResultList();
        setData();

    }
    public void setData(){
        binding.dapan.setText("");
        binding.imageViewGuess.setImageBitmap(questionList.get(index).image);
        binding.labelQuestion.setText(questionList.get(index).getQuestionContext());
        binding.inputResult.setText("");
        binding.sothutu.setText(String.valueOf(index));
        binding.nextQuestion.setClickable(false);
    }
    public void convertToResultList(){
        for(int i=0; i<listWords.size();i++){
            int id = listWords.get(i).getId();
            String english_word = listWords.get(i).english_word;
            String vietnamese_word = listWords.get(i).vietnamese_word;
            String spelling = listWords.get(i).spelling;
            String date = listWords.get(i).getDate();
            String word_form = listWords.get(i).word_form;
            String example = listWords.get(i).getExample();
            String audio = listWords.get(i).getExample();
            boolean is_fav = listWords.get(i).is_fav;
            int topic_id = listWords.get(i).topic_id;


            wordsQuizz words = new wordsQuizz(id, english_word, vietnamese_word, spelling, date, word_form, example, audio, is_fav, topic_id);
//                    wordsQuizz words = new wordsQuizz( Integer.parseInt(snapshot.getId()),snapshot.getString("english_word"),snapshot.getString("vietnamese_word"));
            resultList.add(words);
        }
    }
    public void correct(){
        correctWords.add(resultList.get(index));

        correctCount++;


    }
    public void wrong(){
        wrongWords.add(resultList.get(index));

        wrongCount++;

    }
    public void btnCheck(View view){
        String result = binding.inputResult.toString();
        if(result.equals(questionList.get(index).getAns())){
            binding.dapan.setText("Correct!");
            correct();
        }
        else{
            binding.dapan.setText("Wrong!");
            wrong();
        }
        binding.nextQuestion.setClickable(true);
    }
    public void btnNext(View view){
        if(index <questionList.size()-1){
            index++;
            setData();
        }
        else{
            Intent intent = new Intent(inputText.this, WinningGame.class);
            intent.putExtra("wrong", wrongCount);
            intent.putExtra("correct", correctCount);
//            intent.putExtra("time", Timevalue);
            intent.putExtra("wrongList", wrongWords);
            intent.putExtra("correctList", correctWords);
            intent.putExtra("totalQues", vietQuestion+engQuestion);
            intent.putExtra("idTopic", idTopic);
            intent.putExtra("quiz", 0);
            startActivityForResult(intent, 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == Activity.RESULT_OK){
                finish();

        }
    }
}