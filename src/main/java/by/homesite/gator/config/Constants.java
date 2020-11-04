package by.homesite.gator.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";

    public static final int CONNECT_TIMEOUT = 120;

    public static final int PARSE_ITEMS_PERIOD = 60000;

    public static final int PURGE_ITEMS_PERIOD = 24*60*60000;
    public static final int TTL_ITEMS_DAYS = 7;

    private Constants() {
    }
}
