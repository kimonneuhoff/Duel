package net.vintex.duel.games;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.utils.Utils;

public class Aura implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		p.spigot().respawn();
		Main.getPlayers().remove(p);
		Player k = Main.getPlayers().get(0);
		e.setDeathMessage(null);
		e.getDrops().clear();
		e.setDroppedExp(0);
		k.getInventory().clear();
		k.getInventory().setArmorContents(null);
		k.getInventory().setHeldItemSlot(0);

		p.sendMessage(Main.getGamePrefix() + "§7Du wurdest von " + k.getDisplayName() + " §7getötet.");
		k.sendMessage(Main.getGamePrefix() + "§7Du hast " + p.getDisplayName() + " §7getötet.");
		k.playSound(k.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2, 1);

		for (Player specs : Main.getSpectator()) {
			specs.sendMessage(Main.getGamePrefix() + p.getDisplayName() + " §7wurde von §6" + k.getDisplayName()
					+ " §7getötet.");
		}
		Utils.win(k, p);
		p.spigot().respawn();

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
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE))
			e.setCancelled(true);

	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p)
					&& !Main.getSpectator().contains(p) && Main.getPvp()) {
				if (e.getCause() == DamageCause.BLOCK_EXPLOSION) {
					e.setCancelled(true);
					return;
				}
				if (e.getCause() == DamageCause.ENTITY_EXPLOSION || e.getCause() == DamageCause.VOID) {
				} else if (e.getCause() == DamageCause.ENTITY_ATTACK) {
					e.setDamage(1);
				} else {
					e.setDamage(e.getDamage() / 2);
				}
				e.setCancelled(false);
			} else
				e.setCancelled(true);
		}

	}
}