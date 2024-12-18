package tree.maple.kasima.blocks

import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import tree.maple.kasima.KasiMa


object KasimaBlockTags {
    val CHISELABLE: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, KasiMa.id("chiselable"))

}