package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.api.AAReference;
import alpvax.advancedautocrafting.client.data.lang.AATranslationKeys;
import alpvax.advancedautocrafting.init.AABlocks;
import alpvax.advancedautocrafting.init.AAItems;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("SameParameterValue")
public class AALangProvider extends LanguageProvider {

    public AALangProvider(PackOutput output) {
        super(output, AAReference.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(AAItems.MULTITOOL.get(), "Multitool");

        addBlock(AABlocks.CONTROLLER);
        addBlock(AABlocks.POSITION_MARKER, "Remote Position Marker");
        addBlock(AABlocks.REMOTE_MASTER, "Remote Network Container");
        addBlock(AABlocks.WIRE, "Wire");

        AATranslationKeys.EN_US_MAPPINGS.forEach(this::add);
    }

    private void add(CreativeModeTab tab, String name) {
        if (tab.getDisplayName().getContents() instanceof TranslatableContents t) {
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
