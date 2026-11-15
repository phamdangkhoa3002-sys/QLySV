package com.example.qlysv2;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText edtMaSV, edtHoTen, edtNgaySinh;
    private EditText edtDiaChi, edtLop, edtSoDienThoai;

    private RadioGroup rgGioiTinh;
    private RadioButton rbNam, rbNu;

    private Button btnThem, btnSua, btnXoa;

    private ArrayList<TodoModel> todoList;
    private TodoAdapter todoAdapter;

    private DatabaseHelper databaseHelper;

    private int selectedId = -1;

    private RecyclerView rvSinhVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // System bar spacing
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView,
                    (v, insets) -> {
                        Insets systemBars = insets.getInsets(
                                WindowInsetsCompat.Type.systemBars());

                        v.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom);

                        return insets;
                    });
        }






        // EditText
        edtMaSV = findViewById(R.id.edtMaSV);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtDiaChi = findViewById(R.id.edtDiaChi);
        edtLop = findViewById(R.id.edtLop);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);

        // Gender
        rgGioiTinh = findViewById(R.id.rgGioiTinh);
        rbNam = findViewById(R.id.rbNam);
        rbNu = findViewById(R.id.rbNu);

        // Buttons
        btnThem = findViewById(R.id.btnThem);
        btnSua = findViewById(R.id.btnSua);
        btnXoa = findViewById(R.id.btnXoa);

        // Database
        databaseHelper = new DatabaseHelper(this);

// RecyclerView
        rvSinhVien = findViewById(R.id.rvSinhVien);

        rvSinhVien.setLayoutManager(
                new LinearLayoutManager(this)
        );
// Load students
        loadData();




        // ADD STUDENT
        btnThem.setOnClickListener(v -> {
            if (!validateForm()) {
                return;
            }

            TodoModel model = getFormModel(-1);

            long result = databaseHelper.addTodo(model);

            if (result != -1) {
                Toast.makeText(this,
                        "Thêm sinh viên thành công!",
                        Toast.LENGTH_SHORT).show();

                loadData();
                clearForm();
            } else {
                Toast.makeText(this,
                        "Thêm thất bại! Mã sinh viên có thể đã tồn tại.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // UPDATE STUDENT
        btnSua.setOnClickListener(v -> {
            if (selectedId == -1) {
                Toast.makeText(this,
                        "Vui lòng chọn sinh viên cần sửa!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validateForm()) {
                return;
            }

            TodoModel model = getFormModel(selectedId);

            int result = databaseHelper.updateTodo(model);

            if (result > 0) {
                Toast.makeText(this,
                        "Cập nhật sinh viên thành công!",
                        Toast.LENGTH_SHORT).show();

                loadData();
                clearForm();
            } else {
                Toast.makeText(this,
                        "Cập nhật thất bại! Mã sinh viên có thể đã tồn tại.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // DELETE STUDENT
        btnXoa.setOnClickListener(v -> {
            if (selectedId == -1) {
                Toast.makeText(this,
                        "Vui lòng chọn sinh viên cần xóa!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int result = databaseHelper.deleteTodo(selectedId);

            if (result > 0) {
                Toast.makeText(this,
                        "Xóa sinh viên thành công!",
                        Toast.LENGTH_SHORT).show();

                loadData();
                clearForm();
            } else {
                Toast.makeText(this,
                        "Xóa sinh viên thất bại!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load students from SQLite

    private void loadData() {

        todoList = databaseHelper.getAllTodos();

        todoAdapter = new TodoAdapter(todoList);

        todoAdapter.setOnItemClickListener(
                new TodoAdapter.OnItemClickListener() {

                    // BUTTON SUA
                    @Override
                    public void onEdit(TodoModel model) {

                        selectedId = model.getId();

                        edtMaSV.setText(model.getMaSV());
                        edtHoTen.setText(model.getTitle());
                        edtNgaySinh.setText(model.getDate());
                        edtDiaChi.setText(model.getContent());
                        edtLop.setText(model.getLop());
                        edtSoDienThoai.setText(model.getSoDienThoai());

                        if ("Nam".equals(model.getType())) {
                            rbNam.setChecked(true);
                        } else if ("Nữ".equals(model.getType())) {
                            rbNu.setChecked(true);
                        } else {
                            rgGioiTinh.clearCheck();
                        }

                        Toast.makeText(MainActivity.this,
                                "Đã chọn sinh viên để sửa!",
                                Toast.LENGTH_SHORT).show();
                    }

                    // BUTTON XOA
                    @Override
                    public void onDelete(TodoModel model) {

                        new android.app.AlertDialog.Builder(MainActivity.this)
                                .setTitle("Xác nhận xóa")
                                .setMessage("Bạn có chắc muốn xóa sinh viên "
                                        + model.getTitle() + "?")
                                .setPositiveButton("Xóa", (dialog, which) -> {

                                    int result = databaseHelper.deleteTodo(
                                            model.getId()
                                    );

                                    if (result > 0) {

                                        Toast.makeText(MainActivity.this,
                                                "Xóa sinh viên thành công!",
                                                Toast.LENGTH_SHORT).show();

                                        if (selectedId == model.getId()) {
                                            clearForm();
                                        }

                                        loadData();

                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "Xóa thất bại!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    }
                });

        rvSinhVien.setAdapter(todoAdapter);
    }


    // Read the form and create a student object
    private TodoModel getFormModel(int id) {
        String maSV = edtMaSV.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String ngaySinh = edtNgaySinh.getText().toString().trim();
        String diaChi = edtDiaChi.getText().toString().trim();
        String lop = edtLop.getText().toString().trim();
        String soDienThoai =
                edtSoDienThoai.getText().toString().trim();
        String gioiTinh = getGioiTinh();

        if (id == -1) {
            return new TodoModel(
                    maSV, hoTen, diaChi, ngaySinh,
                    gioiTinh, lop, soDienThoai);
        }

        return new TodoModel(
                id, maSV, hoTen, diaChi, ngaySinh,
                gioiTinh, lop, soDienThoai);
    }

    // Validate form
    private boolean validateForm() {
        String maSV = edtMaSV.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String ngaySinh = edtNgaySinh.getText().toString().trim();
        String diaChi = edtDiaChi.getText().toString().trim();
        String lop = edtLop.getText().toString().trim();
        String soDienThoai =
                edtSoDienThoai.getText().toString().trim();
        String gioiTinh = getGioiTinh();

        if (maSV.isEmpty() || hoTen.isEmpty()
                || ngaySinh.isEmpty() || diaChi.isEmpty()
                || lop.isEmpty() || soDienThoai.isEmpty()
                || gioiTinh.isEmpty()) {

            Toast.makeText(this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Get selected gender
    private String getGioiTinh() {
        int checkedId = rgGioiTinh.getCheckedRadioButtonId();

        if (checkedId == R.id.rbNam) {
            return "Nam";
        } else if (checkedId == R.id.rbNu) {
            return "Nữ";
        }

        return "";
    }

    // Clear form
    private void clearForm() {
        edtMaSV.setText("");
        edtHoTen.setText("");
        edtNgaySinh.setText("");
        edtDiaChi.setText("");
        edtLop.setText("");
        edtSoDienThoai.setText("");

        rgGioiTinh.clearCheck();

        selectedId = -1;
    }

    // Toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.menutoolbarqlysv2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuFindSV) {
            startActivity(new Intent(
                    MainActivity.this, Activity2.class));
            return true;

        } else if (id == R.id.menThongKe) {
            startActivity(new Intent(
                    MainActivity.this, Activity3.class));
            return true;

        } else if (id == R.id.menuExportPDF) {
            startActivity(new Intent(
                    MainActivity.this, Activity3.class));
            return true;

        } else if (id == R.id.menuExit) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Xác nhận thoát")
                    .setMessage("Bạn có chắc chắn muốn thoát chương trình không?")

                    .setPositiveButton("Có", (dialog, which) -> {

                        Toast.makeText(MainActivity.this,
                                "Đã thoát chương trình!",
                                Toast.LENGTH_SHORT).show();

                        finishAffinity();
                    })

                    .setNegativeButton("Không", (dialog, which) -> {
                        dialog.dismiss();
                    })

                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
