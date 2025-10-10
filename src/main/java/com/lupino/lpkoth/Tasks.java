package com.lupino.lpkoth;

import com.lupino.lpkoth.managers.KothManager;
import com.lupino.lpkoth.managers.MessageManager;
import com.lupino.lpkoth.models.Koth;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Tasks {

    private final LPKoth plugin;

    private final KothManager kothManager;
    private final MessageManager messageManager;

    public Tasks(LPKoth plugin, KothManager kothManager,MessageManager messageManager) {
        this.plugin = plugin;
        this.kothManager = kothManager;
        this.messageManager= messageManager;
    }



public void checkKothsTimings(){

    new BukkitRunnable(){
        @Override
        public void run(){
            List<Koth> koths = kothManager.getKoths();
            for (Koth koth :koths){
                kothManager.checkKothTimings(koth);
            }

        }

    }.runTaskTimer(plugin,0,1200);

}


public void checkRunningKoths(){
    new BukkitRunnable() {
        @Override
        public void run (){
            List<Koth> koths = kothManager.getKoths();

            for (Koth koth :koths){
                if (!koth.isRunning())continue;
                koth.setTime(koth.getTime() - 1);

                BossBar bar = koth.getBossBar();
                if (bar != null) {

                    bar.setTitle(messageManager.replacePlaceholders(koth.getBossBarMessage(),Map.of("time",kothManager.formatTime(koth.getTime()))));

                    double progress = Math.max(0, koth.getTime()) / (double) koth.getDuration();
                    bar.setProgress(progress);
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(!bar.getPlayers().contains(player)){
                        bar.addPlayer(player);

                    }

                    if(!kothManager.isInKoth(player,koth))continue;
                    UUID playerUUID = player.getUniqueId();
                    koth.points.put(playerUUID,koth.points.getOrDefault(playerUUID, 0) + 1);
                    player.sendActionBar(messageManager.replacePlaceholders(koth.getSecondsMessage(), Map.of("time",koth.points.get(playerUUID).toString())));
                }

                if (koth.getTime() <= 0) {
                    kothManager.endKoth(koth);
                }

            }

        }


    }.runTaskTimer(plugin,0,20);

}

}
