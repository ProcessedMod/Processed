package redcrafter07.processed.entity

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import java.util.function.Supplier

object ModEntities {
    val ENTITIES: DeferredRegister<EntityType<*>> =
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ProcessedMod.ID)

    val ROCKET = register("rocket", ::RocketEntity, MobCategory.MISC) {
        canSpawnFarFromPlayer()
        fireImmune()
        sized(3f, 4f)
    }

    fun <T : Entity> register(
        name: String, factory: EntityType.EntityFactory<T>, cat: MobCategory, entity: EntityType.Builder<T>.() -> Unit
    ): DeferredHolder<EntityType<*>, EntityType<T>> = ENTITIES.register(name, Supplier {
        val builder = EntityType.Builder.of(factory, cat)
        entity(builder)
        builder.build(name)
    })
}