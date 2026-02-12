package redcrafter07.processed.gui

import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.MenuType.MenuSupplier
import net.neoforged.neoforge.network.IContainerFactory
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import java.util.function.Supplier

object ModMenuTypes {
    val MENUS: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, ProcessedMod.ID)

    val POWERED_FURNACE_MENU = register("powered_furnace_menu", ::PoweredFurnaceMenu)
    val SIFTER_MENU = register("sifter_menu", ::SifterMenu)
    val CRUSHER_MENU = register("crusher_menu", ::CrusherMenu)
    val PURIFIER_MENU = register("purifier_menu", ::PurifierMenu)
    val WASHER_MENU = register("washer_menu", ::WasherMenu)
    val LAUNCH_CONTROLLER_MENU = register("launch_controller_menu", ::LaunchControllerMenu)
    val ITEM_HATCH_MENU = register("item_hatch_menu", ::ItemHatchMenu)
    val FLUID_HATCH_MENU = register("fluid_hatch_menu", ::FluidHatchMenu)

    fun <T : AbstractContainerMenu> register(
        name: String,
        supplier: MenuSupplier<T>
    ): DeferredHolder<MenuType<*>, MenuType<T>> {
        return MENUS.register(name, Supplier { MenuType(supplier, FeatureFlags.DEFAULT_FLAGS) })
    }

    fun <T : AbstractContainerMenu> register(
        name: String,
        factory: IContainerFactory<T>
    ): DeferredHolder<MenuType<*>, MenuType<T>> {
        return MENUS.register<MenuType<T>>(name, Supplier { MenuType<T>(factory, FeatureFlags.DEFAULT_FLAGS) })
    }
}