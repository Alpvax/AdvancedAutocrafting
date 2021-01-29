package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.tile.WireTileEntity;
import alpvax.advancedautocrafting.block.wire.WireConnectionsOptional;
import alpvax.advancedautocrafting.block.wire.WireShape;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class WireBlockV2 extends Block {
  private final WireShape<ConnectionState> shape = new WireShape.Builder<ConnectionState>(true)
                                                 .withCore(3F)
                                                 .withPart(ConnectionState.DISABLED, 2.5F, 4F, 5F)
                                                 .withPart(ConnectionState.INTERFACE, 6F, 0F, 1F)
                                                 .withPartDef("arm", 2F, 0F, 5F)
                                                 .includePart(ConnectionState.CONNECTION, "arm")
                                                 .includePart(ConnectionState.INTERFACE, "arm")
                                                 .build();

  public WireBlockV2(Properties properties) {
    super(properties);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new WireTileEntity();
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onBlockAdded(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
    super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
    worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 1);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void tick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
    super.tick(state, worldIn, pos, rand);
    WireTileEntity tile = AABlocks.TileTypes.WIRE.get().getIfExists(worldIn, pos);
    if (tile != null) {
      for (Direction d : Direction.values()) {
        tile.updateConnection(d, worldIn.getTileEntity(pos.offset(d)));
      }
    }
  }

  /*TODO:WATERLOGGED
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    IWorld world = context.getWorld();
    BlockPos pos = context.getPos();
    WireTileEntity tile = AABlocks.TileTypes.WIRE.get().func_226986_a_(world, pos);
    boolean flag = world.getFluidState(pos).getFluid() == Fluids.WATER;
    return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(flag))
  }
  */

  /*@SuppressWarnings("deprecation")
  @Override
  public void onReplaced(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
    for (Direction d : Direction.values()) {
      BlockPos p = pos.offset(d);
      AABlocks.TileTypes.WIRE.get().func_226986_a_(worldIn, pos).updateNeighbourConnection(d, p, worldIn.getTileEntity(p));
    }
    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }*/

  @Override
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
  }

  @Override
  public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {

  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public BlockState updatePostPlacement(@Nonnull BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld worldIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
    //TODO:WATERLOGGED?
    if (!worldIn.isRemote()) {
      WireTileEntity tile = AABlocks.TileTypes.WIRE.get().getIfExists(worldIn, currentPos);
      if (tile != null) {
        tile.updateConnection(facing, worldIn.getTileEntity(facingPos));
      }
    }
    return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rayTraceResult) {
    ItemStack stack = player.getHeldItem(hand);
    if(!stack.isEmpty() && stack.getCapability(Capabilities.MULTITOOL_CAPABILITY).isPresent()) {
      // Multitool
      if (!worldIn.isRemote) {
        if (player.isSneaking()) {
          worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
          if (!player.isCreative()) {
            spawnAsEntity(worldIn, pos, new ItemStack(this));
          }
        } else {
          Direction dir = rayTracePart(worldIn, pos, () -> player).getDirection().orElse(rayTraceResult.getFace());
          WireTileEntity tile = AABlocks.TileTypes.WIRE.get().getIfExists(worldIn, pos);
          if (tile != null) {
            tile.toggleDisabled(dir);
          }
        }
      }
      return ActionResultType.SUCCESS;
    } else if (stack.isEmpty()) {
      player.sendStatusMessage(new StringTextComponent(getDebugConnections(worldIn, pos)), false);//XXX
      return ActionResultType.SUCCESS;
    }
    return super.onBlockActivated(state, worldIn, pos, player, hand, rayTraceResult);
  }

  private String getDebugConnections(World world, BlockPos pos) {//XXX
    String side = "[" + (world.isRemote ? "CLIENT" : "SERVER") + "]";
    StringBuilder sb = new StringBuilder(side);
    WireTileEntity tile = AABlocks.TileTypes.WIRE.get().getIfExists(world, pos);
    if (tile != null) {
      for (Direction d : Direction.values()) {
        sb.append("\t").append(d.getString()).append(": ").append(tile.getConnection(d).getString()).append("\n");
      }
    }
    return sb.deleteCharAt(sb.length() - 1).toString();
  }

  /*TODO: Waterlogging
  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
  }*/

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
    return rayTracePart(worldIn, pos, () -> {
      Entity e = context.getEntity();
      if (context.hasItem(AAItems.MULTITOOL.get()) || (e instanceof LivingEntity && ((LivingEntity) e).getActiveItemStack().getCapability(Capabilities.MULTITOOL_CAPABILITY).isPresent())
      ) {
        return e;
      }
      return null;
    }).getShapeOrMap(shape::getComplete).orElseGet(shape::getCore);
  }

  protected WireConnectionsOptional rayTracePart(@Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, Supplier<Entity> e) {
    return new WireConnectionsOptional(worldIn, pos).partialRayTrace(shape, e);
  }

}
