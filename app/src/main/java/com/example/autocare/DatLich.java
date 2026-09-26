package com.example.autocare;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DatLich extends AppCompatActivity {

    Spinner spinnerXe, spinnerDichVu;
    Button btnChonNgay;
    Button btnChonGio;
    EditText edtGhiChu;
    Button btnXacNhanDatLich;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    ArrayList<String> danhSachXe;
    ArrayList<String> danhSachDichVu;
    ArrayList<String> danhSachMaXe;
    ArrayList<String> danhSachMaDichVu;

    ArrayAdapter<String> adapterXe;
    ArrayAdapter<String> adapterDichVu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dat_lich);

        // Ánh xạ Spinner
        spinnerXe = findViewById(R.id.spinnerXe);
        spinnerDichVu = findViewById(R.id.spinnerDichVu);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Tạo danh sách
        danhSachXe = new ArrayList<>();
        danhSachDichVu = new ArrayList<>();
        danhSachMaXe = new ArrayList<>();
        danhSachMaDichVu = new ArrayList<>();

        // Ánh xạ các nút
        btnChonNgay = findViewById(R.id.btnChonNgay);
        btnChonGio = findViewById(R.id.btnChonGio);
        edtGhiChu = findViewById(R.id.edtGhiChu);
        btnXacNhanDatLich = findViewById(R.id.btnXacNhanDatLich);

        // Sự kiện chọn ngày
        btnChonNgay.setOnClickListener(v -> chonNgay());

        // Sự kiện chọn giờ
        btnChonGio.setOnClickListener(v -> chonGio());

        // Sự kiện xác nhận đặt lịch
        btnXacNhanDatLich.setOnClickListener(v -> luuLichDat());

        // Adapter xe
        adapterXe = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                danhSachXe
        );

        spinnerXe.setAdapter(adapterXe);

        // Adapter dịch vụ
        adapterDichVu = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                danhSachDichVu
        );

        spinnerDichVu.setAdapter(adapterDichVu);

        // Tải dữ liệu
        taiDanhSachXe();
        taiDanhSachDichVu();
    }

    private void taiDanhSachXe() {

        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập lại",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String userId = firebaseAuth
                .getCurrentUser()
                .getUid();

        firestore.collection("vehicles")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachXe.clear();
                    danhSachMaXe.clear();

                    if (querySnapshot.isEmpty()) {

                        danhSachXe.add("Bạn chưa có xe");

                    } else {

                        querySnapshot.getDocuments().forEach(document -> {

                            danhSachMaXe.add(document.getId());

                            String hangXe =
                                    document.getString("brand");

                            String tenXe =
                                    document.getString("model");

                            String bienSo =
                                    document.getString("licensePlate");

                            String thongTin =
                                    hangXe + " "
                                            + tenXe
                                            + " - "
                                            + bienSo;

                            danhSachXe.add(thongTin);
                        });
                    }

                    adapterXe.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Không tải được danh sách xe",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private void taiDanhSachDichVu() {

        firestore.collection("services")
                .whereEqualTo("hoạt động", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachDichVu.clear();
                    danhSachMaDichVu.clear();

                    if (querySnapshot.isEmpty()) {

                        danhSachDichVu.add("Chưa có dịch vụ");

                    } else {

                        querySnapshot.getDocuments().forEach(document -> {

                            danhSachMaDichVu.add(document.getId());

                            String tenDichVu =
                                    document.getString("name");

                            String gia =
                                    document.getString("giá tiền");

                            String thongTin =
                                    tenDichVu
                                            + " - "
                                            + gia
                                            + " VNĐ";

                            danhSachDichVu.add(thongTin);
                        });
                    }

                    adapterDichVu.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Không tải được danh sách dịch vụ: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void chonNgay() {

        java.util.Calendar lich =
                java.util.Calendar.getInstance();

        int nam =
                lich.get(java.util.Calendar.YEAR);

        int thang =
                lich.get(java.util.Calendar.MONTH);

        int ngay =
                lich.get(java.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, namChon, thangChon, ngayChon) -> {

                            String ngayHienThi =
                                    ngayChon + "/" +
                                            (thangChon + 1) + "/" +
                                            namChon;

                            btnChonNgay.setText(
                                    "Ngày: " + ngayHienThi
                            );
                        },
                        nam,
                        thang,
                        ngay
                );

        datePickerDialog.show();
    }

    private void chonGio() {

        java.util.Calendar lich =
                java.util.Calendar.getInstance();

        int gio =
                lich.get(java.util.Calendar.HOUR_OF_DAY);

        int phut =
                lich.get(java.util.Calendar.MINUTE);

        android.app.TimePickerDialog timePickerDialog =
                new android.app.TimePickerDialog(
                        this,
                        (view, gioChon, phutChon) -> {

                            String gioHienThi =
                                    String.format(
                                            "%02d:%02d",
                                            gioChon,
                                            phutChon
                                    );

                            btnChonGio.setText(
                                    "Giờ: " + gioHienThi
                            );
                        },
                        gio,
                        phut,
                        true
                );

        timePickerDialog.show();
    }

    private void luuLichDat() {

        // Kiểm tra người dùng
        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập lại",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Kiểm tra xe
        if (danhSachXe.isEmpty()
                || danhSachMaXe.isEmpty()
                || spinnerXe.getSelectedItemPosition() < 0
                || spinnerXe.getSelectedItemPosition() >= danhSachMaXe.size()) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn xe",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Kiểm tra dịch vụ
        if (danhSachDichVu.isEmpty()
                || danhSachMaDichVu.isEmpty()
                || spinnerDichVu.getSelectedItemPosition() < 0
                || spinnerDichVu.getSelectedItemPosition() >= danhSachMaDichVu.size()) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn dịch vụ",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Lấy mã người dùng
        String maNguoiDung =
                firebaseAuth.getCurrentUser().getUid();

        // Lấy mã xe
        int viTriXe =
                spinnerXe.getSelectedItemPosition();

        String maXe =
                danhSachMaXe.get(viTriXe);

        // Lấy mã dịch vụ
        int viTriDichVu =
                spinnerDichVu.getSelectedItemPosition();

        String maDichVu =
                danhSachMaDichVu.get(viTriDichVu);

        // Lấy ngày
        String ngay =
                btnChonNgay.getText().toString();

        if (ngay.equals("Chọn ngày")
                || ngay.equals("📅  Chọn ngày")) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn ngày",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Lấy giờ
        String gio =
                btnChonGio.getText().toString();

        if (gio.equals("Chọn giờ")
                || gio.equals("🕐  Chọn giờ")) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn giờ",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Lấy ghi chú
        String ghiChu =
                edtGhiChu.getText().toString().trim();

        // Tạo mã lịch
        String maLich =
                firestore.collection("appointments")
                        .document()
                        .getId();

        // Tạo dữ liệu lịch
        java.util.Map<String, Object> lichDat =
                new java.util.HashMap<>();

        lichDat.put("maLich", maLich);
        lichDat.put("maNguoiDung", maNguoiDung);
        lichDat.put("maXe", maXe);
        lichDat.put("maDichVu", maDichVu);
        lichDat.put("ngay", ngay);
        lichDat.put("gio", gio);
        lichDat.put("ghiChu", ghiChu);
        lichDat.put("trangThai", "PENDING");

        // Lưu vào Firestore
        firestore.collection("appointments")
                .document(maLich)
                .set(lichDat)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Đặt lịch thành công!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Đặt lịch thất bại: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}