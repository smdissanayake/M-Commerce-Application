package lk.sheha.agriconnect.Helper;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save Email & Password
    public void saveUser(String email, String password) {
        String encryptedEmail = AESHelper.encrypt(email);
        String encryptedPassword = AESHelper.encrypt(password);

        editor.putString(KEY_EMAIL, encryptedEmail);
        editor.putString(KEY_PASSWORD, encryptedPassword);
        editor.apply();
    }

    // Get Email
    public String getEmail() {
        String encryptedEmail = sharedPreferences.getString(KEY_EMAIL, null);
        return encryptedEmail != null ? AESHelper.decrypt(encryptedEmail) : null;
    }

    // Get Password
    public String getPassword() {
        String encryptedPassword = sharedPreferences.getString(KEY_PASSWORD, null);
        return encryptedPassword != null ? AESHelper.decrypt(encryptedPassword) : null;
    }

    // Clear Data
    public void clearUser() {
        editor.clear();
        editor.apply();
    }
}
