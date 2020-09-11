package alpvax.advancedautocrafting.block.tile;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.block.ConnectionState;
import alpvax.advancedautocrafting.client.data.model.WireBakedModel;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class WireTileEntity extends TileEntity {
  private final Map<Direction, ConnectionState> connections = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
    for (Direction d : Direction.values()) {
      map.put(d, ConnectionState.NONE);
    }
  });

  //TODO: private ProxyBlockSource controller = null;

  public WireTileEntity() {
    super(AABlocks.TileTypes.WIRE.get());
  }

  @SuppressWarnings("UnusedReturnValue")
  public ConnectionState toggleDisabled(Direction d) {
    return setConnectionDisabled(d, getConnection(d).isNotDisabled());
  }

  public ConnectionState getConnection(Direction d) {
    return connections.get(d);
  }

  private void setConnection(Direction d, ConnectionState state) {
    connections.put(d, state);
    markDirty();
    BlockState blockState = getBlockState();
    //requestModelDataUpdate();//XXX?
    //noinspection ConstantConditions
    getWorld().notifyBlockUpdate(getPos(), blockState, blockState, Constants.BlockFlags.DEFAULT_AND_RERENDER);
  }

  public ConnectionState setConnectionDisabled(Direction d, boolean disabled) {
    if (world == null) {
      throw new NullPointerException("Tried to call 'setConnectionDisabled' before world is set");
    }
    TileEntity neighbour = world.getTileEntity(getPos().offset(d));
    ConnectionState newState = disabled
                                   ? ConnectionState.DISABLED
                                   : getConnectivityForNeighbour(d, neighbour);
    if (getConnection(d) != newState) {
      setConnection(d, newState);
      updateNeighbourConnection(d, neighbour, newState);
    }
    return getConnection(d);
  }

  @SuppressWarnings("UnusedReturnValue")
  public ConnectionState updateConnection(Direction d, @Nullable TileEntity neighbour) {
    ConnectionState state = getConnection(d);
    if (state.isNotDisabled()) {
      ConnectionState newState = getConnectivityForNeighbour(d, neighbour);
      if (newState != state) {
        setConnection(d, newState);
      }
    }
    updateNeighbourConnection(d, neighbour, state);
    return state;
  }

  /**
   * Get the desired ConnectionState for the adjacent TileEntity to a WireBlock.
   * Does not take into consideration the disabled state of the WireBlock.
   * @param d the direction from the wire to the neighbour.
   * @param neighbour the TileEntity to check connectivity with.
   * @return the desired ConnectionState for the neighbour:
   * <br>NONE if there is no valid connection (either no valid TE, or connection from this direction is disabled).
   * <br>CONNECTION if the TE is a wire and is not disabled.
   * <br>INTERFACE if the TE supports the INetworkNode capability from this direction.
   * <br>This method will never return DISABLED (as that is a property of the WireBlock).
   */
  public static ConnectionState getConnectivityForNeighbour(Direction d, @Nullable TileEntity neighbour) {
    if (neighbour != null) {
      Direction opp = d.getOpposite();
      if (neighbour.getType() == AABlocks.TileTypes.WIRE.get()) {
        WireTileEntity t = (WireTileEntity) neighbour;
        if (t.getConnection(opp).isNotDisabled()) {
          return ConnectionState.CONNECTION;
        }
      } else if (neighbour.getCapability(Capabilities.NODE_CAPABILITY, opp).isPresent()) {
        return ConnectionState.INTERFACE;
      }
    }
    return ConnectionState.NONE;
  }

  private void updateNeighbourConnection(Direction d, @Nullable TileEntity neighbour, ConnectionState thisState) {
    if (neighbour != null) {
      Direction opp = d.getOpposite();
      if (neighbour.getType() == AABlocks.TileTypes.WIRE.get()) {
        ((WireTileEntity) neighbour).setConnection(opp, thisState.isNotDisabled() ? ConnectionState.CONNECTION : ConnectionState.NONE);
      } else {
        neighbour.getCapability(Capabilities.NODE_CAPABILITY, d.getOpposite()).ifPresent(node -> {
          //node.connectToWire(opp)//TODO: implement
        });
      }
    }
  }

  @Nonnull
  @Override
  public IModelData getModelData() {
    ModelDataMap.Builder b = new ModelDataMap.Builder();
    connections.forEach((d, s) -> b.withInitial(WireBakedModel.DIRECTION_DATA.get(d), s.func_176610_l()));
    return b.build();
  }

  @Nonnull
  @Override
  public CompoundNBT getUpdateTag() {
    CompoundNBT nbt = super.getUpdateTag();
    CompoundNBT tag = new CompoundNBT();
    connections.forEach((d, s) -> tag.putString(d.func_176610_l(), s.func_176610_l()));
    nbt.put("connections", tag);
    return nbt;
  }

  @Override
  public void handleUpdateTag(BlockState state, CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    requestModelDataUpdate();
  }

  @Override
  public void func_230337_a_(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
    super.func_230337_a_(state, nbt);
    CompoundNBT tag = nbt.getCompound("connections");
    connections.entrySet().forEach(e -> {
      String dir = e.getKey().func_176610_l();
      if (tag.contains(dir, Constants.NBT.TAG_STRING)) {
        e.setValue(ConnectionState.get(tag.getString(dir)));
      }
    });
  }

  @Nonnull
  @Override
  public CompoundNBT write(@Nonnull CompoundNBT compound) {
    super.write(compound);

    CompoundNBT tag = new CompoundNBT();
    connections.forEach((d, s) -> tag.putString(d.func_176610_l(), s.func_176610_l()));
    compound.put("connections", tag);

    return compound;
  }

  @Nullable
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    handleUpdateTag(getBlockState(), pkt.getNbtCompound());
  }
}
