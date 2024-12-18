package tree.maple.kasima

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.BlockTags
import tree.maple.kasima.blocks.KasimaBlockRegistry
import tree.maple.kasima.blocks.KasimaBlockTags
import java.util.concurrent.CompletableFuture


object KasiMaDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = generator.createPack()

		pack.addProvider(::BlockLootTables)
        pack.addProvider(::BlockTagGenerator)
    }
}

private class BlockLootTables(
    dataOutput: FabricDataOutput,
    registryLookup: CompletableFuture<WrapperLookup>
) : FabricBlockLootTableProvider(dataOutput, registryLookup) {
    override fun generate() {
        addDrop(KasimaBlockRegistry.PALE_RUNE_LOG, Blocks.PALE_OAK_LOG)
		addDrop(KasimaBlockRegistry.PALE_RUNE_CORE, Blocks.CREAKING_HEART)

	}
}

private class BlockTagGenerator(output: FabricDataOutput?, registriesFuture: CompletableFuture<WrapperLookup>) :
    FabricTagProvider<Block>(output, RegistryKeys.BLOCK, registriesFuture) {
    override fun configure(lookup: WrapperLookup) {

        val axeMineable = getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
        val chiselable = getOrCreateTagBuilder(KasimaBlockTags.CHISELABLE)

        arrayOf(
            KasimaBlockRegistry.PALE_RUNE_LOG,
            KasimaBlockRegistry.PALE_RUNE_CORE
        ).forEach {
            axeMineable.add(it)
            chiselable.add(it)
        }

    }
}