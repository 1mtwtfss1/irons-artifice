package com.example.examplemod.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {

    public static final KeyMapping OPEN_MODIFIER_MENU = new KeyMapping(
            "key.examplemod.open_modifier_menu",
            GLFW.GLFW_KEY_G,
            KeyMapping.Category.GAMEPLAY
    );

    public static final KeyMapping RELOAD = new KeyMapping(
            "key.examplemod.reload",
            GLFW.GLFW_KEY_R,
            KeyMapping.Category.GAMEPLAY
    );

    private Keybinds() {}
}
