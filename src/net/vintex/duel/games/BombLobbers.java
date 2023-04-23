package net.vintex.duel.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.utils.Utils;

public class BombLobbers implements Listener {

	private List<Player> cooldown = new ArrayList<Player>();

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
		final Player p = e.getPlayer();
		if (!Main.getPlayers().contains(p) || Main.getGameState() != GameState.ingame)
			return;
		if (this.cooldown.contains(p)) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F);
			return;
		}
		e.setCancelled(true);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setVelocity(p.getLocation().getDirection().multiply(0.8D).setY(1.2D));
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 5.0F, -0.3F);
		p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 5.0F, -0.3F);
		p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 10);
		p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 10);
		p.setExp(1.0F);
		p.setLevel(3);
		this.cooldown.add(p);
		p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
		(new BukkitRunnable() {
			int i = 3;

			@Override
			public void run() {
				p.setExp((this.i / 3));
				p.setLevel(this.i);
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
				if (this.i == 0) {
					BombLobbers.this.cooldown.remove(p);
					cancel();
					p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);
					p.setAllowFlight(true);
				}
				if (this.i == 0 || !BombLobbers.this.cooldown.contains(p) || p.getGameMode() != GameMode.SURVIVAL
						|| Main.getGameState() != GameState.ingame)
					cancel();
				this.i--;
			}
		}).runTaskTimer(Main.getPlugin(), 0L, 20L);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		Player k = Main.getOpponentOf(p);

		e.setDeathMessage(null);
		e.getDrops().clear();
		e.setDroppedExp(0);
		k.getInventory().clear();
		k.getInventory().setArmorContents(null);
		k.getInventory().setHeldItemSlot(0);
		p.setAllowFlight(false);
		p.setFlying(false);
		k.setAllowFlight(false);
		k.setFlying(false);
		k.playSound(k.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 2.0F);
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2.0F, 1.0F);

		Utils.win(k, p);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (p.getInventory().getItemInMainHand().getType() == Material.TNT
				|| p.getInventory().getItemInOffHand().getType() == Material.TNT) {
			final TNTPrimed tnt = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
			tnt.setCustomName(p.getName());
			tnt.setFuseTicks(40);
			tnt.setCustomNameVisible(false);
			p.playSound(p.getLocation(), Sound.ENTITY_WITCH_THROW, 1.0F, 50.0F);
			if(p.getEyeHeight() <= 0) tnt.setVelocity(p.getEyeLocation().getDirection().setY(2).multiply(1.8D));
			else tnt.setVelocity(p.getEyeLocation().getDirection().multiply(1.8D));
			new BukkitRunnable() {
				@Override
				public void run() {
					if (tnt.isDead() || tnt.isOnGround())
						cancel();
					if (tnt.getNearbyEntities(0.7D, 1.0D, 0.7D) != null)
						for (Entity entity : tnt.getNearbyEntities(0.7D, 1.0D, 0.7D)) {
							Player t = (Player) entity;
							if (t == p)
								return;
							t.damage(5.0D);
							p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
							cancel();
						}
				}
			}.runTaskTimer(Main.getPlugin(), 2L, 2L);
			if (p.getInventory().getItemInMainHand().getType() == Material.TNT) {
				if (p.getInventory().getItemInMainHand().getAmount() > 1)
					p.getInventory().getItemInMainHand()
							.setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
				else
					p.getInventory().remove(Material.TNT);
			} else if (p.getInventory().getItemInOffHand().getType() == Material.TNT) {
				if (p.getInventory().getItemInOffHand().getAmount() > 1)
					p.getInventory().getItemInOffHand().setAmount(p.getInventory().getItemInOffHand().getAmount() - 1);
				else
					p.getInventory().remove(Material.TNT);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p) && Main.getPvp()) {
				if (e.getCause() == EntityDamageEvent.DamageCause.FALL)
					e.setDamage(e.getDamage() / 2.0D);
				else e.setDamage(e.getDamage() / 3);
				e.setCancelled(false);
			} else
				e.setCancelled(true);
		}
	}
}