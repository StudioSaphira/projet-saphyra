package net.saphyra.neoforge.platform;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.saphyra.common.platform.PlatformCreativeModeTabBuilder;
import net.saphyra.common.platform.PlatformRegistry;
import net.saphyra.common.platform.PlatformRegistryObject;

import java.util.function.Supplier;

/** Reusable neoforge registry; subclasses may add mod-specific registrations. */
public class NeoForgePlatformRegistry implements PlatformRegistry {

    private final DeferredRegister<EntityType<?>> entityTypes;
    private final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> menus;
    private final IEventBus modEventBus;
    private final String namespace;
    private boolean registered;

    private final DeferredRegister.Blocks blocks;
    private final DeferredRegister.Items items;
    private final DeferredRegister<BlockEntityType<?>> blockEntities;
    private final DeferredRegister<CreativeModeTab> creativeModeTabs;

    public NeoForgePlatformRegistry(String namespace, IEventBus modEventBus) {
        this.namespace = java.util.Objects.requireNonNull(namespace, "namespace");
        ResourceLocation.fromNamespaceAndPath(namespace, "validation");
        this.modEventBus = java.util.Objects.requireNonNull(modEventBus, "modEventBus");
        this.entityTypes = DeferredRegister.create(Registries.ENTITY_TYPE, namespace);
        this.menus = DeferredRegister.create(Registries.MENU, namespace);

        this.blocks = DeferredRegister.createBlocks(namespace);

        this.items = DeferredRegister.createItems(namespace);

        this.blockEntities =
                DeferredRegister.create(
                        net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, namespace);

        this.creativeModeTabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, namespace);
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public <T extends Block> PlatformRegistryObject<T> registerBlock(
            String id, Supplier<T> supplier) {
        var holder = blocks.register(id, supplier);

        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }

    @Override
    public <T extends Item> PlatformRegistryObject<T> registerItem(
            String id, Supplier<T> supplier) {
        var holder = items.register(id, supplier);

        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }

    @Override
    public PlatformRegistryObject<Item> registerBlockItem(
            String id, PlatformRegistryObject<? extends Block> block) {
        var holder =
                items.register(
                        id,
                        () ->
                                new net.minecraft.world.item.BlockItem(
                                        block.get(), new Item.Properties()));

        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }

    @Override
    public <T extends BlockEntityType<?>> PlatformRegistryObject<T> registerBlockEntity(
            String id, Supplier<T> supplier) {
        var holder = blockEntities.register(id, supplier);

        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }

    @Override
    public PlatformCreativeModeTabBuilder createCreativeModeTabBuilder() {
        return new NeoForgeCreativeModeTabBuilder();
    }

    @Override
    public PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id, Supplier<PlatformCreativeModeTabBuilder> builder) {
        var holder = creativeModeTabs.register(id, () -> builder.get().build());

        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }

    @Override
    public PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id, ResourceLocation before, Supplier<PlatformCreativeModeTabBuilder> builder) {
        var holder =
                creativeModeTabs.register(
                        id,
                        () -> {
                            PlatformCreativeModeTabBuilder tabBuilder = builder.get();

                            tabBuilder.withTabsBefore(before);

                            return tabBuilder.build();
                        });

        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }

    @Override
    public void register() {
        if (registered) {
            throw new IllegalStateException("Registry already registered: " + namespace);
        }
        registered = true;

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
    public <T extends net.minecraft.world.inventory.AbstractContainerMenu>
            PlatformRegistryObject<net.minecraft.world.inventory.MenuType<T>> registerMenu(
                    String id,
                    java.util.function.BiFunction<
                                    Integer, net.minecraft.world.entity.player.Inventory, T>
                            factory) {
        var holder =
                menus.register(
                        id,
                        () ->
                                new net.minecraft.world.inventory.MenuType<>(
                                        factory::apply,
                                        net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
        return new NeoForgePlatformRegistryObject<>(holder, holder::getId);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity>
            PlatformRegistryObject<BlockEntityType<T>> registerBlockEntity(
                    String id,
                    java.util.function.BiFunction<
                                    net.minecraft.core.BlockPos,
                                    net.minecraft.world.level.block.state.BlockState,
                                    T>
                            factory,
                    Supplier<Block[]> blocks) {
        return registerBlockEntity(
                id, () -> BlockEntityType.Builder.of(factory::apply, blocks.get()).build(null));
    }
}
