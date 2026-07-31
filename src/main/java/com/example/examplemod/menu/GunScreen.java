package com.example.examplemod.menu;

import com.example.examplemod.ExampleMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GunScreen extends AbstractContainerScreen<GunMenu> {
    private static final Identifier TEXTURE = ExampleMod.id("textures/gui/gun_modifier_screen.png");

    public GunScreen(GunMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 159);
        this.inventoryLabelY = this.imageHeight - 94;
        for (var slot : menu.getModifierSlots()) {
            this.addRenderableOnly((graphics, mx, my, a) ->
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("container/slot"), leftPos + slot.x - 1, topPos + slot.y - 1, 18, 18)
            );
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
