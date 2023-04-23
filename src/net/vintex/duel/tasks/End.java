package net.vintex.duel.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.utils.Utils;

public class End {

	public static int schedulerend;
	public static int cdend = 601;

	public static void start() {
		schedulerend = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (cdend != 0) {
					cdend -= 1;
					for (Player players : Bukkit.getOnlinePlayers()) {
						String mins = String.valueOf((cdend / 60));
						if (String.valueOf(mins).contains("."))
							mins = mins.split(".")[0];
						String secs = String.valueOf(cdend - Integer.valueOf(mins) * 60);
						Utils.sendActionBar(players,
								"§cDas Spiel endet in §l" + (Integer.valueOf(mins) < 10 ? "0" + mins : mins) + ":"
										+ (Integer.valueOf(secs) < 10 ? "0" + secs : secs));
					}
					if (cdend == 5 || cdend == 4 || cdend == 3 || cdend == 2 || cdend == 1) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
						}
					}
					if (cdend == 0) {
						Main.setGameState(GameState.ending);
						Bukkit.getScheduler().cancelTasks(Main.getPlugin());
						Bukkit.broadcastMessage(Main.getGamePrefix() + "§7Es gibt keinen Gewinner!");
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
							players.sendTitle("§c§lGame OVER!", "§7Kein Gewinner!");
						}

						for (Player players : Bukkit.getOnlinePlayers()) {
							if (players.getOpenInventory().getTitle().contains("Spectate")) {
								players.closeInventory();
							}
						}
						MinecraftServer.getServer().setMotd("§7Restart-Keine");
						Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
							@Override
							public void run() {
								/*
								 * Player p1 = Main.getPlayers().get(0); Player p2 = Main.getPlayers().get(1);
								 * if(Utils.min == 0 || Utils.sec > 30) Utils.min =+ 1; int gespielt1 =
								 * Utils.min + Methods.getFromTableInt(p1.getUniqueId(), "Spieler",
								 * "spielMinuten"); if(gespielt1 >= 60) {
								 * Methods.setInTableInt(p1.getUniqueId(), "Spieler", "spielStunden",
								 * Methods.getFromTableInt(p1.getUniqueId(), "Spieler", "spielStunden" + 1));
								 * gespielt1 = gespielt1 - 60; } Methods.setInTableInt(p1.getUniqueId(),
								 * "Spieler", "spielMinuten", gespielt1); int gespielt2 = Utils.min +
								 * Methods.getFromTableInt(p2.getUniqueId(), "Spieler", "spielMinuten");
								 * if(gespielt2 >= 60) { Methods.setInTableInt(p2.getUniqueId(), "Spieler",
								 * "spielStunden", Methods.getFromTableInt(p2.getUniqueId(), "Spieler",
								 * "spielStunden" + 1)); gespielt2 = gespielt2 - 60; }
								 * Methods.setInTableInt(p2.getUniqueId(), "Spieler", "spielMinuten",
								 * gespielt2);
								 */
							}
						});
						Restart.start();
					}
				}
			}
		}, 0L, 20L);
	}
}
