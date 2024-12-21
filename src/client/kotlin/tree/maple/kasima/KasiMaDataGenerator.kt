package tree.maple.kasima

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.BlockTags
import tree.maple.kasima.api.registry.RuneRegistry
import tree.maple.kasima.blocks.AxeMineable
import tree.maple.kasima.blocks.KasimaBlocks
import java.util.concurrent.CompletableFuture


object KasiMaDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = generator.createPack()

        pack.addProvider(::BlockTagGenerator)
        pack.addProvider(::BlockLootTables)
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