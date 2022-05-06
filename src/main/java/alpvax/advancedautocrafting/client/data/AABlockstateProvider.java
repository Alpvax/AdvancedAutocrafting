package alpvax.advancedautocrafting.client.data;

import alpvax.advancedautocrafting.AdvancedAutocrafting;
import alpvax.advancedautocrafting.block.axial.AxialBlock;
import alpvax.advancedautocrafting.block.axial.AxialBlockShape;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static alpvax.advancedautocrafting.block.AABlocks.*;

public class AABlockstateProvider extends BlockStateProvider {
    public AABlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, AdvancedAutocrafting.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(CONTROLLER);
        simpleBlock(POSITION_MARKER);
        simpleBlock(REMOTE_MASTER);
        axisBlock(WIRE);
    }

    private void simpleBlock(Supplier<? extends Block> sup) {
        simpleBlock(sup.get());
    }

    private <T extends Comparable<T>> void axisBlock(Supplier<? extends AxialBlock<T>> sup) {
        AxialBlock<T> block = sup.get();
        ResourceLocation blockName = block.getRegistryName();
        //noinspection ConstantConditions
        axisBlock(block, (partName, baseModel) -> models().singleTexture(blockName.toString() + "_" + partName, baseModel.getUncheckedLocation(), blockTexture(block)));
    }

    private <T extends Comparable<T>> void axisBlock(AxialBlock<T> block, BiFunction<String, ModelFile, ModelFile> partMapper) {
        AxialBlockShape<T> shape = block.getBlockShape();
        Map<String, ModelFile> shapeModels = shape.buildBlockModelParts("block/part/", models());
        Map<String, ModelFile> blockModels = new HashMap<>(shapeModels.size());
        shapeModels.forEach((partName, baseModel) -> blockModels.put(partName, partMapper.apply(partName, baseModel)));

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block).part().modelFile(blockModels.get("core")).addModel().end();
        block.forEachDirection((d, prop) -> {
            int yrot = d.getAxis().isHorizontal() ? (((int) d.toYRot()) + 180) % 360 : 0;
            int xrot = d.getStepY() == 0 ? 0 : d.getStepY() > 0 ? 270 : 90;
            shape.forEach(part -> builder.part()
                .modelFile(blockModels.get(part.name))
                .rotationY(yrot)
                .rotationX(xrot)
                .uvLock(true)
                .addModel()
                .condition(prop, part.getAllowedValues()));
        });
    }
}
