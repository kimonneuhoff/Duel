package net.vintex.duel;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.realjongi.cloudy.spigot.utils.events.CloudPacketEvent;
import net.vintex.duel.tasks.Schutzzeit;
import net.vintex.duel.utils.MapLoader;
import net.vintex.duel.utils.RandomMap;
import net.vintex.duel.utils.Utils;

public class PacketReceiver implements Listener {

	@EventHandler
	public void packetReceived(CloudPacketEvent e) {
		String packet = e.getPacketMsg();
		if (packet.startsWith("cloud-duel-")) {
			Bukkit.broadcastMessage(packet);
			packet = ChatColor.stripColor(packet.replace("cloud-duel-", ""));

			Main.setGame(
					Game.valueOf(packet.split("-")[0].equals("1vs1") ? "classic" : packet.split("-")[0].toLowerCase()));
			Main.setMap(packet.split("-")[1]);
			if (packet.split("-")[3].equals("null"))
				Main.setRunden(1);
			else
				Main.setRunden(Integer.valueOf(ChatColor.stripColor(packet.split("-")[3])));
			Main.setKit(ChatColor.stripColor(packet.split("-")[2]));
			if (packet.split("-")[0].equals("Classic"))
				Main.setGameString(Main.getKit());
			else
				Main.setGameString(Main.getGame().getFriendly());

			if (Main.getRunden() == 3) {
				Main.setNeeded(2);
			} else if (Main.getRunden() == 5) {
				Main.setNeeded(3);
			} else if (Main.getRunden() == 7) {
				Main.setNeeded(4);
			} else if (Main.getRunden() == 9) {
				Main.setNeeded(5);
			} else {
				Main.setNeeded(1);
			}

			Utils.playtime();

			/*
			 * if (packet.split("-")[0].equals("SkyWars")) {
			 * Schutzzeit.cdbuild = 21;
			 * }
			 */

			for (File file : new File("/root/Maps/Duel/"
					+ (Main.getGame().getFriendly() == "Classic" ? "1vs1" : Main.getGame().getFriendly()) + "/")
					.listFiles()) {
				System.out.println("found " + file.getName());
				if (file.getName().split("_")[0].equals(packet.split("-")[1])) {
					RandomMap.winnermap = file.getName().split("_")[0];
					RandomMap.winnermapbuilder = file.getName().split("_")[1];
					MapLoader.loadmap();
					break;
				}
			}
		}
	}
}
