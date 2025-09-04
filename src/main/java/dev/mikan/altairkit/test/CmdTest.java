package dev.mikan.altairkit.test;

import dev.mikan.altairkit.api.commands.CmdClass;
import dev.mikan.altairkit.api.commands.actors.Actor;
import dev.mikan.altairkit.api.commands.annotations.*;
import dev.mikan.altairkit.api.yml.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class CmdTest implements CmdClass {

    public CmdTest(JavaPlugin plugin) throws IOException {

        final ConfigManager manager = new ConfigManager(plugin);

        final FileConfiguration config = manager.load("config.yml").get("config.yml");

    }

    @Command("Altair")
    @Complete({"kit","by","mikan"})
    @Permission("dev.mikan.module")
    @Sender(User.PLAYER)
    public void altair(final Actor actor, @Default Player target, String message) {
        target.sendMessage(message);
    }

}
