package net.vintex.duel.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;

import net.vintex.duel.Main;

public class RandomMap {

	public static ArrayList<String> allmaps = new ArrayList<String>();
	public static String winnermap = "";
	public static String winnermapbuilder = "";

	public static void randommap() {
		List<String> maps = new ArrayList<String>();
		for (File f : new File(
				"/root/Maps/Duel/" + (Main.getKit().equals("JumpFight") ? "JumpFight" : Main.getGame().getFriendly()) + "/")
						.listFiles()) {
			if (f.isDirectory()) {
				maps.add(f.getName());
			}
		}
		int count = 1;
		for (String s : maps) {
			Bukkit.getConsoleSender()
					.sendMessage(Main.getGlobalPrefix() + "§7Lade Map §e#" + count + "§7(§e" + s + "§7).");
			allmaps.add(s);
			count++;
		}
		Random r = new Random();
		int i = r.nextInt(allmaps.size());
		winnermap = allmaps.get(i).split("_")[0];
		winnermapbuilder = allmaps.get(i).split("_")[1];
		Bukkit.getConsoleSender()
				.sendMessage(Main.getGlobalPrefix() + "§7Die Map §e" + winnermap + " §7wurde zufällig ausgewählt!");
		MapLoader.loadmap();
	}
}
