package dev.lupino1.lpkoth.models;



import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Koth {


private final String name;
private final Location corner1;
private final Location corner2;

private final List<String> commands;
private final List<String> messages;
private final List<String> timings;

private boolean running = false;

private int time;

private final int duration;

private BossBar bossBar;

private String startMessage;
private String bossBarMessage;
private String secondsMessage;


public Map<UUID,Integer> points = new HashMap<>();
    public Koth(String name, Location corner1, Location corner2, List<String> commands, List<String> messages, List<String> timings, int duration) {
        this.name = name;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.commands = commands;
        this.messages = messages;
        this.timings = timings;
        this.duration = duration;
        this.time = duration;
        this.bossBar = Bukkit.createBossBar("" , BarColor.YELLOW, BarStyle.SOLID);

    }

    public List<String> getMessages() {
        return messages;
    }

    public void setStartMessage(String startMessage) {
        this.startMessage = startMessage;
    }

    public void setBossBarMessage(String bossBarMessage) {
        this.bossBarMessage = bossBarMessage;
    }

    public void setSecondsMessage(String secondsMessage) {
        this.secondsMessage = secondsMessage;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public String getBossBarMessage() {
        return bossBarMessage;
    }

    public String getSecondsMessage() {
        return secondsMessage;
    }

    public void setBossBarColor(String strColor){
        BarColor color = BarColor.valueOf(strColor.toUpperCase());
        if(color == null)return;
        bossBar.setColor(color);
    }
    public Map<UUID, Integer> getPoints() {
        return points;
    }
    public void setPoints(Map<UUID, Integer> map){
        this.points=map;
    }

    public Location getCorner1() {
        return corner1;
    }

    public List<String> getCommands() {
        return commands;
    }

    public boolean isRunning() {
        return running;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void minusTime(int minusTime){
        this.time = this.time-minusTime;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public Location getCorner2() {
        return corner2;
    }

    public List<String> getTimings() {
        return timings;
    }
    public boolean isInside(Location loc) {

        Location loc1 = getCorner1().getBlock().getLocation();
        Location loc2 = getCorner2().getBlock().getLocation();

        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());

        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());

        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());


        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
    public boolean intersects(Koth other) {
        if (other == null || corner1.getWorld() == null || !corner1.getWorld().equals(other.getCorner1().getWorld())) {
            return false;
        }

        double x1Min = Math.min(corner1.getX(), corner2.getX());
        double x1Max = Math.max(corner1.getX(), corner2.getX());
        double y1Min = Math.min(corner1.getY(), corner2.getY());
        double y1Max = Math.max(corner1.getY(), corner2.getY());
        double z1Min = Math.min(corner1.getZ(), corner2.getZ());
        double z1Max = Math.max(corner1.getZ(), corner2.getZ());

        double x2Min = Math.min(other.getCorner1().getX(), other.getCorner2().getX());
        double x2Max = Math.max(other.getCorner1().getX(), other.getCorner2().getX());
        double y2Min = Math.min(other.getCorner1().getY(), other.getCorner2().getY());
        double y2Max = Math.max(other.getCorner1().getY(), other.getCorner2().getY());
        double z2Min = Math.min(other.getCorner1().getZ(), other.getCorner2().getZ());
        double z2Max = Math.max(other.getCorner1().getZ(), other.getCorner2().getZ());

        return (x1Min <= x2Max && x1Max >= x2Min) &&
                (y1Min <= y2Max && y1Max >= y2Min) &&
                (z1Min <= z2Max && z1Max >= z2Min);
    }
}
