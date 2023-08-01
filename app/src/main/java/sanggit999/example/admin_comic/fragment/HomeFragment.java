package sanggit999.example.admin_comic.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static sanggit999.example.admin_comic.api.ApiServer.GET_COMIC_URL;
import static sanggit999.example.admin_comic.api.ApiServer.POST_COMIC_URL;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sanggit999.example.admin_comic.R;
import sanggit999.example.admin_comic.activity.DetailActivity;
import sanggit999.example.admin_comic.adapter.ComicAdapter;
import sanggit999.example.admin_comic.interfaces.OnItemClickListenerComic;
import sanggit999.example.admin_comic.model.Comic;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView comicRecyclerView;
    private FloatingActionButton fabAddComic;
    private ComicAdapter comicAdapter;
    private List<Comic> comicList = new ArrayList<>();


    public HomeFragment() {
    }


    public class GridDividerItemDecoration extends RecyclerView.ItemDecoration {
        private int dividerSize;
        private Paint dividerPaint;

        public GridDividerItemDecoration(int dividerSize, int dividerColor) {
            this.dividerSize = dividerSize;
            dividerPaint = new Paint();
            dividerPaint.setColor(dividerColor);
        }


        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int itemCount = parent.getChildCount();
            int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();

            for (int i = 0; i < itemCount; i++) {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);

                if (position % spanCount != 0) {
                    // Nếu không phải là item đầu tiên của mỗi hàng, vẽ kẻ ngang bên trái
                    int left = child.getLeft() - dividerSize;
                    int top = child.getTop();
                    int right = left + dividerSize;
                    int bottom = child.getBottom();
                    c.drawRect(left, top, right, bottom, dividerPaint);
                }

                // Vẽ kẻ ngang phía trên của mỗi item
                int left = child.getLeft();
                int top = child.getTop() - dividerSize;
                int right = child.getRight();
                int bottom = top + dividerSize;
                c.drawRect(left, top, right, bottom, dividerPaint);


            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        comicRecyclerView = view.findViewById(R.id.comicRecyclerView);
        fabAddComic = view.findViewById(R.id.fabAddComic);

        comicAdapter = new ComicAdapter(getActivity(), comicList, new OnItemClickListenerComic() {
            @Override
            public void onItemClicked(Comic comic) {
                // Chuyển sang ReadComicActivity và truyền đối tượng comic qua Intent;
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("comic", comic);
//                    startActivity(intent);
                startActivityForResult(intent,1);
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });


        // Thiết lập LayoutManager cho RecyclerView;
        comicRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        comicAdapter.setComicList(comicList);
        comicRecyclerView.setAdapter(comicAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        comicRecyclerView.setLayoutManager(gridLayoutManager);

        // Thêm GridDividerItemDecoration vào RecyclerView;
        int dividerSize = (int) getResources().getDimension(R.dimen.divider_size);
        int dividerColor = getResources().getColor(R.color.divider_color);
        GridDividerItemDecoration gridDividerItemDecoration = new GridDividerItemDecoration(dividerSize, dividerColor);
        comicRecyclerView.addItemDecoration(gridDividerItemDecoration);


        fabAddComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAddComic();
            }
        });

        loadData();
        return view;
    }



    //Load lại data bằng Swipe;
    private void loadData() {
        comicList.clear();
        getDataComic();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1&& resultCode ==RESULT_OK){
            loadData();
        }
    }

    //Phương thức POST comic
    private void postDataComic(String titleAddComic, String desAddComic, String authorAddComic, String yearAddComic, String coverImgAddComic, String imagesAddComic) {
        String[] imagesUrl = imagesAddComic.split(",");// Tách chuỗi imagesAddComic thành mảng các đường dẫn ảnh;
        List<String> imagesList = Arrays.asList(imagesUrl);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", titleAddComic);
            jsonObject.put("description", desAddComic);
            jsonObject.put("author", authorAddComic);
            jsonObject.put("year", yearAddComic);
            jsonObject.put("coverImage", coverImgAddComic);

            JSONArray imagesArray = new JSONArray();
            for (String images : imagesList) {
                imagesArray.put(images);
            }
            jsonObject.put("images", imagesArray);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, POST_COMIC_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                loadData();
                Toast.makeText(getActivity(), "Thêm thành công.", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getActivity()).add(request);
    }


    //Phương thức GET Comic;
    private void getDataComic() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, GET_COMIC_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String id = jsonObject.getString("_id");
                        ObjectId _id = new ObjectId(id);
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String author = jsonObject.getString("author");
                        String year = jsonObject.getString("year");
                        String coverImage = jsonObject.getString("coverImage");

                        //Lấy các ảnh;
                        JSONArray imageArray = jsonObject.getJSONArray("images");
                        List<String> imageList = new ArrayList<>();
                        for (int j = 0; j < imageArray.length(); j++) {
                            imageList.add(imageArray.getString(j));
                        }
                        //New Comic;
                        Comic comic = new Comic(_id, title, description, author, year, coverImage, imageList);
                        //Thêm vào List Comic;
                        comicList.add(comic);


                    }



                    comicAdapter.setComicList(comicList);
                    comicAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        });
        Volley.newRequestQueue(getContext()).add(request);
    }


    private void DialogAddComic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_comic, null);


        TextInputLayout tilTitleAddComic = view.findViewById(R.id.tilTitleAddComic);
        TextInputLayout tilDesAddComic = view.findViewById(R.id.tilDesAddComic);
        TextInputLayout tilAuthorAddComic = view.findViewById(R.id.tilAuthorAddComic);
        TextInputLayout tilYearAddComic = view.findViewById(R.id.tilYearAddComic);
        TextInputLayout tilCoverImgAddComic = view.findViewById(R.id.tilCoverImgAddComic);
        TextInputLayout tilImagesAddComic = view.findViewById(R.id.tilImagesAddComic);

        TextInputEditText etTitleAddComic = view.findViewById(R.id.etTitleAddComic);
        TextInputEditText etDesAddComic = view.findViewById(R.id.etDesAddComic);
        TextInputEditText etAuthorAddComic = view.findViewById(R.id.etAuthorAddComic);
        TextInputEditText etYearAddComic = view.findViewById(R.id.etYearAddComic);
        TextInputEditText etCoverImgAddComic = view.findViewById(R.id.etCoverImgAddComic);
        TextInputEditText etImagesAddComic = view.findViewById(R.id.etImagesAddComic);

        Button btnAddComic = view.findViewById(R.id.btnAddComic);
        Button btnHuyComic = view.findViewById(R.id.btnHuyComic);


        AlertDialog dialog = builder.setView(view).show();

        btnAddComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleAddComic = etTitleAddComic.getText().toString().trim();
                String desAddComic = etDesAddComic.getText().toString().trim();
                String authorAddComic = etAuthorAddComic.getText().toString().trim();
                String yearAddComic = etYearAddComic.getText().toString().trim();
                String coverImgAddComic = etCoverImgAddComic.getText().toString().trim();
                String imagesAddComic = etImagesAddComic.getText().toString().trim();
                // Kiểm tra xem các trường đã được điền đầy đủ hay chưa
                if (TextUtils.isEmpty(titleAddComic)) {
                    tilTitleAddComic.setError("Vui lòng nhập tiêu đề truyện");
                    return;
                } else {
                    tilTitleAddComic.setError(null);
                }

                if (TextUtils.isEmpty(desAddComic)) {
                    tilDesAddComic.setError("Vui lòng nhập mô tả truyện");
                    return;
                } else {
                    tilDesAddComic.setError(null);
                }

                if (TextUtils.isEmpty(authorAddComic)) {
                    tilAuthorAddComic.setError("Vui lòng nhập tác giả truyện");
                    return;
                } else {
                    tilAuthorAddComic.setError(null);
                }

                if (TextUtils.isEmpty(yearAddComic)) {
                    tilYearAddComic.setError("Vui lòng nhập năm xuất bản truyện");
                    return;
                } else {
                    tilYearAddComic.setError(null);
                }

                if (TextUtils.isEmpty(coverImgAddComic)) {
                    tilCoverImgAddComic.setError("Vui lòng nhập đường dẫn bìa truyện");
                    return;
                } else {
                    tilCoverImgAddComic.setError(null);
                }
                if (TextUtils.isEmpty(imagesAddComic)) {
                    tilImagesAddComic.setError("Vui lòng nhập đường dẫn bìa truyện");
                    return;
                } else {
                    tilImagesAddComic.setError(null);
                }


                ///POST
                postDataComic(titleAddComic, desAddComic, authorAddComic, yearAddComic, coverImgAddComic, imagesAddComic);

                dialog.dismiss();
            }
        });

        btnHuyComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


}