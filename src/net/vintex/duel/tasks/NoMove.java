package net.vintex.duel.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import de.realjongi.cloudy.spigot.api.CloudAPI;
import de.realjongi.cloudy.spigot.cloudyutils.GameStatus;
import itembuilder.ItemBuilder;
import itembuilder.LeatherArmorMetaBuilder;
import itembuilder.PotionMetaBuilder;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.VillagerWatcher;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Blocks;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.vintex.api.API;
import net.vintex.duel.Game;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.Prio;
import net.vintex.duel.games.SuperJump;
import net.vintex.duel.utils.RandomMap;
import net.vintex.duel.utils.Scoreboard;
import net.vintex.duel.utils.TopTime;
import net.vintex.duel.utils.Utils;

public class NoMove {

	public static int schedulernomove;
	public static int cdnomove = 9;
	public static int animationtask;
	public static double duration = 0.5;

	public static void start() {	
			
		if (Bukkit.getOnlinePlayers().size() != 2)
			return;
		schedulernomove = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (cdnomove != 0) {
					cdnomove -= 1;
					if (cdnomove == 8) {
						MinecraftServer.getServer().setMotd("§8Ingame-" + RandomMap.winnermap);
						CloudAPI.setServerStatus(GameStatus.INGAME);
						Main.setGameState(GameState.prepare);
						 World world = Bukkit.getWorld(RandomMap.winnermap);
						
						 if(Main.getGame().equals(Game.classic) && !Main.getKit().equals("QuickSG")) {
							 Bukkit.getConsoleSender().sendMessage("§7Entferne alle Kisten.."); 
							 Location loc1 = new Location(world, 0, 0, 0);
							 for(int y = 0; loc1.getY()<=150; y++) { for(int x = -150; x<=150; x++) {
								 for(int z = -150; z<=150; z++) { loc1 = new Location(world, x, y, z);
								 	if(loc1.getBlock().getType() == Material.CHEST) {
								 		loc1.getBlock().setType(Material.AIR); 
								 		} 
								 	}
								 } 
							 } 
						}
						 
						
						Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
							@Override
							public void run() {
								for (Player players : Bukkit.getOnlinePlayers()) {
									players.playSound(players.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
								}
								Bukkit.broadcastMessage(
										Main.getGamePrefix() + "§3Es wird auf der Map §f" + RandomMap.winnermap
												+ " §3von §b" + RandomMap.winnermapbuilder + " §3gespielt.");
								if (Main.getGame().equals(Game.superjump)) {
									SuperJump.fertig = false;
									for (Player players : Bukkit.getOnlinePlayers()) {
										TopTime topTime = TopTime.getPlayers().get(players);
										if (topTime.getTopTimes().containsKey(RandomMap.winnermap)) {
											players.sendMessage(Main.getGamePrefix() + "§7Bestzeit §8» §e"
													+ topTime.getTopTimeAsString(RandomMap.winnermap,
															topTime.getTopTimes().get(RandomMap.winnermap)
																	.split("/")[0],
															topTime.getTopTimes().get(RandomMap.winnermap)
																	.split("/")[1]));
										} else {
											players.sendMessage(Main.getGamePrefix() + "§7Bestzeit §8» §eKeine");
										}
									}
								}
								Bukkit.broadcastMessage(" ");
							}
						}, 1L);
					} else if (cdnomove == 6) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
						}
					} else if (cdnomove == 5) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 1);
							players.sendTitle("§e" + Main.getGamePrefix(), "§7Vintex.net", 0, 20, 5);
							players.setExp(0);
							Utils.sendActionBar(players, "");
						}
					} else if (cdnomove == 3) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 1);
							players.sendTitle("§c➌", "");
							Utils.sendActionBar(players, "");
						}
					} else if (cdnomove == 2) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 1);
							players.sendTitle("§e➋", "");
						}
					} else if (cdnomove == 1) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 1);
							players.sendTitle("§a➊", "");
						}
					} else if (cdnomove == 0) {
						Bukkit.getScheduler().cancelTask(schedulernomove);
						for (Player players : Bukkit.getOnlinePlayers()) {
							players.playSound(players.getLocation(), Sound.BLOCK_BELL_USE, 10, 10);
							players.sendTitle("§cSTART!", "§fViel Glück, " + players.getDisplayName() + "§f!", 0, 20,
									5);
						}
						Main.setGameState(GameState.ingame);

						schedulernomove = -1;
						cdnomove = 4;
						if (Main.getGame().equals(Game.knockdown)) {
							Main.todeshöhe = Main.getCfg()
									.getDouble("Spawn." + Main.getGame().getFriendly() + "." + RandomMap.winnermap + ".TodesHöhe");
							RoundEnd.start();
							Main.setPvp(true);
							for (Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);

								p.getInventory().setItem(0, new ItemBuilder(Material.STICK).buildMeta()
										.withEnchant(Enchantment.KNOCKBACK, 2, true).item().build());
								p.getInventory().setItem(2, new ItemBuilder(Material.FIREWORK_ROCKET).buildMeta()
										.withDisplayName("§dJetpack").item().build());
								p.getInventory().setItem(1, new ItemBuilder(Material.SANDSTONE, 6).build());
							}
						} else if (Main.getGame().equals(Game.levitation)) {
							Main.todeshöhe = Main.getCfg()
									.getDouble("Spawn." + Main.getGame().getFriendly() + "." + RandomMap.winnermap + ".TodesHöhe");
							RoundEnd.start();
							Main.setPvp(true);
							for (Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);

								p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 4),
										false);
							}
						/*} else if (Main.getGame().equals("SkyWars")) {
							Schutzzeit.start();
							Main.setGameState(GameState.prepare);
							Main.setPvp(true);
							for (Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);
								p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 4), true);
								p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 20, 0), true);
							}*/
						} else if (Main.getGame().equals(Game.superjump)) {
							Main.todeshöhe = Main.getCfg()
									.getDouble("Spawn." + Main.getGame().getFriendly() + "." + RandomMap.winnermap + ".TodesHöhe");
							End.start();
							Main.setPvp(true);
							for (Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);

								SuperJump.cachedCps.put(p, new ArrayList<>());
								p.getInventory().setItem(4, new ItemBuilder(Material.RED_DYE).buildMeta()
										.withDisplayName("§cZum letzten Checkpoint").item().build());
							}
						} else if (Main.getGame().equals(Game.buildwars)) {
							Main.todeshöhe = Main.getCfg()
									.getDouble("Spawn." + Main.getGame().getFriendly() + "." + RandomMap.winnermap + ".TodesHöhe");
							End.start();
							Main.setPvp(true);
							for (Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);

								p.getInventory().setItem(0, new ItemBuilder().withType(Material.WOODEN_SWORD).build());
								p.getInventory().setItem(1, new ItemBuilder().withType(Material.STONE_PICKAXE)
										.buildMeta().withEnchant(Enchantment.DIG_SPEED, 0, true).item().build());
								p.getInventory().setItem(2,
										new ItemBuilder().withType(Material.SANDSTONE).withAmount(64).build());
								p.getInventory().setItem(3,
										new ItemBuilder().withType(Material.SANDSTONE).withAmount(64).build());

								p.getInventory().setHelmet(new ItemBuilder().withType(Material.IRON_HELMET).build());
								p.getInventory()
										.setChestplate(new ItemBuilder().withType(Material.IRON_CHESTPLATE).build());
								p.getInventory()
										.setLeggings(new ItemBuilder().withType(Material.IRON_LEGGINGS).build());
								p.getInventory().setBoots(new ItemBuilder().withType(Material.IRON_BOOTS).build());
							}
						} else if (Main.getGame().equals(Game.classic)) {
							Main.todeshöhe = Main.getCfg()
									.getDouble("Spawn." + Main.getGame().getFriendly() + "." + RandomMap.winnermap + ".TodesHöhe");
							RoundEnd.start();
							Main.setPvp(true);
							//Bukkit.broadcastMessage("PLAYING CLASSIC WITH KIT " + Main.getKit());

							for (Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.setFireTicks(0);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);
							}

							if (Main.getKit().equals("Trash")) {
								Main.setGameState(GameState.prepare);
								new BukkitRunnable() {
									int i = 1;

									@Override
									public void run() {
										for (Player players : Bukkit.getOnlinePlayers())
											Utils.playClickSound(players);
										if (i <= 14) {
											Inventory inv = Bukkit.createInventory(null, 54, "§8Trash");
											int ns = new Random().nextInt(5);
											if (ns == 0)
												ns++;
											List<ItemStack> items = new ArrayList<ItemStack>();
											// GOOD
											items.add(new ItemStack(Material.COOKED_BEEF, ns));
											items.add(new ItemStack(Material.COOKED_BEEF, ns));
											items.add(new ItemStack(Material.COOKED_BEEF, ns));
											items.add(new ItemStack(Material.COOKED_PORKCHOP, ns));
											items.add(new ItemStack(Material.COOKED_PORKCHOP, ns));
											items.add(new ItemStack(Material.COOKED_PORKCHOP, ns));
											items.add(new ItemStack(Material.COOKED_RABBIT, ns));
											items.add(new ItemStack(Material.COOKED_RABBIT, ns));
											items.add(new ItemStack(Material.COOKED_RABBIT, ns));
											items.add(new ItemStack(Material.WOODEN_SWORD));
											items.add(new ItemStack(Material.WOODEN_SWORD));
											items.add(new ItemStack(Material.STONE_SWORD));
											items.add(new ItemStack(Material.STONE_SWORD));
											items.add(new ItemStack(Material.STONE_SWORD));
											items.add(new ItemStack(Material.DIAMOND_SWORD));
											items.add(new ItemStack(Material.IRON_BOOTS));
											items.add(new ItemStack(Material.IRON_BOOTS));
											items.add(new ItemStack(Material.IRON_CHESTPLATE));
											items.add(new ItemStack(Material.IRON_CHESTPLATE));
											items.add(new ItemStack(Material.IRON_LEGGINGS));
											items.add(new ItemStack(Material.IRON_LEGGINGS));
											items.add(new ItemStack(Material.IRON_HELMET));
											items.add(new ItemStack(Material.IRON_HELMET));
											items.add(new ItemStack(Material.IRON_SWORD));
											items.add(new ItemStack(Material.GOLDEN_BOOTS));
											items.add(new ItemStack(Material.GOLDEN_BOOTS));
											items.add(new ItemStack(Material.GOLDEN_BOOTS));
											items.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
											items.add(new ItemStack(Material.LEATHER_LEGGINGS));
											items.add(new ItemStack(Material.GOLDEN_CHESTPLATE));
											items.add(new ItemStack(Material.GOLDEN_CHESTPLATE));
											items.add(new ItemStack(Material.GOLDEN_CHESTPLATE));
											items.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
											items.add(new ItemStack(Material.GOLDEN_HELMET));
											items.add(new ItemStack(Material.GOLDEN_HELMET));
											items.add(new ItemStack(Material.GOLDEN_HELMET));
											items.add(new ItemStack(Material.GOLDEN_LEGGINGS));
											items.add(new ItemStack(Material.GOLDEN_LEGGINGS));
											items.add(new ItemStack(Material.GOLDEN_LEGGINGS));
											items.add(new ItemStack(Material.TNT, ns));
											items.add(new ItemStack(Material.TNT, ns));
											items.add(new ItemStack(Material.FISHING_ROD));
											items.add(new ItemStack(Material.FISHING_ROD));
											items.add(new ItemStack(Material.BOW));
											items.add(new ItemStack(Material.BOW));
											items.add(new ItemStack(Material.ARROW, ns));
											items.add(new ItemStack(Material.ARROW, ns));
											items.add(new ItemStack(Material.SNOWBALL, ns));
											items.add(new ItemStack(Material.SNOWBALL, ns));
											items.add(new ItemStack(Material.GOLDEN_APPLE));
											items.add(new ItemStack(Material.GOLDEN_APPLE));
											items.add(new ItemStack(Material.GOLDEN_APPLE));
											items.add(tippedArrow(PotionType.SLOWNESS, ns));

											// BAD
											items.add(new ItemStack(Material.DEAD_BUSH, ns));
											items.add(new ItemStack(Material.DEAD_BUSH, ns));
											items.add(new ItemStack(Material.DIRT, ns));
											items.add(new ItemStack(Material.DIRT, ns));
											items.add(new ItemStack(Material.BLAZE_POWDER));
											items.add(new ItemStack(Material.BRICK, ns));
											items.add(new ItemStack(Material.BRICK, ns));
											items.add(new ItemStack(Material.BONE, ns));
											items.add(new ItemStack(Material.BONE, ns));
											items.add(new ItemStack(Material.CLAY_BALL, ns));
											items.add(new ItemStack(Material.CLAY_BALL, ns));
											items.add(new ItemStack(Material.COAL, ns));
											items.add(new ItemStack(Material.COAL, ns));
											items.add(new ItemStack(Material.CLAY_BALL, ns));
											items.add(new ItemStack(Material.CLAY_BALL, ns));
											items.add(new ItemStack(Material.ROTTEN_FLESH, ns));
											items.add(new ItemStack(Material.ROTTEN_FLESH, ns));
											for (int slot = 0; slot <= 53; slot++) {
												inv.setItem(slot, items.get(new Random().nextInt(items.size())));
											}
											for (Player players : Bukkit.getOnlinePlayers())
												players.openInventory(inv);
											i++;
										} else {
											Schutzzeit.start();
											for (Player players : Bukkit.getOnlinePlayers())
												players.closeInventory();
											cancel();
										}
									}
								}.runTaskTimer(Main.getPlugin(), 0, 15);
							} else if (Main.getKit().equals("SG")) {
								for (Player p : Main.getPlayers()) {
									p.getInventory().setItem(0,
											new ItemBuilder().withType(Material.WOODEN_AXE).build());
								}
							} else if (Main.getKit().equals("Lava")) {
								for (Player p : Main.getPlayers()) {
									p.getInventory().setItem(0,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(1,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(2,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(3,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(4,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(5,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(6,
											new ItemBuilder().withType(Material.COBWEB).withAmount(2).build());
									p.getInventory().setItem(7,
											new ItemBuilder().withType(Material.SANDSTONE).withAmount(32).build());
									p.getInventory().setItem(8, new ItemBuilder().withType(Material.DIAMOND_PICKAXE)
											.buildMeta().withEnchant(Enchantment.DIG_SPEED, 2, true).item().build());

									p.getInventory().setItem(27,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(28,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(29,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(30,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(31,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());
									p.getInventory().setItem(32,
											new ItemBuilder().withType(Material.LAVA_BUCKET).build());

									p.getInventory()
											.setHelmet(new ItemBuilder().withType(Material.GOLDEN_HELMET).build());
									p.getInventory().setChestplate(
											new ItemBuilder().withType(Material.DIAMOND_CHESTPLATE).build());
									p.getInventory()
											.setLeggings(new ItemBuilder().withType(Material.GOLDEN_LEGGINGS).build());
									p.getInventory()
											.setBoots(new ItemBuilder().withType(Material.GOLDEN_BOOTS).build());
								}
							} else if (Main.getKit().equals("Sniper")) {
								for (Player p : Main.getPlayers()) {
									p.getInventory().setItem(0,
											new ItemBuilder().withType(Material.WOODEN_SWORD).build());
									p.getInventory().setItem(1,
											new ItemBuilder().withType(Material.BOW).buildMeta()
													.withEnchant(Enchantment.ARROW_DAMAGE, 3, true)
													.withEnchant(Enchantment.ARROW_KNOCKBACK, 1, true)
													.withEnchant(Enchantment.ARROW_INFINITE, 1, true).item().build());
									p.getInventory().setItem(5,
											new ItemBuilder().withType(Material.JUNGLE_LEAVES).withAmount(16).build());
									p.getInventory().setItem(8, new ItemBuilder().withType(Material.ARROW).build());

									p.getInventory()
											.setHelmet(new ItemBuilder().withType(Material.CHAINMAIL_HELMET).build());
									p.getInventory().setChestplate(
											new ItemBuilder().withType(Material.CHAINMAIL_CHESTPLATE).build());
									p.getInventory().setLeggings(
											new ItemBuilder().withType(Material.CHAINMAIL_LEGGINGS).build());
									p.getInventory()
											.setBoots(new ItemBuilder().withType(Material.CHAINMAIL_BOOTS).build());
								}
							} else if (Main.getKit().equals("Taktik")) {
								for (Player p : Main.getPlayers()) {
									p.getInventory().setItem(0,
											new ItemBuilder().withType(Material.STONE_SWORD).build());
									p.getInventory().setItem(1, new ItemBuilder().withType(Material.BOW).build());
									p.getInventory().setItem(2,
											new ItemBuilder().withType(Material.SPLASH_POTION)
													.buildMeta(PotionMetaBuilder.class)
													.withCustomEffect(PotionEffectType.HARM, 0, 1)
													.withColor(Color.PURPLE).item().build());
									p.getInventory().setItem(3,
											new ItemBuilder().withType(Material.SPLASH_POTION)
													.buildMeta(PotionMetaBuilder.class)
													.withCustomEffect(PotionEffectType.HEAL, 0, 1)
													.withColor(Color.FUCHSIA).item().build());

									p.getInventory().setItem(5,
											new ItemBuilder().withType(Material.ARROW).withAmount(5).build());
									p.getInventory().setItem(6,
											new ItemBuilder().withType(Material.GOLDEN_APPLE).build());
									p.getInventory().setItem(7,
											new ItemBuilder().withType(Material.FIRE_CHARGE).build());

									p.getInventory()
											.setHelmet(new ItemBuilder().withType(Material.GOLDEN_HELMET).build());
									p.getInventory().setChestplate(
											new ItemBuilder().withType(Material.DIAMOND_CHESTPLATE).build());
									p.getInventory().setLeggings(
											new ItemBuilder().withType(Material.CHAINMAIL_LEGGINGS).build());
									p.getInventory()
											.setBoots(new ItemBuilder().withType(Material.LEATHER_BOOTS).build());
								}
							} else if (Main.getKit().equals("Horse'n'Arrows")) {
								// TODO: gezÃ¤hmtes pferd spawnen, gleicher sprungwert und gleiche schnelligkeit
								// der pferde
								for (Player p : Main.getPlayers()) {
									p.sendMessage("§3Kit: §bMit diesem Kit kannst du Pfeile werfen!");

									p.getInventory().setItem(0,
											new ItemBuilder().withType(Material.ARROW).withAmount(64).build());
									p.getInventory().setItem(1,
											new ItemBuilder().withType(Material.ARROW).withAmount(64).build());
									p.getInventory().setItem(2,
											new ItemBuilder().withType(Material.HAY_BLOCK).withAmount(64).build());
									p.getInventory().setItem(4,
											new ItemBuilder().withType(Material.HORSE_SPAWN_EGG).build());
									p.getInventory().setItem(5,
											new ItemBuilder().withType(Material.DIAMOND_HORSE_ARMOR).build());

									p.getInventory().setItem(7,
											new ItemBuilder().withType(Material.SPLASH_POTION)
													.buildMeta(PotionMetaBuilder.class)
													.withCustomEffect(PotionEffectType.REGENERATION, 22, 1)
													.withColor(Color.FUCHSIA).item().build());
									p.getInventory().setItem(8,
											new ItemBuilder().withType(Material.GOLDEN_APPLE).build());

									p.getInventory().setItem(28,
											new ItemBuilder().withType(Material.ARROW).withAmount(64).build());

									p.getInventory()
											.setHelmet(new ItemBuilder().withType(Material.IRON_HELMET).build());
									p.getInventory().setChestplate(
											new ItemBuilder().withType(Material.IRON_CHESTPLATE).build());
									p.getInventory().setLeggings(
											new ItemBuilder().withType(Material.CHAINMAIL_LEGGINGS).build());
									p.getInventory()
											.setBoots(new ItemBuilder().withType(Material.LEATHER_BOOTS)
													.buildMeta(LeatherArmorMetaBuilder.class).withColor(Color.RED)
													.item().build());
								}
							} else if (Main.getKit().equals("Slime")) {
								// TODO: doppelsprung

								for (Player p : Main.getPlayers()) {
									p.sendMessage("§3Kit: §bMit diesem Kit kannst du Doppelsprünge machen!");

									p.getInventory().setItem(0, new ItemBuilder().withType(Material.SLIME_BALL)
											.buildMeta().withEnchant(Enchantment.DAMAGE_ALL, 0, true).item().build());
									p.getInventory().setItem(1,
											new ItemBuilder().withType(Material.SLIME_BLOCK).withAmount(64).build());

									p.getInventory().setItem(8,
											new ItemBuilder().withType(Material.SLIME_SPAWN_EGG).withAmount(4).build());

									p.getInventory().setItem(9, new ItemBuilder().withType(Material.COBWEB).build());
									p.getInventory().setItem(14, new ItemBuilder().withType(Material.COBWEB).build());
									p.getInventory().setItem(20, new ItemBuilder().withType(Material.COBWEB).build());
									p.getInventory().setItem(33, new ItemBuilder().withType(Material.COBWEB).build());

									p.getInventory().setItem(10,
											new ItemBuilder().withType(Material.SLIME_BALL).build());
									p.getInventory().setItem(13,
											new ItemBuilder().withType(Material.SLIME_BALL).build());
									p.getInventory().setItem(17,
											new ItemBuilder().withType(Material.SLIME_BALL).build());
									p.getInventory().setItem(23,
											new ItemBuilder().withType(Material.SLIME_BALL).build());
									p.getInventory().setItem(29,
											new ItemBuilder().withType(Material.SLIME_BALL).build());
									p.getInventory().setItem(35,
											new ItemBuilder().withType(Material.SLIME_BALL).build());

									p.getInventory()
											.setHelmet(new ItemBuilder().withType(Material.LEATHER_HELMET)
													.buildMeta(LeatherArmorMetaBuilder.class).withColor(Color.GREEN)
													.item().build());
									p.getInventory()
											.setChestplate(new ItemBuilder().withType(Material.LEATHER_CHESTPLATE)
													.buildMeta(LeatherArmorMetaBuilder.class).withColor(Color.GREEN)
													.item().build());
									p.getInventory()
											.setLeggings(new ItemBuilder().withType(Material.LEATHER_LEGGINGS)
													.buildMeta(LeatherArmorMetaBuilder.class).withColor(Color.GREEN)
													.item().build());
									p.getInventory()
											.setBoots(new ItemBuilder().withType(Material.LEATHER_BOOTS)
													.buildMeta(LeatherArmorMetaBuilder.class).withColor(Color.GREEN)
													.item().build());
								}
							} else if (Main.getKit().equals("Suro")) {
								for (Player p : Main.getPlayers()) {
									p.getInventory().setItem(0, new ItemBuilder().withType(Material.DIAMOND_SWORD)
											.buildMeta().withEnchant(Enchantment.DAMAGE_ALL, 2, true).item().build());
									p.getInventory().setItem(1, new ItemBuilder().withType(Material.BOW).buildMeta()
											.withEnchant(Enchantment.ARROW_DAMAGE, 1, true).item().build());

									p.getInventory().setItem(2,
											new ItemBuilder().withType(Material.OAK_PLANKS).withAmount(44).build());

									p.getInventory().setItem(3, new ItemBuilder().withType(Material.CROSSBOW)
											.buildMeta().withEnchant(Enchantment.RIPTIDE, 1, true).item().build());

									p.getInventory().setItem(4,
											new ItemBuilder().withType(Material.POTION)
													.buildMeta(PotionMetaBuilder.class)
													.withCustomEffect(PotionEffectType.HEAL, 0, 1)
													.withColor(Color.FUCHSIA).item().build());
									p.getInventory().setItem(5,
											new ItemBuilder().withType(Material.SPLASH_POTION)
													.buildMeta(PotionMetaBuilder.class)
													.withCustomEffect(PotionEffectType.HEAL, 0, 1)
													.withColor(Color.FUCHSIA).item().build());
									p.getInventory().setItem(6,
											new ItemBuilder().withType(Material.GOLDEN_APPLE).withAmount(10).build());
									p.getInventory().setItem(7,
											new ItemBuilder().withType(Material.SPLASH_POTION)
													.buildMeta(PotionMetaBuilder.class)
													.withCustomEffect(PotionEffectType.SPEED, 60 * 3, 0)
													.withColor(Color.AQUA).item().build());
									p.getInventory().setItem(8,
											new ItemBuilder().withType(Material.WATER_BUCKET).build());

									p.getInventory().setItem(20,
											new ItemBuilder().withType(Material.BLACK_WOOL).withAmount(7).build());
									p.getInventory().setItem(29,
											new ItemBuilder().withType(Material.GLASS).withAmount(55).build());

									p.getInventory().setItem(23,
											new ItemBuilder().withType(Material.SPLASH_POTION)
													.buildMeta(PotionMetaBuilder.class)
													.withCustomEffect(PotionEffectType.HEAL, 0, 1)
													.withColor(Color.FUCHSIA).item().build());
									p.getInventory().setItem(32,
											new ItemBuilder().withType(Material.SPLASH_POTION)
													.buildMeta(PotionMetaBuilder.class)
													.withCustomEffect(PotionEffectType.HEAL, 0, 1)
													.withColor(Color.FUCHSIA).item().build());

									p.getInventory().setItem(17,
											new ItemBuilder().withType(Material.ARROW).withAmount(18).build());

									p.getInventory().setItem(30, new ItemBuilder().withType(Material.IRON_AXE)
											.buildMeta().withEnchant(Enchantment.DIG_SPEED, 1, true).item().build());

									p.getInventory()
											.setHelmet(new ItemBuilder().withType(Material.DIAMOND_HELMET).buildMeta()
													.withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
													.withEnchant(Enchantment.DURABILITY, 1, true).item().build());
									p.getInventory()
											.setChestplate(new ItemBuilder().withType(Material.DIAMOND_CHESTPLATE)
													.buildMeta()
													.withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
													.withEnchant(Enchantment.DURABILITY, 1, true).item().build());
									p.getInventory().setLeggings(new ItemBuilder().withType(Material.DIAMOND_LEGGINGS)
											.buildMeta().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
											.withEnchant(Enchantment.DURABILITY, 1, true).item().build());
									p.getInventory()
											.setBoots(new ItemBuilder().withType(Material.DIAMOND_BOOTS).buildMeta()
													.withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
													.withEnchant(Enchantment.DURABILITY, 1, true).item().build());
								}
							} else if (Main.getKit().equals("Dreizack")) {
								for (Player p : Main.getPlayers()) {
									p.getInventory().setItem(0, new ItemBuilder().withType(Material.TRIDENT).build());
									p.getInventory().setItem(1, new ItemBuilder().withType(Material.TRIDENT).build());
									p.getInventory().setItem(2, new ItemBuilder().withType(Material.TRIDENT).build());
									p.getInventory().setItem(3, new ItemBuilder().withType(Material.TRIDENT).build());
									p.getInventory().setItem(4, new ItemBuilder().withType(Material.TRIDENT).build());
									p.getInventory().setItem(5, new ItemBuilder().withType(Material.TRIDENT).build());
									p.getInventory().setItem(6, new ItemBuilder().withType(Material.TRIDENT).build());
									p.getInventory().setItem(7, new ItemBuilder().withType(Material.TRIDENT).build());
									p.getInventory().setItem(8, new ItemBuilder().withType(Material.TRIDENT).build());

									p.getInventory()
											.setHelmet(new ItemBuilder().withType(Material.DIAMOND_HELMET).build());
									p.getInventory().setChestplate(
											new ItemBuilder().withType(Material.DIAMOND_CHESTPLATE).build());
									p.getInventory().setLeggings(
											new ItemBuilder().withType(Material.CHAINMAIL_LEGGINGS).build());
									p.getInventory()
											.setBoots(new ItemBuilder().withType(Material.CHAINMAIL_BOOTS).build());
								}
							} else if (Main.getKit().equals("Classic")) {
								for (Player p : Main.getPlayers()) {
									p.getInventory().setItem(0,
											new ItemBuilder().withType(Material.STONE_SWORD).build());
									p.getInventory().setItem(1, new ItemBuilder().withType(Material.BOW).build());
									p.getInventory().setItem(5,
											new ItemBuilder().withType(Material.GOLDEN_APPLE).build());
									p.getInventory().setItem(8, tippedArrow(PotionType.TURTLE_MASTER, 3));

									p.getInventory()
											.setHelmet(new ItemBuilder().withType(Material.GOLDEN_HELMET).build());
									p.getInventory().setChestplate(
											new ItemBuilder().withType(Material.IRON_CHESTPLATE).build());
									p.getInventory().setLeggings(
											new ItemBuilder().withType(Material.CHAINMAIL_LEGGINGS).build());
									p.getInventory().setBoots(new ItemBuilder().withType(Material.IRON_BOOTS).build());
								}
							}

						} else if (Main.getGame().equals(Game.bomblobbers)) {
							Main.todeshöhe = Main.getCfg()
									.getDouble("Spawn." + Main.getGame().getFriendly() + "." + RandomMap.winnermap + ".TodesHöhe");
							End.start();
							Main.setPvp(true);
							for (final Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);

								p.getInventory().setHelmet(new ItemBuilder().withType(Material.IRON_HELMET).build());
								p.getInventory()
										.setChestplate(new ItemBuilder().withType(Material.IRON_CHESTPLATE).build());
								p.getInventory()
										.setLeggings(new ItemBuilder().withType(Material.IRON_LEGGINGS).build());
								p.getInventory().setBoots(new ItemBuilder().withType(Material.IRON_BOOTS).build());

								p.setAllowFlight(true);
								p.setFlying(false);

								final int round = Main.getRunde();
								new BukkitRunnable() {
									@Override
									public void run() {
										if (Main.getRunde() != round)
											cancel();

										if (API.getInStickerMenu().containsKey(p)) {
											int count = 0;
											int slot = 0;
											int slotcount = 0;
											for (int i = 0; i <= 35; i++) {
												if (API.getInStickerMenu().get(p).get(i) != null && API
														.getInStickerMenu().get(p).get(i).getType() == Material.TNT) {
													slot = i;
													slotcount = API.getInStickerMenu().get(p).get(i).getAmount();
													count = +API.getInStickerMenu().get(p).get(i).getAmount();
												}
											}
											if (count <= 2)
												API.getInStickerMenu().get(p).put(slot, new ItemBuilder()
														.withType(Material.TNT).withAmount(slotcount + 1).build());
										} else {
											int count = 0;
											for (int i = 0; i <= p.getInventory().getSize(); i++) {
												if (p.getInventory().getItem(i) != null
														&& p.getInventory().getItem(i).getType() == Material.TNT) {
													count = +p.getInventory().getItem(i).getAmount();
												}
											}
											if (count <= 2)
												p.getInventory()
														.addItem(new ItemBuilder().withType(Material.TNT).build());
										}
									}
								}.runTaskTimer(Main.getPlugin(), 20, 80);
							}
						} else if (Main.getGame().equals(Game.aura)) {
							End.start();
							Main.setPvp(true);
							for (Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);

								p.getInventory().setItem(0, new ItemBuilder().withType(Material.STICK).buildMeta()
										.withEnchant(Enchantment.KNOCKBACK, 4, true).item().build());
								p.getInventory().setItem(1, new ItemBuilder(Material.ENDER_PEARL, 16).build());
								p.getInventory().setItem(2, new ItemBuilder(Material.SNOWBALL, 24).build());
								p.getInventory().setItem(3, new ItemBuilder(Material.GOLDEN_APPLE).build());
								p.getInventory().setItem(4, new ItemBuilder(Material.CREEPER_SPAWN_EGG).build());
								p.getInventory().setItem(7,
										new ItemBuilder().withType(Material.SPLASH_POTION)
												.buildMeta(PotionMetaBuilder.class)
												.withCustomEffect(PotionEffectType.HEAL, 0, 1).withColor(Color.FUCHSIA)
												.item().build());
								p.getInventory().setItem(8,
										new ItemBuilder().withType(Material.SPLASH_POTION)
												.buildMeta(PotionMetaBuilder.class)
												.withCustomEffect(PotionEffectType.SPEED, 60 * 3, 0)
												.withColor(Color.AQUA).item().build());

								p.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).build());
								p.getInventory().setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).build());
								p.getInventory().setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).build());
								p.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).buildMeta()
										.withEnchant(Enchantment.PROTECTION_FALL, 10, true).item().build());
							}
						} else if (Main.getGame().equals(Game.spleef)) {
							RoundEnd.start();
							Main.setPvp(true);
							for (Player p : Main.getPlayers()) {
								API.closeStickerMenu(p);
								p.getInventory().clear();
								p.getInventory().setArmorContents(null);
								p.setHealth(20);
								p.setLevel(0);
								p.setExp(0);
								p.getInventory().setHeldItemSlot(0);

								p.getInventory().setItem(0,
										new ItemBuilder().withType(Material.DIAMOND_SHOVEL).buildMeta()
												.withEnchant(Enchantment.DIG_SPEED, 4, true).unbreakable().item()
												.build());
								p.getInventory().setItem(1, new ItemBuilder().withType(Material.BOW).withDurability(3)
										.buildMeta().withEnchant(Enchantment.ARROW_KNOCKBACK, 2, true).item().build());
								p.getInventory().setItem(8,
										new ItemBuilder().withType(Material.ARROW).withAmount(3).build());
							}
						} else if (Main.getGame().equals(Game.sneakyassasins)) {
							End.start();
							Main.setPvp(true);

							for (final Player p : Main.getPlayers()) {
								p.getInventory().setItem(0, new ItemBuilder().withType(Material.STONE_SWORD).build());
								p.getInventory().setItem(1, new ItemBuilder().withType(Material.BLACK_DYE).withAmount(4)
										.buildMeta().withDisplayName("§eRauchbombe").item().build());
								p.getInventory().setItem(2, new ItemBuilder().withType(Material.GOLD_NUGGET).buildMeta()
										.withDisplayName("§eVillager anheuern").item().build());

								p.getInventory().setHelmet(new ItemBuilder().withType(Material.LEATHER_HELMET).build());
								p.getInventory()
										.setChestplate(new ItemBuilder().withType(Material.LEATHER_CHESTPLATE).build());
								p.getInventory()
										.setLeggings(new ItemBuilder().withType(Material.LEATHER_LEGGINGS).build());
								p.getInventory().setBoots(new ItemBuilder().withType(Material.LEATHER_BOOTS).build());

								final MobDisguise mobDisguise = new MobDisguise(DisguiseType.VILLAGER);
								VillagerWatcher villagerWatcher = (VillagerWatcher) mobDisguise.getWatcher();
								villagerWatcher.setProfession(Profession.NONE);
								mobDisguise.setWatcher(villagerWatcher);
								mobDisguise.setEntity(p);
								mobDisguise.setViewSelfDisguise(false);
								mobDisguise.setSelfDisguiseVisible(false);
								mobDisguise.startDisguise();
								new BukkitRunnable() {
									@Override
									public void run() {
										p.sendMessage(" §6§l» §eDie Verkleidungen sind jetzt für kurze Zeit weg!");
										mobDisguise.stopDisguise();
										new BukkitRunnable() {
											@Override
											public void run() {
												p.sendMessage(" §6§l» §eDie Verkleidungen sind wieder da!");
												mobDisguise.startDisguise();
											}
										}.runTaskLater(Main.getPlugin(), 20 * 3);
									}
								}.runTaskTimer(Main.getPlugin(), 20 * (2 * 60), 20 * (2 * 60));
							}

							new BukkitRunnable() {

								@Override
								public void run() {
									new BukkitRunnable() {
										@Override
										public void run() {
											Bukkit.broadcastMessage(
													" §6§l» §eEs ist ein PowerUp gespawnt! Töte den Villager!");
											final Prio prio = Prio.getPrios()
													.get(new Random().nextInt(Prio.getPrios().size()));
											new BukkitRunnable() {
												@Override
												public void run() {
													if (!prio.getNpc().isSpawned())
														cancel();

													prio.getNpc().setName("§d§lPowerUp");
													for (Entity entity : prio.getNpc().getEntity().getPassengers()) {
														prio.getNpc().getEntity().removePassenger(entity);
													}

													FireworkEffect.Builder fwbuilder = FireworkEffect.builder();
													FireworkEffect effect = fwbuilder.with(FireworkEffect.Type.BURST)
															.withFlicker().withFade(Color.PURPLE,
																	Color.fromBGR(204, 0, 102), Color.FUCHSIA)
															.withColor(Color.WHITE).build();

													final Firework firework = (Firework) prio.getNpc().getEntity()
															.getWorld()
															.spawnEntity(prio.getNpc().getEntity().getLocation(),
																	EntityType.FIREWORK);
													FireworkMeta meta = firework.getFireworkMeta();
													meta.addEffect(effect);
													firework.setFireworkMeta(meta);
													firework.setSilent(true);

													new BukkitRunnable() {
														@Override
														public void run() {
															firework.detonate();
														}
													}.runTaskLater(Main.getPlugin(), 2L);
												}
											}.runTaskTimer(Main.getPlugin(), 40, 40);
										}
									}.runTaskTimer(Main.getPlugin(), 20 * (30), 20 * (60));
								}
							}.runTaskLater(Main.getPlugin(), 40);
						} else if (Main.getGame().equals(Game.woolblock)) {
							Main.todeshöhe = Main.getCfg()
									.getDouble("Spawn." + Main.getGame().getFriendly() + "." + RandomMap.winnermap + ".TodesHöhe");
							End.start();
							for (Player p : Main.getPlayers()) {
								p.setGameMode(GameMode.ADVENTURE);
								Main.getCountedint().put(p, 3);
							}

							final Map<Material, String> colors = new HashMap<Material, String>();
							final Map<Material, IBlockData> colorIds = new HashMap<Material, IBlockData>();
							final List<Material> materialColors = new ArrayList<Material>();

							new BukkitRunnable() {
								@Override
								public void run() {
									colors.put(Material.BLUE_WOOL, "§9§lBLAU");
									colors.put(Material.CYAN_WOOL, "§3§lTÜRKIS");
									colors.put(Material.GREEN_WOOL, "§2§lGRÜN");
									colors.put(Material.LIME_WOOL, "§a§lHELLGRÜN");
									colors.put(Material.ORANGE_WOOL, "§6§lORANGE");
									colors.put(Material.PINK_WOOL, "§d§lROSA");
									colors.put(Material.PURPLE_WOOL, "§5§lLILA");
									colors.put(Material.RED_WOOL, "§c§lROT");
									colors.put(Material.YELLOW_WOOL, "§e§lGELB");

									colorIds.put(Material.BLUE_WOOL, Blocks.BLUE_WOOL.getBlockData());
									colorIds.put(Material.CYAN_WOOL, Blocks.CYAN_WOOL.getBlockData());
									colorIds.put(Material.GREEN_WOOL, Blocks.GREEN_WOOL.getBlockData());
									colorIds.put(Material.LIME_WOOL, Blocks.LIME_WOOL.getBlockData());
									colorIds.put(Material.ORANGE_WOOL, Blocks.ORANGE_WOOL.getBlockData());
									colorIds.put(Material.PINK_WOOL, Blocks.PINK_WOOL.getBlockData());
									colorIds.put(Material.PURPLE_WOOL, Blocks.PURPLE_WOOL.getBlockData());
									colorIds.put(Material.RED_WOOL, Blocks.RED_WOOL.getBlockData());
									colorIds.put(Material.YELLOW_WOOL, Blocks.YELLOW_WOOL.getBlockData());
									colorIds.put(Material.AIR, Blocks.AIR.getBlockData());

									materialColors.add(Material.BLUE_WOOL);
									materialColors.add(Material.CYAN_WOOL);
									materialColors.add(Material.GREEN_WOOL);
									materialColors.add(Material.LIME_WOOL);
									materialColors.add(Material.ORANGE_WOOL);
									materialColors.add(Material.PINK_WOOL);
									materialColors.add(Material.PURPLE_WOOL);
									materialColors.add(Material.RED_WOOL);
									materialColors.add(Material.YELLOW_WOOL);
								}
							}.runTaskAsynchronously(Main.getPlugin());

							new BukkitRunnable() {
								@Override
								public void run() {
									runWoolBlock(materialColors, colors, colorIds);
								}
							}.runTaskLater(Main.getPlugin(), 20);
						}
					}
					for (Player players : Bukkit.getOnlinePlayers()) {
						Scoreboard.getScoreboard().get(players).performBoardUpdate();
						Scoreboard.getScoreboard().get(players).performRangUpdate();
					}
				}
			}
		}, 0L, 20L);
	}

	public static void setBlockInNativeWorld(World world, int x, int y, int z, final Map<Material, IBlockData> colorIds,
			Material color, boolean applyPhysics) {
		net.minecraft.server.v1_16_R3.World nmsWorld = ((CraftWorld) world).getHandle();
		BlockPosition bp = new BlockPosition(x, y, z);
		IBlockData ibd = colorIds.get(color);
		nmsWorld.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
	}

	public static ItemStack tippedArrow(PotionType potionType, int ns) {
		ItemStack result = new ItemStack(Material.TIPPED_ARROW, ns);
		PotionMeta resultMeta = (PotionMeta) result.getItemMeta();
		resultMeta.setBasePotionData(new PotionData(potionType));
		result.setItemMeta(resultMeta);
		return result;
	}

	static boolean first = true;

	public static void runWoolBlock(final List<Material> materialColors, final Map<Material, String> colors,
			final Map<Material, IBlockData> colorIds) {
		if (duration > -1)
			duration = duration - 0.1;

		List<Material> onMap = new ArrayList<Material>();

		World world = Bukkit.getWorld(RandomMap.winnermap);
		int random = new Random().nextInt(3);
		if (random == 0) {
			// RANDOM
			Location loc = new Location(world, -22, 115, 5);
			for (int x = 0; x < 24; x++) {
				for (int z = 0; z < 25; z++) {
					Location blockLoc = loc.clone().add(x, 0, z);
					Material material = materialColors.get(new Random().nextInt(materialColors.size()));
					onMap.add(material);
					setBlockInNativeWorld(world, (int) blockLoc.getX(), 115, (int) blockLoc.getZ(), colorIds, material,
							false);
				}
			}
		} else if (random == 1) {
			// LINES
			Location loc = new Location(world, -22, 115, 5);
			for (int x = 0; x < 24; x++) {
				Material lineColor = materialColors.get(new Random().nextInt(materialColors.size()));
				for (int z = 0; z < 25; z++) {
					Location blockLoc = loc.clone().add(x, 0, z);
					onMap.add(lineColor);
					setBlockInNativeWorld(world, (int) blockLoc.getX(), 115, (int) blockLoc.getZ(), colorIds, lineColor,
							false);
				}
			}
		} else if (random == 2) {
			// BIGBLOCKS
			Location loc = new Location(world, -23, 115, 5);
			int zb = 0;
			Material blockColor = materialColors.get(new Random().nextInt(materialColors.size()));
			for (int x = 1; x < 9; x++) {
				for (int z = 0; z < 25; z++) {
					onMap.add(blockColor);
					Location blockLoc = loc.clone().add(x * 3, 0, z);
					setBlockInNativeWorld(world, (int) blockLoc.getX(), 115, (int) blockLoc.getZ(), colorIds,
							blockColor, false);
					Location blockLoc1 = loc.clone().add(x * 3 - 1, 0, z);
					setBlockInNativeWorld(world, (int) blockLoc1.getX(), 115, (int) blockLoc1.getZ(), colorIds,
							blockColor, false);
					Location blockLoc2 = loc.clone().add(x * 3 - 2, 0, z);
					setBlockInNativeWorld(world, (int) blockLoc2.getX(), 115, (int) blockLoc2.getZ(), colorIds,
							blockColor, false);

					zb++;
					if (zb == 3) {
						zb = 0;
						blockColor = materialColors.get(new Random().nextInt(materialColors.size()));
					}
				}
			}
		}

		final Material chosen = onMap.get(new Random().nextInt(onMap.size()));
		for (Player player : Main.getPlayers()) {
			player.setItemInHand(
					new ItemBuilder(chosen).buildMeta().withDisplayName(colors.get(chosen)).item().build());
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1, 40);
		}

		new BukkitRunnable() {
			double i = 0.0;

			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {

					// TODO: BOSSBAR
					// API.bossBar(player, colors.get(chosen), BarColor.YELLOW,
					// 1.0-((double)i/(first ? 90.0 : 30.0)));
				}
				i++;
				if (i > (first ? 90.0 : 30.0))
					cancel();
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 0, 1);

		new BukkitRunnable() {
			@Override
			public void run() {
				first = false;
				World world = Bukkit.getWorld(RandomMap.winnermap);
				Location loc = new Location(world, -22, 115, 5);
				for (int x = 0; x < 24; x++) {
					for (int z = 0; z < 25; z++) {
						Location blockLoc = loc.clone().add(x, 0, z);
						if (blockLoc.getBlock().getType() != chosen)
							setBlockInNativeWorld(world, (int) blockLoc.getX(), 115, (int) blockLoc.getZ(), colorIds,
									Material.AIR, false);
					}
				}
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.playSound(player.getLocation(), Sound.UI_STONECUTTER_TAKE_RESULT, 1, 40);
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						for (Player player : Main.getPlayers()) {
							for (int i = 0; i <= 9; i++) {
								player.getInventory().setItem(i, null);
							}
						}
						runWoolBlock(materialColors, colors, colorIds);
					}
				}.runTaskLater(Main.getPlugin(), 20 * 5);
			}
		}.runTaskLater(Main.getPlugin(), 15 * 2 * (first ? 3 : 1));
	}
}
