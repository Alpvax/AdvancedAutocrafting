package alpvax.advancedautocrafting.client.data.lang;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AATranslationKeys {
  public static final Map<String, String> EN_US_MAPPINGS = new HashMap<>();

  public static final String ITEM_POS_LORE = register("Bound position: %s", "bound", "position");
  public static final String SOUL_NOT_ENOUGH = register("You do not have enough soul slivers left to do that", "soul", "notenough");
  public static final String SOUL_SLICER_ARRANGEMENT = register("You need to use the %s in your main hand, and your offhand needs to be empty", "soul", "slicer", "arrangement");
  public static final String SOUL_NOT_CONSUMABLE = register("You cannot consume this soul", "soul", "not", "consumable");
  public static final String PLAYER_SOUL = register("A Sliver of %s's Soul", "soul", "player");

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

  private static String register(String translation_US, String... keyParts) {
    String key = key(keyParts);
    EN_US_MAPPINGS.put(key, translation_US);
    return key;
  }
}
