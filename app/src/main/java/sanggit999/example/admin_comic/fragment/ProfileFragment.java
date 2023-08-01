package sanggit999.example.admin_comic.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import sanggit999.example.admin_comic.R;
import sanggit999.example.admin_comic.activity.LoginActivity;
import sanggit999.example.admin_comic.model.User;
import sanggit999.example.admin_comic.singleton.UserDataSingleton;


public class ProfileFragment extends Fragment {

    private TextView textProfileName;

    private Button btnProfileExit;

    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        textProfileName = view.findViewById(R.id.textProfileName);
        btnProfileExit = view.findViewById(R.id.btnProfileExit);


        User user = UserDataSingleton.getInstance().getUser();

        textProfileName.setText(user.getFullname());

        btnProfileExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Thoát!!!")
                        .setMessage("Bạn có muốn đăng xuất?")
                        .setPositiveButton("Không", null)
                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Kết thúc Activity và ra màn hình;
                                Intent intent= new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(getActivity(),"Đăng xuất thành công",Toast.LENGTH_SHORT).show();

                            }
                        }).show();

            }

        });

        return view;
    }
}