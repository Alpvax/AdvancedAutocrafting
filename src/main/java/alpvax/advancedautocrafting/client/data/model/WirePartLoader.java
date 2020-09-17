package alpvax.advancedautocrafting.client.data.model;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Set;
import java.util.stream.Collectors;

public class WirePartLoader extends WireComponentLoader<WirePartLoader.PartContext> {
  public static JsonObject convertToModelJson(JsonDeserializationContext context, JsonObject json, float coreMin) {
    if (json.has("model")) {
      return json.get("model").getAsJsonObject();
    }
    float radius = JSONUtils.getFloat(json, "radius", -1F);
    float start = JSONUtils.getFloat(json, "start", 0F);
    float end = JSONUtils.getFloat(json, "end", coreMin);


    return json;
  }

  @Override
  protected PartContext createContext(JsonDeserializationContext context, JsonObject json, String name) {
    JsonArray when = JSONUtils.getJsonArray(json, "when");
    //noinspection UnstableApiUsage
    return new PartContext(
        name,
        JSONUtils.getFloat(json, "start", 0F),
        JSONUtils.getFloat(json, "end", 8F),
        Streams.stream(when).map(JsonElement::getAsString).collect(Collectors.toSet())
    );
  }

  static class PartContext extends WireLoaderContext {
    private float start;
    private float end;
    private Set<String> when;
    public PartContext(String name, float start, float end, Set<String> when) {
      super(name);
      this.start = start;
      this.end = end;
      this.when = when;
    }

    @Override
    protected Vector3f getFrom(float radius) {
      float f = 8F - radius;
      return new Vector3f(f, f, start);
    }

    @Override
    protected Vector3f getTo(float radius) {
      float f = 8F + radius;
      return new Vector3f(f, f, end);
    }

    public Set<String> getWhen() {
      return when;
    }
  }
}
