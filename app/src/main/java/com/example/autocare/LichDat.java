package com.example.autocare;

public class LichDat {

    private String maLich;
    private String maNguoiDung;
    private String maXe;
    private String maDichVu;
    private String ngay;
    private String gio;
    private String ghiChu;
    private String trangThai;

    // Constructor rỗng
    public LichDat() {
    }

    // Constructor cũ - giữ lại để code hiện tại không bị lỗi
    public LichDat(
            String maLich,
            String maXe,
            String maDichVu,
            String ngay,
            String gio,
            String ghiChu,
            String trangThai) {

        this.maLich = maLich;
        this.maXe = maXe;
        this.maDichVu = maDichVu;
        this.ngay = ngay;
        this.gio = gio;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    // Constructor mới có maNguoiDung
    public LichDat(
            String maLich,
            String maNguoiDung,
            String maXe,
            String maDichVu,
            String ngay,
            String gio,
            String ghiChu,
            String trangThai) {

        this.maLich = maLich;
        this.maNguoiDung = maNguoiDung;
        this.maXe = maXe;
        this.maDichVu = maDichVu;
        this.ngay = ngay;
        this.gio = gio;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public String getMaLich() {
        return maLich;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public String getMaXe() {
        return maXe;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public String getNgay() {
        return ngay;
    }

    public String getGio() {
        return gio;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}