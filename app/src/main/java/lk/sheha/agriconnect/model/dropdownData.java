package lk.sheha.agriconnect.model;

public class dropdownData {

    private int resource;
    private String dropdowntitle;

    public dropdownData(int resource, String dropdowntitle) {
        this.resource = resource;
        this.dropdowntitle = dropdowntitle;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getDropdowntitle() {
        return dropdowntitle;
    }

    public void setDropdowntitle(String dropdowntitle) {
        this.dropdowntitle = dropdowntitle;
    }
}
