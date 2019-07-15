package com.example.fireinstagram.Model;

public class Hikaye {
    private String resimurl;
    private String hikayeid;
    private String kullaniciid;
    private long baslamazamani;
    private long bitiszamani;

    public Hikaye() {
    }

    public Hikaye(String resimurl, String hikayeid, String kullaniciid, long baslamazamani, long bitiszamani) {
        this.resimurl = resimurl;
        this.hikayeid = hikayeid;
        this.kullaniciid = kullaniciid;
        this.baslamazamani = baslamazamani;
        this.bitiszamani = bitiszamani;
    }

    public String getResimurl() {
        return resimurl;
    }

    public void setResimurl(String resimurl) {
        this.resimurl = resimurl;
    }

    public String getHikayeid() {
        return hikayeid;
    }

    public void setHikayeid(String hikayeid) {
        this.hikayeid = hikayeid;
    }

    public String getKullaniciid() {
        return kullaniciid;
    }

    public void setKullaniciid(String kullaniciid) {
        this.kullaniciid = kullaniciid;
    }

    public long getBaslamazamani() {
        return baslamazamani;
    }

    public void setBaslamazamani(long baslamazamani) {
        this.baslamazamani = baslamazamani;
    }

    public long getBitiszamani() {
        return bitiszamani;
    }

    public void setBitiszamani(long bitiszamani) {
        this.bitiszamani = bitiszamani;
    }
}
