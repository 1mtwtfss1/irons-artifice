package io.redspace.irons_artifice.utils;

import io.redspace.irons_artifice.data.ComponentType;
import io.redspace.irons_artifice.data.ValueModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jspecify.annotations.Nullable;

public class Utils {

    public static Vec3 reflect(Vec3 direction, Vec3 normal) {
        return direction.subtract(normal.scale(2 * normal.dot(direction)));
    }

    public static Component formatValueModifierDescription(ValueModifier valueModifier, ComponentType<?> componentType) {
        return formatValueModifierDescription(valueModifier, Component.translatable(String.format("%s.component_type.%s", componentType.getName().getNamespace(), componentType.getName().getPath())));
    }

    public static Component getComponentTranslate(ComponentType<?> componentType) {
        return Component.translatable(String.format("%s.component_type.%s", componentType.getName().getNamespace(), componentType.getName().getPath()));
    }

    public static Component formatValueModifierDescription(ValueModifier valueModifier, Component valueName) {
        double value = valueModifier.amount();
        String identifier = value < 0 ? "minus" : "plus";
        if (valueModifier.operation() != ValueModifier.Operation.ADD) {
            identifier += "_percent";
            value = (1 + value) * 100;
        } else {
            value = Math.abs(value);
        }
        int color;
        if (valueModifier.type() == ValueModifier.Type.NEUTRAL) {
            color = ChatFormatting.YELLOW.getColor();
        } else {
            color = valueModifier.type() == ValueModifier.Type.BENEFICIAL ^ valueModifier.amount() < 0 ?
                    ChatFormatting.GREEN.getColor() : ChatFormatting.RED.getColor();
        }
        return Component.translatable(String.format("irons_artifice.value_modifier.%s", identifier), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value), valueName).withColor(color);
    }

    public static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force) {
        level.getServer().getPlayerList().getPlayers().forEach(player -> ((ServerLevel) level).sendParticles(player, particle, force, force, x, y, z, count, deltaX, deltaY, deltaZ, speed));
    }

    public static Vec3 randomVec3(double scale) {
        return new Vec3(
                (Math.random() - 0.5) * 2 * scale,
                (Math.random() - 0.5) * 2 * scale,
                (Math.random() - 0.5) * 2 * scale
        );
    }

    public static float triangleInterpolate(float x, float start, float peak, float end) {
        if (x <= start || x >= end) {
            return 0.0f;
        }
        if (x <= peak) {
            return (x - start) / (peak - start);
        } else {
            return (end - x) / (end - peak);
        }
    }

    public static double mapClamped(
            double value,
            double inputMin,
            double inputMax,
            double outputMin,
            double outputMax
    ) {
        return Mth.lerp(
                Mth.clamp((value - inputMin) / (inputMax - inputMin), 0, 1),
                outputMin,
                outputMax
        );
    }

    public static boolean canHarm(@Nullable Entity attacker, @Nullable Entity target) {
        if (attacker == null || target == null || attacker == target) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (target.isAlliedTo(attacker) || attacker.isAlliedTo(target)) {
            return false;
        }
        if (target instanceof Player player && attacker instanceof Player player1 && !player1.canHarmPlayer(player)) {
            return false;
        }
        return true;
    }

    public static boolean hasLineOfSight(Entity a, Entity b) {
        return a.level().clip(new ClipContext(a.getBoundingBox().getCenter(), b.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty())).getType() == HitResult.Type.MISS;
    }
}
