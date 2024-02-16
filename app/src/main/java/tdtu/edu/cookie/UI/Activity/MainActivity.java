package tdtu.edu.cookie.UI.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import tdtu.edu.cookie.Database.MainDatabase.CookieDatabase;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Fragment.HomeFragment;
import tdtu.edu.cookie.UI.Fragment.LibraryFragment;
import tdtu.edu.cookie.UI.Fragment.PublicFragment;
import tdtu.edu.cookie.UI.Fragment.SettingFragment;
import tdtu.edu.cookie.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");

        HomeFragment homeFragment = new HomeFragment(user_id);
        SettingFragment settingFragment = new SettingFragment();
        LibraryFragment libraryFragment = new LibraryFragment(user_id);
        PublicFragment publicFragment = new PublicFragment();
        replaceFragment(homeFragment);

        CookieDatabase db = CookieDatabase.getDatabase(getApplicationContext());
        int winning = getIntent().getIntExtra("winningGame",2);
        if(winning==0){
            replaceFragment(homeFragment);

        }
        else if(winning==1){
            replaceFragment(publicFragment);
        }


        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.home){
                replaceFragment(homeFragment);
            } else if(id == R.id.setting){
                replaceFragment(settingFragment);
            } else if(id == R.id.library) {
                replaceFragment(libraryFragment);
            } else if(id == R.id.community){
                replaceFragment(publicFragment);
            }
            return true;
        });

    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}