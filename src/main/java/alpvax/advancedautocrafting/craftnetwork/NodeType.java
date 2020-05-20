package alpvax.advancedautocrafting.craftnetwork;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.craftnetworknodecap.SimpleNetworkNode;
import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class NodeType {
  private static final Map<ResourceLocation, NodeType> types = new HashMap<>();
  public static NodeType get(String id) {
    return get(new ResourceLocation(id));
  }
  public static NodeType get(ResourceLocation id) {
    NodeType t = types.get(id);
    if (t == null) {
      throw new NullPointerException("No NodeType registered with ID: " + id.toString());
    }
    return t;
  }

  private final ResourceLocation id;
  public NodeType(@Nonnull ResourceLocation id) {
    Preconditions.checkNotNull(id);
    Preconditions.checkArgument(!types.containsKey(id), "NodeType IDs must be unique: " + id.toString());
    this.id = id;
    types.put(id, this);
  }

  @Nonnull
  public abstract INetworkNodeInstance create(UUID nodeID, UniversalPos pos);

  public static final NodeType SIMPLE_REPEATER = new NodeType(new ResourceLocation(AdvancedAutocrafting.MODID, "repeater")) {
    @Nonnull
    @Override
    public INetworkNodeInstance create(UUID nodeID, UniversalPos pos) {
      return new SimpleNetworkNode(pos.getPos());
    }
  };
}
