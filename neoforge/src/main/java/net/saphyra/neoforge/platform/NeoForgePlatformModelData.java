package net.saphyra.neoforge.platform;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.scp_genesis.common.copycatblocks.data.CopycatPart;

import java.util.EnumMap;

public final class NeoForgePlatformModelData
        implements PlatformModelData<NeoForgePlatformModelData.Data> {

    public static final ModelProperty<
            EnumMap<CopycatPart, BlockState>
            > COPIED_STATES =
            new ModelProperty<>();

    @Override
    public Data create(
            EnumMap<CopycatPart, BlockState> copiedStates
    ) {
        net.neoforged.neoforge.client.model.data.ModelData modelData =
                net.neoforged.neoforge.client.model.data.ModelData
                        .builder()
                        .with(
                                COPIED_STATES,
                                copiedStates
                        )
                        .build();

        return new Data(modelData);
    }

    public static Data empty() {
        return new Data(
                net.neoforged.neoforge.client.model.data.ModelData.EMPTY
        );
    }

    public static final class Data
            implements PlatformModelData.ModelData {

        private final net.neoforged.neoforge.client.model.data.ModelData modelData;

        private Data(
                net.neoforged.neoforge.client.model.data.ModelData modelData
        ) {
            this.modelData = modelData;
        }

        public net.neoforged.neoforge.client.model.data.ModelData get() {
            return modelData;
        }
    }
}