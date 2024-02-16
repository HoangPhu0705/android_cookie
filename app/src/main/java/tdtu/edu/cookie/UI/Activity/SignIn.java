package tdtu.edu.cookie.UI.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import tdtu.edu.cookie.Database.Entity.Folder;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.Repository.Repository;
import tdtu.edu.cookie.UI.Dialog.LoadingDialog;
import tdtu.edu.cookie.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity {
    ActivitySignInBinding binding;
    FirebaseAuth auth;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingDialog = new LoadingDialog(SignIn.this);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
//        binding.textFieldEmail.getEditText().setText(email);

        auth = FirebaseAuth.getInstance();
        binding.elevatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                String signInEmail = binding.textFieldEmail.getEditText().getText().toString();
                String signInPwd =binding.textFieldPassword.getEditText().getText().toString();
                loginUser(signInEmail,signInPwd);
            }
        });

        binding.textViewMoveToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });


        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ForgotPassword.class);
                startActivityForResult(intent, 1);
//                Intent intent = new Intent(SignIn.this, inputText.class);
//                startActivityForResult(intent, 1);
            }
        });
    }
    public void loginUser(String email, String pwd){
        auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(SignIn.this, MainActivity.class);

                    intent.putExtra("user_id", getUserId());
                    startActivity(intent);
                    loadingDialog.dismiss();

                }
                else{
                    loadingDialog.dismiss();
                    Toast.makeText(SignIn.this, "Tài khoản không tồn tại",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    //get userid
    public String getUserId(){
        return auth.getCurrentUser().getUid();
    }


}