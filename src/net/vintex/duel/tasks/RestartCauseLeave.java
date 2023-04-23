package net.vintex.duel.tasks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import de.realjongi.cloudy.spigot.MainClass;
import de.realjongi.cloudy.spigot.api.CloudAPI;
import net.vintex.duel.Main;

public class RestartCauseLeave {

	public static int schedulerstop2;
	public static int cdstop2 = 16;

	public static void start() {
		schedulerstop2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (cdstop2 != 0) {
					cdstop2 -= 1;
					for (Player players : Bukkit.getOnlinePlayers()) {
						players.setExp(cdstop2 / 5.0F);
					}
					if (cdstop2 == 16) {
						Bukkit.broadcastMessage(Main.getGamePrefix()
								+ "§cDas Duel wurde abgebrochen, weil dein Gegner das Spiel verlassen hat!");
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 1);
						}
					}
					if (cdstop2 == 0) {
						Bukkit.getScheduler().cancelTask(schedulerstop2);
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 2, 1);
							CloudAPI.connect(players, "fallback"); 
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
							@Override
							public void run() {
								Bukkit.shutdown();
							}
						}, 60L);
					}
				}
			}
		}, 0L, 20L);
	}
}
