package sanggit999.example.admin_comic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import sanggit999.example.admin_comic.R;

public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        // Sau một khoảng thời gian, chuyển đến màn hình đăng nhập hoặc màn hình tiếp theo;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 2000); // Cho 2 giây;
    }
}