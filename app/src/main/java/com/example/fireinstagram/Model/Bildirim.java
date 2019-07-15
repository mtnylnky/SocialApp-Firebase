package com.example.fireinstagram.Model;

public class Bildirim {
    private String kullaniciid;
    private String text;
    private String gonderiid;
    private boolean ispost;

    public Bildirim() {
    }

    public Bildirim(String kullaniciid, String text, String gonderiid, boolean ispost) {
        this.kullaniciid = kullaniciid;
        this.text = text;
        this.gonderiid = gonderiid;
        this.ispost = ispost;
    }

    public String getKullaniciid() {
        return kullaniciid;
    }

    public void setKullaniciid(String kullaniciid) {
        this.kullaniciid = kullaniciid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGonderiid() {
        return gonderiid;
    }

    public void setGonderiid(String gonderiid) {
        this.gonderiid = gonderiid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}
