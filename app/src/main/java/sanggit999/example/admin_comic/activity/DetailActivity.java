package sanggit999.example.admin_comic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sanggit999.example.admin_comic.R;
import sanggit999.example.admin_comic.adapter.CommentAdapter;
import sanggit999.example.admin_comic.interfaces.OnItemClickListenerComment;
import sanggit999.example.admin_comic.model.Comic;
import sanggit999.example.admin_comic.model.Comment;
import sanggit999.example.admin_comic.model.User;
import sanggit999.example.admin_comic.singleton.UserDataSingleton;

public class DetailActivity extends AppCompatActivity {

    private TextView titleDetailComic, desDetailComic, authorDetailComic, yearDetailComic;
    private ImageView imgDetailComic;
    private Button btnReadDetailComic, btnCommentComic;
    private ImageButton btnDeleteDetailComic, btnEditDetailComic;


    private TextInputEditText etCommentComic;

    private RecyclerView recyclerCommentComic;

    private List<Comment> commentList = new ArrayList<>();

    private CommentAdapter commentAdapter;

    private Comic comic;
    private Comment comment;
    private User user;
    private ObjectId comicId, userId;
    private int userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleDetailComic = findViewById(R.id.titleDetailComic);
        desDetailComic = findViewById(R.id.desDetailComic);
        authorDetailComic = findViewById(R.id.authorDetailComic);
        yearDetailComic = findViewById(R.id.yearDetailComic);
        imgDetailComic = findViewById(R.id.imgDetailComic);
        btnReadDetailComic = findViewById(R.id.btnReadDetailComic);
        btnCommentComic = findViewById(R.id.btnCommentComic);
        btnDeleteDetailComic = findViewById(R.id.btnDeleteDetailComic);
        btnEditDetailComic = findViewById(R.id.btnEditDetailComic);

        etCommentComic = findViewById(R.id.etCommentComic);
        recyclerCommentComic = findViewById(R.id.recyclerCommentComic);


        comic = (Comic) getIntent().getSerializableExtra("comic");
        user = UserDataSingleton.getInstance().getUser();

        if (comic != null) {
            comicId = comic.get_id();
            titleDetailComic.setText(comic.getTitle());
            desDetailComic.setText(comic.getDescription());
            authorDetailComic.setText(comic.getAuthor());
            yearDetailComic.setText(comic.getYear());

            Glide.with(this).load(comic.getCoverImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imgDetailComic);

        }


        btnReadDetailComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ReadComicActivity.class);
                intent.putExtra("comic", comic);
                startActivity(intent);
            }
        });

        btnDeleteDetailComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị một hộp thoại xác nhận xóa truyện
                new AlertDialog.Builder(DetailActivity.this).setTitle("Thông báo!!!").setMessage("Bạn có chắc chắn muốn xóa truyện này?").setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteComic(comicId);
                    }
                }).setNegativeButton("Hủy", null).show();
            }
        });


        btnEditDetailComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEditComic();
            }
        });


        if (user != null) {
            userId = user.get_id();
            userRole = user.getRole();
        }

        commentAdapter = new CommentAdapter(this, commentList, userRole, new OnItemClickListenerComment() {
            @Override
            public void EditComment(Comment comment) {
                DialogEditComment(comment.get_id(),comment.getContent());
            }

            @Override
            public void DeleteComment(Comment comment) {
                AlertDeleteComment(comment.get_id());
            }
        });

        recyclerCommentComic.setLayoutManager(new LinearLayoutManager(this));
        recyclerCommentComic.setAdapter(commentAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        recyclerCommentComic.addItemDecoration(dividerItemDecoration);

        getCommentsByComicAndUser(comicId, userId);


    }


    private void getCommentsByComicAndUser(ObjectId comicId, ObjectId userId) {
        //Gọi phương thức trong CommentAdapter để lấy danh sách bình luận;
        commentAdapter.getCommentsByComicAndUser(comicId, userId);
        commentList.clear();

        commentAdapter.setCommentList(commentList);
        commentAdapter.updateCommentsWithUsers(commentList);
        commentAdapter.notifyDataSetChanged();
        btnCommentComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostComment();
            }
        });
    }


    private void PostComment() {
        String commentContent = etCommentComic.getText().toString().trim();
        if (!commentContent.isEmpty()) {
            ObjectId comicId = comic.get_id();
            ObjectId userId = user.get_id();
            commentAdapter.PostComment(comicId, userId, commentContent);
            etCommentComic.setText("");

            Log.d("zzzzComments", "New commentList size: " + commentList.size());

        } else {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập nội dung bình luận.", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateComic(ObjectId comicId, Comic comic) {
        String UPDATE_COMIC_URL = "http://10.0.2.2:3000/comics/" + comicId;

        JSONObject jsonComic = new JSONObject();
        try {
            jsonComic.put("title", comic.getTitle());
            jsonComic.put("description", comic.getDescription());
            jsonComic.put("author", comic.getAuthor());
            jsonComic.put("year", comic.getYear());
            jsonComic.put("coverImage", comic.getCoverImage());
        } catch (JSONException e) {
            e.printStackTrace();

        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, UPDATE_COMIC_URL, jsonComic, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), " Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(request);
    }


    private void DialogEditComic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_comic, null);

        TextInputLayout tilTitleEditComic = view.findViewById(R.id.tilTitleEditComic);
        TextInputLayout tilDesEditComic = view.findViewById(R.id.tilDesEditComic);
        TextInputLayout tilAuthorEditComic = view.findViewById(R.id.tilAuthorEditComic);
        TextInputLayout tilYearEditComic = view.findViewById(R.id.tilYearEditComic);
        TextInputLayout tilCoverImgEditComic = view.findViewById(R.id.tilCoverImgEditComic);


        TextInputEditText etTitleEditComic = view.findViewById(R.id.etTitleEditComic);
        TextInputEditText etDesEditComic = view.findViewById(R.id.etDesEditComic);
        TextInputEditText etAuthorEditComic = view.findViewById(R.id.etAuthorEditComic);
        TextInputEditText etYearEditComic = view.findViewById(R.id.etYearEditComic);
        TextInputEditText etCoverImgEditComic = view.findViewById(R.id.etCoverImgEditComic);


        Button btnEditComic = view.findViewById(R.id.btnEditComic);
        Button btnEditHuyComic = view.findViewById(R.id.btnEditHuyComic);

        etCoverImgEditComic.setText(comic.getCoverImage());
        etTitleEditComic.setText(comic.getTitle());
        etDesEditComic.setText(comic.getDescription());
        etAuthorEditComic.setText(comic.getAuthor());
        etYearEditComic.setText(comic.getYear());


        AlertDialog dialog = builder.setView(view).show();

        btnEditComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleEditComic = etTitleEditComic.getText().toString().trim();
                String desEditComic = etDesEditComic.getText().toString().trim();
                String authorEditComic = etAuthorEditComic.getText().toString().trim();
                String yearEditComic = etYearEditComic.getText().toString().trim();
                String coverImgEditComic = etCoverImgEditComic.getText().toString().trim();

                // Kiểm tra xem các trường đã được điền đầy đủ hay chưa
                if (TextUtils.isEmpty(titleEditComic)) {
                    tilTitleEditComic.setError("Vui lòng nhập tiêu đề truyện");
                    return;
                } else {
                    tilTitleEditComic.setError(null);
                }

                if (TextUtils.isEmpty(desEditComic)) {
                    tilDesEditComic.setError("Vui lòng nhập mô tả truyện");
                    return;
                } else {
                    tilDesEditComic.setError(null);
                }

                if (TextUtils.isEmpty(authorEditComic)) {
                    tilAuthorEditComic.setError("Vui lòng nhập tác giả truyện");
                    return;
                } else {
                    tilAuthorEditComic.setError(null);
                }

                if (TextUtils.isEmpty(yearEditComic)) {
                    tilYearEditComic.setError("Vui lòng nhập năm xuất bản truyện");
                    return;
                } else {
                    tilYearEditComic.setError(null);
                }

                if (TextUtils.isEmpty(coverImgEditComic)) {
                    tilCoverImgEditComic.setError("Vui lòng nhập đường dẫn bìa truyện");
                    return;
                } else {
                    tilCoverImgEditComic.setError(null);
                }

                Comic updateComic = new Comic();
                updateComic.setTitle(titleEditComic);
                updateComic.setDescription(desEditComic);
                updateComic.setAuthor(authorEditComic);
                updateComic.setYear(yearEditComic);
                updateComic.setCoverImage(coverImgEditComic);

                UpdateComic(comic.get_id(), updateComic);
                dialog.dismiss();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnEditHuyComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void deleteComic(ObjectId comicId) {
        String DELETE_COMIC_URL = "http://10.0.2.2:3000/comics/" + comicId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, DELETE_COMIC_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(DetailActivity.this, "Xóa truyện thành công.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(DetailActivity.this, "Không thể xóa truyện", Toast.LENGTH_SHORT).show();
            }
        });


        Volley.newRequestQueue(this).add(request);
    }





    private void UpdateComment(ObjectId commentId, Comment comment) {
        String UPDATE_COMMENT_URL = "http://10.0.2.2:3000/comments/" + commentId;


        JSONObject jsonComic = new JSONObject();
        try {
            jsonComic.put("content", comment.getContent());

        } catch (JSONException e) {
            e.printStackTrace();

        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, UPDATE_COMMENT_URL, jsonComic, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getCommentsByComicAndUser(comicId, userId);
                Toast.makeText(getApplicationContext(), " Cập nhật thành công", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(request);
    }

    private void DeleteComment(ObjectId commentId) {
        String DELETE_COMMENT_URL = "http://10.0.2.2:3000/comments/" + commentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, DELETE_COMMENT_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getCommentsByComicAndUser(comicId, userId);
                Toast.makeText(getApplicationContext(), "Xoá thành công", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(request);

    }

    private void DialogEditComment(ObjectId commentId, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_comment, null);

        TextInputLayout tilContentEditComment = view.findViewById(R.id.tilContentEditComment);
        TextInputEditText etContentEditComment = view.findViewById(R.id.etContentEditComment);
        Button btnEditComment = view.findViewById(R.id.btnEditComment);
        Button btnEditHuyComment = view.findViewById(R.id.btnEditHuyComment);


        etContentEditComment.setText(content);


        AlertDialog dialog = builder.setView(view).show();

        btnEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contentEditComic = etContentEditComment.getText().toString().trim();

                // Kiểm tra xem các trường đã được điền đầy đủ hay chưa
                if (TextUtils.isEmpty(contentEditComic)) {
                    tilContentEditComment.setError("Vui lòng nhập bình luận");
                    return;
                } else {
                    tilContentEditComment.setError(null);
                }

                Comment comment1 = new Comment();
                comment1.setContent(contentEditComic);

                UpdateComment(commentId, comment1);
                dialog.dismiss();
            }
        });

        btnEditHuyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }


    public void AlertDeleteComment(ObjectId commentId){
        new AlertDialog.Builder(DetailActivity.this).setTitle("Xoá!").setMessage("Bạn có muốn xoá bình luận này?").setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    DeleteComment(commentId);
            }
        }).setNegativeButton("Không", null).show();
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Thông báo!!!").setMessage("Bạn muốn quay lại?")
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