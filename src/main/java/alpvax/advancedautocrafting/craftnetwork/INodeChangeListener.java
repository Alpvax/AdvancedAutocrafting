package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.util.Direction;

public interface INodeChangeListener {
  void onAdded(NetworkNode node);
  void onRemoved(NetworkNode node);
  void onConnectionChanged(NetworkNode node, Direction d);
}
