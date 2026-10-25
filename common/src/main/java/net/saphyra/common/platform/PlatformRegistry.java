package net.saphyra.common.platform;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.scp_genesis.common.copycatblocks.blockentity.CopycatBlockEntity;

import java.util.function.Supplier;

public interface PlatformRegistry {
    <T extends net.minecraft.world.entity.Entity> PlatformRegistryObject<net.minecraft.world.entity.EntityType<T>>
    registerEntityType(String id, Supplier<net.minecraft.world.entity.EntityType<T>> supplier);

    <T extends Block> PlatformRegistryObject<T> registerBlock(
            String id,
            Supplier<T> supplier
    );

    <T extends Item> PlatformRegistryObject<T> registerItem(
            String id,
            Supplier<T> supplier
    );

    PlatformRegistryObject<Item> registerBlockItem(
            String id,
            PlatformRegistryObject<? extends Block> block
    );

    PlatformRegistryObject<BlockEntityType<CopycatBlockEntity>>
    registerCopycatBlockEntity(String id);

    <T extends BlockEntityType<?>> PlatformRegistryObject<T> registerBlockEntity(
            String id,
            Supplier<T> supplier
    );

    PlatformCreativeModeTabBuilder createCreativeModeTabBuilder();

    PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id,
            Supplier<PlatformCreativeModeTabBuilder> builder
    );

    PlatformRegistryObject<CreativeModeTab> registerCreativeModeTab(
            String id,
            ResourceLocation before,
            Supplier<PlatformCreativeModeTabBuilder> builder
    );

    <T extends net.minecraft.world.inventory.AbstractContainerMenu> PlatformRegistryObject<net.minecraft.world.inventory.MenuType<T>>
    registerMenu(String id, java.util.function.BiFunction<Integer, net.minecraft.world.entity.player.Inventory, T> factory);

    <T extends net.minecraft.world.level.block.entity.BlockEntity> PlatformRegistryObject<BlockEntityType<T>>
    registerBlockEntity(String id, java.util.function.BiFunction<net.minecraft.core.BlockPos,
            net.minecraft.world.level.block.state.BlockState, T> factory, Supplier<Block[]> blocks);

    void register();
}
