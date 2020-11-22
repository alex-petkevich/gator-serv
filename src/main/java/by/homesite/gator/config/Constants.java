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

    public static final int PURGE_ITEMS_PERIOD = 5*24*60*60000;
    public static final int TTL_ITEMS_DAYS = 7;

    public static final int RATES_FETCH_PERIOD = 24*60*60000;

    public static final int ITEM_TYPE_SELL = 0;
    public static final int ITEM_TYPE_BUY = 1;
    public static final int ITEM_TYPE_CHANGE = 2;
    public static final int ITEM_TYPE_SERVICE = 3;
    public static final int ITEM_TYPE_RENT = 4;
    public static final int ITEM_TYPE_IMPORTANT = 5;
    public static final int ITEM_TYPE_CLOSED = 6;

    private Constants() {
    }
}
