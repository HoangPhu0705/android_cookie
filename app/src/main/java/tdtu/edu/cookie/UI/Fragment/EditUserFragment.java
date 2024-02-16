package tdtu.edu.cookie.UI.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import tdtu.edu.cookie.Database.Entity.BitmapConverter;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Activity.ChangePassword;
import tdtu.edu.cookie.UI.Activity.ChangeUsername;
import tdtu.edu.cookie.UI.Activity.FlashCard;
import tdtu.edu.cookie.databinding.FragmentEditUserBinding;
import tdtu.edu.cookie.databinding.FragmentSettingBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentEditUserBinding binding;
    BitmapConverter bitmapConverter;


    public EditUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditUserFragment newInstance(String param1, String param2) {
        EditUserFragment fragment = new EditUserFragment();
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
        binding = FragmentEditUserBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (getArguments() != null) {
            String email = getArguments().getString("email");
            String name = getArguments().getString("name");
            String password = getArguments().getString("password");

            binding.editTextName.setText(name);
            binding.editTextEmail.setText(email);
            binding.editTextPassword.setText(password);
        }


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingFragment settingFragment = new SettingFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingFragment).commit();
            }
        });


        binding.textInputName.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeUsername.class);
                startActivityForResult(intent, 1);
            }
        });


        binding.textInputPassword.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivityForResult(intent, 2);
            }
        });


        binding.cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });


        //Set user's profile image
        getUserProfileImage(binding.profileImage);
    }


    public void getUserProfileImage(final CircleImageView imageView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + user.getUid() + "/profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // The download URL of the image
                String imageUrl = uri.toString();

                // Load the image into the CircleImageView
                Picasso.get().load(imageUrl).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("TAG", exception.getMessage());

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {//Activity Result cho changeUsername Activity
            getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            binding.editTextName.setText(user.getDisplayName());
            FancyToast.makeText(getContext(),"Thay đổi username thành công" ,FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();


        }else if(requestCode == 2 && resultCode == Activity.RESULT_OK){ //Activity Result cho changePassword Activity
            getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
            FancyToast.makeText(getContext(),"Thay đổi mật khẩu thành công" ,FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();


        }else if(requestCode == 3 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                binding.profileImage.setImageBitmap(bitmap);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + user.getUid() + "/profile.jpg");
                storageReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getActivity(), "Thay đổi ảnh cá nhân thành công", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Thay đổi ảnh thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}