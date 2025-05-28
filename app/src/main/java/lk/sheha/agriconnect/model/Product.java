package lk.sheha.agriconnect.model;

import java.io.Serializable;

public class Product implements Serializable {

//    private String id,title, price, description, imageUrl;
    private String id,category,province,pickupLocationlat,pickupLocationlang,pickupDate,paymentMethod,vehicleType,capacity,seller, title, price, description, imageUrl,quantity;
    private Boolean verified;

    public Product(String id, String category, String province, String pickupLocationlat, String pickupLocationlang, String pickupDate, String paymentMethod, String vehicleType, String capacity, String seller, String title, String price, String description, String imageUrl, String quantity, Boolean verified) {
        this.id = id;
        this.category = category;
        this.province = province;
        this.pickupLocationlat = pickupLocationlat;
        this.pickupLocationlang = pickupLocationlang;
        this.pickupDate = pickupDate;
        this.paymentMethod = paymentMethod;
        this.vehicleType = vehicleType;
        this.capacity = capacity;
        this.seller = seller;
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.verified = verified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPickupLocationlat() {
        return pickupLocationlat;
    }

    public void setPickupLocationlat(String pickupLocationlat) {
        this.pickupLocationlat = pickupLocationlat;
    }

    public String getPickupLocationlang() {
        return pickupLocationlang;
    }

    public void setPickupLocationlang(String pickupLocationlang) {
        this.pickupLocationlang = pickupLocationlang;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
//private String name;
//private int imageResId;  // Store drawable resource ID
//
//public Product(String name, int imageResId) {
//    this.name = name;
//    this.imageResId = imageResId;
//}
//
//public int getImageResId() { return imageResId; }
//public String getName() { return name; }


//public Product(String id, String title, String price, String description, String imageUrl) {
//    this.id = id;
//    this.title = title;
//    this.price = price;
//    this.description = description;
//    this.imageUrl = imageUrl;
//}
//
//public String getId() {
//    return id;
//}
//
//public void setId(String id) {
//    this.id = id;
//}
//
//public String getTitle() {
//    return title;
//}
//
//public void setTitle(String title) {
//    this.title = title;
//}
//
//public String getPrice() {
//    return price;
//}
//
//public void setPrice(String price) {
//    this.price = price;
//}
//
//public String getDescription() {
//    return description;
//}
//
//public void setDescription(String description) {
//    this.description = description;
//}
//
//public String getImageUrl() {
//    return imageUrl;
//}
//
//public void setImageUrl(String imageUrl) {
//    this.imageUrl = imageUrl;
//}