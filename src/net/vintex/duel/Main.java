package net.vintex.duel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.realjongi.cloudy.spigot.api.CloudAPI;
import de.realjongi.cloudy.spigot.cloudyutils.GameStatus;
import net.vintex.duel.games.Aura;
import net.vintex.duel.games.BombLobbers;
import net.vintex.duel.games.Classic;
import net.vintex.duel.games.KnockDown;
import net.vintex.duel.games.Levitation;
import net.vintex.duel.games.SkyWars;
import net.vintex.duel.games.SneakyAssasins;
import net.vintex.duel.games.Spleef;
import net.vintex.duel.games.SuperJump;
import net.vintex.duel.games.WoolBlock;
import net.vintex.duel.listeners.Duel;
import net.vintex.duel.tasks.NoMove;
import net.vintex.duel.tasks.RoundEnd;
import net.vintex.duel.utils.RandomMap;
import net.vintex.duel.utils.Scoreboard;
import net.vintex.duel.utils.Utils;

public class Main extends JavaPlugin {

	private static Main plugin;
	private static GameState gameState = GameState.start;
	private static Game game = Game.none;
	private static Boolean pvp = true;
	private static int runden = 0, needed = 1, runde = 1;
	public static double todeshöhe = 0;
	private static List<Player> players = new ArrayList<Player>();
	private static List<Block> blocks = new ArrayList<Block>();
	private static List<Player> spectator = new ArrayList<Player>();
	private static List<Entity> nopickup = new ArrayList<Entity>();
	private static HashMap<Player, Location> noMove = new HashMap<Player, Location>();
	private static HashMap<Player, Integer> countedint = new HashMap<Player, Integer>();
	private static HashMap<Player, Integer> countedint2 = new HashMap<Player, Integer>();
	private static HashMap<Location, Inventory> sgchest = new HashMap<Location, Inventory>();

	private static String globalPrefix = " §6§l ", gamePrefix = " §6§l» ", cloudPrefix = " §3§l» ", gameString = "";
	private static File file = new File("plugins/Duel", "config.yml");
	private static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

	private static String map, kit;
	public static Listener aura, bowduel, classic, levitation, oneline, superjump, bomblobbers, sneakyassasins;
	private static int color = 0, red = 255, green = 0, blue = 0, i = 1;

	private static HashMap<Player, HashMap<Stats, String>> stats = new HashMap<Player, HashMap<Stats, String>>();
	private static ArrayList<Player> wait = new ArrayList<>();

	@Override
	@SuppressWarnings("deprecation")
	public void onEnable() {
		plugin = this;

		getServer().getPluginManager().registerEvents(new Duel(), this);
		getServer().getPluginManager().registerEvents(new PacketReceiver(), this);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (game != Game.none) {
					System.out.println(game.getFriendly());
					new BukkitRunnable() {
						@Override
						public void run() {
							if (game.equals(Game.aura))
								Bukkit.getPluginManager().registerEvents(new Aura(), Main.getPlugin());
							if (game.equals(Game.classic))
								Bukkit.getPluginManager().registerEvents(new Classic(), Main.getPlugin());
							if (game.equals(Game.levitation))
								Bukkit.getPluginManager().registerEvents(new Levitation(), Main.getPlugin());
							if (game.equals(Game.knockdown))
								Bukkit.getPluginManager().registerEvents(new KnockDown(), Main.getPlugin());
							if (game.equals(Game.superjump))
								Bukkit.getPluginManager().registerEvents(new SuperJump(), Main.getPlugin());
							if (game.equals(Game.bomblobbers))
								Bukkit.getPluginManager().registerEvents(new BombLobbers(), Main.getPlugin());
							if (game.equals(Game.bowduel))
								Bukkit.getPluginManager().registerEvents(new Spleef(), Main.getPlugin());
							if (game.equals(Game.sneakyassasins))
								Bukkit.getPluginManager().registerEvents(new SneakyAssasins(), Main.getPlugin());
							if (game.equals(Game.woolblock))
								Bukkit.getPluginManager().registerEvents(new WoolBlock(), Main.getPlugin());
							if (game.equals(Game.spleef))
								Bukkit.getPluginManager().registerEvents(new Spleef(), Main.getPlugin());
							//if (game.equals(Game.skywars))
							//	Bukkit.getPluginManager().registerEvents(new SkyWars(), Main.getPlugin());
							if (!game.equals(Game.sneakyassasins)) {
								Plugin sentinelPlugin = Bukkit.getPluginManager().getPlugin("Sentinel");
								Bukkit.getPluginManager().disablePlugin(sentinelPlugin);
								/*
								 * Plugin libsPlugin = Bukkit.getPluginManager().getPlugin("LibsDisguises");
								 * Bukkit.getPluginManager().disablePlugin(libsPlugin);
								 */
								Plugin citizensPlugin = Bukkit.getPluginManager().getPlugin("Citizens");
								Bukkit.getPluginManager().disablePlugin(citizensPlugin);
							}
						}
					}.runTask(Main.getPlugin());
					cancel();
				}
			}
		}.runTaskTimerAsynchronously(this, 2, 2);

		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			int i = 0;
			List<Player> wait = new ArrayList<Player>();

			@Override
			public void run() {
				i++;
				if (i >= 50) {
					for (Player players : Bukkit.getOnlinePlayers())
						// utils.Scoreboard.getScoreboard().get(players).vintexAnimation();
						i = 0;
				}
				try {
					if (Main.getGame() == null)
						return;
					if (getGame().equals(Game.bowduel)) {
						for (Entity entity : Bukkit.getWorld(RandomMap.winnermap).getEntities()) {
							if (entity.getType() == EntityType.DROPPED_ITEM) {
								entity.remove();
							}
						}
					}
					for (final Player p : Bukkit.getOnlinePlayers()) {
						/*
						 * if(gameState == GameState.INGAME) { if(game.equals("KnockDown") ||
						 * game.equals("1vs1")) { String p1 = players.get(0).getDisplayName(); String p2
						 * = players.get(1).getDisplayName(); Utils.sendActionBar(p, p1 + " §8» §f§l" +
						 * countedint.get(players.get(0)) + " §8┃ " + "§f§l" +
						 * countedint.get(players.get(1)) + " §8« " + p2); } }
						 */
						if (game.equals(Game.superjump) && Main.getPlayers().contains(p)) {
							for (Location every : SuperJump.cp.values()) {
								p.spawnParticle(Particle.VILLAGER_HAPPY, every, 4, 0.5, 0.5, 0.5);
							}
							if (Main.getGameState() == GameState.ingame) {
								if (color == 0) {
									if (blue < 255)
										blue = blue + 15;
									else if (red > 0)
										red = red - 15;
									else if (green < 255)
										green = green + 15;
									else
										color = 1;
								} else {
									if (blue > 0)
										blue = blue - 15;
									else if (red < 255)
										red = red + 15;
									else if (green > 0)
										green = green - 15;
									else
										color = 0;
								}
								ItemStack itemStack = new ItemStack(Material.LEATHER_BOOTS, 1);
								LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
								leatherArmorMeta.setDisplayName("§7Schuhe");
								leatherArmorMeta.setColor(Color.fromRGB(red, green, blue));
								itemStack.setItemMeta(leatherArmorMeta);
								p.getInventory().setBoots(itemStack);
							}
						}
						if ((Main.getGame().equals(Game.knockdown) || Main.getGame().equals(Game.woolblock)
								|| Main.getGame().equals(Game.spleef)) && Main.getGameState() == GameState.ingame
								&& Main.getPlayers().contains(p)) {
							Location loc = p.getLocation();
							if (loc.getY() <= Main.getTodeshöhe()
									|| p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.BEDROCK) {
								if (wait.contains(p))
									return;
								Main.setGameState(GameState.prepare);
								wait.add(p);
								new BukkitRunnable() {
									@Override
									public void run() {
										wait.remove(p);
									}
								}.runTaskLater(Main.getPlugin(), 30);
								for (Player players : Bukkit.getOnlinePlayers()) {
									Scoreboard.getScoreboard().get(players).performBoardUpdate();
								}
								if (Main.getGame().equals(Game.woolblock)) {
									Iterator<Player> itr = Main.getPlayers().iterator();
									while (itr.hasNext()) {
										Player players = itr.next();
										if (players != p) {
											Main.getCountedint().put(p, Main.getCountedint().get(p) - 1);
											if (Main.getCountedint().get(p) == 0) {
												Main.setGameState(GameState.ingame);
												Main.getPlayers().remove(p);
												Utils.win(players, p);
												return;
											}
											p.setVelocity(new Vector(0, 0.65, 0));
											new BukkitRunnable() {
												@Override
												public void run() {
													p.teleport(p.getLocation().add(0, 2, 0));
												}
											}.runTask(Main.getPlugin());
											new BukkitRunnable() {
												@Override
												public void run() {
													final Location loci = p.getLocation();
													loci.setY(116);
													for (int x = -1; x <= 1; x++) {
														for (int z = -1; z <= 1; z++) {
															loci.clone().add(x, 0, z).getBlock()
																	.setType(Material.BEACON);
															final int xi = x, zi = z;
															new BukkitRunnable() {
																@Override
																public void run() {
																	if (loci.clone().add(xi, 0, zi).getBlock()
																			.getType() == Material.BEACON)
																		loci.clone().add(xi, 0, zi).getBlock()
																				.setType(Material.AIR);
																}
															}.runTaskLater(Main.getPlugin(), 90);
														}
													}
												}
											}.runTaskLater(Main.getPlugin(), 6);
											Main.setGameState(GameState.ingame);
											Bukkit.broadcastMessage(
													Main.getGamePrefix() + p.getDisplayName() + " §ehat nur noch §6§l"
															+ Main.getCountedint().get(p) + " §eLeben.");
										}
									}
								} else {
									Iterator<Player> itr = Main.getPlayers().iterator();
									while (itr.hasNext()) {
										Player players = itr.next();
										players.getInventory().clear();
										players.getInventory().setArmorContents(null);
										players.getInventory().setHeldItemSlot(0);
										if (players != p) {
											p.sendMessage(Main.getGamePrefix() + "§7Du wurdest von "
													+ players.getDisplayName() + " §7getötet.");
											players.sendMessage(Main.getGamePrefix() + "§7Du hast " + p.getDisplayName()
													+ " §7getötet.");
											players.playSound(players.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
											p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2, 1);
											for (Player specs : Main.getSpectator()) {
												specs.sendMessage(Main.getGamePrefix() + p.getDisplayName()
														+ " §7wurde von §6" + players.getDisplayName() + " §7getötet.");
											}
											Main.getCountedint().put(players, Main.getCountedint().get(players) + 1);
											Main.setRunde(Main.getRunde() + 1);
											if (Main.getCountedint().get(players) == Main.getNeeded()) {
												Main.setGameState(GameState.ingame);
												Main.getPlayers().remove(p);
												Utils.win(players, p);
												return;
											}
											new BukkitRunnable() {
												@Override
												public void run() {
													for (Player players1 : Main.getPlayers()) {
														Location spawn = new Location(Bukkit.getWorld(Main.getCfg()
																.getString("Spawn." + Main.getGame() + "."
																		+ Main.getMap() + "." + Main.i + ".World")),
																Main.getCfg()
																		.getDouble("Spawn." + Main.getGame() + "."
																				+ Main.getMap() + "." + Main.i + ".X"),
																Main.getCfg()
																		.getDouble("Spawn." + Main.getGame() + "."
																				+ Main.getMap() + "." + Main.i + ".Y"),
																Main.getCfg()
																		.getDouble("Spawn." + Main.getGame() + "."
																				+ Main.getMap() + "." + Main.i + ".Z"),
																(float) Main.getCfg()
																		.getDouble("Spawn." + Main.getGame() + "."
																				+ Main.getMap() + "." + Main.i
																				+ ".Yaw"),
																(float) Main.getCfg()
																		.getDouble("Spawn." + Main.getGame() + "."
																				+ Main.getMap() + "." + Main.i
																				+ ".Pitch"));
														spawn.getChunk().load();
														players1.teleport(spawn);
														Main.getNoMove().put(players1, spawn);
														Main.i++;
														if (Main.i == 3)
															Main.i = 1;
													}
												}
											}.runTask(plugin);
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
									new BukkitRunnable() {
										@Override
										public void run() {
											Iterator<Block> blocks = Main.getBlocks().iterator();
											while (blocks.hasNext()) {
												Block block = blocks.next();
												if (game.equals(Game.spleef))
													block.setType(Material.SNOW_BLOCK);
												else
													block.setType(Material.AIR);
												blocks.remove();
											}
										}
									}.runTask(plugin);
								}
							}
						}
						if (Main.getGame().equals(Game.buildwars) && Main.getPlayers().contains(p)
								&& Main.getGameState() == GameState.ingame) {
							if (p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.RED_CONCRETE) {
								p.setHealth(p.getHealth() - 2);
								// TODO: no regen //kill=regen3
							}
						}
						if (Main.getGameState() == GameState.prepare && noMove.containsKey(p) && Main.getPlayers().contains(p)) {
							if (p.getLocation().getX() != noMove.get(p).getX()
									|| p.getLocation().getZ() != noMove.get(p).getZ()) {
								new BukkitRunnable() {
									@Override
									public void run() {
										p.teleport(noMove.get(p));
									}
								}.runTask(plugin);
							}
						}
						if (Main.getGameState() != GameState.ingame || !Main.getPvp() || !Main.getGame().equals(Game.classic)
								|| Main.getKit().equals("Classic")) {
							if (p.getFoodLevel() != 24)
								p.setFoodLevel(24);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}, 2, 2);

		Main.setGameState(GameState.start);
		CloudAPI.setServerStatus(GameStatus.LOBBY);
		CloudAPI.setSlots(0, 2);

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}

	@Override
	public void onDisable() {

	}

	public static GameState getGameState() {
		return gameState;
	}

	public static void setGameState(GameState state) {
		gameState = state;
	}

	public static Main getPlugin() {
		return plugin;
	}

	public static Game getGame() {
		return game;
	}

	public static void setGame(Game game) {
		Main.game = game;
	}

	public static Boolean getPvp() {
		return pvp;
	}

	public static void setPvp(Boolean pvp) {
		Main.pvp = pvp;
	}

	public static HashMap<Stats, String> getStats(Player p) {
		return stats.get(p);
	}

	public static ArrayList<Player> getWait() {
		return wait;
	}

	public static Player getOpponentOf(Player p) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (getPlayers().contains(player) && player != p) {
				return player;
			}
		}
		return null;
	}

	public static List<Block> getBlocks() {
		return blocks;
	}

	public static List<Player> getPlayers() {
		return players;
	}

	public static List<Player> getSpectator() {
		return spectator;
	}

	public static List<Entity> getNopickup() {
		return nopickup;
	}

	public static HashMap<Player, Location> getNoMove() {
		return noMove;
	}

	public static HashMap<Player, Integer> getCountedint() {
		return countedint;
	}

	public static HashMap<Player, Integer> getCountedint2() {
		return countedint2;
	}

	public static HashMap<Location, Inventory> getSgchest() {
		return sgchest;
	}

	public static HashMap<Player, HashMap<Stats, String>> getStats() {
		return stats;
	}

	public static int getRunden() {
		return runden;
	}

	public static int getNeeded() {
		return needed;
	}

	public static int getRunde() {
		return runde;
	}

	public static double getTodeshöhe() {
		return todeshöhe;
	}

	public static String getGlobalPrefix() {
		return globalPrefix;
	}

	public static String getGamePrefix() {
		return gamePrefix;
	}

	public static String getCloudPrefix() {
		return cloudPrefix;
	}

	@Override
	public File getFile() {
		return file;
	}

	public static FileConfiguration getCfg() {
		return cfg;
	}

	public static String getMap() {
		return map;
	}

	public static String getKit() {
		return kit;
	}

	public static void setMap(String map) {
		Main.map = map;
	}

	public static void setKit(String kit) {
		Main.kit = kit;
	}

	public static void setRunden(int runden) {
		Main.runden = runden;
	}

	public static void setNeeded(int needed) {
		Main.needed = needed;
	}

	public static void setRunde(int runde) {
		Main.runde = runde;
	}

	public static void setGamePrefix(String gamePrefix) {
		Main.gamePrefix = gamePrefix;
	}

	public static int getI() {
		return i;
	}

	public static void setI(int i) {
		Main.i = i;
	}

	public static String getGameString() {
		return gameString;
	}

	public static void setGameString(String gameString) {
		Main.gameString = gameString;
	}
}
