package lk.sheha.agriconnect.model;

public class CatItems {
    private String Name,des;
    private int cat_img;

    public String getName() {
        return Name;
    }

    public String getDes() {
        return des;
    }

    public int getCat_img() {
        return cat_img;
    }

    public CatItems(String name, String des, int cat_img) {
        Name = name;
        this.des = des;
        this.cat_img = cat_img;
    }
}
