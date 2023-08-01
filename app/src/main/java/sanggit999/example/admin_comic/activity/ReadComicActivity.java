package sanggit999.example.admin_comic.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import sanggit999.example.admin_comic.R;
import sanggit999.example.admin_comic.adapter.ReadComicAdapter;
import sanggit999.example.admin_comic.model.Comic;

public class ReadComicActivity extends AppCompatActivity {


    private RecyclerView imageRecyclerView;
    ///private FloatingActionButton fabAddImage;


    private List<String> imageList = new ArrayList<>();

    private ReadComicAdapter readComicAdapter;

    private Comic comic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_comic);
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
       /// fabAddImage = findViewById(R.id.fabAddImage);


        //Lấy intent của Comic;
        comic = (Comic) getIntent().getSerializableExtra("comic");
        if (comic != null) {
            // Lấy danh sách các ảnh của truyện từ đối tượng Comic;
            imageList = comic.getImages();
            // Tạo ReadComicAdapter và gắn vào imageRecyclerView
            imageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            readComicAdapter = new ReadComicAdapter(this, imageList);
            readComicAdapter.setImageList(imageList);
            imageRecyclerView.setAdapter(readComicAdapter);


        }

    }


    //Dialog thoát;
    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát!!!")
                .setMessage("Bạn muốn đọc tiếp?")
                .setPositiveButton("Có", null)
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Kết thúc Activity và quay lại màn hình trước đó;
                        finish();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        showAlertDialog();
    }
}