package net.saphyra.common.api.lighting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Consumer-owned values shared by static and dynamic lighting; no loader or client dependencies. */
public final class LightProfiles {

    private static final Map<Block, Function<BlockState, LightEmission>> BLOCKS = new ConcurrentHashMap<>();
    private static final Map<Item, Function<ItemStack, LightEmission>> ITEMS = new ConcurrentHashMap<>();

    private LightProfiles() {}

    /** Registers an explicit profile; returning NONE disables emission for the current state. */
    public static void registerBlock(Block block, Function<BlockState, LightEmission> profile) {
        if (BLOCKS.putIfAbsent(block, profile) != null) throw new IllegalStateException("Duplicate block light profile: " + block);
    }

    public static void registerItem(Item item, Function<ItemStack, LightEmission> profile) {
        if (ITEMS.putIfAbsent(item, profile) != null) throw new IllegalStateException("Duplicate item light profile: " + item);
    }

    /** Fast palette predicate without invoking the consumer's state-dependent profile. */
    public static boolean hasBlockProfile(BlockState state) {
        return BLOCKS.containsKey(state.getBlock());
    }

    public static @Nullable LightEmission block(BlockState state) {
        var profile = BLOCKS.get(state.getBlock());
        return profile == null ? null : profile.apply(state);
    }

    public static @Nullable LightEmission item(ItemStack stack) {
        var profile = ITEMS.get(stack.getItem());
        return profile == null ? null : profile.apply(stack);
    }
}
