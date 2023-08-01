package sanggit999.example.admin_comic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import sanggit999.example.admin_comic.fragment.HomeFragment;
import sanggit999.example.admin_comic.fragment.ProfileFragment;
import sanggit999.example.admin_comic.dialog.WelcomeDialog;
import sanggit999.example.admin_comic.model.User;
import sanggit999.example.admin_comic.singleton.UserDataSingleton;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Lấy dữ liệu trong UserDataSingleton;
        user = UserDataSingleton.getInstance().getUser();
        if(user !=null){
            String fullname = user.getFullname();
            WelcomeDialog.showWelcomeDialog(this, fullname);
            Log.d("USERDATASINGLETON","_id"+user.get_id()+"username"+user.getUsername());
        }



        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_home) {
                    // Xử lý khi chọn Home
                    if (currentFragment instanceof HomeFragment) {
                        return true;
                    } else {
                        currentFragment = new HomeFragment();
                        switchFragment(currentFragment);
                        return true;
                    }
                } else if (item.getItemId() == R.id.menu_profile) {
                    // Xử lý khi chọn Profile
                    if (currentFragment instanceof ProfileFragment) {
                        return true;
                    } else {
                        currentFragment = new ProfileFragment();
                        switchFragment(currentFragment);
                        return true;
                    }
                }
                return false;
            }
        });

        // Hiển thị HomeFragment ban đầu
        currentFragment = new HomeFragment();
        switchFragment(currentFragment);
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát!!!")
                .setMessage("Bạn có muốn thoát?")
                .setPositiveButton("Không", null)
                .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Kết thúc Activity và ra màn hình;
                        finish();
                    }
                }).show();

    }
}
