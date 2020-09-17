package alpvax.advancedautocrafting.client.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.math.vector.Vector3f;

public class WireCoreLoader extends WireComponentLoader<WireLoaderContext> {
  @Override
  protected WireLoaderContext createContext(JsonDeserializationContext context, JsonObject json, String name) {
    return new WireLoaderContext(name) {
      @Override
      protected Vector3f getFrom(float radius) {
        float f = 8F - radius;
        return new Vector3f(f, f, f);
      }

      @Override
      protected Vector3f getTo(float radius) {
        float f = 8F + radius;
        return new Vector3f(f, f, f);
      }
    };
  }
}
