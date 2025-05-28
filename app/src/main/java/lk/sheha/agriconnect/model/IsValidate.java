package lk.sheha.agriconnect.model;

public class IsValidate {

    public static boolean isValidSriLankanMobile(String mobile) {
        String regex = "^07[01245678]\\d{7}$";
        return mobile.matches(regex);
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }
}
