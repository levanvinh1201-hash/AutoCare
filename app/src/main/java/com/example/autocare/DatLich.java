package com.example.autocare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DatLich extends AppCompatActivity {

    Spinner spinnerXe;
    Spinner spinnerDichVu;

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

    // Mã dịch vụ truyền từ TrangChu
    String maDichVuDuocChon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_dat_lich
        );

        // =====================================================
        // NHẬN MÃ DỊCH VỤ TỪ TRANG CHỦ
        // =====================================================

        maDichVuDuocChon =
                getIntent().getStringExtra(
                        "maDichVu"
                );

        // =====================================================
        // ÁNH XẠ
        // =====================================================

        spinnerXe =
                findViewById(
                        R.id.spinnerXe
                );

        spinnerDichVu =
                findViewById(
                        R.id.spinnerDichVu
                );

        btnChonNgay =
                findViewById(
                        R.id.btnChonNgay
                );

        btnChonGio =
                findViewById(
                        R.id.btnChonGio
                );

        edtGhiChu =
                findViewById(
                        R.id.edtGhiChu
                );

        btnXacNhanDatLich =
                findViewById(
                        R.id.btnXacNhanDatLich
                );

        // =====================================================
        // FIREBASE
        // =====================================================

        firebaseAuth =
                FirebaseAuth.getInstance();

        firestore =
                FirebaseFirestore.getInstance();

        // =====================================================
        // KHỞI TẠO DANH SÁCH
        // =====================================================

        danhSachXe =
                new ArrayList<>();

        danhSachDichVu =
                new ArrayList<>();

        danhSachMaXe =
                new ArrayList<>();

        danhSachMaDichVu =
                new ArrayList<>();

        // =====================================================
        // ADAPTER XE
        // =====================================================

        adapterXe =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        danhSachXe
                );

        spinnerXe.setAdapter(
                adapterXe
        );

        // =====================================================
        // ADAPTER DỊCH VỤ
        // =====================================================

        adapterDichVu =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        danhSachDichVu
                );

        spinnerDichVu.setAdapter(
                adapterDichVu
        );

        // =====================================================
        // SỰ KIỆN
        // =====================================================

        btnChonNgay.setOnClickListener(
                v -> chonNgay()
        );

        btnChonGio.setOnClickListener(
                v -> chonGio()
        );

        btnXacNhanDatLich.setOnClickListener(
                v -> luuLichDat()
        );

        // =====================================================
        // TẢI DỮ LIỆU
        // =====================================================

        taiDanhSachXe();

        taiDanhSachDichVu();
    }

    // =========================================================
    // TẢI DANH SÁCH XE
    // =========================================================

    private void taiDanhSachXe() {

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập lại",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String userId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        firestore
                .collection("vehicles")
                .whereEqualTo(
                        "userId",
                        userId
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachXe.clear();
                    danhSachMaXe.clear();

                    if (querySnapshot.isEmpty()) {

                        danhSachXe.add(
                                "Bạn chưa có xe"
                        );

                    } else {

                        querySnapshot
                                .getDocuments()
                                .forEach(document -> {

                                    danhSachMaXe.add(
                                            document.getId()
                                    );

                                    String hangXe =
                                            document.getString(
                                                    "brand"
                                            );

                                    String tenXe =
                                            document.getString(
                                                    "model"
                                            );

                                    String bienSo =
                                            document.getString(
                                                    "licensePlate"
                                            );

                                    if (hangXe == null) {
                                        hangXe = "";
                                    }

                                    if (tenXe == null) {
                                        tenXe = "";
                                    }

                                    if (bienSo == null) {
                                        bienSo = "";
                                    }

                                    String thongTin =
                                            hangXe
                                                    + " "
                                                    + tenXe
                                                    + " - "
                                                    + bienSo;

                                    danhSachXe.add(
                                            thongTin
                                    );
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

    // =========================================================
    // TẢI DANH SÁCH DỊCH VỤ
    // =========================================================

    private void taiDanhSachDichVu() {

        firestore
                .collection("services")
                .whereEqualTo(
                        "hoạt động",
                        true
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachDichVu.clear();
                    danhSachMaDichVu.clear();

                    if (querySnapshot.isEmpty()) {

                        danhSachDichVu.add(
                                "Chưa có dịch vụ"
                        );

                    } else {

                        querySnapshot
                                .getDocuments()
                                .forEach(document -> {

                                    String maDichVu =
                                            document.getId();

                                    danhSachMaDichVu.add(
                                            maDichVu
                                    );

                                    String tenDichVu =
                                            document.getString(
                                                    "name"
                                            );

                                    String gia =
                                            document.getString(
                                                    "giá tiền"
                                            );

                                    if (tenDichVu == null) {
                                        tenDichVu =
                                                "Dịch vụ";
                                    }

                                    if (gia == null) {
                                        gia =
                                                "Liên hệ";
                                    }

                                    String thongTin =
                                            tenDichVu
                                                    + " - "
                                                    + gia
                                                    + " VNĐ";

                                    danhSachDichVu.add(
                                            thongTin
                                    );
                                });
                    }

                    adapterDichVu.notifyDataSetChanged();

                    // =================================================
                    // SAU KHI TẢI XONG → CHỌN DỊCH VỤ ĐƯỢC TRUYỀN VÀO
                    // =================================================

                    chonDichVuDaTruyen();

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

    // =========================================================
    // TỰ CHỌN DỊCH VỤ
    // =========================================================

    private void chonDichVuDaTruyen() {

        if (maDichVuDuocChon == null
                || maDichVuDuocChon.isEmpty()) {

            return;
        }

        int viTri =
                danhSachMaDichVu.indexOf(
                        maDichVuDuocChon
                );

        if (viTri >= 0
                && viTri < danhSachDichVu.size()) {

            spinnerDichVu.setSelection(
                    viTri
            );
        }
    }

    // =========================================================
    // CHỌN NGÀY
    // =========================================================

    private void chonNgay() {

        Calendar lich =
                Calendar.getInstance();

        int nam =
                lich.get(
                        Calendar.YEAR
                );

        int thang =
                lich.get(
                        Calendar.MONTH
                );

        int ngay =
                lich.get(
                        Calendar.DAY_OF_MONTH
                );

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (
                                view,
                                namChon,
                                thangChon,
                                ngayChon
                        ) -> {

                            String ngayHienThi =
                                    ngayChon
                                            + "/"
                                            + (thangChon + 1)
                                            + "/"
                                            + namChon;

                            btnChonNgay.setText(
                                    "Ngày: "
                                            + ngayHienThi
                            );
                        },
                        nam,
                        thang,
                        ngay
                );

        datePickerDialog.show();
    }

    // =========================================================
    // CHỌN GIỜ
    // =========================================================

    private void chonGio() {

        Calendar lich =
                Calendar.getInstance();

        int gio =
                lich.get(
                        Calendar.HOUR_OF_DAY
                );

        int phut =
                lich.get(
                        Calendar.MINUTE
                );

        TimePickerDialog timePickerDialog =
                new TimePickerDialog(
                        this,
                        (
                                view,
                                gioChon,
                                phutChon
                        ) -> {

                            String gioHienThi =
                                    String.format(
                                            "%02d:%02d",
                                            gioChon,
                                            phutChon
                                    );

                            btnChonGio.setText(
                                    "Giờ: "
                                            + gioHienThi
                            );
                        },
                        gio,
                        phut,
                        true
                );

        timePickerDialog.show();
    }

    // =========================================================
    // LƯU LỊCH ĐẶT
    // =========================================================

    private void luuLichDat() {

        // =====================================================
        // KIỂM TRA USER
        // =====================================================

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập lại",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // KIỂM TRA XE
        // =====================================================

        if (danhSachXe.isEmpty()
                || danhSachMaXe.isEmpty()
                || spinnerXe.getSelectedItemPosition() < 0
                || spinnerXe.getSelectedItemPosition()
                >= danhSachMaXe.size()) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn xe",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // KIỂM TRA DỊCH VỤ
        // =====================================================

        if (danhSachDichVu.isEmpty()
                || danhSachMaDichVu.isEmpty()
                || spinnerDichVu.getSelectedItemPosition() < 0
                || spinnerDichVu.getSelectedItemPosition()
                >= danhSachMaDichVu.size()) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn dịch vụ",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // USER ID
        // =====================================================

        String maNguoiDung =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        // =====================================================
        // XE
        // =====================================================

        int viTriXe =
                spinnerXe.getSelectedItemPosition();

        String maXe =
                danhSachMaXe.get(
                        viTriXe
                );

        // =====================================================
        // DỊCH VỤ
        // =====================================================

        int viTriDichVu =
                spinnerDichVu.getSelectedItemPosition();

        String maDichVu =
                danhSachMaDichVu.get(
                        viTriDichVu
                );

        // =====================================================
        // NGÀY
        // =====================================================

        String ngay =
                btnChonNgay
                        .getText()
                        .toString();

        if (ngay.equals("Chọn ngày")
                || ngay.equals("📅  Chọn ngày")) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn ngày",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // GIỜ
        // =====================================================

        String gio =
                btnChonGio
                        .getText()
                        .toString();

        if (gio.equals("Chọn giờ")
                || gio.equals("🕐  Chọn giờ")) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn giờ",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // GHI CHÚ
        // =====================================================

        String ghiChu =
                edtGhiChu
                        .getText()
                        .toString()
                        .trim();

        // =====================================================
        // TẠO MÃ LỊCH
        // =====================================================

        String maLich =
                firestore
                        .collection("appointments")
                        .document()
                        .getId();

        // =====================================================
        // DỮ LIỆU
        // =====================================================

        Map<String, Object> lichDat =
                new HashMap<>();

        lichDat.put(
                "maLich",
                maLich
        );

        lichDat.put(
                "maNguoiDung",
                maNguoiDung
        );

        lichDat.put(
                "maXe",
                maXe
        );

        lichDat.put(
                "maDichVu",
                maDichVu
        );

        lichDat.put(
                "ngay",
                ngay
        );

        lichDat.put(
                "gio",
                gio
        );

        lichDat.put(
                "ghiChu",
                ghiChu
        );

        lichDat.put(
                "trangThai",
                "PENDING"
        );

        // =====================================================
        // LƯU FIRESTORE
        // =====================================================

        firestore
                .collection("appointments")
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