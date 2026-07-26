package com.example.examplemod;

import com.example.examplemod.client.Keybinds;
import com.example.examplemod.client.gun.GunInHandRenderer;
import com.example.examplemod.client.particle.BlockDustParticle;
import com.example.examplemod.client.particle.BulletImpactParticle;
import com.example.examplemod.client.particle.BulletTrailParticle;
import com.example.examplemod.client.particle.ImpactBlockParticle;
import com.example.examplemod.client.pose.ModArmPoseParams;
import com.example.examplemod.item.GunItem;
import com.example.examplemod.registry.EntityRegistry;
import com.example.examplemod.menu.GunScreen;
import com.example.examplemod.registry.ItemRegistry;
import com.example.examplemod.registry.MenuRegistry;
import com.example.examplemod.registry.ParticleRegistry;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
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
import net.neoforged.neoforge.client.extensions.IModelProviderExtension;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Mod(value = ExampleMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {
    public ExampleModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        ItemRegistry.GUN.get().geoRenderProvider.setValue(new GeoRenderProvider() {
            private final Supplier<GeoItemRenderer<GunItem>> renderer = Suppliers.memoize(() -> new GunInHandRenderer(new DefaultedItemGeoModel<>(ExampleMod.id("example_revolver"))));

            @Override
            public @Nullable GeoItemRenderer<GunItem> getGeoItemRenderer() {
                return this.renderer.get();
            }
        });
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
    }

    @SubscribeEvent
    static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions pistolPose = new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return ModArmPoseParams.PISTOL.getValue();
            }
        };
        IClientItemExtensions riflePose = new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return ModArmPoseParams.RIFLE.getValue();
            }
        };
        // todo: let gun provide it
        event.registerItem(pistolPose, ItemRegistry.GUN.get(), ItemRegistry.GUN1.get(), ItemRegistry.GUN2.get());
        event.registerItem(riflePose, ItemRegistry.GUN3.get());
    }
}
