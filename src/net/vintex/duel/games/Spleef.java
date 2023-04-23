package net.vintex.duel.games;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.vintex.duel.GameState;
import net.vintex.duel.Main;

public class Spleef implements Listener {

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {

		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE))
			e.setCancelled(true);
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
			e.setCancelled(false);
			Main.getBlocks().add(e.getBlock());
		} else
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
			if (e.getCause() == DamageCause.PROJECTILE) {
				e.setDamage(0);
				e.setCancelled(false);
			} else
				e.setCancelled(true);
		}

	}
}