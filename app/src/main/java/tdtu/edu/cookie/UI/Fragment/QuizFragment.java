package tdtu.edu.cookie.UI.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import tdtu.edu.cookie.Database.Entity.Question;
import tdtu.edu.cookie.Database.Entity.wordsQuizz;

import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Activity.Quiz;
import tdtu.edu.cookie.UI.Activity.SignIn;
import tdtu.edu.cookie.databinding.FragmentQuizBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private int correctCount = 0;
    private int wrongCount = 0;
    private int index = 0;
    private int Timevalue;
    private ArrayList<Question> questionList = new ArrayList<>();
    private Question currentQuestion;
    private FragmentQuizBinding binding;
    private FirebaseFirestore db;
    private ArrayList<wordsQuizz> listWords = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizFragment newInstance(String param1, String param2) {
        QuizFragment fragment = new QuizFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_quiz, container, false);
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        progressBar = view.findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        getWordFromFirebase();

        Timevalue = 10;
        Timevalue = 10;
        countDownTimer = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Timevalue = Timevalue-1;
                progressBar.setProgress(Timevalue);
            }

            @Override
            public void onFinish() {
                Dialog dialog = new Dialog(getActivity());
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                dialog.setContentView(R.layout.time_out_dialog);
                dialog.show();
            }
        }.start();

        return view;
    }
    public void setData(){
        currentQuestion = questionList.get(index);
        binding.questContext.setText(currentQuestion.getQuestionContext());
        binding.textOpA.setText(currentQuestion.getoA());
        binding.textOpB.setText(currentQuestion.getoB());
        binding.textOpC.setText(currentQuestion.getoC());

        binding.AnswerA.setCardBackgroundColor(getResources().getColor(R.color.white));
        binding.AnswerB.setCardBackgroundColor(getResources().getColor(R.color.white));
        binding.AnswerC.setCardBackgroundColor(getResources().getColor(R.color.white));



    }
    public void getWordFromFirebase() {
        db.collection("Topics").document("2").collection("Words").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int size = 0;
                for (DocumentSnapshot snapshot : task.getResult()) {

                    wordsQuizz words = new wordsQuizz(Integer.parseInt(snapshot.getId()), snapshot.getString("english_word"), snapshot.getString("vietnamese_word"));
                    listWords.add(words);
                }

            }
        });
    }


}