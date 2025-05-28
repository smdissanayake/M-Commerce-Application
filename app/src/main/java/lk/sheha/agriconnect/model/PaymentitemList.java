package lk.sheha.agriconnect.model;

import java.io.Serializable;

public class PaymentitemList implements Serializable {

    private String name,title,qut,price,imageuri,isdriver;

    public PaymentitemList(String name, String title, String qut, String price, String imageuri,String isdriver) {
        this.name = name;
        this.title = title;
        this.qut = qut;
        this.price = price;
        this.imageuri = imageuri;
        this.isdriver =isdriver;

    }

    public String getIsdriver() {
        return isdriver;
    }

    public void setIsdriver(String isdriver) {
        this.isdriver = isdriver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQut() {
        return qut;
    }

    public void setQut(String qut) {
        this.qut = qut;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }
}
