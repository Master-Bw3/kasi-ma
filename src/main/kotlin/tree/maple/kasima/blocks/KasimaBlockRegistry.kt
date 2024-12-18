package tree.maple.kasima.blocks

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.MapColor
import net.minecraft.block.enums.NoteBlockInstrument
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa


object KasimaBlockRegistry {

    val flammableBlockRegistry: FlammableBlockRegistry = FlammableBlockRegistry.getDefaultInstance()

    val PALE_RUNE_LOG = register(
        ::RuneLog,
        Blocks.createLogSettings(
            Blocks.PALE_OAK_PLANKS.defaultMapColor,
            Blocks.PALE_OAK_WOOD.defaultMapColor,
            BlockSoundGroup.WOOD
        ), "pale_rune_log",
        registerItem = false,
        flammable = true
    ).let { KasimaChiselConversionRegistry.register(Identifier.ofVanilla("pale_oak_log"), it) }

    val PALE_RUNE_CORE = register(
        ::RuneCore,
        AbstractBlock.Settings.create()
            .mapColor(MapColor.ORANGE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(10.0f).sounds(BlockSoundGroup.CREAKING_HEART),
        "pale_rune_core",
        registerItem = false,
        flammable = false
    ).let { KasimaChiselConversionRegistry.register(Identifier.ofVanilla("creaking_heart"), it) }

    private fun <T : Block> register(
        constructor: (AbstractBlock.Settings) -> T,
        settings: AbstractBlock.Settings,
        name: String,
        registerItem: Boolean,
        flammable: Boolean
    ): T {
        // Register the block and its item.
        val id: Identifier = Identifier.of(KasiMa.id, name)
        val blockKey = RegistryKey.of(RegistryKeys.BLOCK, id)
        val block = constructor.invoke(settings.registryKey(blockKey))

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
        if (registerItem) {
            val itemKey = RegistryKey.of(RegistryKeys.ITEM, id)
            val blockItem = BlockItem(
                block,
                Item.Settings().useBlockPrefixedTranslationKey()
                    .registryKey(itemKey)
            )
            Registry.register(Registries.ITEM, itemKey, blockItem)
        }

        val registered = Registry.register(Registries.BLOCK, blockKey, block)

        if (flammable) {
            flammableBlockRegistry.add(registered, 5, 5)
        }

        return registered
    }

    fun initialize() {}
}