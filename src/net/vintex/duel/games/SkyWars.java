package net.vintex.duel.games;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.utils.Utils;

public class SkyWars implements Listener {

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
			specs.sendMessage(
					Main.getGamePrefix() + p.getDisplayName() + " §7wurde von §6" + k.getDisplayName() + " §7getötet.");
		}
		Utils.win(k, p);
		p.spigot().respawn();
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getView().getTitle().contains("Kitauswahl")) {
			if (e.getPlayer().getInventory().getItem(0) == null) {
				/*
				 * InventoryMenuBuilder inv = new InventoryMenuBuilder(3*9, "Â§8Kitauswahl");
				 * 
				 * inv.withItem(2+9, new
				 * ItemBuilder(Material.STICK).buildMeta().withEnchant(Enchantment.KNOCKBACK, 1,
				 * false).withDisplayName("Â§aKnockbackstick") .withLore("Â§6Â§nAusrÃ¼stung:",
				 * "Â§71x Knockbackstick (RÃ¼ckstoÃŸ II)",
				 * "Â§71x Diamantschuhe (Federfall III)").item().build(), new ItemListener() {
				 * public void onInteract(Player p, ClickType clickType, ItemStack itemStack) {
				 * p.getInventory().setItem(0, new
				 * ItemBuilder(Material.STICK).buildMeta().withEnchant(Enchantment.KNOCKBACK, 1,
				 * false).item().build()); p.getInventory().setBoots(new
				 * ItemBuilder(Material.DIAMOND_BOOTS).buildMeta().withEnchant(Enchantment.
				 * PROTECTION_FALL, 2, false).item().build()); p.closeInventory();
				 * p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 10.0F); } },
				 * ClickType.LEFT);
				 * 
				 * inv.withItem(3+9, new
				 * ItemBuilder(Material.BLAZE_POWDER).buildMeta().withDisplayName("Â§bMagier")
				 * .withLore("Â§6Â§nAusrÃ¼stung:", "Â§71x Wurftrank der StÃ¤rke I (0:30)",
				 * "Â§71x Wurftrank der Regeneration I (0:33)",
				 * "Â§71x Wurftrank der Langsamkeit I (1:07)",
				 * "Â§71x Wurftrank der SchwÃ¤che I (1:07)").item().build(), new ItemListener()
				 * { public void onInteract(Player p, ClickType clickType, ItemStack itemStack)
				 * { p.getInventory().setItem(0, new
				 * ItemBuilder(Material.SPLASH_POTION).buildMeta(PotionMetaBuilder.class).
				 * withBasePotionData(PotionType.STRENGTH).item().build());
				 * p.getInventory().setItem(1, new
				 * ItemBuilder(Material.SPLASH_POTION).buildMeta(PotionMetaBuilder.class).
				 * withBasePotionData(PotionType.REGEN).item().build());
				 * p.getInventory().setItem(2, new
				 * ItemBuilder(Material.SPLASH_POTION).buildMeta(PotionMetaBuilder.class).
				 * withBasePotionData(PotionType.SLOWNESS).item().build());
				 * p.getInventory().setItem(3, new
				 * ItemBuilder(Material.SPLASH_POTION).buildMeta(PotionMetaBuilder.class).
				 * withBasePotionData(PotionType.WEAKNESS).item().build()); p.closeInventory();
				 * p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 10.0F); } },
				 * ClickType.LEFT);
				 * 
				 * inv.withItem(4+9, new
				 * ItemBuilder(Material.BOW).buildMeta().withDisplayName("Â§cBogen")
				 * .withLore("Â§6Â§nAusrÃ¼stung:", "Â§71x Bogen", "Â§73x Pfeil").item().build(),
				 * new ItemListener() { public void onInteract(Player p, ClickType clickType,
				 * ItemStack itemStack) { p.getInventory().setItem(0, new
				 * ItemBuilder(Material.BOW).build()); p.getInventory().setBoots(new
				 * ItemBuilder(Material.ARROW, 3).build()); p.closeInventory();
				 * p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 10.0F); } },
				 * ClickType.LEFT);
				 * 
				 * inv.withItem(5+9, new
				 * ItemBuilder(Material.IRON_SWORD).buildMeta().withDisplayName("Â§eAssassin")
				 * .withLore("Â§6Â§nAusrÃ¼stung:", "Â§71x Eisenschwert (SchÃ¤rfe I)",
				 * "Â§71x Eisenschuhe (Federfall III)").item().build(), new ItemListener() {
				 * public void onInteract(Player p, ClickType clickType, ItemStack itemStack) {
				 * p.getInventory().setItem(0, new
				 * ItemBuilder(Material.IRON_SWORD).buildMeta().withEnchant(Enchantment.
				 * DAMAGE_ALL, 0, false).item().build()); p.getInventory().setBoots(new
				 * ItemBuilder(Material.IRON_BOOTS).buildMeta().withEnchant(Enchantment.
				 * PROTECTION_FALL, 2, false).item().build()); p.closeInventory();
				 * p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 10.0F); } },
				 * ClickType.LEFT);
				 * 
				 * inv.withItem(6+9, new
				 * ItemBuilder(Material.IRON_CHESTPLATE).buildMeta().withDisplayName("Â§dTank")
				 * .withLore("Â§6Â§nAusrÃ¼stung:", "Â§71x Eisenhelm", "Â§71x Eisenhemd",
				 * "Â§71x Eisenhose", "Â§71x Eisenschuhe",
				 * "Â§71x Goldener Apfel").item().build(), new ItemListener() { public void
				 * onInteract(Player p, ClickType clickType, ItemStack itemStack) {
				 * p.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).build());
				 * p.getInventory().setChestplate(new
				 * ItemBuilder(Material.IRON_CHESTPLATE).build());
				 * p.getInventory().setLeggings(new
				 * ItemBuilder(Material.IRON_LEGGINGS).build()); p.getInventory().setBoots(new
				 * ItemBuilder(Material.IRON_BOOTS).build()); p.getInventory().setItem(0, new
				 * ItemBuilder(Material.GOLDEN_APPLE).build()); p.closeInventory();
				 * p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 10.0F); } },
				 * ClickType.LEFT);
				 * 
				 * inv.show(e.getPlayer());
				 */
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE)) {
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p))
				e.setCancelled(false);
			else
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE)) {
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p))
				e.setCancelled(false);
			else
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.CREATIVE)) {
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p))
				e.setCancelled(false);
			else
				e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (Main.getGameState() == GameState.ingame && Main.getPlayers().contains(p)
					&& !Main.getSpectator().contains(p) && Main.getPvp()) {
				e.setCancelled(false);
			} else
				e.setCancelled(true);
		}

	}
}