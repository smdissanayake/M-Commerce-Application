package lk.sheha.agriconnect.model;

public class CartItem {

    private String Title,Qty,Distric,price;
    private int image_src;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getDistric() {
        return Distric;
    }

    public void setDistric(String distric) {
        Distric = distric;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImage_src() {
        return image_src;
    }

    public void setImage_src(int image_src) {
        this.image_src = image_src;
    }

    public CartItem(String title, String qty, String distric, String price, int image_src) {
        Title = title;
        Qty = qty;
        Distric = distric;
        this.price = price;
        this.image_src = image_src;
    }
}
