package org.abx.webappgen.utils;

public class ElementUtils {


    public static final String defaultPackage = "webappgen";

    public static long elementHashCode(String element) {
        return element.hashCode();
    }

    public static long mapHashCode(String map, String key) {
        return elementHashCode(map + "." + key);
    }

}
