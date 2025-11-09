package com.lupino.lpkoth.listeners;

import com.lupino.lpkoth.Keys;
import com.lupino.lpkoth.LPKoth;
import com.lupino.lpkoth.commands.KothCommand;
import com.lupino.lpkoth.enums.InputState;
import com.lupino.lpkoth.managers.KothManager;
import com.lupino.lpkoth.managers.MessageManager;
import com.lupino.lpkoth.models.Koth;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MenuListener implements Listener {

    private final KothCommand kothCommand;
    private final MessageManager messageManager;

    private final KothManager kothManager;

    private final LPKoth plugin;

    public final Map<UUID, InputState> waitingForInput = new HashMap<>();
    public MenuListener(KothCommand kothCommand, MessageManager messageManager, KothManager kothManager, LPKoth plugin) {
        this.kothCommand = kothCommand;
        this.messageManager = messageManager;
        this.kothManager = kothManager;
        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        ItemStack hand = event.getCurrentItem();
        Player player  = (Player) event.getWhoClicked();

        if(event.getView().getTitle().equalsIgnoreCase(messageManager.translateColors("&0Koth menu"))){
            event.setCancelled(true);

            if(hasPersi(hand, Keys.COR1_BUTTON)){
                player.closeInventory();
                player.sendMessage(messageManager.translateColors("&7Click on corner1 block."));
                waitingForInput.put(player.getUniqueId(), InputState.COR1);
                return;
            }

            if(hasPersi(hand, Keys.COR2_BUTTON)){
                player.closeInventory();
                player.sendMessage(messageManager.translateColors("&7Click on corner2 block."));
                waitingForInput.put(player.getUniqueId(), InputState.COR2);
                return;
            }

            if(hasPersi(hand, Keys.NAME_BUTTON)){
                player.closeInventory();
                player.sendMessage(messageManager.translateColors("&7Write name in chat:"));
                waitingForInput.put(player.getUniqueId(), InputState.NAME);
                return;
            }

            if(hasPersi(hand, Keys.SAVE_BUTTON)){

                if (!isAllTempArenaDataSet()) {
                    player.sendMessage(messageManager.translateColors("&cFirst set all values!"));
                    return;
                }

                if(!areAllLocationsInSameWorld()){
                    player.sendMessage(messageManager.translateColors("&cAll locations need to be in the same world!"));
                    return;
                }

                int duration = plugin.getConfig().getInt("default-duration",300);
                kothManager.saveKothToConfig(new Koth(kothCommand.tempName,kothCommand.tempCorner1,kothCommand.tempCorner2,null,null,null,duration));
                player.sendMessage(messageManager.translateColors("&aKoth successfully loaded! For more configuration use config files."));
                resetTempArenaDataSet();
                player.closeInventory();
            }
        }
    }

    public boolean areAllLocationsInSameWorld() {
        World world = kothCommand.tempCorner1.getWorld();

        return world != null && world.equals(kothCommand.tempCorner2.getWorld());
    }
    public void resetTempArenaDataSet() {
        kothCommand.tempName = null;
        kothCommand.tempCorner1 = null ;
        kothCommand.tempCorner2 = null;
    }
    public boolean isAllTempArenaDataSet() {
        return kothCommand.tempName != null &&
                kothCommand.tempCorner1 != null &&
                kothCommand.tempCorner2 != null;
    }
    public String getPersiValue(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public boolean hasPersi(ItemStack hand,NamespacedKey key){
        if (hand != null && hand.hasItemMeta() && Objects.requireNonNull(hand.getItemMeta()).getPersistentDataContainer().has(key)) {
            return true;
        }
        return false;
    }
}
