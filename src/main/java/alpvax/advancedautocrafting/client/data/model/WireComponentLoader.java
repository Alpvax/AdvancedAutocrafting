package alpvax.advancedautocrafting.client.data.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class WireComponentLoader<T extends WireLoaderContext> {

  /*void makeFacesJson(JsonDeserializationContext context, JsonObject json, JsonObject part) {
    if (json.has("faces")) {
      part.add("faces", json.get("faces"));
      json.remove("faces");
    } else {
      JsonObject faces = new JsonObject();
      String texture = JSONUtils.getString(json, "texture", "#core");
      JsonArray uv = JSONUtils.getJsonArray(json, "uv", null);
      for (Direction d : Direction.values()) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", texture);
        if (uv != null) {
          face.add("uv", uv);
        }
        faces.add(d.func_176610_l(), face);
      }
      part.add("faces", faces);
    }
  }*/

  protected final Map<Direction, BlockPartFace> getFaces(T loaderContext) {
    EnumMap<Direction, BlockPartFace> map = new EnumMap<>(Direction.class);
    for (Direction d : Direction.values()) {
      loaderContext.getFace(d).ifPresent(f -> map.put(d, f));
    }
    if (map.isEmpty()) {
      throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
    }
    return map;
  }

  protected void loadFaceData(JsonDeserializationContext context, JsonObject json, T loaderContext) {
    if (json.has("faces")) {
      JSONUtils.getJsonObject(json, "faces").entrySet().forEach(e -> {
        Direction direction = Direction.byName(e.getKey());
        if (direction == null) {
          throw new JsonParseException("Unknown facing: " + e.getKey());
        } else {
          loaderContext.setFace(direction, context.deserialize(e.getValue(), BlockPartFace.class));
        }
      });
    } else {
      if (json.has("facedefaults")) {
        JsonObject faceDefaults = json.getAsJsonObject("facedefaults");
        if (faceDefaults.has("tintindex")) {
          loaderContext.setDefaultTintIndex(JSONUtils.getInt(faceDefaults, "tintindex"));
        }
        if (faceDefaults.has("texture")) {
          loaderContext.setDefaultTexture(JSONUtils.getString(faceDefaults, "texture"));
        }
        if (faceDefaults.has("uv")) {
          loaderContext.setDefaultUV(loadUVArray(context, JSONUtils.getJsonArray(faceDefaults, "uv"), "facedefaults.uv", loaderContext));
        }
      }
      if (json.has("facedata")) {
        JsonObject faceData = json.getAsJsonObject("facedata");
        JsonArray skip = JSONUtils.getJsonArray(faceData, "skip", new JsonArray());
        //noinspection ConstantConditions
        for (JsonElement element : skip) {
          loaderContext.setFaceData(Direction.byName(element.getAsString()), null);
        }

        JsonArray cull = JSONUtils.getJsonArray(faceData, "cull", new JsonArray());
        //noinspection ConstantConditions
        for (JsonElement element : cull) {
          Direction d = Direction.byName(element.getAsString());
          loaderContext.setCullFace(d, d);
        }

        loopJsonDirections(faceData, "tintindex", JsonElement::getAsInt, loaderContext::setTintIndex);
        loopJsonDirections(faceData, "texture", JsonElement::getAsString, loaderContext::setTexture);
        loopJsonDirections(faceData, "uv", JsonElement::getAsJsonArray,
            (d, jarr) -> loaderContext.setUV(d, loadUVArray(context, jarr, d.func_176610_l(), loaderContext))
        );
      }
    }
  }

  protected final BlockFaceUV loadUVArray(JsonDeserializationContext context, JsonArray jarr, String jarrName, T loaderContext) {
    int l = jarr.size();
    float[] uv = null;
    int rotation = 0;
    if (l % 2 == 1) { // if odd
      l--;
      rotation = JSONUtils.getInt(jarr.get(l), jarrName + "[" + l + "]");
    }
    if (rotation < 0 || rotation > 270 || rotation % 90 != 0) {
      throw new JsonParseException("Invalid rotation " + rotation + " found, only 0/90/180/270 allowed");
    }
    if (l > 0) {
      if (l != 4) {
        throw new JsonParseException("Expected 4 uv values, found: " + l);
      }
      uv = new float[4];
      for(int i = 0; i < 4; ++i) {
        uv[i] = JSONUtils.getFloat(jarr.get(i), "uv[" + i + "]");
      }
    }
    return new BlockFaceUV(uv, rotation);
  }

  private <U> void loopJsonDirections(JsonObject json, String memberName, Function<JsonElement, U> mapper, BiConsumer<Direction, U> callback) {
    JSONUtils.getJsonObject(json, memberName, new JsonObject()).entrySet().forEach(e -> {
      Direction d = Direction.byName(e.getKey());
      if (d == null) {
        throw new JsonParseException("Unknown face: " + e.getKey());
      }
      callback.accept(d, mapper.apply(e.getValue()));
    });
  }

  public T initialiseContext(JsonDeserializationContext context, JsonObject json, String name) {
    T loaderContext = createContext(context, json, name);
    if (json.has("radius")) {
      float radius = JSONUtils.getFloat(json, "radius");
      if (radius < 0F || radius > 8F) {
        throw new JsonParseException("radius must be between 0.0 and 8.0. Got: " + radius);
      }
      loaderContext.setRadius(radius);
    }
    return loaderContext;
  }

  protected abstract T createContext(JsonDeserializationContext context, JsonObject json, String name);

  BlockPart makePart(JsonDeserializationContext context, JsonObject json, T loaderContext) {
    return new BlockPart(
        loaderContext.getFrom(),
        loaderContext.getTo(),
        getFaces(loaderContext),
        null, //No rotation
        JSONUtils.getBoolean(json, "shade", true)
    );
  }

  @SuppressWarnings("UnstableApiUsage")
  BlockModel makeSubModel(JsonDeserializationContext context, JsonObject json, T loaderContext) {
    // noinspection ConstantConditions
    return new BlockModel(
        null,
        Lists.newArrayList(makePart(context, json, loaderContext)),
        WireModelBuilder.parseTextures(json),
        JSONUtils.getBoolean(json, "ambientocclusion", true),
        json.has("gui_light")
            ? BlockModel.GuiLight.func_230179_a_(JSONUtils.getString(json, "gui_light"))
            : null,
        json.has("display")
            ? JSONUtils.deserializeClass(json, "display", context, ItemCameraTransforms.class)
            : ItemCameraTransforms.DEFAULT,
        Streams.stream(JSONUtils.getJsonArray(json, "overrides", new JsonArray()))
            .map(e -> context.<ItemOverride>deserialize(e, ItemOverride.class))
            .collect(Collectors.toList())
    );
  }

  public Pair<BlockModel, T> load(JsonDeserializationContext context, JsonObject json, String name) {
    T loaderContext = initialiseContext(context, json, name);
    if (json.has("model")) {
      return Pair.of(JSONUtils.deserializeClass(json, "model", context, BlockModel.class), loaderContext);
    }
    loaderContext.checkValid();
    loadFaceData(context, json, loaderContext);
    return Pair.of(makeSubModel(context, json, loaderContext), loaderContext);
  }
}
