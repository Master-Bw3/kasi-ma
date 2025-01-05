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
import tree.maple.kasima.spellEngine.operators.Operator
import java.util.function.Supplier


private val REGISTRY_KEY: RegistryKey<Registry<Operator>> =
    RegistryKey.ofRegistry(KasiMa.id("operator"))

private val REGISTRY: Registry<Operator> = FabricRegistryBuilder.from(
    SimpleRegistry(
        REGISTRY_KEY,
        Lifecycle.stable()
    )
).buildAndRegister()


object OperatorRegistry : Registry<Operator> by REGISTRY {
    fun register(operator: Operator, id: Identifier): Operator {
        return Registry.register(
            REGISTRY,
            id,
            operator
        )
    }

    fun initialize() {}
}
