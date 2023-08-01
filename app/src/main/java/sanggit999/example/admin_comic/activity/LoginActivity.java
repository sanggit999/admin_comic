package sanggit999.example.admin_comic.activity;



import static sanggit999.example.admin_comic.api.ApiServer.LOGIN_URL;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;


import sanggit999.example.admin_comic.MainActivity;
import sanggit999.example.admin_comic.R;

import sanggit999.example.admin_comic.model.User;
import sanggit999.example.admin_comic.singleton.UserDataSingleton;


public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilLoginUsername;
    private TextInputLayout tilLoginPassword;
    private TextInputEditText etLoginUsername;
    private TextInputEditText etLoginPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView tvLoginSignUp;


    //Remember me;
    private static final String PREF_NAME = "LoginPref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private static final String KEY_REMEMBER_ME = "remember_me";
    private SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilLoginUsername = findViewById(R.id.tilLoginUsername);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        tvLoginSignUp = findViewById(R.id.tvLoginSignUp);









        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (rememberMe) {
            String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            etLoginUsername.setText(savedUsername);
            etLoginPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }






        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });

        tvLoginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện chuyển đến màn hình đăng nhập;
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    private void validateFields() {
        String username = etLoginUsername.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();
        String specialCharacters = "[!$%^&*()_+=|<>?{}\\[\\]~-]";

        if (TextUtils.isEmpty(username)) {
            tilLoginUsername.setError("Không để trống tài khoản!");
            tilLoginUsername.requestFocus();
            return; //
        } else if (username.length() < 5) {
            tilLoginUsername.setError("Tài khoản không hợp lệ!");
            tilLoginUsername.requestFocus();
            return;
        } else if (username.contains(" ")) {
            tilLoginUsername.setError("Tài khoản không có dấu cách!");
            tilLoginUsername.requestFocus();
            return;
        } else if (username.matches(".*" + specialCharacters + ".*")) {
            tilLoginUsername.setError("Tài khoản không chứa ký tự đặc biệt!");
            tilLoginUsername.requestFocus();
            return;
        } else {
            tilLoginUsername.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            tilLoginPassword.setError("Không để trống mật khẩu!");
            tilLoginPassword.requestFocus();
            return;
        } else if (password.length() < 5) {
            tilLoginPassword.setError("Mật khẩu quá ngắn!");
            tilLoginPassword.requestFocus();
            return;
        } else if (password.contains(" ")) {
            tilLoginPassword.setError("Mật khẩu không có dấu cách!");
            tilLoginPassword.requestFocus();
            return;
        } else if (password.matches(".*" + specialCharacters + ".*")) {
            tilLoginPassword.setError("Mật khẩu không có kí tự đặc biệt!");
            tilLoginPassword.requestFocus();
            return;
        } else {
            tilLoginPassword.setError(null);
        }

        loginUser(username, password);
    }







    //Đăng nhập;
    private void loginUser(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Gửi yêu cầu đăng nhập bằng Volley;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean exists = response.getBoolean("exists");
                            if (exists) {
                                // Tài khoản tồn tại;
                                String message = response.getString("message");
                                if (message.equals("Đăng nhập thành công")) {
                                    int role = response.getInt("role");
                                    String fullname = response.getString("fullname");
                                    String email = response.getString("email");
                                    ObjectId _id = new ObjectId(response.getString("_id")); // Chuyển chuỗi "_id" thành đối tượng ObjectId

                                    // Xử lý đăng nhập thành công
                                    if (role == 2) {
                                        User user = new User(_id, username, password, email, fullname, role);
                                        Log.d("SAVE USER", "_id" + user.get_id());
                                        // Lưu đối tượng User vào SharedPreferences hoặc các cơ chế lưu trữ khác;

                                        if (cbRememberMe.isChecked()) {
                                            // Lưu thông tin đăng nhập vào SharedPreferences;
                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            editor.putString(KEY_USERNAME, username);
                                            editor.putString(KEY_PASSWORD, password);

                                            editor.putBoolean(KEY_REMEMBER_ME, true);
                                            editor.apply();

                                        } else {
                                            // Xóa thông tin đăng nhập khỏi SharedPreferences;
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.remove(KEY_USERNAME);
                                            editor.remove(KEY_PASSWORD);
                                            editor.remove(KEY_REMEMBER_ME);
                                            editor.apply();
                                        }

                                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                                        // Tiến hành chuyển hướng đến màn hình tương ứng;
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        UserDataSingleton.getInstance().setUser(user);

                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Check user không đăng nhập;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setTitle("Thông báo!!!")
                                                .setMessage("Tài khoản không có quyền truy cập.")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                } else {
                                    // Đăng nhập không thành công;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setTitle("Thông báo!!!")
                                            .setMessage("Tài khoản hoặc mật khẩu không đúng.")
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            } else {
                                // Tài khoản không tồn tại;
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Thông báo!!!")
                                        .setMessage("Tài khoản không tồn tại.")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Lỗi đăng nhập.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi từ API Node.js (nếu cần)
                        Toast.makeText(getApplicationContext(), "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                        Log.e("ErrorVolley", error.toString());
                    }
                });

        Volley.newRequestQueue(this).add(request);
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


