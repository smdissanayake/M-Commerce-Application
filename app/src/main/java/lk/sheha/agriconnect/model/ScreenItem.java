package lk.sheha.agriconnect.model;

public class ScreenItem {

    private String Title;
     int imageNumber;
     private  String Des;

    public ScreenItem(String title, int imageNumber, String des) {
        this.Title = title;
        this.imageNumber = imageNumber;
        this.Des = des;
    }

    public String getDes() {

        return Des;
    }

    public void setDes(String des) {
        Des = des;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getImageNumber() {
        return imageNumber;
    }

    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }


}
