package com.example.examplemod;

import com.example.examplemod.client.Keybinds;
import com.example.examplemod.client.gun.GunInHandRenderer;
import com.example.examplemod.client.particle.BlockDustParticle;
import com.example.examplemod.client.particle.BulletImpactParticle;
import com.example.examplemod.client.particle.BulletTrailParticle;
import com.example.examplemod.client.particle.ImpactBlockParticle;
import com.example.examplemod.client.particle.MuzzleFlashLargeParticle;
import com.example.examplemod.client.pose.GunArmPoses;
import com.example.examplemod.gun.ArmPoseKind;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.menu.GunScreen;
import com.example.examplemod.registry.EntityRegistry;
import com.example.examplemod.registry.ItemRegistry;
import com.example.examplemod.registry.MenuRegistry;
import com.example.examplemod.registry.ParticleRegistry;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.google.common.base.Suppliers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(value = ExampleMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {
    public ExampleModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        for (GunItem gun : guns()) {
            Identifier modelId = gun.getGeoModelId();
            if (modelId == null) {
                continue;
            }
            gun.geoRenderProvider.setValue(new GeoRenderProvider() {
                private final Supplier<GeoItemRenderer<GunItem>> renderer =
                        Suppliers.memoize(() -> new GunInHandRenderer(new DefaultedItemGeoModel<>(modelId)));

                @Override
                public @Nullable GeoItemRenderer<GunItem> getGeoItemRenderer() {
                    return this.renderer.get();
                }
            });
        }
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.GUN_MENU.get(), GunScreen::new);
    }

    @SubscribeEvent
    static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.BULLET.get(), NoopRenderer::new);
    }

    @SubscribeEvent
    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(Keybinds.OPEN_MODIFIER_MENU);
    }

    @SubscribeEvent
    static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ParticleRegistry.BLOCK_IMPACT.get(), new ImpactBlockParticle.Provider());
        event.registerSpriteSet(ParticleRegistry.BLOCK_DUST.get(), BlockDustParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.BULLET_TRAIL.get(), BulletTrailParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.BULLET_IMPACT.get(), BulletImpactParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.MUZZLE_FLASH_LARGE.get(), MuzzleFlashLargeParticle.Provider::new);
    }

    @SubscribeEvent
    static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions pistolPose = armPoseExtension(GunArmPoses.PISTOL.getValue());
        IClientItemExtensions riflePose = armPoseExtension(GunArmPoses.RIFLE.getValue());

        List<Item> pistols = new ArrayList<>();
        List<Item> rifles = new ArrayList<>();
        for (GunItem gun : guns()) {
            if (gun.getArmPoseKind() == ArmPoseKind.PISTOL) {
                pistols.add(gun);
            } else {
                rifles.add(gun);
            }
        }
        if (!pistols.isEmpty()) {
            event.registerItem(pistolPose, pistols.toArray(Item[]::new));
        }
        if (!rifles.isEmpty()) {
            event.registerItem(riflePose, rifles.toArray(Item[]::new));
        }
    }

    private static IClientItemExtensions armPoseExtension(HumanoidModel.ArmPose pose) {
        return new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return pose;
            }
        };
    }

    private static List<GunItem> guns() {
        List<GunItem> guns = new ArrayList<>();
        for (DeferredHolder<Item, ? extends Item> holder : ItemRegistry.ITEMS.getEntries()) {
            if (holder.get() instanceof GunItem gunItem) {
                guns.add(gunItem);
            }
        }
        return guns;
    }
}
