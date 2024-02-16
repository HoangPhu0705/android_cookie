package tdtu.edu.cookie.UI.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import tdtu.edu.cookie.Database.Entity.Competitors;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Adapter.CompetitorAdapter;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;
import tdtu.edu.cookie.databinding.ActivityBxhBinding;
import tdtu.edu.cookie.databinding.ActivityQuizBinding;

public class Bxh extends AppCompatActivity {
    ActivityBxhBinding binding;
    FirebaseFirestore db;
    ArrayList<Competitors> competitorsArrayList= new ArrayList<>();

    CompetitorAdapter competitorAdapter;
    RecyclerView rv;
    LoadingDialog loadingDialog;
    String idTopic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bxh);
        binding = ActivityBxhBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        idTopic = getIntent().getStringExtra("idTopic");

        setQuizPoint();

        competitorAdapter = new CompetitorAdapter(this, competitorsArrayList);
        rv = findViewById(R.id.recyclerViewBxh);
        rv.setAdapter(competitorAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));



        List<String> topicList = new ArrayList<>();
        topicList.add("Xếp hạng theo điểm làm Quiz");
        topicList.add("Xếp hạng theo điểm làm Gõ từ");
        topicList.add("Xếp hạng theo thời gian làm Quiz");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, topicList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBxh.setAdapter(adapter);
        binding.imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        binding.spinnerBxh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setQuizPoint();
                        break;
                    case 1:
                        setTextPoint();

                        break;
                    case 2:
                        setQuizTime();

                        break;
                    // Thêm các case khác nếu cần
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Bxh.this, "BBBB", Toast.LENGTH_SHORT).show();

            }
        });

    }
    public void setQuizPoint(){

        CollectionReference studentsCollection=db.collection("Topics").document(idTopic).collection("competitor");
        studentsCollection.whereEqualTo("quiz",1)

                .orderBy("score",Query.Direction.DESCENDING)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        competitorsArrayList.clear();
                        int count =1;
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            String name = snapshot.get("Name").toString();
                            String email = snapshot.get("email").toString();
                            String id = snapshot.get("id").toString();

                            String score ="Điểm"+String.valueOf(snapshot.getDouble("score")) ;
                            Timestamp timeStamp= snapshot.getTimestamp("timestamp");
                            Competitors competitor = new Competitors(id,"Hạng "+String.valueOf(count),name,email,score,1,timeStamp,1);
                            competitorsArrayList.add(competitor);
                            count++;
                        }

                        loadingDialog.dismiss();
                        competitorAdapter.notifyDataSetChanged();
                    }
                });
    }
    public void setQuizTime(){

        CollectionReference studentsCollection=db.collection("Topics").document(idTopic).collection("competitor");
        studentsCollection.whereEqualTo("quiz",1)
                .whereEqualTo("score",100)
                .orderBy("time",Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        competitorsArrayList.clear();
                        int count =1;
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            String name = snapshot.get("Name").toString();
                            String email = snapshot.get("email").toString();
                            String id = snapshot.get("id").toString();

                            String score ="Thời gian: "+String.valueOf(snapshot.getDouble("time")+"s") ;
                            Timestamp timeStamp= snapshot.getTimestamp("timestamp");
                            Competitors competitor = new Competitors(id,"Hạng "+String.valueOf(count),name,email,score,1,timeStamp,1);
                            competitorsArrayList.add(competitor);
                            count++;
                        }

                        loadingDialog.dismiss();
                        competitorAdapter.notifyDataSetChanged();
                    }
                });
    }
    public void setTextPoint(){

        CollectionReference studentsCollection=db.collection("Topics").document(idTopic).collection("competitor");
        studentsCollection.whereEqualTo("quiz",0)

                .orderBy("score",Query.Direction.DESCENDING)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        competitorsArrayList.clear();
                        int count =1;

                        for (DocumentSnapshot snapshot : task.getResult()) {
                            String name = snapshot.get("Name").toString();
                            String email = snapshot.get("email").toString();
                            String score ="Điểm"+String.valueOf(snapshot.getDouble("score")) ;
                            Timestamp timeStamp= snapshot.getTimestamp("timestamp");
                            String id = snapshot.get("id").toString();

                            Competitors competitor = new Competitors(id,"Hạng "+String.valueOf(count),name,email,score,1,timeStamp,1);
                            competitorsArrayList.add(competitor);
                            count++;
                        }

                        loadingDialog.dismiss();
                        competitorAdapter.notifyDataSetChanged();
                    }
                });
    }
}