package alpvax.advancedautocrafting.craftnetwork.nodefunctionality;

import alpvax.advancedautocrafting.craftnetwork.INetworkNode;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public abstract class NodeFunctionality<I, O> {
  public abstract boolean isValid(INetworkNode node);
  public abstract O act(INetworkNode node, I input);

  public static final NodeFunctionality<ItemStack, Void> DROP = new NodeFunctionality<>() {
    @Override
    public boolean isValid(INetworkNode node) {
      return node.getBlockState().isAir(node.getWorld(), node.getPos());
    }

    @Override
    public Void act(INetworkNode node, ItemStack input) {
      World world = node.getWorld();
      ItemEntity itementity = new ItemEntity(world, node.getX() + 0.5, node.getY() + 0.5, node.getZ() + 0.5, input);
      itementity.setDefaultPickupDelay();
      world.addEntity(itementity);
      return null;
    }
  };

  public static final NodeFunctionality<ItemStack, ItemStack> INSERT = new NodeFunctionality<>() {
    @Override
    public boolean isValid(INetworkNode node) {
      return node.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent();
    }

    @Override
    public ItemStack act(INetworkNode node, ItemStack input) {
      IItemHandler handler = node.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(EmptyHandler.INSTANCE);
      return handler.insertItem(0, input, false); //TODO: which slot?
    }
  };
}
