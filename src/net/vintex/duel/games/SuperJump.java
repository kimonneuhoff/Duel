package net.vintex.duel.games;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.utils.Scoreboard;
import net.vintex.duel.utils.Utils;

public class SuperJump implements Listener {

	public static Map<Player, Location> cp = new HashMap<Player, Location>();
	public static boolean fertig = false;
	public static Map<Player, List<Block>> cachedCps = new HashMap<Player, List<Block>>();

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
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE))
			e.setCancelled(true);

	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (Main.getGameState() == GameState.ingame) {
			Player p = e.getPlayer();
			if (Main.getSpectator().contains(p) || !Main.getPlayers().contains(p))
				return;
			if (p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.IRON_BLOCK) {
				final Location loc = p.getLocation();
				Block b = p.getWorld().getBlockAt(loc);
				if (b.getType().equals(Material.STONE_PRESSURE_PLATE)
						|| b.getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
						|| b.getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
					if (b.getLocation().distance(cp.get(p)) >= 5) {
						if (cachedCps.get(p).contains(b))
							return;
						cachedCps.get(p).add(b);
						loc.setPitch(0);
						cp.put(p, loc);
						Main.getCountedint().put(p, Main.getCountedint().get(p) + 1);
						Bukkit.broadcastMessage(Main.getGamePrefix() + p.getDisplayName() + " §7hat den §e"
								+ Main.getCountedint().get(p) + ". §7Checkpoint erreicht.");
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.2F);
						return;
					}
				}
			}
			if (p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.DIAMOND_BLOCK) {
				for (Player players : Main.getPlayers()) {
					if (players != p) {
						Main.getPlayers().remove(players);
						Utils.win(p, players);
						break;
					}
				}
			}
			if ((p.getLocation().getBlock().getType() == Material.WATER
					|| p.getLocation().getBlock().getType() == Material.LAVA
					|| p.getLocation().getY() <= Main.getTodeshöhe()) && Main.getPlayers().contains(p)) {
				p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
				p.teleport(cp.get(p));
				Main.getCountedint2().put(p, Main.getCountedint2().get(p) + 1);
				Utils.cooldown(p);
				for (Player players1 : Bukkit.getOnlinePlayers()) {
					Scoreboard.getScoreboard().get(players1).performBoardUpdate();
					Scoreboard.getScoreboard().get(players1).performRangUpdate();
				}
			}
		}
	}
}
