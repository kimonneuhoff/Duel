package net.vintex.duel.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import de.realjongi.cloudy.spigot.api.CloudAPI;
import de.realjongi.cloudy.spigot.cloudyutils.CloudyPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayInClientCommand;
import net.vintex.duel.Game;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.Stats;
import net.vintex.duel.games.SuperJump;
import net.vintex.duel.tasks.NoMove;
import net.vintex.duel.tasks.Restart;
import net.vintex.duel.tasks.RestartCauseLeave;
import net.vintex.duel.utils.Scoreboard;
import net.vintex.duel.utils.TopTime;
import net.vintex.duel.utils.Utils;

public class Duel implements Listener {

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		if (Main.getGameState() == GameState.ending) {
			e.disallow(null, Main.getGlobalPrefix() + "§cDer Server kann nicht mehr betreten werden!");
		} else if (Main.getGameState() == GameState.lobby) {
			if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())
				e.disallow(null, Main.getGlobalPrefix() + "§cDer Server ist voll!");
		} else {
			if (Main.getSpectator().size() >= 3 && CloudyPlayer.getPlayer(p.getUniqueId()).getDataInt("rangId") < 5) {
				e.disallow(null, Main.getGlobalPrefix() + "§cAuf diesem Server sind bereits zu viele Spectator!");
				return;
			}
			e.allow();
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();

		Scoreboard sb = new Scoreboard(p);
		sb.performBoardUpdate();
		sb.performRangUpdate();

		e.setJoinMessage(null);
		Utils.sendActionBar(p, " ");
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setFireTicks(0);
		p.setLevel(0);
		p.setExp(0);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.getInventory().setHeldItemSlot(0);
		for (PotionEffect effects : p.getActivePotionEffects()) {
			p.removePotionEffect(effects.getType());
		}
		p.sendTitle(" ", " ");
		p.setCollidable(true);

		/*
		 * AttributeInstance instance = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		 * instance.setBaseValue(17.0D);
		 */

		CloudAPI.setSlots(Bukkit.getOnlinePlayers().size(), 2);

		/*
		 * Location lobby = new
		 * Location(Bukkit.getWorld(Main.getCfg().getString("Lobby.World")),
		 * Main.getCfg().getDouble("Lobby.X"), Main.getCfg().getDouble("Lobby.Y"),
		 * Main.getCfg().getDouble("Lobby.Z"), (float)
		 * Main.getCfg().getDouble("Lobby.Yaw"), (float)
		 * Main.getCfg().getDouble("Lobby.Pitch")); lobby.getChunk().load();
		 * p.teleport(lobby);
		 */

		p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1);

		if (Main.getGameState() != GameState.lobby) {
			Main.getSpectator().add(p);
			for (Player players : Bukkit.getOnlinePlayers()) {
				players.hidePlayer(p);
			}
			for (Player players : Main.getSpectator()) {
				players.showPlayer(p);
				p.showPlayer(players);
			}
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					p.sendMessage(Main.getGamePrefix() + "§7Du bist nun ein Spectator.");
				}
			}, 1L);

			Location lobby = new Location(Bukkit.getWorld(Main.getCfg().getString("Lobby.World")),
					Main.getCfg().getDouble("Lobby.X"), Main.getCfg().getDouble("Lobby.Y"),
					Main.getCfg().getDouble("Lobby.Z"), (float) Main.getCfg().getDouble("Lobby.Yaw"),
					(float) Main.getCfg().getDouble("Lobby.Pitch"));
			lobby.getChunk().load();
			p.teleport(lobby);

			p.spigot().setCollidesWithEntities(false);
			p.setGameMode(GameMode.ADVENTURE);
			p.getInventory().setItem(0, Utils.newitem(Material.COMPASS, 1, "§6§m–§r §eTeleporter"));
			p.getInventory().setItem(8, Utils.newitem(Material.FEATHER, 1, "§4§m–§r §cVerlassen.."));
			p.updateInventory();
			p.setAllowFlight(true);
			p.setFlying(true);
			p.setCollidable(false);

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
				}
			}, 1L);
		} else {

			Main.getPlayers().add(p);

			Main.getStats().put(p, new HashMap<Stats, String>());
			Main.getStats().get(p).put(Stats.damage, "0");
			Main.getStats().get(p).put(Stats.damageX, "0");
			Main.getStats().get(p).put(Stats.hits, "0");
			Main.getStats().get(p).put(Stats.hitsX, "0");
			Main.getStats().get(p).put(Stats.shots, "0");
			Main.getStats().get(p).put(Stats.shotsX, "0");

			String game = Main.getGame().getFriendly();
			Bukkit.broadcastMessage(game + " " + Main.getMap() + " " + Main.getI());
			Location spawn = new Location(
					Bukkit.getWorld(Main.getCfg()
							.getString("Spawn." + game + "." + Main.getMap() + "." + Main.getI() + ".World")),
					Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + Main.getI() + ".X"),
					Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + Main.getI() + ".Y"),
					Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + Main.getI() + ".Z"),
					(float) Main.getCfg().getDouble("Spawn." + game + "." + Main.getMap() + "." + Main.getI() + ".Yaw"),
					(float) Main.getCfg()
							.getDouble("Spawn." + game + "." + Main.getMap() + "." + Main.getI() + ".Pitch"));
			Bukkit.broadcastMessage("tete " + spawn.getX());

			spawn.getChunk().load();
			p.teleport(spawn);
			Main.getNoMove().put(p, spawn);
			Main.setI(Main.getI() + 1);
			if (Main.getI() == 3)
				Main.setI(1);

			Main.getCountedint().put(p, 0);
			Main.getCountedint2().put(p, 0);
			if (Main.getGame().equals(Game.superjump)) {
				SuperJump.cp.put(p, spawn);
				new TopTime(p);
			}

			NoMove.start();

			p.spigot().setCollidesWithEntities(true);
			p.setGameMode(GameMode.SURVIVAL);
			p.updateInventory();
			p.setAllowFlight(false);
			p.setFlying(false);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		e.setQuitMessage(null);

		CloudAPI.setSlots(Bukkit.getOnlinePlayers().size() - 1, 2);

		if (Bukkit.getOnlinePlayers().size() <= 2 && Main.getGameState() == GameState.lobby) {
			Bukkit.getScheduler().cancelTask(NoMove.schedulernomove);
			RestartCauseLeave.start();
		}

		if ((Main.getGameState() == GameState.ingame || Main.getGameState() == GameState.prepare)
				&& Main.getPlayers().contains(p)) {
			Main.getPlayers().remove(p);
			Main.setGameState(GameState.ending);
			Bukkit.getScheduler().cancelTasks(Main.getPlugin());
			Bukkit.broadcastMessage(Main.getGamePrefix() + "§7Es gibt keinen Gewinner!");
			for (Player players : Bukkit.getOnlinePlayers()) {
				players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
				players.sendTitle("§c§lGame OVER!", "§7Kein Gewinner!");
			}

			for (Player players : Bukkit.getOnlinePlayers()) {
				if (players.getOpenInventory().getTitle().contains("Spectate")) {
					players.closeInventory();
				}
			}
			MinecraftServer.getServer().setMotd("§7Restart-Keine");
			Restart.start();
		}

		Main.getPlayers().remove(p);
		Main.getSpectator().remove(p);
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		e.setCancelled(true);
		String msg = e.getMessage();
		if (Main.getSpectator().contains(p))
			return;
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (msg.contains(players.getName()) && p != players) {
				players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
				msg = msg.replace(players.getName(), "§b@" + players.getName() + "§f");
			}
		}
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.sendMessage(p.getPlayerListName().replace(p.getName(), p.getDisplayName()) + " §8» §f" + msg);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (Main.getGameState() == GameState.prepare) {
			Player p = e.getPlayer();
			if (p.getGameMode() == GameMode.SURVIVAL) {
				if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY()
						|| e.getFrom().getZ() != e.getTo().getZ()) {
					e.setTo(e.getFrom());
					p.damage(1);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(Main.getGameState() == GameState.prepare) {
			e.setDamage(0);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (Main.getGameState() == GameState.prepare) {

		}

		if (e.getItem() != null && e.getItem().getType() == Material.CREEPER_SPAWN_EGG)
			return;
		if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
				&& Main.getGameState() == GameState.ingame) {
			Main.getStats(p).put(Stats.hits, (Integer.valueOf(Main.getStats(p).get(Stats.hits)) + 1) + "");
		}
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material main = p.getInventory().getItemInMainHand().getType();

			if (e.getItem() != null && e.getItem().getItemMeta().getDisplayName() != null) {
				if(e.getItem().getType() == Material.DRAGON_BREATH) {
					p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2.0F, 1.0F);
					CloudAPI.connect(p, "fallback");
					return;
				}
				
				String name = ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName());

				if (Main.getGame() == Game.sneakyassasins) {
					if (name.contains("Rauchbombe") && !Main.getWait().contains(p)) {
						Main.getWait().add(p);
						new BukkitRunnable() {
							@Override
							public void run() {
								Main.getWait().remove(p);
							}
						}.runTaskLater(Main.getPlugin(), 20);
						e.setCancelled(true);
						p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 2);
						p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 2);
						p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 2);
						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 4, 4);
						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 4, 4);
						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 4, 4);
						for (Entity entity : p.getNearbyEntities(5, 5, 5)) {
							if (entity instanceof Player && entity != p
									&& ((Player) entity).getGameMode() == GameMode.SURVIVAL) {
								final Player near = (Player) entity;
								near.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 5));
								near.hidePlayer(p);
								new BukkitRunnable() {

									@Override
									public void run() {
										near.showPlayer(p);
									}
								}.runTaskLater(Main.getPlugin(), 50);
							}
						}
						if (main == Material.BLACK_DYE) {
							if (p.getInventory().getItemInMainHand().getAmount() > 1)
								p.getInventory().getItemInMainHand()
										.setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
							else
								p.getInventory().remove(Material.BLACK_DYE);
						} else if (p.getInventory().getItemInOffHand().getType() == Material.BLACK_DYE) {
							if (p.getInventory().getItemInOffHand().getAmount() > 1)
								p.getInventory().getItemInOffHand()
										.setAmount(p.getInventory().getItemInOffHand().getAmount() - 1);
							else
								p.getInventory().remove(Material.BLACK_DYE);
						}
					}
				}
				if (Main.getGame() == Game.superjump) {
					if (name.contains("Checkpoint")) {
						e.setCancelled(true);
						if (p.getExp() == 0) {
							p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
							p.teleport(SuperJump.cp.get(p));
							Utils.cooldown(p);
							Main.getCountedint2().put(p, Main.getCountedint2().get(p) + 1);
							for (Player players1 : Bukkit.getOnlinePlayers()) {
								Scoreboard.getScoreboard().get(players1).performBoardUpdate();
								Scoreboard.getScoreboard().get(players1).performRangUpdate();
							}
						}
					}
				}
			}
		}

		// EP FIX
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			if (e.getItem().getType() == Material.ENDER_PEARL) {
				e.setCancelled(true);
				p.setCooldown(Material.ENDER_PEARL, 0);
				EnderPearl ep = p.launchProjectile(EnderPearl.class);
				ep.setVelocity(p.getEyeLocation().getDirection().multiply(2));

			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player d = (Player) e.getDamager();
			if (p == d)
				e.setCancelled(true);
			else {
				if (Main.getGameState() == GameState.ingame && Main.getPvp() && Main.getGame() != Game.spleef) {
					Main.getStats(d).put(Stats.damage,
							(Double.valueOf(Main.getStats(d).get(Stats.damage)) + e.getDamage()) + "");
					if (d.getFallDistance() > 0 && !d.isOnGround())
						Main.getStats(d).put(Stats.damageX,
								(Double.valueOf(Main.getStats(d).get(Stats.damageX)) + e.getDamage()) + "");
					Main.getStats(d).put(Stats.hits, (Integer.valueOf(Main.getStats(d).get(Stats.hits)) + 1) + "");
					Main.getStats(d).put(Stats.hitsX, (Integer.valueOf(Main.getStats(d).get(Stats.hits)) + 1) + "");

					e.setCancelled(false);
				} else {
					if (Main.getGame() == Game.spleef) {
						if (e.getCause() == DamageCause.ENTITY_ATTACK)
							e.setCancelled(true);
						else
							e.setCancelled(false);
					}
				}
			}
		} else {
			if (e.getDamager() instanceof Projectile) {
				Main.getStats((Player) ((Projectile) e.getDamager()).getShooter()).put(Stats.shotsX,
						(Integer.valueOf(
								Main.getStats((Player) ((Projectile) e.getDamager()).getShooter()).get(Stats.shotsX))
								+ 1) + "");
			}

			if (e.getDamager() instanceof Snowball)
				e.setDamage(1);
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() != null) {
			Main.getStats((Player) e.getEntity().getShooter()).put(Stats.shots,
					(Integer.valueOf(Main.getStats((Player) e.getEntity().getShooter()).get(Stats.shots))
							+ 1) + "");
		}
	}

	/*
	 * CANCEL-EVENTS
	 */
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (Main.getGameState() != GameState.ingame || !Main.getPvp() || (Main.getGame() == Game.classic && Main.getKit() != "SG" && Main.getKit() != "Trash"))
			e.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		e.setYield(0);
		if (Main.getGame() != Game.aura && Main.getGame() != Game.bomblobbers) {
			e.setCancelled(true);
			e.blockList().clear();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		Bukkit.broadcastMessage("SPAWN CREATURE: " + e.getSpawnReason().toString());
		if (e.getSpawnReason() == SpawnReason.CUSTOM || e.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
			e.setCancelled(false);
		} else {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent e) {
		((Player) e.getEntity()).spigot().respawn();
		((CraftPlayer) ((Player) e.getEntity())).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
		new BukkitRunnable() {
			@Override
			public void run() {
				((Player) e.getEntity()).spigot().respawn();
				((CraftPlayer) ((Player) e.getEntity())).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
			}
		}.runTaskLater(Main.getPlugin(), 2);
	}
	//
}
