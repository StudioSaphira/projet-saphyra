package net.saphyra.fabric.platform;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.saphyra.common.constants.ModConstants;

import net.saphyra.common.registry.ModBlocks;
import net.saphyra.common.registry.ModItems;
import net.saphyra.common.registry.ModTabs;

import net.saphyra.common.platform.PlatformCreativeModeTabBuilder;
import net.saphyra.common.platform.PlatformRegistry;
import net.saphyra.common.platform.PlatformRegistryObject;

import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class FabricPlatformRegistry implements PlatformRegistry {

    private ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(
                ModConstants.MOD_ID,
                path
        );
    }

    // ------------------------------------------------------------------------
    // Blocks
    // ------------------------------------------------------------------------

    @Override
    public <T extends Block> PlatformRegistryObject<T> registerBlock(
            String id,
            Supplier<T> supplier
    ) {
        ResourceLocation location = id(id);

        T block = Registry.register(
                BuiltInRegistries.BLOCK,
                location,
                supplier.get()
        );

        return new FabricPlatformRegistryObject<>(
                block,
                location
        );
    }

    // ------------------------------------------------------------------------
    // Items
    // ------------------------------------------------------------------------

    @Override
    public <T extends Item> PlatformRegistryObject<T> registerItem(
            String id,
            Supplier<T> supplier
    ) {
        ResourceLocation location = id(id);

        T item = Registry.register(
                BuiltInRegistries.ITEM,
                location,
                supplier.get()
        );

        return new FabricPlatformRegistryObject<>(
                item,
                location
        );
    }

    @Override
    public PlatformRegistryObject<Item> registerBlockItem(
            String id,
            PlatformRegistryObject<? extends Block> block
    ) {
        ResourceLocation location = id(id);

        Item item = Registry.register(
                BuiltInRegistries.ITEM,
                location,
                new BlockItem(
                        block.get(),
                        new Item.Properties()
                )
        );

        return new FabricPlatformRegistryObject<>(
                item,
                location
        );
    }

    // ------------------------------------------------------------------------
    // Block Entities
    // ------------------------------------------------------------------------

    @Override
    public PlatformRegistryObject<BlockEntityType<CopycatBlockEntity>>
    registerCopycatBlockEntity(String id) {

        ResourceLocation location = id(id);

        BlockEntityType<CopycatBlockEntity> type =
                BlockEntityType.Builder.of(
                        CopycatBlockEntity::new,
                        ModBlocks.COPYCAT_CUBE.get(),
                        ModBlocks.COPYCAT_SLAB.get(),
                        ModBlocks.COPYCAT_STAIRS.get(),
                        ModBlocks.COPYCAT_SLOPE.get(),
                        ModBlocks.COPYCAT_VERTICAL_SLOPE.get(),
                        ModBlocks.COPYCAT_PANEL.get(),
                        ModBlocks.COPYCAT_HALF_PANEL.get(),
                        ModBlocks.COPYCAT_HALF_SLAB.get(),
                        ModBlocks.COPYCAT_HALF_STAIRS.get(),
                        ModBlocks.COPYCAT_HALF_SLOPE.get(),
                        ModBlocks.COPYCAT_HORIZONTAL_HALF_SLOPE.get(),
                        ModBlocks.COPYCAT_VERTICAL_HALF_SLOPE.get()
                ).build(null);

        Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                location,
                type
        );

        return new FabricPlatformRegistryObject<>(
                type,
                location
        );
    }

    @Override
    public <T extends BlockEntityType<?>> PlatformRegistryObject<T> registerBlockEntity(
            String id,
            Supplier<T> supplier
    ) {
        ResourceLocation location = id(id);

        T blockEntityType = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                location,
                supplier.get()
        );

        return new FabricPlatformRegistryObject<>(
                blockEntityType,
                location
        );
    }

    // ------------------------------------------------------------------------
    // Creative Mode Tabs
    // ------------------------------------------------------------------------

    @Override
    public PlatformCreativeModeTabBuilder createCreativeModeTabBuilder() {
        return new FabricCreativeModeTabBuilder();
    }

    @Override
    public PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id,
            Supplier<PlatformCreativeModeTabBuilder> builder
    ) {
        ResourceLocation location = id(id);

        CreativeModeTab tab = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                location,
                builder.get().build()
        );

        FabricTabSettings.register(location, tab);

        return new FabricPlatformRegistryObject<>(
                tab,
                location
        );
    }

    @Override
    public PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id,
            ResourceLocation before,
            Supplier<PlatformCreativeModeTabBuilder> builder
    ) {
        ResourceLocation location = id(id);

        CreativeModeTab tab = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                location,
                builder.get().withTabsBefore(before).build()
        );

        FabricTabSettings.register(location, tab);

        return new FabricPlatformRegistryObject<>(
                tab,
                location
        );
    }

    // ------------------------------------------------------------------------
    // Registration
    // ------------------------------------------------------------------------

    @Override
    public void register() {
        ModEntities.register(this);
        ModBlocks.register(this);
        ModItems.register(this);
        ModBlockEntities.register(this);
        ModMenus.register(this);
        ModTabs.register(this);
    }
    @Override
    public <T extends Entity> PlatformRegistryObject<EntityType<T>> registerEntityType(
            String id, Supplier<EntityType<T>> supplier) {
        ResourceLocation location = id(id);
        EntityType<T> type = Registry.register(BuiltInRegistries.ENTITY_TYPE, location, supplier.get());
        return new FabricPlatformRegistryObject<>(type, location);
    }
    @Override
    public <T extends net.minecraft.world.inventory.AbstractContainerMenu> PlatformRegistryObject<net.minecraft.world.inventory.MenuType<T>>
    registerMenu(String id, java.util.function.BiFunction<Integer, net.minecraft.world.entity.player.Inventory, T> factory) {
        ResourceLocation location = id(id);
        var type = Registry.register(BuiltInRegistries.MENU, location, new net.minecraft.world.inventory.MenuType<>(factory::apply, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
        return new FabricPlatformRegistryObject<>(type, location);
    }
    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> PlatformRegistryObject<BlockEntityType<T>>
    registerBlockEntity(String id, java.util.function.BiFunction<net.minecraft.core.BlockPos,
            net.minecraft.world.level.block.state.BlockState, T> factory, Supplier<Block[]> blocks) {
        return registerBlockEntity(id, () -> BlockEntityType.Builder.of(factory::apply, blocks.get()).build(null));
    }
}