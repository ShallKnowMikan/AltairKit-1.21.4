//package dev.mikan.altairkit.test;
//
//import dev.mikan.altairkit.api.commands.CmdClass;
//import dev.mikan.altairkit.api.commands.actors.Actor;
//import dev.mikan.altairkit.api.commands.annotations.*;
//import org.bukkit.entity.Player;
//
//public class CmdTest implements CmdClass {
//
//    public CmdTest(){
//
//
//    }
//
//    @Command("Altair")
//    @Complete({"kit","by","mikan"})
//    @Permission("dev.mikan.module")
//    @Sender(User.PLAYER)
//    public void altair(final Actor actor, @Default Player target, String message) {
//        target.sendMessage(message);
//    }
//
//}
