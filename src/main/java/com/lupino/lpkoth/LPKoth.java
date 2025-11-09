package com.lupino.lpkoth;

import com.lupino.lpkoth.commands.KothCommand;
import com.lupino.lpkoth.listeners.ChatListener;
import com.lupino.lpkoth.listeners.MenuListener;
import com.lupino.lpkoth.listeners.PlayerInteractListener;
import com.lupino.lpkoth.listeners.PlayerLeaveListener;
import com.lupino.lpkoth.managers.KothManager;
import com.lupino.lpkoth.managers.MessageManager;
import com.lupino.lpkoth.tabcompleters.KothCompleter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class LPKoth extends JavaPlugin {

    private MessageManager messageManager;
    private KothManager kothManager;

    @Override
    public void onEnable() {
    Keys.initialize(this);
    saveDefaultConfig();

        messageManager = new MessageManager(this);
        kothManager = new KothManager(this,messageManager);
        KothCommand kothCommand = new KothCommand(this,messageManager,kothManager);
        MenuListener menuListener = new MenuListener(kothCommand,messageManager,kothManager,this);

        getServer().getPluginManager().registerEvents(new ChatListener(this,menuListener,kothCommand,messageManager,kothManager), this);
        getServer().getPluginManager().registerEvents(menuListener,this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this,messageManager,menuListener,kothCommand),this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this,menuListener),this);

        getCommand("koth").setExecutor(kothCommand);
        getCommand("koth").setTabCompleter(new KothCompleter(kothManager));

        Tasks tasks = new Tasks(this,kothManager,messageManager);
        tasks.checkKothsTimings();
        tasks.checkRunningKoths();
        kothManager.loadKoths();
        Bukkit.getConsoleSender().sendMessage(messageManager.translateColors("&8==================================="));
        Bukkit.getConsoleSender().sendMessage(messageManager.translateColors("&aPlugin successfully enabled"));
        Bukkit.getConsoleSender().sendMessage(messageManager.translateColors("&7Author: &fLupino"));
        Bukkit.getConsoleSender().sendMessage(messageManager.translateColors("&8==================================="));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(messageManager.translateColors("&8==================================="));
        Bukkit.getConsoleSender().sendMessage(messageManager.translateColors("&aPlugin successfully disabled"));
        Bukkit.getConsoleSender().sendMessage(messageManager.translateColors("&8==================================="));
    }
}
