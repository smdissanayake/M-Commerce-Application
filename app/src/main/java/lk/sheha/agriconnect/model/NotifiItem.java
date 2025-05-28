package lk.sheha.agriconnect.model;

import java.io.Serializable;

public class NotifiItem implements Serializable {

    private String Hours,Product_Id,Rent_Date,description,droplang,droplat,from,mob,status,to;

    public NotifiItem(String hours, String product_Id, String rent_Date, String description, String droplang, String droplat, String from, String mob, String status, String to) {
        Hours = hours;
        Product_Id = product_Id;
        Rent_Date = rent_Date;
        this.description = description;
        this.droplang = droplang;
        this.droplat = droplat;
        this.from = from;
        this.mob = mob;
        this.status = status;
        this.to = to;
    }

    public String getHours() {
        return Hours;
    }

    public void setHours(String hours) {
        Hours = hours;
    }

    public String getProduct_Id() {
        return Product_Id;
    }

    public void setProduct_Id(String product_Id) {
        Product_Id = product_Id;
    }

    public String getRent_Date() {
        return Rent_Date;
    }

    public void setRent_Date(String rent_Date) {
        Rent_Date = rent_Date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDroplang() {
        return droplang;
    }

    public void setDroplang(String droplang) {
        this.droplang = droplang;
    }

    public String getDroplat() {
        return droplat;
    }

    public void setDroplat(String droplat) {
        this.droplat = droplat;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
