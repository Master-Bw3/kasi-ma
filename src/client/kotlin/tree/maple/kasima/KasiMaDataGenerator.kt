package tree.maple.kasima

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.client.data.*
import net.minecraft.client.data.VariantSettings.Rotation
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.BlockTags
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import tree.maple.kasima.api.registry.RuneRegistry
import tree.maple.kasima.blocks.AxeMineable
import tree.maple.kasima.blocks.KasimaBlocks
import tree.maple.kasima.blocks.RuneLog
import java.util.concurrent.CompletableFuture


object KasiMaDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = generator.createPack()

        pack.addProvider(::BlockTagGenerator)
        pack.addProvider(::BlockLootTables)
        pack.addProvider(::ModelGenerator)
    }
}

private class BlockLootTables(
    dataOutput: FabricDataOutput,
    registryLookup: CompletableFuture<WrapperLookup>
) : FabricBlockLootTableProvider(dataOutput, registryLookup) {
    override fun generate() {
        RuneRegistry.forEach {
            addDrop(it.block.get(), it.material.get())
        }

        addDrop(KasimaBlocks.PALE_RUNE_CORE, Blocks.CREAKING_HEART)
    }
}

private class BlockTagGenerator(output: FabricDataOutput?, registriesFuture: CompletableFuture<WrapperLookup>) :
    FabricTagProvider<Block>(output, RegistryKeys.BLOCK, registriesFuture) {
    override fun configure(lookup: WrapperLookup) {

        val axeMineable = getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)

        Registries.BLOCK.filter { it is AxeMineable }
            .forEach {
                axeMineable.add(it)
            }
    }
}

private class ModelGenerator(generator: FabricDataOutput) :
    FabricModelProvider(generator) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        RuneRegistry.filter { it.block.get() is RuneLog }
            .map { Pair(it.block.get() as RuneLog, RuneRegistry.getId(it)!!) }.forEach { (block, id) ->
            blockStateModelGenerator.blockStateCollector.accept(
                MultipartBlockStateSupplier.create(block)
                    .with(
                        When.create().set(Properties.AXIS, Direction.Axis.X),
                        BlockStateVariant.create()
                            .put(VariantSettings.MODEL, block.horizontalModel)
                            .put(VariantSettings.X, Rotation.R90)
                            .put(VariantSettings.Y, Rotation.R90)
                    )
                    .with(
                        When.create().set(Properties.AXIS, Direction.Axis.Y),
                        BlockStateVariant.create()
                            .put(VariantSettings.MODEL, block.verticalModel)
                    )
                    .with(
                        When.create().set(Properties.AXIS, Direction.Axis.Z),
                        BlockStateVariant.create()
                            .put(VariantSettings.MODEL, block.horizontalModel)
                            .put(VariantSettings.X, Rotation.R90)
                    )
                    .with(
                        BlockStateVariant.create()
                            .put(VariantSettings.MODEL, TexturedModel.CUBE_ALL.upload(block, blockStateModelGenerator.modelCollector))
                    )
            )
        }


    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {

    }
}