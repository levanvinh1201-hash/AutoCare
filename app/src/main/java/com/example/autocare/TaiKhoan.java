package com.example.autocare;

public class TaiKhoan {

    private String userId;
    private String hoTen;
    private String email;
    private String role;

    public TaiKhoan() {
    }

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
}