package tdtu.edu.cookie.UI.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import tdtu.edu.cookie.Database.Entity.Competitor;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.Database.Entity.wordsQuizz;

import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Adapter.AdapterWord2;
import tdtu.edu.cookie.UI.Adapter.AdapterWords;
import tdtu.edu.cookie.databinding.ActivityQuizBinding;
import tdtu.edu.cookie.databinding.ActivityWinningGameBinding;

public class WinningGame extends AppCompatActivity {
    CircularProgressBar circularProgressBar;
    int correctCount;
    int wrongCount;
    ActivityWinningGameBinding binding;
    FirebaseUser user;
    int time;
    FirebaseFirestore db;
    AdapterWord2 adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_game);
        binding = ActivityWinningGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        correctCount = getIntent().getIntExtra("correct",0);
        wrongCount = getIntent().getIntExtra("wrong",0);
        time = getIntent().getIntExtra("time",0);
        int total = getIntent().getIntExtra("totalQues",0);
        String idTopic = getIntent().getStringExtra("idTopic");
        int quiz = getIntent().getIntExtra("quiz",0);
        int is_public = getIntent().getIntExtra("is_public",1);


//
        ArrayList<wordsQuizz> wrongWords = (ArrayList<wordsQuizz>) getIntent().getSerializableExtra("wrongList");
        ArrayList<wordsQuizz> correctWords = (ArrayList<wordsQuizz>) getIntent().getSerializableExtra("correctList");


//        circularProgressBar = binding.circularProgressBar;
        binding.circularProgressBar.setProgress(correctCount);
        binding.result.setText(String.valueOf(correctCount)+"/10");
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Timestamp currentTimestamp = Timestamp.now();
        int timeComplete = 60-time;
        double score = (( double) correctCount/ total) *100;
        Competitor competitor = new Competitor(user.getUid(),user.getDisplayName(),user.getEmail(),score,timeComplete,currentTimestamp,quiz);
        String Id = user.getUid()+String.valueOf(quiz);

        if(score<50){
            binding.feedback.setText("Cần ôn tập lại!");
        }else if(score<70){
            binding.feedback.setText("Khá lắm!");

        }else if(score<=99){
            binding.feedback.setText("Xuất sắc!");

        }else {
            binding.feedback.setText("Quét sạch!");

        }
        db.collection("Topics").document(idTopic).collection("competitor").document(Id).set(competitor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        binding.correctWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(WinningGame.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.feedback_words);
                ImageView backIcon = dialog.findViewById(R.id.backIcon);


                adapter = new AdapterWord2(WinningGame.this,correctWords);
                RecyclerView recyclerView = dialog.findViewById(R.id.recyclerviewWords);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(WinningGame.this));
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
        });

        binding.wrongWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(WinningGame.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.feedback_words);
                ImageView backIcon = dialog.findViewById(R.id.backIcon);


                adapter = new AdapterWord2(WinningGame.this,wrongWords);
                RecyclerView recyclerView = dialog.findViewById(R.id.recyclerviewWords);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(WinningGame.this));
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
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(WinningGame.this, MainActivity.class);
//                intent.putExtra("winningGame",is_public);
//                startActivity(intent);
//                finish();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

}