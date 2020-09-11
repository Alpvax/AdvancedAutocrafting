package alpvax.advancedautocrafting.block;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.block.tile.WireTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WireBlockV2 extends Block {

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

  /*@Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    for (Direction d : Direction.values()) {
      BlockPos p = pos.offset(d);
      AABlocks.TileTypes.WIRE.get().func_226986_a_(worldIn, pos).updateNeighbourConnection(d, p, worldIn.getTileEntity(p));
    }
    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }*/

  @SuppressWarnings("deprecation")
  @Override
  public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    if (!worldIn.isRemote()) {
      BlockPos dPos = fromPos.subtract(pos);
      Direction d = Direction.byLong(dPos.getX(), dPos.getY(), dPos.getZ());
      WireTileEntity tile = AABlocks.TileTypes.WIRE.get().func_226986_a_(worldIn, pos);
      if (tile != null) {
        tile.updateConnection(d, worldIn.getTileEntity(fromPos));
      }
    }
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
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
          Vector3d start = new Vector3d(player.prevPosX, player.prevPosY + player.getEyeHeight(), player.prevPosZ);
          Vector3d end = start.add(player.getLook(0).scale(player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()));
          Direction dir = null;//TODO: rayTracePart(state, pos, start, end).direction();
          if (dir == null) {
            dir = rayTraceResult.getFace();
          }
          WireTileEntity tile = AABlocks.TileTypes.WIRE.get().func_226986_a_(worldIn, pos);
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
    WireTileEntity tile = AABlocks.TileTypes.WIRE.get().func_226986_a_(world, pos);
    if (tile != null) {
      for (Direction d : Direction.values()) {
        sb.append("\t").append(d.func_176610_l()).append(": ").append(tile.getConnection(d).func_176610_l()).append("\n");
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
    /*TODO: implement shape
    if(context instanceof EntitySelectionContext) {
      Entity e = context.getEntity();
      if (e != null) {
        if (context.hasItem(AAItems.MULTITOOL.get())
                || (e instanceof LivingEntity && ((LivingEntity)e).getActiveItemStack().getCapability(Capabilities.MULTITOOL_CAPABILITY).isPresent())
        ) {
          Vector3d start = new Vector3d(e.prevPosX, e.prevPosY + e.getEyeHeight(), e.prevPosZ);
          Vector3d end = start.add(e.getLook(0).scale(ForgeMod.REACH_DISTANCE.get().clampValue(Double.MAX_VALUE)));
          //TODO: return getPartialBlockHighlight(state, rayTracePart(state, pos, start, end));
        }
      }
    }
    Map<Direction, T> values = Maps.newEnumMap(Direction.class);
    directionToPropertyMap.forEach((d, prop) -> values.put(d, state.get(prop)));
    return shape.getCombinedShape(values);
     */
    double f = 5D/16D;
    double t = 11D/16D;
    return VoxelShapes.create(f,f,f,t,t,t);
  }
}
