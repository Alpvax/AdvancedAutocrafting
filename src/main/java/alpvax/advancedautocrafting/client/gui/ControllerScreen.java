package alpvax.advancedautocrafting.client.gui;

import alpvax.advancedautocrafting.container.ControllerContainer;
import alpvax.advancedautocrafting.container.util.ContainerBlockHolder;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ControllerScreen extends ContainerScreen<ControllerContainer> {
  //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AdvancedAutocrafting.MODID, "textures/gui/container/controller.png");

  private float rotation = 45F;
  private final List<BlockGUIRenderer> blocks;

  public ControllerScreen(ControllerContainer container, PlayerInventory inv, ITextComponent title) {
    super(container, inv, title);
    xSize = 300;
    ySize = 200;
    blocks = container.getBlocks().stream()
                 .map(BlockGUIRenderer::new)
                 .collect(Collectors.toList());
  }

  public static void drawEntityOnScreen(float posX, float posY, float scale, float horzRotation, Entity p_228187_5_) {
    RenderSystem.pushMatrix();
    RenderSystem.translatef(posX, posY, 1050.0F);
    RenderSystem.scalef(1.0F, 1.0F, -1.0F);
    MatrixStack matrixstack = new MatrixStack();
    matrixstack.translate(0.0D, 0.0D, 1000.0D);
    matrixstack.scale(scale, scale, scale);
    Quaternion quaternionz = Vector3f.ZP.rotationDegrees(180.0F);
    Quaternion quaternionx = Vector3f.XP.rotationDegrees(-20F);
    Quaternion quaterniony = Vector3f.YP.rotationDegrees(horzRotation);//Added
    quaternionz.multiply(quaternionx);
    quaternionz.multiply(quaterniony);//Added
    matrixstack.rotate(quaternionz);
    EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
    quaternionx.conjugate();
    entityrenderermanager.setCameraOrientation(quaternionx);
    entityrenderermanager.setRenderShadow(false);
    IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
    RenderSystem.runAsFancy(() -> {
      entityrenderermanager.renderEntityStatic(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
    });
    irendertypebuffer$impl.finish();
    entityrenderermanager.setRenderShadow(true);
    RenderSystem.popMatrix();
  }

  @Override //resize
  protected void func_231160_c_() {
    super.func_231160_c_();
    AtomicInteger j = new AtomicInteger(0);
    //blocks.forEach(b -> b.setPositionAndScale(guiLeft + 8, guiTop + 18 + j.getAndAdd(18), 10));
    // Fit in 32x32 box, with 3px padding between:
    blocks.forEach(b -> b.setPositionAndScale(guiLeft + 8, guiTop + 18 + j.getAndAdd(35), 30F / 1.7F));
  }

  @Override //render
  public void func_230430_a_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    func_230446_a_(matrixStack);//renderBackground
    blocks.forEach(BlockGUIRenderer::update);
    super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
    func_230459_a_(matrixStack, mouseX, mouseY);//renderHoveredToolTip
    rotation = (rotation + partialTicks) % 360F; // 18-sec full rotation
  }
  /*@Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }*/

  @Override //drawGuiContainerForegroundLayer
  protected void func_230451_b_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
    field_230712_o_.func_243246_a(matrixStack, field_230704_d_, (float)this.field_238742_p_, (float)this.field_238743_q_, 0xFFFFFF);//4210752);
    field_230712_o_.func_243246_a(matrixStack, this.playerInventory.getDisplayName(), (float)this.field_238744_r_, (float)this.field_238745_s_, 0xFFFFFF);//4210752);

    for (BlockGUIRenderer block : blocks) {
      if (block.renderBlockToolTip(matrixStack, mouseX, mouseY)) {
        break;
      }
    }
  }
  /*@Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
  }*/

  @Override //drawGuiContainerBackgroundLayer //TODO: actual screen render
  protected void func_230450_a_(@Nonnull MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    /*TODO: background
    this.field_230706_i_.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
    int i = this.guiLeft;
    int j = this.guiTop;
    func_238474_b_(matrixStack, i, j, 0, 0, this.xSize, 3 * 18 + 17); //blit
    func_238474_b_(matrixStack, i, j + 3 * 18 + 17, 0, 126, this.xSize, 96); //blit
     */
    for (BlockGUIRenderer block : blocks) {
      block.renderBlock(rotation);
    }
  }

  private FallingBlockEntity makeEntity(BlockState state) {
    Vector3d pos = getMinecraft().player.getPositionVec();
    FallingBlockEntity entity = new FallingBlockEntity(getMinecraft().world, pos.x, pos.y, pos.z, state);
    entity.fallTime = Integer.MIN_VALUE;
    return entity;
  }


  public class BlockGUIRenderer {
    private final ContainerBlockHolder holder;
    //private BlockState state = null;
    private FallingBlockEntity entity;
    private ITextComponent name;
    private float scale = 10F;
    private int x;
    private int y;
    public BlockGUIRenderer(ContainerBlockHolder holder) {
      this.holder = holder;
      holder.addListener(h -> this.update());
    }

    public void setPositionAndScale(int x, int y, float scale) {
      this.x = x;
      this.y = y;
      this.scale = scale;
    }

    private void update() {
      BlockState s = holder.getBlockState();
      entity = makeEntity(s);
      ItemStack stack = new ItemStack(s.getBlock());
      name = new StringTextComponent("").func_230529_a_(stack.getDisplayName()).func_240699_a_(stack.getRarity().color);
    }

    public boolean isHovered(int mouseX, int mouseY) {
      return mouseX >= x && mouseX - x <  scale * 1.414F && mouseY >= y && mouseY - y < scale * 1.7F;
    }

    public void renderBlock(int x, int y, int scale, float rotation) {
      setPositionAndScale(x, y, scale);
      renderBlock(rotation);
    }
    public void renderBlock(float rotation) {
      drawEntityOnScreen(x + 1F + scale * 1.414F / 2F, y + scale * 1.054F, scale, rotation, entity);
    }

    public boolean renderBlockToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
      if (isHovered(mouseX, mouseY)) {
        Minecraft mc = getMinecraft();
        BlockPos pos = holder.getPos();
        List<ITextComponent> tooltip = Lists.newArrayList(
            name,
            new StringTextComponent(String.format("Position: {%d, %d, %d}", pos.getX(), pos.getY(), pos.getZ())).func_240699_a_(TextFormatting.GRAY)
        );
        if (func_231173_s_() || !holder.isClientWorld()) { //hasShiftDown
          tooltip.add(new StringTextComponent("Dimension: " + holder.getWorldID().toString()).func_240699_a_(TextFormatting.GRAY));
        }
        renderToolTip(matrixStack, Lists.transform(tooltip, ITextComponent::func_241878_f), mouseX - guiLeft, mouseY - guiTop, field_230712_o_);
        return true;
      }
      return false;
    }
  }
}
