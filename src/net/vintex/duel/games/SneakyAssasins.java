package net.vintex.duel.games;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.Prio;
import net.vintex.duel.utils.Utils;

public class SneakyAssasins implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {

		Player p = e.getEntity();
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
			specs.sendMessage(
					Main.getGamePrefix() + p.getDisplayName() + " §7wurde von §6" + k.getDisplayName() + " §7getötet.");
		}
		Utils.win(k, p);
		p.spigot().respawn();

	}

	@EventHandler
	public void onNPCRightClick(NPCRightClickEvent e) {
		if (e.getClicker().getInventory().getItemInMainHand() != null
				&& e.getClicker().getInventory().getItemInMainHand().getType() == Material.GOLD_NUGGET) {
			e.getClicker().getInventory().remove(Material.GOLD_NUGGET);
			e.getClicker().playSound(e.getClicker().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 2);
			Prio.buyed.put(Prio.getPrios().get(new Random().nextInt(Prio.getPrios().size())).getNpc().getEntity()
					.getEntityId(), e.getClicker());
		}
	}

	@EventHandler
	public void onNPC(NPCDeathEvent e) {
		e.getNPC().destroy();
	}

	@EventHandler
	public void onNPCLeftClick(NPCDamageByEntityEvent e) {
		Player p = (Player) e.getDamager();
		if (Main.getPlayers().contains(p) && e.getNPC().getName().contains("Power")
				&& p.getInventory().getItemInMainHand() != null
				&& (p.getInventory().getItemInMainHand().getType() == Material.STONE_SWORD
						|| p.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD)) {
			e.getNPC().despawn();
			e.getNPC().destroy();
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 2);
			random(p);
			Bukkit.broadcastMessage(" §6§l» §eDas PowerUp wurde von " + p.getDisplayName() + " §eaufgenommen!");
		}
	}

	public boolean random(Player p) {
		int armor = new Random().nextInt(5);
		String to = "";
		String as = "";
		if (armor == 0) {
			to = "BOOTS";
			if (p.getInventory().getBoots() == null)
				as = "LEATHER";
			if (p.getInventory().getBoots().getType().toString().contains("LEATHER"))
				as = "CHAINMAIL";
			if (p.getInventory().getBoots().getType().toString().contains("CHAINMAIL"))
				return random(p);
			p.getInventory().setBoots(new ItemStack(Material.valueOf(as + "_" + to)));
		}
		if (armor == 1) {
			to = "LEGGINGS";
			if (p.getInventory().getLeggings() == null)
				as = "LEATHER";
			if (p.getInventory().getLeggings().getType().toString().contains("LEATHER"))
				as = "CHAINMAIL";
			if (p.getInventory().getLeggings().getType().toString().contains("CHAINMAIL"))
				return random(p);
			p.getInventory().setLeggings(new ItemStack(Material.valueOf(as + "_" + to)));
		}
		if (armor == 2) {
			to = "CHESTPLATE";
			if (p.getInventory().getChestplate() == null)
				as = "LEATHER";
			if (p.getInventory().getChestplate().getType().toString().contains("LEATHER"))
				as = "CHAINMAIL";
			if (p.getInventory().getChestplate().getType().toString().contains("CHAINMAIL"))
				return random(p);
			p.getInventory().setChestplate(new ItemStack(Material.valueOf(as + "_" + to)));
		}
		if (armor == 3) {
			to = "HELMET";
			if (p.getInventory().getHelmet() == null)
				as = "LEATHER";
			if (p.getInventory().getHelmet().getType().toString().contains("LEATHER"))
				as = "CHAINMAIL";
			if (p.getInventory().getHelmet().getType().toString().contains("CHAINMAIL"))
				return random(p);
			p.getInventory().setHelmet(new ItemStack(Material.valueOf(as + "_" + to)));
		}
		if (armor == 4) {
			to = "SWORD";
			if (p.getInventory().getItemInMainHand() == null)
				as = "STONE";
			if (p.getInventory().getItemInMainHand().getType().toString().contains("STONE"))
				as = "IRON";
			if (p.getInventory().getItemInMainHand().getType().toString().contains("IRON"))
				return random(p);
			p.getInventory().setItemInMainHand(new ItemStack(Material.valueOf(as + "_" + to)));
		}
		return false;
	}

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
			if (!e.getBlock().getType().isSolid() && e.getBlock().getType() != Material.FLOWER_POT
					&& !e.getBlock().getType().toString().contains("RAIL"))
				e.setCancelled(false);
			else
				e.setCancelled(true);
		} else
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

		if (e.getCause() == DamageCause.ENTITY_EXPLOSION || e.getCause() == DamageCause.BLOCK_EXPLOSION) {
			e.setCancelled(true);
			return;
		}
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p)
					&& !Main.getSpectator().contains(p) && Main.getPvp()) {
				if (e.getCause() == DamageCause.FALL) {
					e.setDamage(e.getDamage() / 2);
				}
				e.setCancelled(false);
			} else
				e.setCancelled(true);
		}

	}
}