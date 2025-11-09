package com.lupino.lpkoth.tabcompleters;

import com.lupino.lpkoth.managers.KothManager;
import com.lupino.lpkoth.models.Koth;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KothCompleter implements TabCompleter {
    private final KothManager kothManager;

    public KothCompleter(KothManager kothManager) {
        this.kothManager = kothManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        if(sender instanceof Player player){

            if(!player.hasPermission("lpkoth.admin"))return null;

            if(args.length==1){
                list.add("create");
                list.add("reload");
                list.add("stop");
                list.add("end");
                list.add("start");
                list.add("list");
                list.add("tp");
                return list;
            }

            if(args.length ==2&&args[0].equalsIgnoreCase("stop")||args[0].equalsIgnoreCase("end")||args[0].equalsIgnoreCase("start")||args[0].equalsIgnoreCase("tp")){

                for(String name: kothManager.getAllKothNames()){
                    list.add(name);
                }
            }
            return list;
        }
        return null;
    }
}
