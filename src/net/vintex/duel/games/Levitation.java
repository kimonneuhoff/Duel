package net.vintex.duel.games;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.tasks.NoMove;
import net.vintex.duel.tasks.RoundEnd;
import net.vintex.duel.utils.Scoreboard;
import net.vintex.duel.utils.Utils;

public class Levitation implements Listener {

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
			} else
				e.setCancelled(true);
		}

	}

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

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (Main.getGameState() == GameState.ingame) {
			Player p = e.getPlayer();
			Location loc = p.getLocation();
			p.sendMessage(loc.getY() + " / " + Main.getTodeshöhe());
			if (loc.getY() >= Main.getTodeshöhe()) {
				Main.setGameState(GameState.prepare);
				for (Player players : Bukkit.getOnlinePlayers()) {
					Scoreboard.getScoreboard().get(players).performBoardUpdate();
				}
				Iterator<Player> itr = Main.getPlayers().iterator();
				while (itr.hasNext()) {
					Player players = itr.next();
					players.getInventory().clear();
					players.getInventory().setArmorContents(null);
					players.getInventory().setHeldItemSlot(0);
					if (players != p) {
						p.sendMessage(Main.getGamePrefix() + "§7Du hast die Runde gewonnen!");
						players.sendMessage(Main.getGamePrefix() + p.getDisplayName() + " §7hat die Runde gewonnen!");
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
						players.playSound(players.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2, 1);
						for (Player specs : Main.getSpectator()) {
							specs.sendMessage(Main.getGamePrefix() + p.getDisplayName() + " §7hat die Runde gewonnen!");
						}
						Main.getCountedint().put(p, Main.getCountedint().get(p) + 1);
						Main.setRunde(Main.getRunde() + 1);
						if (Main.getCountedint().get(p) == Main.getNeeded()) {
							Main.setGameState(GameState.ingame);
							Main.getPlayers().remove(players);
							Utils.win(p, players);
							return;
						}
						for (Player players1 : Main.getPlayers()) {
							Location spawn = new Location(
									Bukkit.getWorld(Main.getCfg()
											.getString("Spawn." + Main.getGame() + "." + Main.getMap() + "."
													+ Main.getI() + ".World")),
									Main.getCfg().getDouble(
											"Spawn." + Main.getGame() + "." + Main.getMap() + "." + Main.getI() + ".X"),
									Main.getCfg().getDouble(
											"Spawn." + Main.getGame() + "." + Main.getMap() + "." + Main.getI() + ".Y"),
									Main.getCfg().getDouble(
											"Spawn." + Main.getGame() + "." + Main.getMap() + "." + Main.getI() + ".Z"),
									(float) Main.getCfg()
											.getDouble("Spawn." + Main.getGame() + "." + Main.getMap() + "."
													+ Main.getI() + ".Yaw"),
									(float) Main.getCfg().getDouble("Spawn." + Main.getGame() + "." + Main.getMap()
											+ "." + Main.getI() + ".Pitch"));
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
				Iterator<Block> blocks = Main.getBlocks().iterator();
				while (blocks.hasNext()) {
					Block block = blocks.next();
					block.setType(Material.AIR);
					Main.getBlocks().remove(block);
				}
			}
		}
	}
}
