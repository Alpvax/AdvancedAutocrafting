package alpvax.advancedautocrafting.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alpvax.advancedautocrafting.core.AdvancedAutocrafting;
import alpvax.advancedautocrafting.tile.TileEntityCraftingManager;
import alpvax.advancedautocrafting.util.DimensionPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCraftingLinker extends Item
{
	public static Style ERROR_STYLE = new Style().setColor(TextFormatting.RED);

	public static void setSelectedManager(@Nonnull ItemStack stack, @Nullable TileEntityCraftingManager manager)
	{
		if(manager != null)
		{
			stack.setTagInfo("SelectedCraftingManager", new DimensionPos(manager.getWorld(), manager.getPos()).serializeNBT());
		}
		else if(isBound(stack))
		{
			stack.getTagCompound().removeTag("SelectedCraftingManager");
		}
	}

	public static boolean isBound(ItemStack stack)
	{
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("SelectedCraftingManager", NBT.TAG_COMPOUND);
	}

	public static DimensionPos getManagerPos(ItemStack stack)
	{
		NBTTagCompound nbt = stack.getSubCompound("SelectedCraftingManager");
		if(nbt != null)
		{
			return new DimensionPos(nbt);
		}
		return null;
	}

	public static @Nullable TileEntityCraftingManager getSelectedManager(ItemStack stack)
	{
		DimensionPos dpos = getManagerPos(stack);
		if(dpos != null)
		{
			World world = dpos.getWorld();
			if(world != null)
			{
				TileEntity tile = world.getTileEntity(dpos.getPos());
				if(tile instanceof TileEntityCraftingManager)
				{
					return (TileEntityCraftingManager)tile;
				}
			}
		}
		return null;
	}

	public ItemCraftingLinker()
	{
		setMaxStackSize(1);
	}
	private static void sendSingleChatLine(World worldIn, ITextComponent chatmessage)
	{
		if(worldIn.isRemote)
		{
			AdvancedAutocrafting.proxy.sendPlayerChatMessage(chatmessage, 698545369);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!player.isSneaking())
		{
			return EnumActionResult.PASS;
		}
		ItemStack itemstack = player.getHeldItem(hand);
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntityCraftingManager)
		{
			setSelectedManager(itemstack, (TileEntityCraftingManager)tile);
			sendSingleChatLine(worldIn, new TextComponentTranslation("linker.managerselected", pos.getX(), pos.getY(), pos.getZ()));
			return EnumActionResult.SUCCESS;
		}
		TileEntityCraftingManager manager = getSelectedManager(itemstack);
		if(manager == null)
		{
			sendSingleChatLine(worldIn, new TextComponentTranslation("linker.selectmanagerfirst").setStyle(ERROR_STYLE));
			return EnumActionResult.FAIL;
		}
		return manager.linkBlock(worldIn, pos, facing) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		if(!isBound(stack))
		{
			tooltip.add(new TextComponentTranslation("linker.selectedmanager.none").setStyle(ERROR_STYLE).getFormattedText());
		}
		else
		{
			DimensionPos dpos = getManagerPos(stack);
			TileEntityCraftingManager tile = getSelectedManager(stack);
			BlockPos tilepos = dpos.getPos();
			boolean flag = dpos.isSameWorld(playerIn.world);
			StringBuilder s = new StringBuilder("linker.selectedmanager.");
			if(tile == null)
			{
				s.append("unloaded.");
			}
			tooltip.add(s.append(flag ? "samedim" : "diffdim").toString());
			if(flag)
			{
				tooltip.add(I18n.format("linker.selectedmanager.samedim", tilepos.getX(), tilepos.getY(), tilepos.getZ()));
				if(tile != null && GuiScreen.isShiftKeyDown())
				{
					tooltip.add(I18n.format("manager.tooltip.blockslinked"));
					World world = tile.getWorld();
					for(BlockPos pos : tile.getLinkedBlocks())
					{
						IBlockState state = world.getBlockState(pos).getActualState(world, pos);
						tooltip.add(String.format("%s at (%d, %d, %d)", advanced ? state : state.getBlock().getLocalizedName(), pos.getX(), pos.getY(), pos.getZ(), tilepos.getX(), tilepos.getY(), tilepos.getZ()));
					}
				}
			}
			else
			{
				tooltip.add(I18n.format("linker.selectedmanager.diffdim", tilepos.getX(), tilepos.getY(), tilepos.getZ(), dpos.getDimension()));
			}
		}
	}
}
