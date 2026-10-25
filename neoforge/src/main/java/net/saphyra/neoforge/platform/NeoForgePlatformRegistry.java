package net.saphyra.neoforge.platform;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.saphyra.common.constants.ModConstants;
import net.saphyra.common.registry.ModBlocks;
import net.saphyra.common.registry.ModItems;
import net.saphyra.common.registry.ModTabs;
import net.scp_genesis.common.copycatblocks.blockentity.CopycatBlockEntity;
import net.saphyra.common.platform.PlatformCreativeModeTabBuilder;
import net.saphyra.common.platform.PlatformRegistry;
import net.saphyra.common.platform.PlatformRegistryObject;

import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class NeoForgePlatformRegistry
        implements PlatformRegistry {

    private final DeferredRegister<EntityType<?>> entityTypes = DeferredRegister.create(Registries.ENTITY_TYPE, ModConstants.MOD_ID);
    private final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> menus = DeferredRegister.create(Registries.MENU, ModConstants.MOD_ID);
    private final IEventBus modEventBus;

    private final DeferredRegister.Blocks blocks;
    private final DeferredRegister.Items items;
    private final DeferredRegister<BlockEntityType<?>> blockEntities;
    private final DeferredRegister<CreativeModeTab> creativeModeTabs;

    public NeoForgePlatformRegistry(
            IEventBus modEventBus
    ) {
        this.modEventBus = modEventBus;

        this.blocks =
                DeferredRegister.createBlocks(
                        ModConstants.MOD_ID
                );

        this.items =
                DeferredRegister.createItems(
                        ModConstants.MOD_ID
                );

        this.blockEntities =
                DeferredRegister.create(
                        net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE,
                        ModConstants.MOD_ID
                );

        this.creativeModeTabs =
                DeferredRegister.create(
                        Registries.CREATIVE_MODE_TAB,
                        ModConstants.MOD_ID
                );
    }

    @Override
    public <T extends Block> PlatformRegistryObject<T> registerBlock(
            String id,
            Supplier<T> supplier
    ) {
        var holder = blocks.register(id, supplier);

        return new NeoForgePlatformRegistryObject<>(
                holder,
                holder::getId
        );
    }

    @Override
    public <T extends Item> PlatformRegistryObject<T> registerItem(
            String id,
            Supplier<T> supplier
    ) {
        var holder = items.register(id, supplier);

        return new NeoForgePlatformRegistryObject<>(
                holder,
                holder::getId
        );
    }

    @Override
    public PlatformRegistryObject<Item> registerBlockItem(
            String id,
            PlatformRegistryObject<? extends Block> block
    ) {
        var holder = items.register(
                id,
                () -> new net.minecraft.world.item.BlockItem(
                        block.get(),
                        new Item.Properties()
                )
        );

        return new NeoForgePlatformRegistryObject<>(
                holder,
                holder::getId
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public PlatformRegistryObject<BlockEntityType<CopycatBlockEntity>>
    registerCopycatBlockEntity(String id) {

        var holder = blockEntities.register(
                id,
                () -> BlockEntityType.Builder.of(
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
                ).build(null)
        );

        return new NeoForgePlatformRegistryObject<>(
                holder,
                holder::getId
        );
    }

    @Override
    public <T extends BlockEntityType<?>> PlatformRegistryObject<T> registerBlockEntity(
            String id,
            Supplier<T> supplier
    ) {
        var holder = blockEntities.register(id, supplier);

        return new NeoForgePlatformRegistryObject<>(
                holder,
                holder::getId
        );
    }

    @Override
    public PlatformCreativeModeTabBuilder createCreativeModeTabBuilder() {
        return new NeoForgeCreativeModeTabBuilder();
    }

    @Override
    public PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id,
            Supplier<PlatformCreativeModeTabBuilder> builder
    ) {
        var holder = creativeModeTabs.register(
                id,
                () -> builder.get().build()
        );

        return new NeoForgePlatformRegistryObject<>(
                holder,
                holder::getId
        );
    }

    @Override
    public PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id,
            ResourceLocation before,
            Supplier<PlatformCreativeModeTabBuilder> builder
    ) {
        var holder = creativeModeTabs.register(
                id,
                () -> {
                    PlatformCreativeModeTabBuilder tabBuilder = builder.get();

                    tabBuilder.withTabsBefore(before);

                    return tabBuilder.build();
                }
        );

        return new NeoForgePlatformRegistryObject<>(
                holder,
                holder::getId
        );
    }

    @Override
    public void register() {
        ModEntities.register(this);
        ModBlocks.register(this);
        ModItems.register(this);
        ModBlockEntities.register(this);
        ModMenus.register(this);
        ModTabs.register(this);

        entityTypes.register(modEventBus);
        blocks.register(modEventBus);
        items.register(modEventBus);
        blockEntities.register(modEventBus);
        menus.register(modEventBus);
        creativeModeTabs.register(modEventBus);
    }
    @Override
    public <T extends Entity> PlatformRegistryObject<EntityType<T>> registerEntityType(
            String id, Supplier<EntityType<T>> supplier) {
        var holder = entityTypes.register(id, supplier);
        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }
    @Override
    public <T extends net.minecraft.world.inventory.AbstractContainerMenu> PlatformRegistryObject<net.minecraft.world.inventory.MenuType<T>>
    registerMenu(String id, java.util.function.BiFunction<Integer, net.minecraft.world.entity.player.Inventory, T> factory) {
        var holder = menus.register(id, () -> new net.minecraft.world.inventory.MenuType<>(factory::apply, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }
    @SuppressWarnings("DataFlowIssue")
    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> PlatformRegistryObject<BlockEntityType<T>>
    registerBlockEntity(String id, java.util.function.BiFunction<net.minecraft.core.BlockPos,
            net.minecraft.world.level.block.state.BlockState, T> factory, Supplier<Block[]> blocks) {
        return registerBlockEntity(id, () -> BlockEntityType.Builder.of(factory::apply, blocks.get()).build(null));
    }
}