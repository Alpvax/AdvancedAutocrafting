package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.AABlocks;
import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import alpvax.advancedautocrafting.item.AAItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.RegistryObject;
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

  private void add(ItemGroup group, String name) {
    ITextComponent t = group.getDisplayName();
    if (t instanceof TranslationTextComponent) {
      add(((TranslationTextComponent) t).getKey(), name);
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
