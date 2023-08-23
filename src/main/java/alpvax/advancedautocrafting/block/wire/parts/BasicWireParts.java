package alpvax.advancedautocrafting.block.wire.parts;

public enum BasicWireParts {}/*implements IWirePart {
    NONE,
    ARM(2F / 16, 0.5F),
    DISABLED(2.5F / 16, CORE_RADIUS + 1F / 16),
    BLOCK_INTERFACE(d -> makeAxialShape(d, 6F / 16, 0.5F - 1F / 16, 0.5F));

    private final EnumMap<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
    BasicWireParts() {}
    BasicWireParts(float radius, float length) {
        this(d -> makeAxialShape(d, radius, length));
    }
    BasicWireParts(Function<Direction, VoxelShape> shapeFactory) {
        for (var d : Direction.values()) {
            shapes.put(d, shapeFactory.apply(d));
        }
    }

    @Override
    public VoxelShape getShape(@NotNull Direction direction) {
        return shapes.computeIfAbsent(direction, d -> Shapes.empty());
    }

    @Override
    public boolean canConnect() {
        return this != DISABLED;
    }
}*/
