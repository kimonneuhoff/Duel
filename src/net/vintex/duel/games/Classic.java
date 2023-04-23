package net.vintex.duel.games;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import itembuilder.ItemBuilder;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.Stats;
import net.vintex.duel.tasks.NoMove;
import net.vintex.duel.tasks.RoundEnd;
import net.vintex.duel.utils.RandomMap;
import net.vintex.duel.utils.Scoreboard;
import net.vintex.duel.utils.Utils;

public class Classic implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		final Player p = e.getEntity();
		e.setDeathMessage(null);
		e.getDrops().clear();
		e.setDroppedExp(0);
		for (Player players : Bukkit.getOnlinePlayers()) {
			Scoreboard.getScoreboard().get(players).performBoardUpdate();
		}
		int i = 1;
		Iterator<Player> itr = Main.getPlayers().iterator();
		while (itr.hasNext()) {
			Player players = itr.next();
			for (PotionEffect potionEffect : players.getActivePotionEffects()) {
				if (potionEffect.getDuration() != Integer.MAX_VALUE) {
					players.removePotionEffect(potionEffect.getType());
				}
			}
			players.setHealth(20);
			players.setFoodLevel(20);
			players.getInventory().clear();
			players.getInventory().setArmorContents(null);
			players.getInventory().setHeldItemSlot(0);
			players.setFireTicks(0);
			String game = Main.getGame().getFriendly();
			Location spawn = new Location(
					Bukkit.getWorld(
							Main.getCfg().getString("Spawn." + game + "." + Main.getMap() + "." + i + ".World")),
					Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".X"),
					Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".Y"),
					Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".Z"),
					(float) Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".Yaw"),
					(float) Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + i + ".Pitch"));
			spawn.getChunk().load();
			players.teleport(spawn);
			Main.getNoMove().put(players, spawn);
			i++;
			if (players != p) {
				double hearts = players.getHealth();
				hearts *= 100.0D;
				hearts = Math.round(hearts);
				hearts /= 100.0D;

				p.sendMessage(Main.getGamePrefix() + "§7Du wurdest von " + players.getDisplayName() + " §7mit §c"
						+ (hearts / 2.0) + " ❤ §7getötet.");
				players.sendMessage(Main.getGamePrefix() + "§7Du hast " + p.getDisplayName() + " §7mit §c"
						+ (hearts / 2.0) + " ❤ §7getötet.");
				// players.playSound(players.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
				// p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2, 1);
				for (Player specs : Main.getSpectator()) {
					specs.sendMessage(Main.getGamePrefix() + p.getDisplayName() + " §7wurde von §6"
							+ players.getDisplayName() + " §7mit §c" + (hearts / 2.0) + " ❤ §7getötet.");
				}

				// players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
				double hearts1 = Double.valueOf(Main.getStats().get(players).get(Stats.damage));
				hearts1 *= 100.0D;
				hearts1 = Math.round(hearts1);
				hearts1 /= 100.0D;

				players.sendMessage("§6§m--§e Match Stats §6§m-------------------------------------");
				players.sendMessage("§fSchaden zugefügt: §c" + (hearts1 / 2.0) + " ❤ §f- §e"
						+ Math.round((Double.valueOf(Main.getStats().get(players).get(Stats.damageX))
								/ Double.valueOf(Main.getStats().get(players).get(Stats.damage))) * 100)
						+ "% Kritisch");
				players.sendMessage(
						"§fSchläge: §d" + Integer.valueOf(Main.getStats().get(players).get(Stats.hits)) + " §f- §e"
								+ Math.round(
										((Integer.valueOf(Main.getStats().get(players).get(Stats.hitsX))
												/ Integer.valueOf(Main.getStats().get(players).get(Stats.hits)))
												* 100))
								+ "% (" + Integer.valueOf(Main.getStats().get(players).get(Stats.hitsX)) + ") getroffen");
				if (!Main.getStats().get(players).get(Stats.shots).equals("0"))
					players.sendMessage(
							"§fPfeile: §d" + Integer.valueOf(Main.getStats().get(players).get(Stats.shots)) + " §f- §e"
									+ Math.round((Integer.valueOf(Main.getStats().get(players).get(Stats.shotsX))
											/ Integer.valueOf(Main.getStats().get(players).get(Stats.shots))) * 100)
									+ "% (" + Integer.valueOf(Main.getStats().get(players).get(Stats.shotsX)) + ") getroffen");
				players.sendMessage("§6§m--------------------------------------------------");

				Main.getStats().put(players, new HashMap<Stats, String>());
				Main.getStats().get(players).put(Stats.damage, "0");
				Main.getStats().get(players).put(Stats.damageX, "0");
				Main.getStats().get(players).put(Stats.hits, "0");
				Main.getStats().get(players).put(Stats.hitsX, "0");
				Main.getStats().get(players).put(Stats.shots, "0");
				Main.getStats().get(players).put(Stats.shotsX, "0");

				// p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
				double hearts2 = Double.valueOf(Main.getStats().get(p).get(Stats.damage));
				hearts2 *= 100.0D;
				hearts2 = Math.round(hearts2);
				hearts2 /= 100.0D;

				p.sendMessage("§6§m--§e Match Stats §6§m-------------------------------------");
				p.sendMessage(
						"§fSchaden zugefügt: §c" + (hearts2 / 2.0) + " ❤ §f- §e"
								+ Math.round((Double.valueOf(Main.getStats().get(p).get(Stats.damageX))
										/ Double.valueOf(Main.getStats().get(p).get(Stats.damage))) * 100)
								+ "% Kritisch");
				p.sendMessage(
						"§fSchläge: §d" + Integer.valueOf(Main.getStats().get(p).get(Stats.hits)) + " §f- §e"
								+ Math.round(
										((Integer.valueOf(Main.getStats().get(p).get(Stats.hitsX))
												/ Integer.valueOf(Main.getStats().get(p).get(Stats.hits)))
												* 100))
								+ "% (" + Integer.valueOf(Main.getStats().get(p).get(Stats.hitsX)) + ") getroffen");
				if (!Main.getStats().get(p).get(Stats.shots).equals("0"))
					p.sendMessage(
							"§fPfeile: §d" + Integer.valueOf(Main.getStats().get(p).get(Stats.shots)) + " §f- §e"
									+ Math.round((Integer.valueOf(Main.getStats().get(p).get(Stats.shotsX))
											/ Integer.valueOf(Main.getStats().get(p).get(Stats.shots))) * 100)
									+ "% (" + Integer.valueOf(Main.getStats().get(p).get(Stats.shotsX)) + ") getroffen");
				p.sendMessage("§6§m--------------------------------------------------");

				Main.getStats().put(p, new HashMap<Stats, String>());
				Main.getStats().get(p).put(Stats.damage, "0");
				Main.getStats().get(p).put(Stats.damageX, "0");
				Main.getStats().get(p).put(Stats.hits, "0");
				Main.getStats().get(p).put(Stats.hitsX, "0");
				Main.getStats().get(p).put(Stats.shots, "0");
				Main.getStats().get(p).put(Stats.shotsX, "0");

				Main.getCountedint().put(players, Main.getCountedint().get(players) + 1);
				Main.setRunde(Main.getRunde() + 1);
				if (Main.getCountedint().get(players) == Main.getNeeded()) {
					Main.getPlayers().remove(p);
					Utils.win(players, p);
				} else {
					Main.setGameState(GameState.prepare);
					Bukkit.getScheduler().cancelTask(RoundEnd.schedulerroundend);
					RoundEnd.schedulerroundend = -1;
					RoundEnd.cdroundend = 181;
					for (Player players1 : Bukkit.getOnlinePlayers()) {
						Scoreboard.getScoreboard().get(players1).performBoardUpdate();
						Scoreboard.getScoreboard().get(players1).performRangUpdate();
					}
					NoMove.start();
					Main.getSgchest().clear();
					for (Entity entity : Bukkit.getWorld(RandomMap.winnermap).getEntities()) {
						if (entity.getType() == EntityType.DROPPED_ITEM || entity.getType() == EntityType.ARROW
								|| entity.getType() == EntityType.TRIDENT || entity.getType() == EntityType.SLIME) {
							entity.remove();
						}
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							Iterator<Block> blocks = Main.getBlocks().iterator();
							while (blocks.hasNext()) {
								Block block = blocks.next();
								block.setType(Material.AIR);
								blocks.remove();
							}
						}
					}.runTask(Main.getPlugin());
				}
			}
			p.spigot().respawn();
		}
	}

	@EventHandler
	public void onSpread(BlockFromToEvent e) {
		Block b = e.getBlock();
		Main.getBlocks().add(b);
		Main.getBlocks().add(e.getToBlock());
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE)) {
			if (Main.getKit().equals("Trash") || Main.getKit().equals("QuickSG")) {
				if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p)) {
					e.setCancelled(false);
				} else {
					e.setCancelled(true);
				}
			} else {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		e.getBlock().getDrops().clear();
		e.setDropItems(false);
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) {
			e.setCancelled(false);
			return;
		}
		if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p)
				&& !Main.getSpectator().contains(p)) {
			if (!e.getBlock().getType().isSolid() && e.getBlock().getType() != Material.FLOWER_POT
					&& !e.getBlock().getType().toString().contains("RAIL"))
				e.setCancelled(false);
			else {
				if (Main.getBlocks().contains(e.getBlock())) {
					e.setCancelled(false);
					Main.getBlocks().remove(e.getBlock());
				} else
					e.setCancelled(true);
			}
		} else
			e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() != GameMode.CREATIVE) {
			if (Main.getGameState() == GameState.ingame) {
				e.setCancelled(false);
				if (e.getBlock().getType() == Material.TNT) {
					e.getBlock().setType(Material.AIR);
					TNTPrimed tnt = e.getBlock().getWorld().spawn(e.getBlock().getLocation(), TNTPrimed.class);
					tnt.setFuseTicks(40);
				} else
					Main.getBlocks().add(e.getBlock());
			} else {
				e.setCancelled(true);
			}

		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p)
					&& !Main.getSpectator().contains(p) && Main.getPvp()) {
				if (e.getCause() == DamageCause.FALL) {
					e.setCancelled(true);
					return;
				}
				e.setCancelled(false);
				if (p.getInventory().getItemInOffHand().getType() == Material.SHIELD && p.isBlocking()) {
					p.getInventory().setItemInOffHand(null);
					p.setCooldown(Material.SHIELD, 20);
					new BukkitRunnable() {
						@Override
						public void run() {
							p.getInventory().setItemInOffHand(new ItemBuilder().withType(Material.SHIELD).build());
						}
					}.runTaskLater(Main.getPlugin(), 2);
				}
			} else
				e.setCancelled(true);
		}

	}
}
