package com.example.autocare;

public class TaiKhoan {

    private String userId;
    private String hoTen;
    private String email;
    private String role;
    private String soDienThoai;

    // Constructor rỗng để Firestore đọc dữ liệu
    public TaiKhoan() {
    }

    // Giữ constructor cũ để không ảnh hưởng code đang sử dụng
    public TaiKhoan(
            String userId,
            String hoTen,
            String email,
            String role) {

        this.userId = userId;
        this.hoTen = hoTen;
        this.email = email;
        this.role = role;
    }

    // Constructor có số điện thoại
    public TaiKhoan(
            String userId,
            String hoTen,
            String email,
            String role,
            String soDienThoai) {

        this.userId = userId;
        this.hoTen = hoTen;
        this.email = email;
        this.role = role;
        this.soDienThoai = soDienThoai;
    }

    public String getUserId() {
        return userId;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}