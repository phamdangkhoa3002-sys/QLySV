package com.example.qlysv2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Activity2 extends AppCompatActivity {

    private EditText edtMaSV;
    private Button btnTimKiem;
    private Button btnQuayLai;
    private TextView tvThongTinSV;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_2);

        // Connect views
        edtMaSV = findViewById(R.id.edtMaSV);
        btnTimKiem = findViewById(R.id.btnTimKiem);
        btnQuayLai = findViewById(R.id.btnQuayLai);
        tvThongTinSV = findViewById(R.id.tvThongTinSV);

        // Connect SQLite database
        databaseHelper = new DatabaseHelper(this);

        // Hide results until a search is performed
        tvThongTinSV.setVisibility(View.GONE);

        // Back button
        btnQuayLai.setOnClickListener(v -> finish());

        // Search button
        btnTimKiem.setOnClickListener(v -> timKiemSinhVien());

        // Handle system bars safely
        View mainView = findViewById(R.id.main);

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    mainView, (view, insets) -> {

                        Insets systemBars = insets.getInsets(
                                WindowInsetsCompat.Type.systemBars());

                        view.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom);

                        return insets;
                    });
        }
    }

    // Search for a student by student code
    private void timKiemSinhVien() {

        String maSV = edtMaSV.getText().toString().trim();

        if (maSV.isEmpty()) {
            tvThongTinSV.setVisibility(View.GONE);

            Toast.makeText(this,
                    "Vui lòng nhập mã sinh viên!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<TodoModel> danhSach =
                databaseHelper.getAllTodos();

        TodoModel sinhVienTimDuoc = null;

        for (TodoModel sv : danhSach) {
            if (sv.getMaSV() != null
                    && sv.getMaSV().equalsIgnoreCase(maSV)) {
                sinhVienTimDuoc = sv;
                break;
            }
        }

        if (sinhVienTimDuoc != null) {

            TodoModel sv = sinhVienTimDuoc;

            String thongTin =
                    "Mã sinh viên: " + sv.getMaSV() + "\n\n"
                            + "Họ tên: " + sv.getTitle() + "\n\n"
                            + "Ngày sinh: " + sv.getDate() + "\n\n"
                            + "Giới tính: " + sv.getType() + "\n\n"
                            + "Mã lớp: " + sv.getLop() + "\n\n"
                            + "Địa chỉ: " + sv.getContent() + "\n\n"
                            + "Số điện thoại: " + sv.getSoDienThoai();

            tvThongTinSV.setText(thongTin);
            tvThongTinSV.setVisibility(View.VISIBLE);

        } else {

            tvThongTinSV.setVisibility(View.GONE);

            Toast.makeText(this,
                    "Không tìm thấy sinh viên có mã " + maSV,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
