package utils;

public final class AuthContext {

    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    private AuthContext() {}

    public static void setToken(String token) {
        TOKEN.set(token);
        USER_ID.set(JwtUtils.extractSub(token));
    }

    public static String getToken() { return TOKEN.get(); }
    public static String getUserId() { return USER_ID.get(); }
    public static String getBearerToken() { return "Bearer " + getToken(); }

    public static void clear() {
        TOKEN.remove();
        USER_ID.remove();
    }
}
