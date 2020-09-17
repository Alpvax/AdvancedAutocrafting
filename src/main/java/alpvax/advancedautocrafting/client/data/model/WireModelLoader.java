package alpvax.advancedautocrafting.client.data.model;

import alpvax.advancedautocrafting.block.ConnectionState;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.client.model.IModelLoader;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.stream.Collectors;

public class WireModelLoader implements IModelLoader<WireModelBuilder<ConnectionState>.Geometry> {
  private static WireCoreLoader CORE_LOADER = new WireCoreLoader();
  private static WirePartLoader PART_LOADER = new WirePartLoader();

  @Override
  public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

  }

  @Nonnull
  @Override
  public WireModelBuilder<ConnectionState>.Geometry read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents) {
    WireModelBuilder<ConnectionState> builder = new WireModelBuilder<>(ConnectionState.class)
                                                    .loadTextures(modelContents)
                                                    .withCore(CORE_LOADER.load(deserializationContext, JSONUtils.getJsonObject(modelContents, "core"), "core").getLeft());
    JSONUtils.getJsonObject(modelContents, "parts").entrySet().forEach(e -> {
      String name = e.getKey();
      Pair<BlockModel, WirePartLoader.PartContext> p = PART_LOADER.load(deserializationContext, JSONUtils.getJsonObject(e.getValue(), name), name);
      builder.withPart(name, p.getLeft(), p.getRight().getWhen().stream()
                                              .map(ConnectionState::get)
                                              .filter(Optional::isPresent)
                                              .map(Optional::get)
                                              .collect(Collectors.toSet())
      );
    });
    return builder.build();
  }
}
