package net.saphyra.common.platform;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * Loader-neutral registrations scoped to the consuming mod's namespace.
 *
 * <p>Declare content in dependency order, then call {@link #register()} once. Fabric registers
 * values immediately; NeoForge resolves deferred suppliers on its registry events. Never resolve a
 * deferred handle during static initialization.
 */
public interface PlatformRegistry {

    /** Returns the namespace owned by this registry instance. */
    String namespace();

    <T extends net.minecraft.world.entity.Entity>
            PlatformRegistryObject<net.minecraft.world.entity.EntityType<T>> registerEntityType(
                    String id, Supplier<net.minecraft.world.entity.EntityType<T>> supplier);

    <T extends Block> PlatformRegistryObject<T> registerBlock(String id, Supplier<T> supplier);

    <T extends Item> PlatformRegistryObject<T> registerItem(String id, Supplier<T> supplier);

    PlatformRegistryObject<Item> registerBlockItem(
            String id, PlatformRegistryObject<? extends Block> block);

    <T extends BlockEntityType<?>> PlatformRegistryObject<T> registerBlockEntity(
            String id, Supplier<T> supplier);

    PlatformCreativeModeTabBuilder createCreativeModeTabBuilder();

    PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id, Supplier<PlatformCreativeModeTabBuilder> builder);

    PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id, ResourceLocation before, Supplier<PlatformCreativeModeTabBuilder> builder);

    <T extends net.minecraft.world.inventory.AbstractContainerMenu>
            PlatformRegistryObject<net.minecraft.world.inventory.MenuType<T>> registerMenu(
                    String id,
                    java.util.function.BiFunction<
                                    Integer, net.minecraft.world.entity.player.Inventory, T>
                            factory);

    <T extends net.minecraft.world.level.block.entity.BlockEntity>
            PlatformRegistryObject<BlockEntityType<T>> registerBlockEntity(
                    String id,
                    java.util.function.BiFunction<
                                    net.minecraft.core.BlockPos,
                                    net.minecraft.world.level.block.state.BlockState,
                                    T>
                            factory,
                    Supplier<Block[]> blocks);

    /**
     * Finishes registration; NeoForge attaches deferred registries to the owning mod's event bus.
     */
    void register();
}
