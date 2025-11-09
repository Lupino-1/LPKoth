package com.lupino.lpkoth.managers;


import com.lupino.lpkoth.models.Koth;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KothManager {
    private final List<Koth> koths = new ArrayList<>();
    private final File dataFile;
    private FileConfiguration dataConfig;

    private final JavaPlugin plugin;

    private final MessageManager messageManager;

    public KothManager(JavaPlugin plugin,MessageManager messageManager) {
        dataFile = new File(plugin.getDataFolder(), "koths.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                plugin.saveResource("koths.yml",false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.messageManager= messageManager;
        this.plugin= plugin;
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    public void loadKoths() {
        koths.clear();
        ConfigurationSection regionSection = dataConfig.getConfigurationSection("koths");
        if (regionSection == null) return;

        for (String name : regionSection.getKeys(false)) {
            ConfigurationSection section = regionSection.getConfigurationSection(name);
            if (section == null) continue;

            Location corner1 = section.getLocation("corner1");
            Location corner2 = section.getLocation("corner2");
            Integer duration = section.getInt("duration",300);
            List<String> commands = section.getStringList("commands");
            List<String> timings = section.getStringList("timings");
            List<String> messages = section.getStringList("messages");
            String color = section.getString("bossbar-color");
            String startMessage = section.getString("start-message");
            String bossBarMessage = section.getString("bossbar-message");
            String secondsMessage = section.getString("seconds-message");
            if (corner1 == null || corner2 == null||duration == null) {
                Bukkit.getLogger().warning("Koth " + name + " is missing required data.");
                continue;
            }
            if (!section.getBoolean("enable")){
                continue;
            }

            Koth koth = new Koth(name,corner1,corner2,commands,messages,timings,duration);
            koths.add(koth);
            koth.setBossBarColor(color);
            koth.setStartMessage(startMessage);
            koth.setBossBarMessage(bossBarMessage);
            koth.setSecondsMessage(secondsMessage);
        }
    }

    public void saveKothToConfig(Koth koth){
        String path = "koths."+koth.getName();
        dataConfig.set(path+".corner1",koth.getCorner1());
        dataConfig.set(path+".corner2",koth.getCorner2());
        dataConfig.set(path+".duration",koth.getDuration());
        dataConfig.set(path+".commands",List.of("10 give %player_position_1% diamond"));
        dataConfig.set(path+".timings",List.of("22:00"));
        dataConfig.set(path+".enable",true);
        dataConfig.set(path+".start-message","&9&lKoth started");
        dataConfig.set(path+".bossbar-message","&fKoth ends in &9%time%");
        dataConfig.set(path+".bossbar-color","YELLOW");
        dataConfig.set(path+".seconds-message","&fYou've been in koth for &9%time%&f s.");
        dataConfig.set(path+".messages",List.of("%player_position_1% &fYou were &9first&f in koth congratulations.", "%player_position_2% &fYou were &9first&f in koth congratulations.", "%player_position_3% &fYou were &9first&f in koth congratulations."));
        saveData();
        koths.add(koth);
    }

    public List<Koth> getKoths() {
        return koths;
    }


    public void deleteKoth(Koth koth){
        if (koth == null) return;
        koths.remove(koth);
        dataConfig.set("koths." + koth.getName(), null);
        saveData();
    }

    public List<String> getAllKothNames() {
        List<String> list = new ArrayList<>();
        for(Koth koth:getKoths()){
            String name= koth.getName();
            list.add(name);
        }
        return list;
    }

    public Koth getKothFromName(String name){
        for(Koth koth:koths){
            if(koth.getName().equalsIgnoreCase(name)){
                return koth;
            }
        }
        return null;
    }
    public void startKoth(Koth koth){
        if (koth == null)return;
        koth.points.clear();
        koth.setRunning(true);
        BossBar bar = koth.getBossBar();
        for (Player player : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(player);
        }
        bar.setVisible(true);
        koth.setTime(koth.getDuration());
        MessageManager.broadcastMessage(messageManager.translateColors(koth.getStartMessage()));
    }
    public String replacePlayerPositionPlaceholders(String message, Map<UUID, Integer> points) {
        Pattern pattern = Pattern.compile("%player_position_(\\d+)%");
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();

        List<UUID> sortedPlayers = points.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        while (matcher.find()) {
            int position = Integer.parseInt(matcher.group(1)) - 1;
            String replacement;

            if (position >= 0 && position < sortedPlayers.size()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(sortedPlayers.get(position));
                replacement = player.getName() != null ? player.getName() : "Unknown";
            } else {
                replacement = "None";
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    public void parseAndExecuteChanceCommand(String input) {
        String[] parts = input.trim().split(" ", 2);

        double chance = 100.0;
        String command = input;

        try {
            chance = Double.parseDouble(parts[0]);
            if (parts.length < 2) {

                return;
            }
            command = parts[1];
        } catch (NumberFormatException ignored) {}

        if (Math.random() <= (chance / 100.0)) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        } else {
        }
    }

    public void parseAndSendMessage(String input) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split(" ", 2);
        if (parts.length < 2) {
            Bukkit.getLogger().warning("[KOTH] Invalid message format: '" + input + "'");
            return;
        }

        String name = parts[0];
        String message = parts[1];

        Player player = Bukkit.getPlayer(name);
        if (player == null) return;

        MessageManager.sendMessage(player, messageManager.translateColors(message));
    }

    public void tpToKoth(Player player,Koth koth){
        if (koth == null)return;
        player.teleport(koth.getCorner1());

    }
    public void stopKoth(Koth koth){
        if (koth == null)return;
        koth.points.clear();
        koth.setRunning(false);
        BossBar bar = koth.getBossBar();
        if (bar != null) bar.setVisible(false);
        koth.setTime(koth.getDuration());
    }
    public void endKoth(Koth koth){
        if (koth == null)return;

        koth.setRunning(false);
        BossBar bar = koth.getBossBar();
        if (bar != null) bar.setVisible(false);
        for(String s:koth.getCommands()){
            String string = replacePlayerPositionPlaceholders(s,koth.points);
            parseAndExecuteChanceCommand(string);
        }
        for (String message:koth.getMessages()){
            String string = replacePlayerPositionPlaceholders(message,koth.points);
            parseAndSendMessage(string);
        }

        koth.setTime(koth.getDuration());
        koth.points.clear();
    }

    public void checkKothTimings(Koth koth){
        List<String> timings = dataConfig.getStringList("koths."+koth.getName()+".timings");
        ZoneId zone = ZoneId.of("Europe/London");
        DayOfWeek today = ZonedDateTime.now(zone).getDayOfWeek();
        LocalTime now = LocalTime.now(zone);

        for (String timeString : timings) {
            String[] parts = timeString.split(" ");
            String dayPart;
            String timePart;

            if (parts.length == 2) {
                dayPart = parts[0].toUpperCase();
                timePart = parts[1];
            } else {
                dayPart = "";
                timePart = parts[0];
            }

            LocalTime targetTime = LocalTime.parse(timePart);

            if ((dayPart.isEmpty() || DayOfWeek.valueOf(dayPart).equals(today)) && now.truncatedTo(ChronoUnit.MINUTES).equals(targetTime)) {
                startKoth(koth);

            }
        }
    }

    public boolean isInKoth(Player player, Koth koth) {

        Location playerLoc = player.getLocation().getBlock().getLocation();
        if(playerLoc.getWorld()!=koth.getCorner1().getWorld()) return false;

        Location loc1 = koth.getCorner1().getBlock().getLocation();
        Location loc2 = koth.getCorner2().getBlock().getLocation();

        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());

        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());

        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());


        return playerLoc.getX() >= minX && playerLoc.getX() <= maxX &&
                playerLoc.getY() >= minY && playerLoc.getY() <= maxY &&
                playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZ;
    }

    public String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%d:%02d", minutes, secs);
        } else {
            return String.valueOf(secs);
        }
    }
    public Koth getKothAt(Location loc) {
        for (Koth koth : koths) {
            if (koth.isInside(loc)) {
                return koth;
            }
        }
        return null;
    }
    public boolean isOverlapping(Koth newKoth) {
        for (Koth existing : koths) {
            if (existing.intersects(newKoth)) {
                return true;
            }
        }
        return false;
    }
    public void loadConfig() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void reload() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                loadConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadConfig();
        }
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}