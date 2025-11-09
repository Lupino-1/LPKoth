package dev.lupino1.lpkoth.listeners;

import dev.lupino1.lpkoth.LPKoth;
import dev.lupino1.lpkoth.commands.KothCommand;
import dev.lupino1.lpkoth.enums.InputState;
import dev.lupino1.lpkoth.managers.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class PlayerInteractListener implements Listener {
    private final LPKoth plugin;
    private final MessageManager messageManager;
    private final MenuListener menuListener;
    private final KothCommand kothCommand;

    public PlayerInteractListener(LPKoth plugin, MessageManager messageManager, MenuListener menuListener, KothCommand kothCommand) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.menuListener = menuListener;
        this.kothCommand = kothCommand;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();
        if (event.getHand()!= EquipmentSlot.HAND||event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_AIR)return;
        UUID uuid = player.getUniqueId();
        if (!menuListener.waitingForInput.containsKey(uuid)) return;
        InputState state = menuListener.waitingForInput.remove(uuid);
        Location location = event.getClickedBlock().getLocation();
        if (location==null)return;
        event.setCancelled(true);
        String x = String.valueOf(location.getX());
        String y = String.valueOf(location.getY());
        String z = String.valueOf(location.getZ());

        if (state == InputState.COR1) {
            kothCommand.tempCorner1 = location;
            player.sendMessage(messageManager.translateColors( "&aCorner1 set to: &fX: &#00fff5"+x+" &fY: &#00fff5"+y+" &fZ: &#00fff5"+z));
        } else if (state == InputState.COR2) {
            kothCommand.tempCorner2 = location;
            player.sendMessage(messageManager.translateColors( "&aCorner2 set to: &fX: &#00fff5"+x+" &fY: &#00fff5"+y+" &fZ: &#00fff5"+z));
        }
        kothCommand.openMenu(player);
    }
}
