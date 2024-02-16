package tdtu.edu.cookie.UI.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import tdtu.edu.cookie.Database.Entity.Question;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.Database.Entity.wordsQuizz;

import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;
import tdtu.edu.cookie.databinding.ActivityQuizBinding;
import tdtu.edu.cookie.databinding.ActivitySignInBinding;

public class Quiz extends AppCompatActivity {
    CountDownTimer countDownTimer;
    ProgressBar progressBar;
    int correctCount=0;
    int wrongCount=0;
    int index =0;
    int Timevalue ;
    ArrayList<Question> questionList = new ArrayList<>();
    Question currentQuestion;
    ActivityQuizBinding binding;
    FirebaseFirestore db;
    ArrayList<wordsQuizz> listWords = new ArrayList<>();
    ArrayList<wordsQuizz> wrongWords = new ArrayList<>();
    ArrayList<wordsQuizz> correctWords = new ArrayList<>();

    String idTopic;
    LoadingDialog loadingDialog;
    int engQuestion;
    int vietQuestion;
    int is_public;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = findViewById(R.id.progressBar);
        idTopic =String.valueOf(getIntent().getIntExtra("idTopic",0)) ;
        engQuestion = getIntent().getIntExtra("engQuestion",0);
        vietQuestion = getIntent().getIntExtra("vietQuestion",0);
        is_public = getIntent().getIntExtra("is_public",1);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        db = FirebaseFirestore.getInstance();
        getWordFromFirebase();

        Timevalue = 60;
        binding.imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                finish();
            }
        });
        countDownTimer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Timevalue = Timevalue-1;
                progressBar.setProgress(Timevalue);
            }

            @Override
            public void onFinish() {
                Dialog dialog = new Dialog(Quiz.this);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                dialog.setContentView(R.layout.time_out_dialog);
                dialog.show();
                dialog.findViewById(R.id.btn_again).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Quiz.this, MainActivity.class);
                        intent.putExtra("winningGame",is_public);
                        startActivity(intent);
                    }
                });
            }
        }.start();
//        setData();

    }
    public void setData(){
        currentQuestion = questionList.get(index);
        binding.questContext.setText(currentQuestion.getQuestionContext());
        binding.textOpA.setText(currentQuestion.getoA());
        binding.textOpB.setText(currentQuestion.getoB());
        binding.textOpC.setText(currentQuestion.getoC());
        binding.textOpD.setText(currentQuestion.getoD());
        binding.sothutu.setText(String.valueOf(index+1));

        binding.AnswerA.setCardBackgroundColor(getResources().getColor(R.color.white));
        binding.AnswerB.setCardBackgroundColor(getResources().getColor(R.color.white));
        binding.AnswerC.setCardBackgroundColor(getResources().getColor(R.color.white));
        binding.AnswerD.setCardBackgroundColor(getResources().getColor(R.color.white));



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


                    wordsQuizz words = new wordsQuizz(id, english_word, vietnamese_word, spelling, date, word_form, example, audio, is_fav, topic_id);
//                    wordsQuizz words = new wordsQuizz( Integer.parseInt(snapshot.getId()),snapshot.getString("english_word"),snapshot.getString("vietnamese_word"));
                    listWords.add(words);
                }
                loadingDialog.dismiss();
                CreateQuestion();

            }
        });
    }
    public void CreateQuestion(){

        Random random = new Random();


        Collections.shuffle(listWords);

        for(int i=0; i<engQuestion;i++){
            ArrayList<String> options = new ArrayList<>();
            int randomInRange1 = random.nextInt(listWords.size());

            options.add(listWords.get(randomInRange1).getVietnamese_word());
            while(options.size() != 4){
                int randomInRange = random.nextInt(listWords.size());
                String randomWord = listWords.get(randomInRange).getVietnamese_word();

                if (!options.contains(randomWord) && !randomWord.equals(listWords.get(randomInRange1).getVietnamese_word())) {
                    options.add(randomWord);
                }
            }
            Collections.shuffle(options);
            questionList.add(new Question(listWords.get(randomInRange1).getEnglish_word(),options.get(0),options.get(1),options.get(2),options.get(3),listWords.get(randomInRange1).getVietnamese_word()));

        }

        for(int i=0; i<vietQuestion;i++){
            ArrayList<String> options = new ArrayList<>();
            int randomInRange1 = random.nextInt(listWords.size());

            options.add(listWords.get(randomInRange1).getEnglish_word());
            while(options.size() != 4){
                int randomInRange = random.nextInt(listWords.size());
                String randomWord = listWords.get(randomInRange).getEnglish_word();

                if (!options.contains(randomWord) && !randomWord.equals(listWords.get(randomInRange1).getEnglish_word())) {
                    options.add(randomWord);
                }
            }
            Collections.shuffle(options);
            questionList.add(new Question(listWords.get(randomInRange1).getVietnamese_word(),options.get(0),options.get(1),options.get(2),options.get(3),listWords.get(randomInRange1).getEnglish_word()));

        }
setData();  }

    public void disableOption(){
        binding.AnswerA.setClickable(false);
        binding.AnswerB.setClickable(false);
        binding.AnswerC.setClickable(false);
        binding.AnswerD.setClickable(false);

    }
    public void enableOption(){
        binding.AnswerA.setClickable(true);
        binding.AnswerB.setClickable(true);
        binding.AnswerC.setClickable(true);
        binding.AnswerD.setClickable(true);

    }
    public void correct(){
        correctWords.add(listWords.get(index));

        correctCount++;


    }
    public void wrong(){
        wrongWords.add(listWords.get(index));

        wrongCount++;

    }
    public void resetColor(){
        binding.AnswerA.setCardBackgroundColor(getResources().getColor(R.color.white));
        binding.AnswerB.setCardBackgroundColor(getResources().getColor(R.color.white));
        binding.AnswerC.setCardBackgroundColor(getResources().getColor(R.color.white));
        binding.AnswerD.setCardBackgroundColor(getResources().getColor(R.color.white));

    }
    public void clickOptionA(View view){
        disableOption();
        if(currentQuestion.getoA().equals(currentQuestion.getAns())){
            binding.AnswerA.setCardBackgroundColor(getResources().getColor(R.color.forest));
            correct();
        }
        else{
            binding.AnswerA.setCardBackgroundColor(getResources().getColor(R.color.red));
            wrong();
        }
    }
    public void clickOptionB(View view){
        disableOption();
        if(currentQuestion.getoB().equals(currentQuestion.getAns())){
            binding.AnswerB.setCardBackgroundColor(getResources().getColor(R.color.forest));
            correct();
        }
        else{
            binding.AnswerB.setCardBackgroundColor(getResources().getColor(R.color.red));
            wrong();
        }
    }
    public void clickOptionC(View view){
        disableOption();

        if(currentQuestion.getoC().equals(currentQuestion.getAns())){
            binding.AnswerC.setCardBackgroundColor(getResources().getColor(R.color.forest));
            correct();
        }
        else{
            binding.AnswerC.setCardBackgroundColor(getResources().getColor(R.color.red));
            wrong();
        }
    }
    public void clickOptionD(View view){
        disableOption();

        if(currentQuestion.getoD().equals(currentQuestion.getAns())){
            binding.AnswerD.setCardBackgroundColor(getResources().getColor(R.color.forest));
            correct();
        }
        else{
            binding.AnswerD.setCardBackgroundColor(getResources().getColor(R.color.red));
            wrong();
        }
    }
    public void btnNext(View view){
        if(index <questionList.size()-1){
            index++;
            setData();
            enableOption();
        }
        else{
            countDownTimer.cancel();
            Intent intent = new Intent(Quiz.this, WinningGame.class);
            intent.putExtra("wrong", wrongCount);
            intent.putExtra("correct", correctCount);
            intent.putExtra("time", Timevalue);
//            wordsQuizz w = wrongWords.get(1);
            intent.putExtra("wrongList", wrongWords);
            intent.putExtra("correctList", correctWords);
            intent.putExtra("totalQues", vietQuestion+engQuestion);
            intent.putExtra("idTopic", idTopic);
            intent.putExtra("quiz", 1);
            intent.putExtra("is_public", is_public);

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