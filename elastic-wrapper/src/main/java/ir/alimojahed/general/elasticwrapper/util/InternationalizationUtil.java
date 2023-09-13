package ir.alimojahed.general.elasticwrapper.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * @author Ali Mojahed on 10/9/2022
 * @project iso3
 **/
public class InternationalizationUtil {
    public static boolean isNameMetadataValid(Map<String, String> nameMetadata) {
        Set<String> keys = nameMetadata.keySet();

        //TODO: parametrized for all languages and read it from properties
        if (!keys.contains("fa") || !keys.contains("en")) {
            return false;
        }

        //TODO: validate names based on language

        return true;
    }

    public static String getName(HttpServletRequest request, Map<String, String> nameMetadata) {
        String language = request.getHeader("Accept-Language");

        if (language.equals("fa-IR")) {
            language = "fa";
        }

        if (Util.isNullOrEmpty(language)) {
            language = "fa";
        }
        String name;

        if (nameMetadata.containsKey(language)) {
            name = nameMetadata.get(language);
        } else {
            name = nameMetadata.getOrDefault("fa", "نامشخص");
        }

        return name;
    }
}
