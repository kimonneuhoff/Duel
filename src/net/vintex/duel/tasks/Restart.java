package net.vintex.duel.tasks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import de.realjongi.cloudy.spigot.MainClass;
import de.realjongi.cloudy.spigot.api.CloudAPI;
import net.vintex.duel.Main;
import net.vintex.duel.utils.Scoreboard;
import net.vintex.duel.utils.Utils;

public class Restart {

	public static int schedulerstop;
	public static int cdstop = 16;

	public static void start() {
		schedulerstop = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (cdstop > 0) {
					cdstop -= 1;
					for (Player players : Bukkit.getOnlinePlayers()) {
						players.setExp(cdstop / 15.0F);
					}
					if (cdstop == 15) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							if (Main.getPlayers().contains(players))
								Main.getPlayers().remove(players);
							if (Main.getSpectator().contains(players))
								Main.getSpectator().remove(players);
							players.getInventory().setHeldItemSlot(0);

							Scoreboard.getScoreboard().get(players).performBoardUpdate();

							for (Player players1 : Bukkit.getOnlinePlayers()) {
								players1.showPlayer(players);
								players.showPlayer(players1);
							}

							players.setGameMode(GameMode.SURVIVAL);
							players.setAllowFlight(true);
							players.setFlying(true);
							players.spigot().setCollidesWithEntities(true);
							for (PotionEffect effects : players.getActivePotionEffects()) {
								players.removePotionEffect(effects.getType());
							}
							players.setHealth(20);
							players.setFoodLevel(20);
							players.setHealth(20);
							players.setFireTicks(0);
							players.setLevel(0);
							players.setExp(0.0F);

							/*
							 * Location lobby = new
							 * Location(Bukkit.getWorld(Main.getCfg().getString("Lobby.World")),
							 * Main.getCfg().getDouble("Lobby.X"), Main.getCfg().getDouble("Lobby.Y"),
							 * Main.getCfg().getDouble("Lobby.Z"), (float)
							 * Main.getCfg().getDouble("Lobby.Yaw"), (float)
							 * Main.getCfg().getDouble("Lobby.Pitch")); lobby.getChunk().load();
							 * players.teleport(lobby);
							 */

							players.setVelocity(new Vector(0, 0.4, 0));

							players.playSound(players.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 1);

							players.getInventory().clear();
							players.getInventory().setArmorContents(null);
							players.getInventory().setItem(8,
									Utils.newitem(Material.DRAGON_BREATH, 1, "§c§l §cVerlassen §7[Rechtsklick]"));
							
							Utils.sendActionBar(players, " ");
						}
					}
					if(cdstop == 15) {
	                      Bukkit.broadcastMessage(" §c§l» §cDer Server restartet in §e15 §cSekunden.");
					} else if(cdstop == 13) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
							players.sendMessage("§6§m-----------------------------------------");
							players.sendMessage("§f");
							players.sendMessage(players.getDisplayName() + "§f, wir hoffen du hattest Spaß in §e"+ Main.getGameString() + "§f!");;
							players.sendMessage("§f");
							players.sendMessage("§fMelde Ideen, Kritik und mehr auf unserem Discord!");
							players.sendMessage("§8➥ §bVintex.net/Discord");
							players.sendMessage("§f");
							players.sendMessage("§6§m-----------------------------------------");
						}
					} else if(cdstop == 10) {
	                      Bukkit.broadcastMessage(" §c§l» §cDer Server restartet in §e10 §cSekunden.");
					} else if(cdstop == 5) {
	                      Bukkit.broadcastMessage(" §c§l» §cDer Server restartet in §e5 §cSekunden.");
					} else if(cdstop == 3) {
	                      Bukkit.broadcastMessage(" §c§l» §cDer Server restartet in §e3 §cSekunden.");
					} else if(cdstop == 2) {
	                      Bukkit.broadcastMessage(" §c§l» §cDer Server restartet in §e2 §cSekunden.");
					} else if(cdstop == 1) {
	                      Bukkit.broadcastMessage(" §c§l» §cDer Server restartet in §eeiner §cSekunden.");
					} else if(cdstop == 0) {
	                      Bukkit.broadcastMessage(" §c§l» §cDer Server restartet jetzt.");
							for (Player players : Bukkit.getOnlinePlayers()) {
								players.playSound(players.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2, 1);
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
