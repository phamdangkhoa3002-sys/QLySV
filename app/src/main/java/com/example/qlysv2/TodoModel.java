package com.example.qlysv2;

public class TodoModel {
    private int id;
    private String maSV;
    private String title;
    private String content;
    private String date;
    private String type;
    private String lop;
    private String soDienThoai;

    public TodoModel(int id, String maSV, String title,
                     String content, String date, String type,
                     String lop, String soDienThoai) {
        this.id = id;
        this.maSV = maSV;
        this.title = title;
        this.content = content;
        this.date = date;
        this.type = type;
        this.lop = lop;
        this.soDienThoai = soDienThoai;
    }

    public TodoModel(String maSV, String title, String content,
                     String date, String type, String lop,
                     String soDienThoai) {
        this(-1, maSV, title, content, date, type, lop, soDienThoai);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
