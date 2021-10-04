package com.redcrafter07.ultrautilities.world.gen;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class ModOreGeneration {
    public static void generateOres(final BiomeLoadingEvent event)  {
        for (OreType ore : OreType.values()) {
            OreFeatureConfig oreFeatureConfig = new OreFeatureConfig(
                OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, ore.getBlock().get().getDefaultState(), ore.getMaxVeinSize()
            );

            ConfiguredPlacement<TopSolidRangeConfig> configuredPlacement = Placement.RANGE.configure(
                    new TopSolidRangeConfig(ore.getMinHeight(), ore.getMinHeight(), ore.getMaxHeight())
            );

            ConfiguredFeature<?, ?> oreFeature = registerOreFeature(ore, oreFeatureConfig, configuredPlacement);

            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, oreFeature);
        }
    }

    private static ConfiguredFeature<?, ?> registerOreFeature(OreType ore, OreFeatureConfig oreFeatureConfig,
                                                              ConfiguredPlacement configuredPlacement)  {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, ore.getBlock().get().getRegistryName(),
                Feature.ORE.withConfiguration((oreFeatureConfig)).withPlacement(configuredPlacement).square().count(ore.getMaxVeinSize()));
    };
}
