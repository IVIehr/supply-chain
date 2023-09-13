package ir.alimojahed.general.elasticwrapper.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ali Mojahed on 11/12/2022
 * @project iso3
 **/
public class MapBuilder<T, U> {

    private Map<T, U> map = new HashMap<>();

    private MapBuilder() {

    }

    public static <T, U> MapBuilder<T, U> builder(Class<T> key, Class<U> value) {
        return new MapBuilder<>();
    }


    public MapBuilder<T, U> put(T key, U value) {
        map.put(key, value);

        return this;
    }

    public Map<T, U> build() {
        return map;
    }

}
