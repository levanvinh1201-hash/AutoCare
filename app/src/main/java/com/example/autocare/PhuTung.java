package com.example.autocare;

public class PhuTung {

    private String phuTungId;
    private String ten;
    private String gia;
    private String hinhAnh;
    private String moTa;
    private int soLuong;
    private boolean conHang;

    // Firebase Firestore cần constructor rỗng
    public PhuTung() {
    }

    public PhuTung(
            String phuTungId,
            String ten,
            String gia,
            String hinhAnh,
            String moTa,
            int soLuong,
            boolean conHang
    ) {
        this.phuTungId = phuTungId;
        this.ten = ten;
        this.gia = gia;
        this.hinhAnh = hinhAnh;
        this.moTa = moTa;
        this.soLuong = soLuong;
        this.conHang = conHang;
    }

    public String getPhuTungId() {
        return phuTungId;
    }

    public String getTen() {
        return ten;
    }

    public String getGia() {
        return gia;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public String getMoTa() {
        return moTa;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public boolean isConHang() {
        return conHang;
    }

    public void setPhuTungId(String phuTungId) {
        this.phuTungId = phuTungId;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setGia(String gia) {
        this.gia = gia;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public void setConHang(boolean conHang) {
        this.conHang = conHang;
    }
}