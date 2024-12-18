package tree.maple.kasima.items

import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa


object KasimaItemRegistry {

    val CHISEL = register(::Chisel, Item.Settings(), "chisel")

    fun register(constructor: (Item.Settings) -> Item, settings: Item.Settings, name: String): Item {
        val id = KasiMa.id(name)

        val itemKey = RegistryKey.of(RegistryKeys.ITEM, id)
        val item = constructor.invoke(settings.registryKey(itemKey))

        return Registry.register(Registries.ITEM, itemKey, item)
    }

    fun initialize() {}
}