package lk.sheha.agriconnect.Helper;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtil {

    public static String hasPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public static boolean checkPassword(String enteredPassword, String storedHashedPassword){
        return BCrypt.checkpw(enteredPassword,storedHashedPassword);
    }
}
