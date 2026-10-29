package net.saphyra.fabric.compat.iris;

import org.spongepowered.asm.mixin.extensibility.IRemapper;

/** Supplies the two absent Iris descriptors before Loader's development remapper sees them. */
final class IrisDevelopmentRemapper implements IRemapper {

    private final IRemapper delegate;

    IrisDevelopmentRemapper(IRemapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        if (descriptor == null) {
            var mappings = net.fabricmc.loader.api.FabricLoader.getInstance().getMappingResolver();
            String weatherOwner = mappings.mapClassName("intermediary", "net.minecraft.class_761").replace('.', '/');
            String armorOwner = mappings.mapClassName("intermediary", "net.minecraft.class_970").replace('.', '/');
            if (("net/minecraft/class_761".equals(owner) || weatherOwner.equals(owner))
                    && ("method_22714".equals(name) || "renderSnowAndRain".equals(name))) {
                return mappings.mapMethodName("intermediary", "net.minecraft.class_761", "method_22714",
                        "(Lnet/minecraft/class_765;FDDD)V");
            }
            if (("net/minecraft/class_970".equals(owner) || armorOwner.equals(owner))
                    && ("method_4169".equals(name) || "renderArmorPiece".equals(name))) {
                return mappings.mapMethodName("intermediary", "net.minecraft.class_970", "method_4169",
                        "(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_1309;Lnet/minecraft/class_1304;ILnet/minecraft/class_572;)V");
            }
        }
        return delegate.mapMethodName(owner, name, descriptor);
    }

    @Override public String mapFieldName(String owner, String name, String descriptor) {
        return delegate.mapFieldName(owner, name, descriptor);
    }
    @Override public String map(String name) { return delegate.map(name); }
    @Override public String unmap(String name) { return delegate.unmap(name); }
    @Override public String mapDesc(String descriptor) { return delegate.mapDesc(descriptor); }
    @Override public String unmapDesc(String descriptor) { return delegate.unmapDesc(descriptor); }
}
