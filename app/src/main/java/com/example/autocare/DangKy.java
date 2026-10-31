package com.example.autocare;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.CredentialManagerCallback;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;import android.view.View;

public class DangKy extends AppCompatActivity {

    EditText edtHoTen, edtEmail, edtMatKhau, edtXacNhanMatKhau;

    Button btnDangKy;
    View btnDangKyGoogle;

    TextView tvDangNhap;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    CredentialManager credentialManager;

    private boolean dangXuLy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dang_ky);

        edtHoTen = findViewById(R.id.edtHoTen);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtXacNhanMatKhau = findViewById(R.id.edtXacNhanMatKhau);

        btnDangKy = findViewById(R.id.btnDangKy);
        btnDangKyGoogle = findViewById(R.id.btnDangKyGoogle);

        tvDangNhap = findViewById(R.id.tvDangNhap);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        credentialManager = CredentialManager.create(this);

        // Đăng ký bằng email
        btnDangKy.setOnClickListener(v -> dangKyTaiKhoan());

        // Đăng ký bằng Google
        btnDangKyGoogle.setOnClickListener(v -> dangNhapGoogle());


        tvDangNhap.setOnClickListener(v -> finish());
    }

    // ==========================================
    // ĐĂNG KÝ BẰNG EMAIL VÀ MẬT KHẨU
    // ==========================================

    private void dangKyTaiKhoan() {

        if (dangXuLy) return;

        String hoTen = edtHoTen.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        String matKhau = edtMatKhau.getText().toString();
        String xacNhanMatKhau =
                edtXacNhanMatKhau.getText().toString();

        if (TextUtils.isEmpty(hoTen)) {
            edtHoTen.setError("Vui lòng nhập họ tên");
            edtHoTen.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(matKhau)) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return;
        }

        if (matKhau.length() < 6) {
            edtMatKhau.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtMatKhau.requestFocus();
            return;
        }

        if (!matKhau.equals(xacNhanMatKhau)) {
            edtXacNhanMatKhau.setError("Mật khẩu không khớp");
            edtXacNhanMatKhau.requestFocus();
            return;
        }

        setDangXuLy(true);

        firebaseAuth
                .createUserWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {
                        setDangXuLy(false);

                        String loi = task.getException() != null
                                ? task.getException().getMessage()
                                : "Đăng ký thất bại";

                        thongBao(loi);
                        return;
                    }

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user == null) {
                        setDangXuLy(false);
                        thongBao("Không lấy được thông tin tài khoản");
                        return;
                    }

                    luuThongTinNguoiDung(
                            user,
                            hoTen,
                            false
                    );
                });
    }

    // ==========================================
    // ĐĂNG NHẬP / ĐĂNG KÝ BẰNG GOOGLE
    // ==========================================

    private void dangNhapGoogle() {

        if (dangXuLy) return;

        setDangXuLy(true);

        GetGoogleIdOption googleIdOption =
                new GetGoogleIdOption.Builder()
                        .setServerClientId(
                                getString(R.string.google_web_client_id)
                        )
                        .setFilterByAuthorizedAccounts(false)
                        .build();

        GetCredentialRequest request =
                new GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<
                        GetCredentialResponse,
                        GetCredentialException>() {

                    @Override
                    public void onResult(
                            @NonNull GetCredentialResponse result) {

                        runOnUiThread(() ->
                                xuLyGoogleCredential(
                                        result.getCredential()
                                )
                        );
                    }

                    @Override
                    public void onError(
                            @NonNull GetCredentialException e) {

                        runOnUiThread(() -> {
                            setDangXuLy(false);

                            thongBao(
                                    "Không thể đăng nhập Google: "
                                            + e.getMessage()
                            );
                        });
                    }
                }
        );
    }

    private void xuLyGoogleCredential(Credential credential) {

        if (!(credential instanceof CustomCredential)) {
            setDangXuLy(false);
            thongBao("Không nhận được thông tin Google");
            return;
        }

        CustomCredential customCredential =
                (CustomCredential) credential;

        if (!GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                .equals(customCredential.getType())) {

            setDangXuLy(false);
            thongBao("Loại tài khoản không được hỗ trợ");
            return;
        }

        try {

            GoogleIdTokenCredential googleCredential =
                    GoogleIdTokenCredential.createFrom(
                            customCredential.getData()
                    );

            xacThucGoogleVoiFirebase(
                    googleCredential.getIdToken()
            );

        } catch (Exception e) {

            setDangXuLy(false);
            thongBao("Không đọc được thông tin tài khoản Google: "
                    + e.getMessage());
        }
    }

    private void xacThucGoogleVoiFirebase(String idToken) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {

                        setDangXuLy(false);

                        String loi = task.getException() != null
                                ? task.getException().getMessage()
                                : "Xác thực Google thất bại";

                        thongBao(loi);
                        return;
                    }

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user == null) {
                        setDangXuLy(false);
                        thongBao("Không lấy được thông tin Google");
                        return;
                    }

                    String hoTen = user.getDisplayName();

                    if (hoTen == null || hoTen.trim().isEmpty()) {
                        hoTen = "Khách hàng AutoCare";
                    }

                    luuThongTinNguoiDung(
                            user,
                            hoTen,
                            true
                    );
                });
    }

    // ==========================================
    // LƯU THÔNG TIN VÀO FIRESTORE
    // ==========================================

    private void luuThongTinNguoiDung(
            FirebaseUser user,
            String hoTen,
            boolean dangNhapGoogle) {

        String userId = user.getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    // Nếu hồ sơ đã tồn tại, không ghi đè
                    // họ tên, email hoặc quyền người dùng.
                    if (document.exists()) {

                        setDangXuLy(false);

                        if (dangNhapGoogle) {
                            moTrangChu();
                        } else {
                            thongBao("Đăng ký tài khoản thành công!");
                            finish();
                        }

                        return;
                    }

                    Map<String, Object> nguoiDung =
                            new HashMap<>();

                    nguoiDung.put("userId", userId);
                    nguoiDung.put("hoTen", hoTen);
                    nguoiDung.put("email", user.getEmail());
                    nguoiDung.put("role", "customer");

                    firestore.collection("users")
                            .document(userId)
                            .set(nguoiDung)
                            .addOnSuccessListener(unused -> {

                                setDangXuLy(false);

                                if (dangNhapGoogle) {

                                    thongBao(
                                            "Đăng nhập Google thành công!"
                                    );

                                    moTrangChu();

                                } else {

                                    thongBao(
                                            "Đăng ký tài khoản thành công!"
                                    );

                                    finish();
                                }
                            })
                            .addOnFailureListener(e -> {

                                setDangXuLy(false);

                                thongBao(
                                        "Không lưu được thông tin: "
                                                + e.getMessage()
                                );
                            });

                })
                .addOnFailureListener(e -> {

                    setDangXuLy(false);

                    thongBao(
                            "Không kiểm tra được tài khoản: "
                                    + e.getMessage()
                    );
                });
    }

    // ==========================================
    // CHUYỂN ĐẾN TRANG CHỦ
    // ==========================================

    private void moTrangChu() {

        Intent intent = new Intent(
                DangKy.this,
                TrangChu.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    // ==========================================
    // HÀM HỖ TRỢ
    // ==========================================

    private void setDangXuLy(boolean value) {

        dangXuLy = value;

        btnDangKy.setEnabled(!value);
        btnDangKyGoogle.setEnabled(!value);
    }

    private void thongBao(String noiDung) {

        Toast.makeText(
                DangKy.this,
                noiDung,
                Toast.LENGTH_LONG
        ).show();
    }
}