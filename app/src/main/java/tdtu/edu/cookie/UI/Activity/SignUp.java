package tdtu.edu.cookie.UI.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import tdtu.edu.cookie.R;
import tdtu.edu.cookie.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String signUpEmail=binding.textFieldEmail.getEditText().getText().toString();
                String signUpPassword =binding.textFieldPassword.getEditText().getText().toString();
                String signUpConfirmPassword =binding.textFieldConfirmPassword.getEditText().getText().toString();

                if(signUpEmail.isEmpty() || signUpPassword.isEmpty()){
                    Toast.makeText(SignUp.this,"Please enter your email and password",Toast.LENGTH_SHORT).show();

                }
                else if(!signUpConfirmPassword.equals(signUpPassword)){
                    Toast.makeText(SignUp.this,"Confirm password is not correct",Toast.LENGTH_SHORT).show();

                }
                else{
                    registerUser(signUpEmail,signUpPassword);
                }
            }
        });

        binding.textViewMoveToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });
    }
    public void registerUser(String email, String pwd){
        auth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignUp.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUp.this,"Signup successfully",Toast.LENGTH_SHORT).show();
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName("User2010230123")
                                .build();
                        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {


                                } else {
                                }
                            }
                        });
                    }
                    Intent intent = new Intent(SignUp.this, SignIn.class);

                    String signUpEmail=binding.textFieldEmail.getEditText().getText().toString();

                    intent.putExtra("email", signUpEmail);

                    startActivity(intent);

                }
                else{
                    Toast.makeText(SignUp.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}