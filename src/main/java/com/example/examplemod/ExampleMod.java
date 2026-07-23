package com.example.examplemod;

import com.example.examplemod.network.PayloadRegistry;
import com.example.examplemod.registry.DataAttachmentRegistry;
import com.example.examplemod.registry.DataComponentRegistry;
import com.example.examplemod.registry.EntityRegistry;
import com.example.examplemod.registry.ItemRegistry;
import com.example.examplemod.registry.MenuRegistry;
import com.example.examplemod.registry.ParticleRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemRegistry.GUN1.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for(var i : ItemRegistry.ITEMS.getEntries()){
                    output.accept(i.get());
                }
            }).build());

    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        ItemRegistry.register(modEventBus);
        DataComponentRegistry.register(modEventBus);
        EntityRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);
        DataAttachmentRegistry.register(modEventBus);
        ParticleRegistry.register(modEventBus);
        modEventBus.addListener(PayloadRegistry::register);
        CREATIVE_MODE_TABS.register(modEventBus);

//        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

}
