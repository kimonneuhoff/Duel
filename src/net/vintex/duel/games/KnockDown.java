package net.vintex.duel.games;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.tasks.NoMove;
import net.vintex.duel.tasks.RoundEnd;
import net.vintex.duel.utils.Scoreboard;
import net.vintex.duel.utils.Utils;

public class KnockDown implements Listener {

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {

		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE))
			e.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		final Block block = e.getBlock();

		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) {
			e.setCancelled(false);
			return;
		}
		if (Main.getGameState() == GameState.ingame) {
			if (e.getBlock().getType() == Material.SANDSTONE) {
				e.setCancelled(false);
				Main.getBlocks().add(e.getBlock());
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (Main.getBlocks().contains(block)) {
							block.setType(Material.RED_SANDSTONE);
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
							@Override
							public void run() {
								if (Main.getBlocks().contains(block)) {
									block.setType(Material.AIR);
									for (Player players : Bukkit.getOnlinePlayers()) {
										players.playEffect(block.getLocation(), Effect.STEP_SOUND, 1);
									}
									Main.getBlocks().remove(block);
								}
							}
						}, 20);
					}
				}, 40);
			} else
				e.setCancelled(true);
		} else
			e.setCancelled(true);

	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {

		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p)
					&& !Main.getSpectator().contains(p) && Main.getPvp()) {
				if (e.getCause() != DamageCause.ENTITY_ATTACK) {
					e.setCancelled(true);
					return;
				}
				e.setDamage(0);
				e.setCancelled(false);
				// p.getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND,
				// Material.REDSTONE_BLOCK);
			} else
				e.setCancelled(true);
		}

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();

		e.setDeathMessage(null);
		e.getDrops().clear();
		e.setDroppedExp(0);
		for (Player players : Bukkit.getOnlinePlayers()) {
			Scoreboard.getScoreboard().get(players).performBoardUpdate();
		}
		Iterator<Player> itr = Main.getPlayers().iterator();
		while (itr.hasNext()) {
			Player players = itr.next();
			players.getInventory().clear();
			players.getInventory().setArmorContents(null);
			players.getInventory().setHeldItemSlot(0);
			p.spigot().respawn();
			if (players != p) {
				p.sendMessage(Main.getGamePrefix() + "§7Du wurdest von " + players.getDisplayName() + " §7getötet.");
				players.sendMessage(Main.getGamePrefix() + "§7Du hast " + p.getDisplayName() + " §7getötet.");
				players.playSound(players.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
				p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2, 1);
				for (Player specs : Main.getSpectator()) {
					specs.sendMessage(Main.getGamePrefix() + p.getDisplayName() + " §7wurde von §6"
							+ players.getDisplayName() + " §7getötet.");
				}
				Main.getCountedint().put(players, Main.getCountedint().get(players) + 1);
				Main.setRunde(Main.getRunde() + 1);
				if (Main.getCountedint().get(players) == Main.getNeeded()) {
					Main.getPlayers().remove(p);
					Utils.win(players, p);
				} else {
					Main.setGameState(GameState.prepare);
					for (Player players1 : Main.getPlayers()) {
						Location spawn = new Location(
								Bukkit.getWorld(Main.getCfg()
										.getString("Spawn." + Main.getGame() + "." + Main.getMap() + "." + Main.getI()
												+ ".World")),
								Main.getCfg().getDouble(
										"Spawn." + Main.getGame() + "." + Main.getMap() + "." + Main.getI() + ".X"),
								Main.getCfg().getDouble(
										"Spawn." + Main.getGame() + "." + Main.getMap() + "." + Main.getI() + ".Y"),
								Main.getCfg().getDouble(
										"Spawn." + Main.getGame() + "." + Main.getMap() + "." + Main.getI() + ".Z"),
								(float) Main.getCfg().getDouble(
										"Spawn." + Main.getGame() + "." + Main.getMap() + "." + Main.getI() + ".Yaw"),
								(float) Main.getCfg().getDouble("Spawn." + Main.getGame() + "." + Main.getMap() + "."
										+ Main.getI() + ".Pitch"));
						spawn.getChunk().load();
						players1.teleport(spawn);
						Main.getNoMove().put(players1, spawn);
						Main.setI(Main.getI() + 1);
						if (Main.getI() == 3)
							Main.setI(1);
					}
					Bukkit.getScheduler().cancelTask(RoundEnd.schedulerroundend);
					RoundEnd.schedulerroundend = -1;
					RoundEnd.cdroundend = 181;
					NoMove.start();
					for (Player players1 : Bukkit.getOnlinePlayers()) {
						Scoreboard.getScoreboard().get(players1).performBoardUpdate();
						Scoreboard.getScoreboard().get(players1).performRangUpdate();
					}
				}
			}
		}
		Iterator<Block> blocks = Main.getBlocks().iterator();
		while (blocks.hasNext()) {
			Block block = blocks.next();
			block.setType(Material.AIR);
			Main.getBlocks().remove(block);
		}
	}

}
