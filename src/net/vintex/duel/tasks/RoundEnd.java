package net.vintex.duel.tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.vintex.duel.Main;
import net.vintex.duel.utils.Utils;

public class RoundEnd {

	public static int schedulerroundend;
	public static int cdroundend = 181;

	public static void start() {
		schedulerroundend = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (cdroundend != 0) {
					cdroundend -= 1;
					for (Player players : Bukkit.getOnlinePlayers()) {
						String mins = String.valueOf((cdroundend / 60));
						if (String.valueOf(mins).contains("."))
							mins = mins.split(".")[0];
						if (Integer.valueOf(mins) < 10)
							mins = "0" + mins;
						Utils.sendActionBar(players,
								"§cDas Spiel endet in §l" + mins + ":"
										+ (((cdroundend - Integer.valueOf(mins) * 60) <= 9
												? "0" + (cdroundend - Integer.valueOf(mins) * 60)
												: cdroundend - Integer.valueOf(mins) * 60)));
					}
					if (cdroundend == 5 || cdroundend == 4 || cdroundend == 3 || cdroundend == 2 || cdroundend == 1) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
						}
					}
					if (cdroundend == 0) {
						Bukkit.getScheduler().cancelTask(schedulerroundend);
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
						}
						int i = new Random().nextInt(2);
						Main.getPlayers().get(i).setHealth(0);
						Main.getPlayers().get(i).spigot().respawn();
						schedulerroundend = -1;
						cdroundend = 181;
					}
				}
			}
		}, 0L, 20L);
	}
}
