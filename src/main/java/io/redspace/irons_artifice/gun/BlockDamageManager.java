package io.redspace.irons_artifice.gun;

import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.entity.Bullet;
import com.google.common.eventbus.Subscribe;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@EventBusSubscriber
public class BlockDamageManager {


    record BlockHealth(float health, float maxHealth, long lastUpdated, int ownerId) {
    }

    // todo: wipe pos cache on block break
    private static final BlockDamageManager INSTANCE = new BlockDamageManager();
    private final Map<ResourceKey<Level>, Map<BlockPos, BlockHealth>> damageManagersByLevel;

    public BlockDamageManager() {
        this.damageManagersByLevel = new HashMap<>();
    }

    private Map<BlockPos, BlockHealth> resolveManager(Level level) {
        return damageManagersByLevel.computeIfAbsent(level.dimension(), key -> new HashMap<>());
    }
    // fixme: no generic block break event??
//    @SubscribeEvent
//    public static void onBlockBreak(LivingDestroyBlockEvent event){}

    @SubscribeEvent
    public static void blockDamageManagerTick(ServerTickEvent.Post event) {
        for (Level level : event.getServer().getAllLevels()) {
            long gameTime = level.getGameTime();
            var manager = INSTANCE.resolveManager(level);
            if (gameTime % 100 == 0) {
                for (Iterator<Map.Entry<BlockPos, BlockHealth>> it = manager.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<BlockPos, BlockHealth> entry = it.next();
                    // 400 is time used on client
                    if (entry.getValue().lastUpdated < gameTime - 400) {
                        it.remove();
                        level.destroyBlockProgress(entry.getValue().ownerId(), entry.getKey(), -1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void clearDamageManager(ServerStoppedEvent event) {
        INSTANCE.damageManagersByLevel.clear();
    }

    /**
     * @return whether block was destroyed
     */
    public static boolean applyDamage(ServerLevel level, BlockPos pos, BlockState state, float damage, Bullet bullet) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed < 0) {
            return false;
        }
        float blockMaxHealth = (destroySpeed + state.getBlock().getExplosionResistance()) * 5;
        // todo: block specific multipliers, config, blacklist, etc
        if (state.is(Blocks.TARGET)) {
            return false;
        }

        float blockCurrentHealth;
        var manager = INSTANCE.resolveManager(level);
        int id = bullet.getId();
        if (manager.containsKey(pos)) {
            BlockHealth health = manager.get(pos);
            blockCurrentHealth = health.health;
            id = health.ownerId;
        } else {
            blockCurrentHealth = blockMaxHealth;
        }
        float lastProgress = 1 - blockCurrentHealth / blockMaxHealth;
        blockCurrentHealth -= damage;
        float destroyProgress = 1 - blockCurrentHealth / blockMaxHealth;
        if (destroyProgress >= 1) {
            // todo: checks and stuff? events? drop modifiers?
            level.destroyBlockProgress(id, pos, -1);
            level.destroyBlock(pos, false);
            manager.remove(pos);
            return true;
        } else {
            manager.put(pos, new BlockHealth(blockCurrentHealth, blockMaxHealth, level.getGameTime(), id));
            int stage = (int) (destroyProgress * 10);
            int lastStage = (int) (lastProgress * 10);
            if (stage != lastStage) {
                level.destroyBlockProgress(id, pos, (int) (destroyProgress * 10));
            }
            return false;
        }
    }
}
