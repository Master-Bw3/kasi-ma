package tree.maple.kasima.api.registry

import com.mojang.serialization.Lifecycle
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.spellEngine.compiler.Token


private val REGISTRY_KEY: RegistryKey<Registry<TokenEntry>> =
    RegistryKey.ofRegistry(KasiMa.id("token"))

private val REGISTRY: Registry<TokenEntry> = FabricRegistryBuilder.from(
    SimpleRegistry(
        REGISTRY_KEY,
        Lifecycle.stable()
    )
).buildAndRegister()


object BlockTokenRegistry : Registry<TokenEntry> by REGISTRY {
    fun register(token: Token, material: Identifier, id: Identifier): TokenEntry {
        return Registry.register(
            REGISTRY,
            id,
            TokenEntry(token, material)
        )
    }

    fun initialize() {}
}

data class TokenEntry(val token: Token, val material: Identifier)