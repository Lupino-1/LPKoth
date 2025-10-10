package com.lupino.lpkoth.listeners;

import com.lupino.lpkoth.LPKoth;
import com.lupino.lpkoth.commands.KothCommand;
import com.lupino.lpkoth.enums.InputState;
import com.lupino.lpkoth.managers.KothManager;
import com.lupino.lpkoth.managers.MessageManager;
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

        // Zákaz mezer
        if (message.contains(" ")) {
            player.sendMessage("§cOnly one word without spaces!");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (state == InputState.NAME) {
                // Validace názvu
                if (!message.matches("^[a-zA-Z0-9]+$")) {
                    player.sendMessage("§cName can only contain letters and numbers (a-z, A-Z, 0-9)!");
                    return;
                }

                // Zkontroluj, jestli jméno už existuje
                boolean nameTaken = kothManager.getAllKothNames().stream()
                        .anyMatch(existing -> existing.equalsIgnoreCase(message));

                if (nameTaken) {
                    player.sendMessage("§cKoth with this name already exists!");
                    return;
                }

                menuListener.waitingForInput.remove(uuid);
                kothCommand.tempName = message;
                player.sendMessage("§aName set to: §f" + message);
                kothCommand.openMenu(player);
            }
        });
    }

}
