package sanggit999.example.admin_comic.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.TextView;

import sanggit999.example.admin_comic.R;

public class WelcomeDialog {
    public static void showWelcomeDialog(Context context, String fullname) {
        // Tạo một Dialog mới
        Dialog dialog = new Dialog(context);

        // Bỏ tiêu đề của Dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Đặt layout cho Dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_welcome, null);
        dialog.setContentView(view);

        // Đặt nền trong suốt cho Dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Tìm và đặt tên người dùng trong TextView
        TextView fullnameTextView = view.findViewById(R.id.fullnameTextView);
        fullnameTextView.setText(fullname);

        // Tìm và đặt sự kiện click cho nút Close
        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng Dialog khi người dùng nhấn nút Close
                dialog.dismiss();
            }
        });

        // Hiển thị Dialog lên màn hình
        dialog.show();
    }
}
