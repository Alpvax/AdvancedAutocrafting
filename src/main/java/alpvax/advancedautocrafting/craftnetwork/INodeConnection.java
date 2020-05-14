package alpvax.advancedautocrafting.craftnetwork;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INodeConnection {
  /**
   * Get the axis between adjacent positions.
   * Return null for non-adjacent positions.
   */
  @Nullable Direction.Axis getAxis();

  /**
   * @return the "lower" {@link UniversalPos}
   */
  @Nonnull UniversalPos getA();
  /**
   * @return the "higher" {@link UniversalPos}
   */
  @Nonnull UniversalPos getB();


}
