package dev.lupino1.lpkoth;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class Keys {
    public static NamespacedKey COR1_BUTTON;
    public static NamespacedKey COR2_BUTTON;
    public static NamespacedKey SAVE_BUTTON;
    public static NamespacedKey NAME_BUTTON;
    public static void initialize(Plugin plugin) {
        COR1_BUTTON = new NamespacedKey(plugin,"cor1_button");
        COR2_BUTTON = new NamespacedKey(plugin,"cor2_button");
        SAVE_BUTTON = new NamespacedKey(plugin,"save_button");
        NAME_BUTTON = new NamespacedKey(plugin,"name_button");
    }
}
