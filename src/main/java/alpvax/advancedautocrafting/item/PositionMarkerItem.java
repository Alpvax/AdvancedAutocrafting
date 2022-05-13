package alpvax.advancedautocrafting.item;

import alpvax.advancedautocrafting.Capabilities;
import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import alpvax.advancedautocrafting.client.render.BlockHighlightRender;
import alpvax.advancedautocrafting.util.IPositionReference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class PositionMarkerItem extends BlockItem {

    public PositionMarkerItem(Block block, Properties properties) {
        super(block, properties);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    /*
     * Only on Client
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        stack.getCapability(Capabilities.POSITION_MARKER_CAPABILITY).ifPresent(ref -> {
            tooltip.add(new TranslatableComponent(AATranslationKeys.ITEM_POS_LORE, ref.getPosition()).withStyle(ChatFormatting.GRAY));
            if (flagIn.isAdvanced() || !ref.matchesLevel(level) || Screen.hasShiftDown()) {
                tooltip.add(new TranslatableComponent(AATranslationKeys.ITEM_DIM_LORE, ref.getDimensionKey().location()).withStyle(ChatFormatting.GRAY));
            }
        });
        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return stack.getCapability(Capabilities.POSITION_MARKER_CAPABILITY).resolve()
            .flatMap(ref -> {
                if (ref.matchesLevel(level)) {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> BlockHighlightRender.manager.toggle(ref.getPosition(), 69, 120, 18, 160));
                    return Optional.of(InteractionResultHolder.consume(stack));
                }
                return Optional.empty();
            })
            .orElseGet(() -> InteractionResultHolder.pass(stack));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return DistExecutor.unsafeRunForDist(() -> () -> isRendering(stack), () -> () -> super.isFoil(stack));
    }

    /*
     * Only on Client
     */
    private boolean isRendering(ItemStack stack) {
        //TODO: check level / overhaul Highlight renderer
        return stack.getCapability(Capabilities.POSITION_MARKER_CAPABILITY)
            .map(p -> BlockHighlightRender.manager.contains(p.getPosition()))
            .orElse(false);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new IPositionReference.PositionMarkerItemStack(stack);
    }
}
