package alpvax.advancedautocrafting.client.data.lang;

import alpvax.advancedautocrafting.api.AAReference;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AATranslationKeys {
    public static final Map<String, String> EN_US_MAPPINGS = new HashMap<>();

    public static final String CREATIVE_TAB = registerPlain("Advanced Autocrafting", "itemGroup." + AAReference.MODID);
    public static final String ITEM_POS_LORE = register("Bound position: %s", "bound", "position");
    public static final String ITEM_DIM_LORE = register("Bound dimension: %s", "bound", "dimension");

    private static String key(String... parts) {
        return AAReference.MODID + "." + String.join(".", parts);
    }

    @SuppressWarnings("ConstantConditions")
    private static Map<String, String> keys(String prefix, String suffix, String... variables) {
        Map<String, String> m = Maps.newHashMap();
        Arrays.stream(variables).forEach(var -> {
            String s = prefix + "." + var;
            if (suffix != null) {
                s += "." + suffix;
            }
            m.put(var, s);
        });
        return m;
    }

    private static String register(String translation_US, String... keyParts) {
        String key = key(keyParts);
        return registerPlain(translation_US, key);
    }
    private static String registerPlain(String translation_US, String key) {
        EN_US_MAPPINGS.put(key, translation_US);
        return key;
    }
}
