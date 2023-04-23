package net.vintex.duel.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.vintex.duel.Game;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.utils.Utils;

public class Schutzzeit {

	public static int schedulerbuild;
	public static int cdbuild = 11;

	public static void start() {

		final int fcd = cdbuild - 1;

		schedulerbuild = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (cdbuild != 0) {
					cdbuild -= 1;
					for (Player players : Bukkit.getOnlinePlayers()) {
						players.setExp(cdbuild / fcd);
						Utils.sendActionBar(players, "§eDie Schutzzeit endet in §l" + cdbuild);
					}
					if (cdbuild == 19) {
						Main.setGameState(GameState.prepare);
						for (Player players : Main.getPlayers()) {

							/*
							 * InventoryMenuBuilder inv = new InventoryMenuBuilder(3*9, "§8Kitauswahl");
							 * 
							 * inv.withItem(2+9, new
							 * ItemBuilder(Material.STICK).buildMeta().withEnchant(Enchantment.KNOCKBACK, 1,
							 * false).withDisplayName("§aKnockbackstick") .withLore("§6§nAusrüstung:",
							 * "§71x Knockbackstick (Rückstoß II)",
							 * "§71x Diamantschuhe (Federfall III)").item().build(), new ItemListener() {
							 * public void onInteract(Player p, ClickType clickType, ItemStack itemStack) {
							 * p.closeInventory(); p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,
							 * 10.0F, 10.0F);
							 * 
							 * p.getInventory().setItem(0, new
							 * ItemBuilder(Material.STICK).buildMeta().withEnchant(Enchantment.KNOCKBACK, 1,
							 * false).item().build()); p.getInventory().setBoots(new
							 * ItemBuilder(Material.DIAMOND_BOOTS).buildMeta().withEnchant(Enchantment.
							 * PROTECTION_FALL, 2, false).item().build()); } }, ClickType.LEFT);
							 * 
							 * inv.withItem(3+9, new
							 * ItemBuilder(Material.BLAZE_POWDER).buildMeta().withDisplayName("§bMagier")
							 * .withLore("§6§nAusrüstung:", "§71x Wurftrank der Stärke I (0:30)",
							 * "§71x Wurftrank der Regeneration I (0:33)",
							 * "§71x Wurftrank der Langsamkeit I (1:07)",
							 * "§71x Wurftrank der Schwäche I (1:07)").item().build(), new ItemListener() {
							 * public void onInteract(Player p, ClickType clickType, ItemStack itemStack) {
							 * p.closeInventory(); p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,
							 * 10.0F, 10.0F);
							 * 
							 * p.getInventory().setItem(0, new
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
							 * withBasePotionData(PotionType.WEAKNESS).item().build()); } },
							 * ClickType.LEFT);
							 * 
							 * inv.withItem(4+9, new
							 * ItemBuilder(Material.BOW).buildMeta().withDisplayName("§cBogen")
							 * .withLore("§6§nAusrüstung:", "§71x Bogen", "§73x Pfeil").item().build(), new
							 * ItemListener() { public void onInteract(Player p, ClickType clickType,
							 * ItemStack itemStack) { p.closeInventory(); p.playSound(p.getLocation(),
							 * Sound.UI_BUTTON_CLICK, 10.0F, 10.0F);
							 * 
							 * p.getInventory().setItem(0, new ItemBuilder(Material.BOW).build());
							 * p.getInventory().setBoots(new ItemBuilder(Material.ARROW, 3).build()); } },
							 * ClickType.LEFT);
							 * 
							 * inv.withItem(5+9, new
							 * ItemBuilder(Material.IRON_SWORD).buildMeta().withDisplayName("§eAssassin")
							 * .withLore("§6§nAusrüstung:", "§71x Eisenschwert (Schärfe I)",
							 * "§71x Eisenschuhe (Federfall III)").item().build(), new ItemListener() {
							 * public void onInteract(Player p, ClickType clickType, ItemStack itemStack) {
							 * p.closeInventory(); p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,
							 * 10.0F, 10.0F);
							 * 
							 * p.getInventory().setItem(0, new
							 * ItemBuilder(Material.IRON_SWORD).buildMeta().withEnchant(Enchantment.
							 * DAMAGE_ALL, 0, false).item().build()); p.getInventory().setBoots(new
							 * ItemBuilder(Material.IRON_BOOTS).buildMeta().withEnchant(Enchantment.
							 * PROTECTION_FALL, 2, false).item().build()); } }, ClickType.LEFT);
							 * 
							 * inv.withItem(6+9, new
							 * ItemBuilder(Material.IRON_CHESTPLATE).buildMeta().withDisplayName("§dTank")
							 * .withLore("§6§nAusrüstung:", "§71x Eisenhelm", "§71x Eisenhemd",
							 * "§71x Eisenhose", "§71x Eisenschuhe", "§71x Goldener Apfel").item().build(),
							 * new ItemListener() { public void onInteract(Player p, ClickType clickType,
							 * ItemStack itemStack) { p.closeInventory(); p.playSound(p.getLocation(),
							 * Sound.UI_BUTTON_CLICK, 10.0F, 10.0F);
							 * 
							 * p.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).build());
							 * p.getInventory().setChestplate(new
							 * ItemBuilder(Material.IRON_CHESTPLATE).build());
							 * p.getInventory().setLeggings(new
							 * ItemBuilder(Material.IRON_LEGGINGS).build()); p.getInventory().setBoots(new
							 * ItemBuilder(Material.IRON_BOOTS).build()); p.getInventory().setItem(0, new
							 * ItemBuilder(Material.GOLDEN_APPLE).build()); } }, ClickType.LEFT);
							 * 
							 * inv.show(players);
							 */
						}
					}
					if (cdbuild <= 5) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 10.0F);
						}
					}
					if (cdbuild == 0) {
						Bukkit.getScheduler().cancelTask(schedulerbuild);
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
							Utils.sendActionBar(players, " ");

							if (Main.getPlayers().contains(players)) {

							}
						}
						Main.setGameState(GameState.ingame);
						Main.setPvp(true);
						if (Main.getGame().equals(Game.bowduel) || Main.getGame().equals(Game.woolblock)
								)//|| Main.getGame().equals("SkyWars"))
							End.start();
						else
							RoundEnd.start();
						schedulerbuild = -1;
						cdbuild = 11;
					}
				}
			}
		}, 0L, 20L);
	}
}
