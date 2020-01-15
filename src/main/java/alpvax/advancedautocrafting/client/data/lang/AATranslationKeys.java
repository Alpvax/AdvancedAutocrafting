package alpvax.advancedautocrafting.client.data.lang;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public class AATranslationKeys {
  public static final String ITEM_POS_LORE = key("bound", "position");

  private static String key(String... parts) {
    return AdvancedAutocrafting.MODID + ":" + String.join(".", parts);
  }
  private static Map<String, String> keys(String prefix, String suffix, String... variables) {
    Map<String, String> m = Maps.newHashMap();
    Arrays.stream(variables).forEach((var) -> {
      String s = prefix + "." + var;
      if(suffix != null) {
        s += "." + suffix;
      }
      m.put(var, s);
    });
    return m;
  }
}
