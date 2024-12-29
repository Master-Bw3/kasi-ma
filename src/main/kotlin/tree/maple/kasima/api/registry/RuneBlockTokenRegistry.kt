package tree.maple.kasima.api.registry

import com.mojang.serialization.Lifecycle
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.block.Block
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.spellEngine.compiler.Token
import tree.maple.kasima.spellEngine.types.SpellFunction
import java.util.function.Supplier


private val REGISTRY_KEY: RegistryKey<Registry<RuneRegistryEntry>> =
    RegistryKey.ofRegistry(KasiMa.id("rune"))

private val REGISTRY: Registry<RuneRegistryEntry> = FabricRegistryBuilder.from(
    SimpleRegistry(
        REGISTRY_KEY,
        Lifecycle.stable()
    )
).buildAndRegister()


object RuneBlockTokenRegistry : Registry<RuneRegistryEntry> by REGISTRY {
    fun register(token: Token, function: SpellFunction?, block: Supplier<Block>, material: Supplier<Block>, id: Identifier): Token {
        return Registry.register(
            REGISTRY,
            id,
            RuneRegistryEntry(token, function, block, material)
        ).token
    }

    fun initialize() {}
}

data class RuneRegistryEntry(val token: Token, val function: SpellFunction?, val block: Supplier<Block>, val material: Supplier<Block>)