package net.vintex.duel.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import de.realjongi.cloudy.spigot.cloudyutils.CloudyPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.vintex.duel.Game;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;
import net.vintex.duel.tasks.Restart;

public class Utils {

	public static int sec = 0;
	public static int min = 0;

	public static String sect = "00";
	public static String mint = "00";

	public static void win(final Player p1, final Player p2) {
		if (Main.getGameState() != GameState.ingame && Main.getGameState() != GameState.prepare)
			return;
		Main.setGameState(GameState.ending);

		Bukkit.getScheduler().cancelTasks(Main.getPlugin());

		Bukkit.broadcastMessage(Main.getGamePrefix() + p1.getDisplayName() + " §ehat das Spiel gewonnen!");

		if (Main.getGame().equals(Game.superjump)) {
			if (TopTime.getPlayers().get(p1).isTopTime(RandomMap.winnermap, min, sec)) {
				TopTime topTime = TopTime.getPlayers().get(p1);
				topTime.saveNewTopTime(RandomMap.winnermap, min, sec);
				p1.sendMessage(Main.getGamePrefix() + "§aDu hast eine neue Bestzeit von §e"
						+ topTime.getTopTimeAsString(RandomMap.winnermap,
								topTime.getTopTimes().get(RandomMap.winnermap).split("/")[0],
								topTime.getTopTimes().get(RandomMap.winnermap).split("/")[1])
						+ " §aerreicht!");
				topTime.convertTopTimes();
			}
		}

		for (final Player players : Bukkit.getOnlinePlayers()) {
			players.sendTitle("§c§lGAME OVER!", p1.getDisplayName() + " §7ist der Gewinner!");
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					players.sendTitle(" ", " ");
				}
			}, 60);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				if (p1 == null || !p1.isOnline())
					cancel();
				p1.getWorld().spawnParticle(Particle.SPELL_INSTANT, p1.getLocation().add(0.5, 0.3, 0), 8);
				p1.getWorld().spawnParticle(Particle.SPELL_INSTANT, p1.getLocation().add(0, 0.3, 0.5), 8);
				p1.getWorld().spawnParticle(Particle.SPELL_INSTANT, p1.getLocation().add(-0.5, 0.3, 0), 8);
				p1.getWorld().spawnParticle(Particle.SPELL_INSTANT, p1.getLocation().add(0, 0.3, -0.5), 8);
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 0, 3);

		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				CloudyPlayer.getPlayer(p1.getUniqueId()).setDataInt("Coins",
						CloudyPlayer.getPlayer(p1.getUniqueId()).getDataInt("Coins") + 2);
				/*
				 * if(Utils.min == 0 || Utils.sec > 30) Utils.min =+ 1; int gespielt1 =
				 * Utils.min + Methods.getFromTableInt(p1.getUniqueId(), "Spieler",
				 * "spielMinuten"); if(gespielt1 >= 60) {
				 * Methods.setInTableInt(p1.getUniqueId(), "Spieler", "spielStunden",
				 * Methods.getFromTableInt(p1.getUniqueId(), "Spieler", "spielStunden" + 1));
				 * gespielt1 = gespielt1- 60; } Methods.setInTableInt(p1.getUniqueId(),
				 * "Spieler", "spielMinuten", gespielt1); int gespielt2 = Utils.min +
				 * Methods.getFromTableInt(p2.getUniqueId(), "Spieler", "spielMinuten");
				 * if(gespielt2 >= 60) { Methods.setInTableInt(p2.getUniqueId(), "Spieler",
				 * "spielStunden", Methods.getFromTableInt(p2.getUniqueId(), "Spieler",
				 * "spielStunden" + 1)); gespielt2 = gespielt2 - 60; }
				 * Methods.setInTableInt(p2.getUniqueId(), "Spieler", "spielMinuten",
				 * gespielt2);
				 */
			}
		});
		Restart.start();
	}

	public static void playtime() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (Main.getGameState() == GameState.ingame) {
					sec = sec + 1;
					if (sec >= 60) {
						min = min + 1;
						sec = 0;
					}
					if (sec < 10) {
						sect = "0" + sec;
					} else {
						sect = "" + sec;
					}
					if (min < 10) {
						mint = "0" + min;
					} else {
						mint = "" + min;
					}
				}
			}
		}, 20, 20);
	}

	public static Location teleportBehindLoc(Location l, double multi) {
		double nX;
		double nZ;
		float nang = (l.getYaw() + 90);
		if (nang < 0) {
			nang += 360;
		}
		nX = Math.cos(Math.toRadians(nang));
		nZ = Math.sin(Math.toRadians(nang));
		Location loc = new Location(l.getWorld(), (l.getX() - (nX * multi)), l.getY(), (l.getZ() - (nZ * multi)),
				l.getYaw(), l.getPitch());
		return loc;
	}

	public static ItemStack tippedArrow(PotionType potionType, int amount) {
		ItemStack result = new ItemStack(Material.TIPPED_ARROW, amount);
		PotionMeta resultMeta = (PotionMeta) result.getItemMeta();
		resultMeta.setBasePotionData(new PotionData(potionType));
		result.setItemMeta(resultMeta);
		return result;
	}

	public static ItemStack getPlayerSkull(String owner, String displayname) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		headMeta.setOwner(owner);
		headMeta.setDisplayName(displayname);
		head.setItemMeta(headMeta);
		return head;
	}

	public static void sendActionBar(Player p, String message) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}

	public static ItemStack colorarmorbowduell(Material m, Color c) {
		ItemStack armor = new ItemStack(m);
		LeatherArmorMeta armormeta = (LeatherArmorMeta) armor.getItemMeta();
		armormeta.setColor(c);
		armormeta.addEnchant(Enchantment.DURABILITY, 2, false);
		armormeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
		armor.setItemMeta(armormeta);
		return armor;
	}

	public static ItemStack newiteme(Material m, int i, String name, Enchantment e, int ei) {
		ItemStack item = new ItemStack(m, i);
		ItemMeta itemMeta = item.getItemMeta();
		if (name != null) {
			itemMeta.setDisplayName(name);
		}
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
		itemMeta.addEnchant(e, ei, false);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack newiteme2(Material m, int i, String name, Enchantment e, int ei, Enchantment e2, int ei2) {
		ItemStack item = new ItemStack(m, i);
		ItemMeta itemMeta = item.getItemMeta();
		if (name != null) {
			itemMeta.setDisplayName(name);
		}
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
		itemMeta.addEnchant(e, ei, false);
		itemMeta.addEnchant(e2, ei2, false);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack newitem(Material m, int i, String name) {
		ItemStack item = new ItemStack(m, i);
		ItemMeta itemMeta = item.getItemMeta();
		if (name != null) {
			itemMeta.setDisplayName(name);
		}
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack newitems(Material m, int i, String name, int s) {
		ItemStack item = new ItemStack(m, i, (short) s);
		ItemMeta itemMeta = item.getItemMeta();
		if (name != null) {
			itemMeta.setDisplayName(name);
		}
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack newitems(Material m, int i, String name, int s, List<String> l) {
		ItemStack item = new ItemStack(m, i, (short) s);
		ItemMeta itemMeta = item.getItemMeta();
		if (name != null) {
			itemMeta.setDisplayName(name);
		}
		itemMeta.setLore(l);
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack newiteme(Material m, int i, String name) {
		ItemStack item = new ItemStack(m, i);
		ItemMeta itemMeta = item.getItemMeta();
		if (name != null) {
			itemMeta.setDisplayName(name);
		}
		itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack newitemc(Material m, int i, String name, int r, int g, int b) {
		ItemStack item = new ItemStack(m, i);
		LeatherArmorMeta itemMeta = (LeatherArmorMeta) item.getItemMeta();
		if (name != null) {
			itemMeta.setDisplayName(name);
		}
		itemMeta.setColor(Color.fromRGB(r, g, b));
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack Skull(String Display, String Owner) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
		SkullMeta headm = (SkullMeta) head.getItemMeta();
		headm.setDisplayName(Display);
		headm.setOwner(Owner);
		head.setItemMeta(headm);
		return head;
	}

	public static void playOpenSound(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_STONE_BREAK, 10.0F, 10.0F);
	}

	public static void playClickSound(Player p) {
		p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
	}

	public static void cooldown(final Player p) {
	}
}
