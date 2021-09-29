package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fmllegacy.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("SameParameterValue")
public class AALangProvider extends LanguageProvider {

  public AALangProvider(DataGenerator gen) {
    super(gen, AdvancedAutocrafting.MODID, "en_us");
  }

  @Override
  protected void addTranslations() {
    add(AAItems.ITEM_GROUP, "Advanced Autocrafting");

    add(AAItems.REMOTE_POS.get(), "Remote Position Reference");
    add(AAItems.MULTITOOL.get(), "Multitool");

    addBlock(AABlocks.CONTROLLER);
    addBlock(AABlocks.REMOTE_MARKER, "Remote Position Marker");
    addBlock(AABlocks.REMOTE_MASTER, "Remote Network Container");
    addBlock(AABlocks.WIRE, "Wire");

    AATranslationKeys.EN_US_MAPPINGS.forEach(this::add);
  }

  private void add(CreativeModeTab tab, String name) {
    if (tab.getDisplayName() instanceof TranslatableComponent t) {
      add(t.getKey(), name);
    }
  }

  private void addBlock(RegistryObject<Block> block) {
    add(block.get(), toEnglishName(block.getId()));
  }

  private String toEnglishName(ResourceLocation internalName) {
    return "Autocrafting " + formatEnglish(internalName.getPath());
  }

  private String formatEnglish(String name) {
    return Arrays.stream(name.split("_"))
        .map(StringUtils::capitalize)
        .collect(Collectors.joining(" "));
  }
}
