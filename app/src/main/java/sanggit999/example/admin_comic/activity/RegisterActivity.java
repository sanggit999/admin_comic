package sanggit999.example.admin_comic.activity;

import static sanggit999.example.admin_comic.api.ApiServer.REGISTER_URL;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import sanggit999.example.admin_comic.R;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilRegisterFullName;
    private TextInputLayout tilRegisterEmail;
    private TextInputLayout tilRegisterUsername;
    private TextInputLayout tilRegisterPassword;
    private TextInputLayout tilRegisterRePassword;
    private TextInputEditText etRegisterFullName;
    private TextInputEditText etRegisterEmail;
    private TextInputEditText etRegisterUsername;
    private TextInputEditText etRegisterPassword;
    private TextInputEditText etRegisterRePassword;
    private Button btnRegister;
    private TextView tvRegisterLogin;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilRegisterUsername = findViewById(R.id.tilRegisterUsername);
        tilRegisterFullName = findViewById(R.id.tilRegisterFullName);
        tilRegisterEmail = findViewById(R.id.tilRegisterEmail);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        tilRegisterRePassword = findViewById(R.id.tilRegisterRePassword);
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterFullName = findViewById(R.id.etRegisterFullName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterRePassword = findViewById(R.id.etRegisterRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvRegisterLogin = findViewById(R.id.tvRegisterLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });

        tvRegisterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện chuyển đến màn hình đăng nhập;
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void validateFields() {
        String fullName = etRegisterFullName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String username = etRegisterUsername.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String rePassword = etRegisterRePassword.getText().toString().trim();
        String specialCharacters = "[!$%^&*()_+=|<>?{}\\[\\]~-]";

        if (fullName.isEmpty()) {
            tilRegisterFullName.setError("Vui lòng nhập họ và tên!");
            tilRegisterFullName.requestFocus();
            return;
        } else if (fullName.length() < 5) {
            tilRegisterFullName.setError("Họ tên quá ngắn!");
            tilRegisterFullName.requestFocus();
            return;
        } else {
            tilRegisterFullName.setError(null);
        }

        if (email.isEmpty()) {
            tilRegisterEmail.setError("Vui lòng nhập địa chỉ email!");
            tilRegisterEmail.requestFocus();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilRegisterEmail.setError("Địa chỉ email không hợp lệ!");
            tilRegisterEmail.requestFocus();
            return;
        } else {
            tilRegisterEmail.setError(null);
        }

        if (username.isEmpty()) {
            tilRegisterUsername.setError("Vui lòng nhập tên tài khoản!");
            tilRegisterUsername.requestFocus();
            return;
        } else if (username.length() < 5) {
            tilRegisterUsername.setError("Tên tài khoản phải có ít nhất 5 ký tự!");
            tilRegisterUsername.requestFocus();
            return;
        } else if (username.contains(" ")) {
            tilRegisterUsername.setError("Tên tài khoản không được chứa dấu cách!");
            tilRegisterUsername.requestFocus();
            return;
        } else if (username.matches(".*" + specialCharacters + ".*")) {
            tilRegisterUsername.setError("Tên tài khoản không được chứa ký tự đặc biệt!");
            tilRegisterUsername.requestFocus();
            return;
        } else {
            tilRegisterUsername.setError(null);
        }

        if (password.isEmpty()) {
            tilRegisterPassword.setError("Vui lòng nhập mật khẩu!");
            tilRegisterPassword.requestFocus();
            return;
        } else if (password.length() < 5) {
            tilRegisterPassword.setError("Mật khẩu phải có ít nhất 5 ký tự!");
            tilRegisterPassword.requestFocus();
            return;
        } else if (password.contains(" ")) {
            tilRegisterPassword.setError("Mật khẩu không được chứa dấu cách!");
            tilRegisterPassword.requestFocus();
            return;
        } else if (password.matches(".*" + specialCharacters + ".*")) {
            tilRegisterPassword.setError("Mật khẩu không được chứa ký tự đặc biệt!");
            tilRegisterPassword.requestFocus();
            return;
        } else {
            tilRegisterPassword.setError(null);
        }

        if (rePassword.isEmpty()) {
            tilRegisterRePassword.setError("Vui lòng nhập lại mật khẩu!");
            tilRegisterRePassword.requestFocus();
            return;
        } else if (!rePassword.equals(password)) {
            tilRegisterRePassword.setError("Mật khẩu không khớp!");
            tilRegisterRePassword.requestFocus();
            return;
        } else {
            tilRegisterRePassword.setError(null);
        }

        // Tiến hành đăng ký tài khoản
        registerUser(fullName, email, username, password);
    }

    private void registerUser(String fullName, String email, String username, String password) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
            jsonObject.put("fullname", fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Gửi yêu cầu đăng nhập bằng Volley;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REGISTER_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("exists")) {
                        boolean exists = response.getBoolean("exists");
                        if (exists) {
                            // Tài khoản tồn tại;
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setTitle("Thông báo!!!")
                                    .setMessage("Tài khoản đã tồn tại.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }else {
                            // Đăng ký thành công;
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setTitle("Thông báo!!!")
                                    .setMessage("Tài khoản đăng ký thành công.")
                                    .setPositiveButton("Đăng nhập ngay?", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                    else {
                        // Phản hồi không có trường "exists"
                        Toast.makeText(getApplicationContext(), "Phản hồi không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(request);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo!")
                .setMessage("Có muốn tiếp tục?")
                .setPositiveButton("Có", null)
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Kết thúc Activity và quay lại màn hình trước đó;
                        finish();
                    }
                }).show();

    }

}