package net.vintex.duel.utils;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import net.vintex.duel.Game;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.Prio;

public class MapLoader {

	private static int i = 1;

	public static boolean loadmap() {
		try {
			System.out.println("c1");
			File file = new File(
					"/root/Maps/Duel/" + (Main.getGame().getFriendly() == "Classic" ? "1vs1" : Main.getGame().getFriendly()) + "/" + RandomMap.winnermap + "_" + RandomMap.winnermapbuilder);
			Bukkit.getConsoleSender().sendMessage(Main.getGlobalPrefix() + "§7Map wird jetzt geladen..");
			FileUtils.copyDirectory(file, new File(RandomMap.winnermap));
			Bukkit.getConsoleSender().sendMessage(Main.getGlobalPrefix() + "§7Map wurde erfolgreich geladen..");
			Bukkit.getConsoleSender().sendMessage(Main.getGlobalPrefix() + "§7Erstelle Welt..");
			Bukkit.getServer().createWorld(new WorldCreator(RandomMap.winnermap));
			System.out.println("c2");
			for (World worlds : Bukkit.getWorlds()) {
				worlds.setAutoSave(false);
				worlds.setTime(6000);
				worlds.setGameRuleValue("doMobSpawning", "false");
				worlds.setGameRuleValue("doDaylightCycle", "false");
				worlds.setGameRuleValue("randomTickSpeed", "0");
				worlds.setGameRuleValue("doFireTick", "false");
				worlds.setGameRuleValue("announceAdvancements", "false");
			}
			if (Main.getGame().equals(Game.sneakyassasins)) {
				for (int count = 0; count <= 70; count++) {
					String game = Main.getGame().getFriendly();
					Location spawn = new Location(
							Bukkit.getWorld(Main.getCfg()
									.getString("Spawn." + game + "." + Main.getMap() + "." + i + ".World")),
							Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".X") + 3,
							Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".Y"),
							Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".Z"),
							(float) Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".Yaw"),
							(float) Main.getCfg()
									.getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".Pitch"));
					new Prio(spawn);
					i++;
					if (i == 3)
						i = 1;
				}
			}
			Main.setGameState(GameState.lobby);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
