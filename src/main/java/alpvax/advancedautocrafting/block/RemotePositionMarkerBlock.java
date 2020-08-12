package alpvax.advancedautocrafting.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class RemotePositionMarkerBlock extends Block {
  public RemotePositionMarkerBlock(Properties properties) {
    super(properties);
  }

  @SuppressWarnings("deprecation")
  public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
    TileEntity tileentity = builder.get(LootParameters.BLOCK_ENTITY);
    if (tileentity instanceof ShulkerBoxTileEntity) {
      ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
      builder = builder.withDynamicDrop(new ResourceLocation("position"), (p_220168_1_, p_220168_2_) -> {
        for(int i = 0; i < shulkerboxtileentity.getSizeInventory(); ++i) {
          p_220168_2_.accept(shulkerboxtileentity.getStackInSlot(i));
        }

      });
    }

    return super.getDrops(state, builder);
  }
}
