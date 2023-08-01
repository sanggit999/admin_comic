package sanggit999.example.admin_comic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sanggit999.example.admin_comic.R;
import sanggit999.example.admin_comic.interfaces.OnItemClickListenerComment;
import sanggit999.example.admin_comic.model.Comment;
import sanggit999.example.admin_comic.model.User;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    private RequestQueue requestQueue;
    private OnItemClickListenerComment onItemClickListenerComment;


    private int userRole;

    public CommentAdapter(Context context, List<Comment> commentList, int userRole, OnItemClickListenerComment onItemClickListenerComment) {
        this.context = context;
        this.commentList = commentList;
        this.userRole = userRole;
        this.onItemClickListenerComment = onItemClickListenerComment;
        this.requestQueue = Volley.newRequestQueue(context);

    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        User user = comment.getUser();
        if (user != null) {
            String fullname = user.getFullname();
            holder.fullNameTextView.setText(fullname);
        } else {
            holder.fullNameTextView.setText("Unknown User");
        }

        holder.contentTextView.setText(comment.getContent());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String createdAtString = dateFormat.format(comment.getCreatedAt());
        holder.createAtTextView.setText(createdAtString);


        holder.btnItemEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListenerComment != null) {
                    onItemClickListenerComment.EditComment(comment);
                }
            }
        });


        holder.btnItemDeleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListenerComment != null) {
                    onItemClickListenerComment.DeleteComment(comment);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    ///Lấy bình luận theo userId với comicId;
    public void getCommentsByComicAndUser(ObjectId comicId, ObjectId userId) {
        String URL_GET_COMMENT = "http://10.0.2.2:3000/comments/" + comicId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_GET_COMMENT, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Comment> comments = parseComments(response);
                        setCommentList(comments);
                        Log.d("zzzzComments", "Updated commentList size: " + comments.size());
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ErrorVolleyAdapter", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("userId", userId.toString()); // Truyền userId vào header
                headers.put("role", String.valueOf(userRole)); // Truyền vai trò (role) vào header
                return headers;
            }
        };


        requestQueue.add(request);
    }


    // Thêm phương thức để cập nhật thông tin người dùng cho mỗi bình luận;
    public void updateCommentsWithUsers(List<Comment> comments) {
        for (Comment comment : comments) {
            ObjectId userId = comment.getUserId();
            // Gọi API để lấy thông tin người dùng dựa vào userId
            getUserFromServer(userId, new OnUserLoadedListener() {
                @Override
                public void onUserLoaded(User user) {
                    comment.setUser(user);
                    notifyDataSetChanged(); // Cập nhật lại RecyclerView sau khi có thông tin người dùng mới
                }

                @Override
                public void onUserLoadError() {
                    // Xử lý lỗi nếu cần thiết
                }
            });
        }
    }


    // Giao diện để thông báo khi thông tin người dùng được tải lên từ server;
    interface OnUserLoadedListener {
        void onUserLoaded(User user);

        void onUserLoadError();
    }

    // Lấy thông tin người dùng dựa vào userId;
    private void getUserFromServer(ObjectId userId, OnUserLoadedListener listener) {
        String URL_GET_USER = "http://10.0.2.2:3000/users/" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_GET_USER, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý dữ liệu JSON từ server và chuyển đổi thành đối tượng User

                        User user = parseUser(response);
                        if (user != null) {
                            listener.onUserLoaded(user);
                        } else {
                            listener.onUserLoadError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onUserLoadError();
                        Log.e("ErrorVolleyAdapter", error.toString());
                    }
                });

        requestQueue.add(request);
    }

    // Phương thức lấy User;
    private User parseUser(JSONObject jsonObject) {
        try {
            ObjectId _id = new ObjectId(jsonObject.getString("_id"));
            String fullname = jsonObject.getString("fullname");
            // Tạo đối tượng User từ thông tin lấy được từ JSON
            User user = new User(_id, fullname);
            // Gán các thông tin khác nếu cần

            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    //Lấy danh sách Comments
    private List<Comment> parseComments(JSONArray jsonArray) {
        List<Comment> comments = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ObjectId _id = new ObjectId(jsonObject.getString("_id"));
                ObjectId comicId = new ObjectId(jsonObject.getString("comicId"));
                ObjectId userId = new ObjectId(jsonObject.getString("userId"));
                String content = jsonObject.getString("content");

                // Chuyển đổi chuỗi createdAt thành kiểu Date;
                String createdAtString = jsonObject.getString("createdAt");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date createdAtDate = sdf.parse(createdAtString);
                long createdAt = createdAtDate.getTime();


                // Tạo đối tượng Comment và gán thông tin người dùng và truyện
                Comment comment = new Comment(_id, comicId, userId, content, createdAt);
                Log.d("zzzzComment", "_id:" + _id);

                getUserFromServer(userId, new OnUserLoadedListener() {
                    @Override
                    public void onUserLoaded(User user) {
                        // Gán thông tin người dùng vào bình luận
                        comment.setUser(user);
                        // Sau khi gán thông tin người dùng, cập nhật RecyclerView
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onUserLoadError() {

                    }
                });
                comments.add(comment);
            }

            commentList = comments;
            notifyDataSetChanged();

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return comments;
    }

    //Thêm comment;
    public void PostComment(ObjectId comicId, ObjectId userId, String content) {
        String POST_COMMENT_URL = "http://10.0.2.2:3000/comments";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("comicId", comicId.toString());
            jsonObject.put("userId", userId.toString());
            jsonObject.put("content", content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, POST_COMMENT_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(context, "Bình luận thành công.", Toast.LENGTH_SHORT).show();
                getCommentsByComicAndUser(comicId, userId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorVolleyAdapter", error.toString());
            }
        });
        requestQueue.add(request);


    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView, contentTextView, createAtTextView;
        ImageButton btnItemDeleteComment,btnItemEditComment;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            createAtTextView = itemView.findViewById(R.id.createdAtTextView);
            btnItemDeleteComment = itemView.findViewById(R.id.btnItemDeleteComment);
            btnItemEditComment = itemView.findViewById(R.id.btnItemEditComment);

        }


    }

}
