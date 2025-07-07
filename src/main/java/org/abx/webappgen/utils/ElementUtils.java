package org.abx.webappgen.utils;

public class ElementUtils {

    public static final String Anonymous = "Anonymous";

    public static final String User = "User";

    public static final String Admin = "Admin";

    public static final String defaultPackage = "webappgen";

    public static final long hideDefaultsId;
    public static long elementHashCode(String element) {
        return element.hashCode();
    }

    public static long mapHashCode(String map, String key) {
        return elementHashCode(map + "." + key);
    }

    static {
        hideDefaultsId = mapHashCode("app.Env", "hideDefaults");
    }
}
