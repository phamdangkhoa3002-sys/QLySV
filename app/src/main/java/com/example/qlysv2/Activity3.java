package com.example.qlysv2;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.content.Intent;
import android.os.Environment;
import android.graphics.Color;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activity3 extends AppCompatActivity {

    private EditText edtMaLop;
    private Button btnTimTheoLop, btnXuatPDF, btnQuayLai;
    private ListView lvDanhSachSV;

    private ArrayList<String> danhSachSV;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper databaseHelper;
    private ActivityResultLauncher<String> createPdfLauncher;
    private byte[] pdfData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create PDF file picker
        createPdfLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/pdf"),
                uri -> {
                    if (uri != null) {
                        savePDF(uri);
                    }
                }
        );

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_3);

        // Connect views
        edtMaLop = findViewById(R.id.edtMaLop);
        btnTimTheoLop = findViewById(R.id.btnTimTheoLop);
        btnXuatPDF = findViewById(R.id.btnXuatPDF);
        btnQuayLai = findViewById(R.id.btnQuayLai);
        lvDanhSachSV = findViewById(R.id.lvDanhSachSV);

        // Connect database
        databaseHelper = new DatabaseHelper(this);

        // Create list adapter
        danhSachSV = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                danhSachSV
        );

        lvDanhSachSV.setAdapter(adapter);

        // Back button
        btnQuayLai.setOnClickListener(v -> finish());

        // Search students by class code
        btnTimTheoLop.setOnClickListener(v -> timSinhVienTheoLop());



// Export PDF
        btnXuatPDF.setOnClickListener(v -> {

            if (danhSachSV.isEmpty()) {
                Toast.makeText(this,
                        "Vui lòng tìm danh sách sinh viên trước!",
                        Toast.LENGTH_SHORT).show();
            } else {
                xuatPDF();
            }

        });

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

    // Search students by class code
    private void timSinhVienTheoLop() {

        String maLop = edtMaLop.getText().toString().trim();

        if (maLop.isEmpty()) {
            Toast.makeText(this,
                    "Vui lòng nhập mã lớp!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear old results
        danhSachSV.clear();

        // Get all students from SQLite
        ArrayList<TodoModel> allStudents =
                databaseHelper.getAllTodos();

        // Filter students by class code
        for (TodoModel sv : allStudents) {

            if (sv.getLop() != null
                    && sv.getLop().equalsIgnoreCase(maLop)) {

                String thongTin =
                        "Mã sinh viên: " + sv.getMaSV() + "\n"
                                + "Họ tên: " + sv.getTitle() + "\n"
                                + "Ngày sinh: " + sv.getDate() + "\n"
                                + "Giới tính: " + sv.getType() + "\n"
                                + "Mã lớp: " + sv.getLop() + "\n"
                                + "Địa chỉ: " + sv.getContent() + "\n"
                                + "Số điện thoại: " + sv.getSoDienThoai();

                danhSachSV.add(thongTin);
            }
        }

        adapter.notifyDataSetChanged();

        if (danhSachSV.isEmpty()) {
            Toast.makeText(this,
                    "Không tìm thấy sinh viên thuộc lớp " + maLop,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Tìm thấy " + danhSachSV.size() + " sinh viên!",
                    Toast.LENGTH_SHORT).show();
        }
    }
    // Generate PDF
    private void xuatPDF() {

        PdfDocument pdfDocument = new PdfDocument();

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.rgb(108, 74, 182));
        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);

        Paint headerPaint = new Paint();
        headerPaint.setColor(Color.DKGRAY);
        headerPaint.setTextSize(13);
        headerPaint.setFakeBoldText(true);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.LTGRAY);
        linePaint.setStrokeWidth(1);

        int pageWidth = 595;
        int pageHeight = 842;

        int pageNumber = 1;
        int y = 160;

        String maLop = edtMaLop.getText().toString().trim();

        // Create first page
        PdfDocument.Page page = taoTrangPDF(
                pdfDocument, pageNumber, pageWidth, pageHeight,
                maLop, titlePaint, headerPaint
        );

        Canvas canvas = page.getCanvas();

        // Export date
        String ngayXuat = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());

        canvas.drawText(
                "Ngày xuất: " + ngayXuat,
                40, 125, textPaint
        );

        canvas.drawText(
                "Tổng số sinh viên: " + danhSachSV.size(),
                40, 145, textPaint
        );

        // Draw student information
        int stt = 1;

        for (String sinhVien : danhSachSV) {

            String[] lines = sinhVien.split("\n");

            // Check page space before drawing a student
            int studentHeight = lines.length * 20 + 35;

            if (y + studentHeight > 780) {

                // Finish old page
                canvas.drawText(
                        "Trang " + pageNumber,
                        275, 820, textPaint
                );

                pdfDocument.finishPage(page);

                // Create next page
                pageNumber++;

                page = taoTrangPDF(
                        pdfDocument, pageNumber,
                        pageWidth, pageHeight,
                        maLop, titlePaint, headerPaint
                );

                canvas = page.getCanvas();

                y = 130;
            }

            // Student number
            canvas.drawText(
                    "SINH VIÊN " + stt,
                    40, y, headerPaint
            );

            y += 22;

            // Student details
            for (String line : lines) {

                canvas.drawText(
                        line,
                        55, y,
                        textPaint
                );

                y += 20;
            }

            // Separator line
            y += 5;

            canvas.drawLine(
                    40, y,
                    555, y,
                    linePaint
            );

            y += 25;

            stt++;
        }

        // Finish last page
        canvas.drawText(
                "Trang " + pageNumber,
                275, 820, textPaint
        );

        pdfDocument.finishPage(page);

        // Convert PDF into byte array
        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            pdfDocument.writeTo(outputStream);

            pdfData = outputStream.toByteArray();

            pdfDocument.close();

            // Open Android file picker
            String fileName = "DanhSachSinhVien_" + maLop + ".pdf";

            createPdfLauncher.launch(fileName);

        } catch (IOException e) {

            pdfDocument.close();

            Toast.makeText(this,
                    "Lỗi tạo PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Create PDF page
    private PdfDocument.Page taoTrangPDF(
            PdfDocument pdfDocument,
            int pageNumber,
            int pageWidth,
            int pageHeight,
            String maLop,
            Paint titlePaint,
            Paint headerPaint) {

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(
                        pageWidth,
                        pageHeight,
                        pageNumber
                ).create();

        PdfDocument.Page page =
                pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        // Report title
        canvas.drawText(
                "DANH SÁCH SINH VIÊN",
                40, 60,
                titlePaint
        );

        // Class code
        canvas.drawText(
                "Mã lớp: " + maLop,
                40, 95,
                headerPaint
        );

        // Horizontal line
        Paint linePaint = new Paint();
        linePaint.setColor(Color.LTGRAY);
        linePaint.setStrokeWidth(2);

        canvas.drawLine(
                40, 105,
                555, 105,
                linePaint
        );

        return page;
    }
    // Save PDF file
    private void savePDF(Uri uri) {

        if (pdfData == null) {
            return;
        }

        try {

            OutputStream outputStream =
                    getContentResolver().openOutputStream(uri);

            if (outputStream != null) {

                outputStream.write(pdfData);
                outputStream.close();

                Toast.makeText(this,
                        "Xuất PDF thành công!",
                        Toast.LENGTH_LONG).show();

                pdfData = null;

            }

        } catch (IOException e) {

            Toast.makeText(this,
                    "Lỗi lưu PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
