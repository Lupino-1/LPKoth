package dev.lupino1.lpkoth.listeners;

import dev.lupino1.lpkoth.LPKoth;
import dev.lupino1.lpkoth.commands.KothCommand;
import dev.lupino1.lpkoth.enums.InputState;
import dev.lupino1.lpkoth.managers.KothManager;
import dev.lupino1.lpkoth.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {
    private final LPKoth plugin;
    private final MenuListener menuListener;
    private final KothCommand kothCommand;
    private final MessageManager messageManager;
    private final KothManager kothManager;
    public ChatListener(LPKoth plugin, MenuListener menuListener, KothCommand kothCommand, MessageManager messageManager, KothManager kothManager) {
        this.plugin = plugin;
        this.menuListener = menuListener;
        this.kothCommand = kothCommand;
        this.messageManager = messageManager;
        this.kothManager = kothManager;
    }
    @EventHandler
    public void onChatInput(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (!menuListener.waitingForInput.containsKey(uuid)) return;

        InputState state = menuListener.waitingForInput.get(uuid);
        if (state != InputState.NAME)return;
        String message = event.getMessage().trim();
        event.setCancelled(true);

        if (message.contains(" ")) {
            player.sendMessage(messageManager.translateColors("&cOnly one word without spaces!"));
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (state == InputState.NAME) {
                if (!message.matches("^[a-zA-Z0-9]+$")) {
                    player.sendMessage(messageManager.translateColors("&cName can only contain letters and numbers (a-z, A-Z, 0-9)!"));
                    return;
                }
                boolean nameTaken = kothManager.getAllKothNames().stream().anyMatch(existing -> existing.equalsIgnoreCase(message));
                if (nameTaken) {
                    player.sendMessage(messageManager.translateColors("&cKoth with this name already exists!"));
                    return;
                }
                menuListener.waitingForInput.remove(uuid);
                kothCommand.tempName = message;
                player.sendMessage(messageManager.translateColors("&aName set to: &f" + message));
                kothCommand.openMenu(player);
            }
        });
    }

}
