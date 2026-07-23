package com.example.examplemod.events;

import com.example.examplemod.entity.Bullet;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class SeverEvents {


    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        //    @SubscribeEvent
//    public static void onDamage(LivingIncomingDamageEvent event) {
//        // fixme: https://github.com/neoforged/NeoForge/issues/3348
//        if (event.getSource().getDirectEntity() instanceof Bullet) {
//            event.getContainer().setPostAttackInvulnerabilityTicks(0);
//        }
//    }
        if (event.getSource().getDirectEntity() instanceof Bullet) {
            event.getEntity().invulnerableTime = 0;
        }
    }
}
