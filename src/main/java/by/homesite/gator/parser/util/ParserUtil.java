package by.homesite.gator.parser.util;

public class ParserUtil
{
    private ParserUtil() {

    }

    public static String getAbsLink(String link, String parentLink) {

        if (link.startsWith("http"))
            return link;

        String result = link;
        if (link.startsWith("/")) {
            result = parentLink.substring(0, parentLink.indexOf("/")) + link;
        }
        if (link.startsWith("./")) {
            result = parentLink.substring(0, parentLink.lastIndexOf("/")) + link.substring(1);
        }

        return result;
    }
}
