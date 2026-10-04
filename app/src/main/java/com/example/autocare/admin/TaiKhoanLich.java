package com.example.autocare.admin;

public class TaiKhoanLich {

    private String userId;
    private String hoTen;
    private String email;
    private int soLuongLich;

    public TaiKhoanLich() {
    }

    public TaiKhoanLich(
            String userId,
            String hoTen,
            String email,
            int soLuongLich) {

        this.userId = userId;
        this.hoTen = hoTen;
        this.email = email;
        this.soLuongLich = soLuongLich;
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

    public int getSoLuongLich() {
        return soLuongLich;
    }
}