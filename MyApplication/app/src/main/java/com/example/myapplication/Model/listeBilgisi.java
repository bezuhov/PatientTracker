package com.example.myapplication.Model;

public class listeBilgisi {
    String zamanBilgisi,aciklama,imEylem;

    public listeBilgisi(String zamanBilgisi, String aciklama, String imEylem) {
        this.zamanBilgisi = zamanBilgisi;
        this.aciklama = aciklama;
        this.imEylem = imEylem;
    }

    public listeBilgisi() {
    }

    public String getZamanBilgisi() {
        return zamanBilgisi;
    }

    public void setZamanBilgisi(String zamanBilgisi) {
        this.zamanBilgisi = zamanBilgisi;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getImEylem() {
        return imEylem;
    }

    public void setImEylem(String imEylem) {
        this.imEylem = imEylem;
    }
}
