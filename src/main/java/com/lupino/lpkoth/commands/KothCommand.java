package com.lupino.lpkoth.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.lupino.lpkoth.Keys;
import com.lupino.lpkoth.LPKoth;
import com.lupino.lpkoth.managers.KothManager;
import com.lupino.lpkoth.managers.MessageManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KothCommand implements CommandExecutor {

    private final LPKoth plugin;
    private final MessageManager messageManager;

    private final KothManager kothManager;
    public Location tempCorner1;
    public Location tempCorner2;

    public String tempName;

    public KothCommand(LPKoth plugin, MessageManager messageManager, KothManager kothManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.kothManager = kothManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length < 1) {
                return true;
            }
            String string = args[0].toLowerCase();
            switch (string) {
                case "create":
                    if(!player.hasPermission("koth.admin")) {
                        player.sendMessage(messageManager.translateColors("&cYou don't have permission"));
                        //messageManager.sendMessageFromConfig(player,"no-permission");
                        return true;
                    }
                    openMenu(player);
                    return true;
                case "reload":
                    if(!player.hasPermission("koth.admin")) {
                        player.sendMessage(messageManager.translateColors("&cYou don't have permission"));
                        //messageManager.sendMessageFromConfig(player,"no-permission");
                        return true;
                    }

                    try {
                        messageManager.reloadMessages();
                        plugin.reloadConfig();
                        kothManager.reload();
                        kothManager.loadKoths();
                        player.sendMessage(messageManager.translateColors("&aPlugin successfully reloaded"));
                        //messageManager.sendMessageFromConfig(player,"commands.reload-command-message");


                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "An error occurred while reloading config.yml.");
                        e.printStackTrace();
                    }
                    return true;
                case "list":
                    if(!player.hasPermission("koth.admin")) {
                        player.sendMessage(messageManager.translateColors("&cYou don't have permission"));
                        //messageManager.sendMessageFromConfig(player,"no-permission");
                        return true;
                    }
                    player.sendMessage(messageManager.translateColors("&bKoths &f"+kothManager.getAllKothNames()));
                    return true;
                case "stop":
                    if(!player.hasPermission("koth.admin")) {
                        player.sendMessage(messageManager.translateColors("&cYou don't have permission"));
                        //messageManager.sendMessageFromConfig(player,"no-permission");
                        return true;
                    }
                    if (args.length<2){
                        player.sendMessage(messageManager.translateColors("&cUsage /koth stop <name>"));

                        return true;
                    }


                    for(String name:kothManager.getAllKothNames()){
                        if(args[1].equalsIgnoreCase(name)){
                            if (!kothManager.getKothFromName(name).isRunning()){
                                player.sendMessage(messageManager.translateColors("&cThis koth is not running"));
                                continue;
                            }
                            kothManager.stopKoth(kothManager.getKothFromName(name));


                            //messageManager.sendMessageFromConfig(player,"commands.delete-arena-command-message",Map.of("arena",name));

                            return true;
                        }


                    }
                    return true;


                case "end":
                    if(!player.hasPermission("koth.admin")) {
                        player.sendMessage(messageManager.translateColors("&cYou don't have permission"));
                        //messageManager.sendMessageFromConfig(player,"no-permission");
                        return true;
                    }
                    if (args.length<2){
                        player.sendMessage(messageManager.translateColors("&cUsage /koth end <name>"));

                        return true;
                    }
                    for(String name:kothManager.getAllKothNames()){
                        if(args[1].equalsIgnoreCase(name)){
                            if (!kothManager.getKothFromName(name).isRunning()){
                                player.sendMessage(messageManager.translateColors("&cThis koth is not running"));
                                continue;
                            }

                            kothManager.endKoth(kothManager.getKothFromName(name));


                            //messageManager.sendMessageFromConfig(player,"commands.delete-arena-command-message",Map.of("arena",name));

                            return true;
                        }


                    }
                    return true;


                case "start":
                    if(!player.hasPermission("koth.admin")) {
                        player.sendMessage(messageManager.translateColors("&cYou don't have permission"));
                        //messageManager.sendMessageFromConfig(player,"no-permission");
                        return true;
                    }
                    if (args.length<2){
                        player.sendMessage(messageManager.translateColors("&cUsage /koth start <name>"));

                        return true;
                    }


                    for(String name:kothManager.getAllKothNames()){
                        if(args[1].equalsIgnoreCase(name)){
                            if (kothManager.getKothFromName(name).isRunning()){
                                player.sendMessage(messageManager.translateColors("&cThis koth is already running"));
                                continue;
                            }
                            kothManager.startKoth(kothManager.getKothFromName(name));


                            //messageManager.sendMessageFromConfig(player,"commands.delete-arena-command-message",Map.of("arena",name));

                            return true;
                        }


                    }
                    return true;
                case "tp":
                    if(!player.hasPermission("koth.admin")) {
                        player.sendMessage(messageManager.translateColors("&cYou don't have permission"));
                        return true;
                    }
                    if (args.length<2){
                        player.sendMessage(messageManager.translateColors("&cUsage /koth tp <name>"));

                        return true;
                    }


                    for(String name:kothManager.getAllKothNames()){
                        if(args[1].equalsIgnoreCase(name)){

                            kothManager.tpToKoth(player,kothManager.getKothFromName(name));


                            //messageManager.sendMessageFromConfig(player,"commands.delete-arena-command-message",Map.of("arena",name));

                            return true;
                        }


                    }
                    return true;
                case "delete":
                    if(!player.hasPermission("koth.admin")) {
                        player.sendMessage(messageManager.translateColors("&cYou don't have permission"));
                        return true;
                    }
                    if (args.length<2){
                        player.sendMessage(messageManager.translateColors("&cUsage /koth delete <name>"));

                        return true;
                    }


                    for(String name:kothManager.getAllKothNames()){
                        if(args[1].equalsIgnoreCase(name)){

                            kothManager.deleteKoth(kothManager.getKothFromName(name));


                            //messageManager.sendMessageFromConfig(player,"commands.delete-arena-command-message",Map.of("arena",name));

                            return true;
                        }


                    }
                    return true;








            }

        }


        return false;
    }
    public void openMenu(Player player){

        Inventory inventory = Bukkit.createInventory(player,45,messageManager.translateColors("&0Koth menu"));
        for (int i = 0; i < 45; i++) {
            if (isBlackEdge(i)) {
                inventory.setItem(i, createItem("GRAY_STAINED_GLASS_PANE", "", null, 0));
            }
        }
        inventory.setItem(22, createItemWithPersi("NAME_TAG", "&bName", getLoreForText("Name", tempName), 0, Keys.NAME_BUTTON));
        inventory.setItem(20, createItemWithPersi("CAULDRON", "&bCorner 1", getLoreForCorner("Corner 1", tempCorner1), 0, Keys.COR1_BUTTON));
        inventory.setItem(24, createItemWithPersi("CAULDRON", "&bCorner 2", getLoreForCorner("Corner 2", tempCorner2), 0, Keys.COR2_BUTTON));
        inventory.setItem(40, createItemWithPersi("DIAMOND", "&aSave", messageManager.translateLore(List.of("&7Click to save koth.")), 0, Keys.SAVE_BUTTON));


        player.openInventory(inventory);


    }
    private List<String> getLoreForCorner(String label, Location loc) {
        if (loc == null) {
            return messageManager.translateLore(List.of("&7" + label + ": &cNot set"));
        }

        return messageManager.translateLore(List.of("&7"+label+":","&7x: "+loc.getX(),"&7y: "+loc.getY(),"&7z: "+loc.getX()));
    }

    private List<String> getLoreForText(String label, String value) {
        if (value == null || value.isBlank()) {
            return messageManager.translateLore(List.of("&7" + label + ": &cNot set"));
        }
        return messageManager.translateLore(List.of("&7" + label + ": &7" + value));
    }


    private boolean isBlackEdge(int index) {

        if ( index < 9) return true;

        if (index > 35 && index < 45) return true;

        if (index % 9 == 0) return true;

        if ((index + 1) % 9 == 0) return true;



        return false;
    }

    public ItemStack createItemWithPersi(String material, String name, List<String> lore, int modelData, NamespacedKey key) {
        ItemStack item = createItem(material, name, lore, modelData);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createItem(String material, String name, List<String> lore, int modelData) {
        ItemStack item;

        if (material.startsWith("texture-")) {
            item = new ItemStack(Material.PLAYER_HEAD);
            try {
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                if (skullMeta != null) {
                    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

                    PlayerTextures textures = profile.getTextures();

                    try {
                        URL urlObject = new URL("https://textures.minecraft.net/texture/" + material.substring(8));
                        textures.setSkin(urlObject);
                    } catch (MalformedURLException ignored) {}

                    profile.setTextures(textures);
                    skullMeta.setPlayerProfile(profile);

                    if (name != null && !name.isEmpty()) {
                        skullMeta.setDisplayName(messageManager.translateColors(name));
                    }
                    if (lore != null && !lore.isEmpty()) {
                        skullMeta.setLore(lore);
                    }
                    if (modelData > 0) {
                        skullMeta.setCustomModelData(modelData);
                    }

                    item.setItemMeta(skullMeta);
                }
            } catch (ClassCastException ignored) {}
        } else {
            Material mat = Material.getMaterial(material.toUpperCase());
            if (mat == null) throw new IllegalArgumentException("Invalid material: " + material);

            item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                if (name != null && !name.isEmpty()) {
                    meta.setDisplayName(messageManager.translateColors(name));
                }
                if (lore != null && !lore.isEmpty()) {
                    meta.setLore(lore);
                }
                if (modelData > 0) {
                    meta.setCustomModelData(modelData);
                }

                item.setItemMeta(meta);
            }
        }

        return item;
    }
}
