package alpvax.advancedautocrafting.client.data.model;

import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public abstract class WireLoaderContext {
  private static final FaceData EMPTY = new FaceData();

  static class FaceData {
    private Direction cullFace = null;
    private int tintIndex = -1;
    private String texture = null;
    private BlockFaceUV uv = null;
  }

  private final String name;
  private float radius = -1;
  private int tintIndex = -1;
  private String texture = null;
  private BlockFaceUV uv = null;
  private final Map<Direction, FaceData> faceData = new EnumMap<>(Direction.class);
  private final Map<Direction, BlockPartFace> faces = new EnumMap<>(Direction.class);

  public WireLoaderContext(String name) {
    this.name = name;
  }

  public void checkValid() {
    if (radius < 0F) {
      throw new JsonParseException("Missing radius, expected to find a Float");
    }
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public void setDefaultTintIndex(int tintIndex) {
    this.tintIndex = tintIndex;
  }

  public void setDefaultTexture(String texture) {
    this.texture = texture;
  }

  public void setDefaultUV(BlockFaceUV uv) {
    this.uv = uv;
  }

  public String getName() {
    return name;
  }

  public Vector3f getFrom() {
    return getFrom(radius);
  }

  protected abstract Vector3f getFrom(float radius);

  public Vector3f getTo() {
    return getTo(radius);
  }

  protected abstract Vector3f getTo(float radius);

  public boolean faceExists(Direction direction) {
    return getFaceData(direction).isPresent();
  }

  @Nullable
  protected Direction getCullFace(Direction direction) {
    return getFaceData(direction).map(f -> f.cullFace).orElse(null);
  }

  protected int getTintIndex(Direction direction) {
    return getFaceData(direction).map(f -> f.tintIndex).orElse(tintIndex);
  }

  protected String getTexture(Direction direction) {
    return getFaceData(direction).map(f -> f.texture).orElse(texture != null ? texture : ("#" + getName()));
  }

  protected BlockFaceUV getUV(Direction direction) {
    return getFaceData(direction).map(f -> f.uv).orElse(uv != null ? uv : new BlockFaceUV(null, 0));
  }

  protected Optional<FaceData> getFaceData(Direction direction) {
    if (direction == null) {
      return Optional.empty();
    }
    FaceData data = faceData.computeIfAbsent(direction, d -> new FaceData());
    return Optional.ofNullable(data == EMPTY ? null : data);
  }

  public void setFaceData(Direction direction, @Nullable FaceData data) {
    if (direction != null) {
      faceData.put(direction, data == null ? EMPTY : data);
    }
  }

  public void setFace(Direction direction, BlockPartFace face) {
    faces.put(direction, face);
    faceData.put(direction, EMPTY);
  }

  public void setCullFace(Direction direction, Direction cullface) {
    getFaceData(direction).ifPresent(f -> f.cullFace = cullface);
  }

  public void setTintIndex(Direction direction, int tintIndex) {
    getFaceData(direction).ifPresent(f -> f.tintIndex = tintIndex);
  }

  public void setTexture(Direction direction, String faceTexture) {
    getFaceData(direction).ifPresent(f -> f.texture = faceTexture);
  }

  public void setUV(Direction direction, BlockFaceUV uv) {
    getFaceData(direction).ifPresent(f -> f.uv = uv);
  }

  public Optional<BlockPartFace> getFace(Direction direction) {
    return Optional.ofNullable(faces.computeIfAbsent(direction,
        d -> faceExists(d)
                 ? new BlockPartFace(getCullFace(d), getTintIndex(d), getTexture(d), getUV(d))
                 : null
    ));
  }
}
