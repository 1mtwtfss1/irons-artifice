package com.example.examplemod.registry;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.Bullet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EntityRegistry {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(ExampleMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Bullet>> BULLET = ENTITY_TYPES.registerEntityType(
            "bullet",
            Bullet::new,
            MobCategory.MISC,
            builder->builder.sized(0.25f,0.25f)
    );

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
