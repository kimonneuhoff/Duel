package net.vintex.duel.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import de.realjongi.cloudy.spigot.api.CloudAPI;
import de.realjongi.cloudy.spigot.cloudyutils.CloudyPlayer;
import de.realjongi.cloudy.spigot.cloudyutils.RangSystem;
import net.vintex.duel.Game;
import net.vintex.duel.GameState;
import net.vintex.duel.Main;

public class Scoreboard {

	public static HashMap<Player, Scoreboard> scoreboard = new HashMap<Player, Scoreboard>();
	private Player p;
	private int update = 0;

	public Scoreboard(Player p) {
		this.p = p;

		final org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		p.setScoreboard(board);

		Team admin = board.registerNewTeam("a");
		Team senior = board.registerNewTeam("b");
		Team mod = board.registerNewTeam("c");
		Team dev = board.registerNewTeam("d");
		Team content = board.registerNewTeam("e");
		Team builder = board.registerNewTeam("f");
		Team vip = board.registerNewTeam("g");
		Team prime = board.registerNewTeam("h");
		Team spieler = board.registerNewTeam("i");
		Team spectator = board.registerNewTeam("j");
		spectator.setCanSeeFriendlyInvisibles(true);
		spectator.setColor(ChatColor.DARK_GRAY);
		spectator.setPrefix("§8");
		spieler.setColor(ChatColor.AQUA);
		prime.setColor(ChatColor.GREEN);
		vip.setColor(ChatColor.DARK_PURPLE);
		builder.setColor(ChatColor.DARK_GREEN);
		content.setColor(ChatColor.BLUE);
		dev.setColor(ChatColor.YELLOW);
		mod.setColor(ChatColor.RED);
		senior.setColor(ChatColor.RED);
		admin.setColor(ChatColor.GOLD);

		scoreboard.put(p, this);
	}

	public void performBoardUpdate() {
		update++;
		final org.bukkit.scoreboard.Scoreboard board = p.getScoreboard();

		if (board.getObjective("Lobby") != null)
			board.getObjective("Lobby").unregister();
		board.registerNewObjective("Lobby", "dummy");

		/*
		 * if (Main.getGameState() == GameState.lobby) {
		 * Objective obj = board.getObjective("Lobby");
		 * obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		 * obj.setDisplayName("§6§l» §e§lDUEL");
		 * obj.getScore("§c").setScore(7);
		 * obj.getScore(" §7❱ Spiel").setScore(6);
		 * obj.getScore("   §8» §f" +
		 * ChatColor.stripColor(Main.getGamePrefix().split(" ")[1])).setScore(5);
		 * obj.getScore("§d").setScore(4);
		 * obj.getScore(" §7❱ Server").setScore(3);
		 * obj.getScore("   §8» §f" + CloudAPI.getServerName()).setScore(2);
		 * obj.getScore("§e").setScore(1);
		 * } else
		 */if (Main.getGameState() == GameState.prepare || Main.getGameState() == GameState.ingame) {
			if (Main.getGame().equals(Game.classic) || Main.getGame().equals(Game.knockdown)) {

				int start = Main.getPlayers().size() + 2;
				int i = 2;

				final Objective obj = board.getObjective("Lobby");
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
				obj.setDisplayName("§e§lDUEL §700:00");
				obj.getScore("§c").setScore(start + 4);
				obj.getScore(" §7❱ Runde").setScore(start + 3);
				obj.getScore("   §8» §f" + Main.getRunde() + " von " + Main.getRunden()).setScore(start + 2);
				obj.getScore("§a").setScore(start + 1);
				obj.getScore(" §7❱ Siege").setScore(start);
				for (Player players : Main.getPlayers()) {
					obj.getScore("   §8» §e" + players.getName() + " §8× §7" + Main.getCountedint().get(players))
							.setScore(i);
					i++;
				}
				obj.getScore("§d").setScore(1);

				new BukkitRunnable() {
					int current = update;

					@Override
					public void run() {
						if (update != current)
							cancel();
						obj.setDisplayName("§e§lDUEL §7" + Utils.mint + ":" + Utils.sect);
					}
				}.runTaskTimer(Main.getPlugin(), 2, 2);
			} else if (Main.getGame().equals(Game.superjump)) {
				final Objective obj = board.getObjective("Lobby");
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
				obj.setDisplayName("§e§lDUEL §700:00");
				obj.getScore("§7" + Main.getPlayers().get(1).getName())
						.setScore(Main.getCountedint2().get(Main.getPlayers().get(1)));
				obj.getScore("§7" + Main.getPlayers().get(0).getName())
						.setScore(Main.getCountedint2().get(Main.getPlayers().get(0)));

				new BukkitRunnable() {
					int current = update;

					@Override
					public void run() {
						if (update != current)
							cancel();
						obj.setDisplayName("§e§lDUEL §7" + Utils.mint + ":" + Utils.sect);
					}
				}.runTaskTimer(Main.getPlugin(), 2, 2);
			}
		}
	}

	public void performRangUpdate() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			int rangId = CloudyPlayer.getPlayer(p.getUniqueId()).getDataInt("RangId");
			org.bukkit.scoreboard.Scoreboard board = p.getScoreboard();
			for (Player players : Bukkit.getOnlinePlayers()) {
				int rangIds = CloudyPlayer.getPlayer(players.getUniqueId()).getDataInt("RangId");
				if (Main.getSpectator().contains(p)) {
					board.getTeam("l").addPlayer(players);
					// } else if(NickAPI.isNicked(p)) {
					// board.getTeam("k").addPlayer(players);
				} else {
					if (rangIds == 9) {
						board.getTeam("a").addPlayer(players);
					} else if (rangIds == 8) {
						board.getTeam("b").addPlayer(players);
					} else if (rangIds == 7) {
						board.getTeam("c").addPlayer(players);
					} else if (rangIds == 6) {
						board.getTeam("d").addPlayer(players);
					} else if (rangIds == 5) {
						board.getTeam("e").addPlayer(players);
					} else if (rangIds == 4) {
						board.getTeam("f").addPlayer(players);
					} else if (rangIds == 3) {
						board.getTeam("g").addPlayer(players);
					} else if (rangIds == 2) {
						board.getTeam("h").addPlayer(players);
					} else if (rangIds == 1) {
						board.getTeam("i").addPlayer(players);
					}
				}
				p.setScoreboard(board);
				String shortName = p.getName();
				for (int i = 0; i < shortName.length(); i += 14) {
					shortName = shortName.substring(i, Math.min(i + 14, shortName.length()));
				}
				p.setCustomName("§e" + shortName);
				p.setCustomNameVisible(false);
				if (Main.getSpectator().contains(p)) {
					players.getScoreboard().getTeam("l").addPlayer(p);
					p.setDisplayName("§8" + p.getName());
					p.setPlayerListName("§8" + p.getName());
					/*
					 * } else if(NickAPI.isNicked(p)) {
					 * players.getScoreboard().getTeam("k").addPlayer(p); p.setDisplayName("§b" +
					 * p.getName()); p.setPlayerListName("§bSpieler §8┃ §7" + p.getName());
					 */
				} else {
					if (rangId == 9) {
						players.getScoreboard().getTeam("a").addPlayer(p);
						p.setDisplayName("§6" + p.getName());
					} else if (rangId == 8) {
						players.getScoreboard().getTeam("b").addPlayer(p);
						p.setDisplayName("§c" + p.getName());
					} else if (rangId == 7) {
						players.getScoreboard().getTeam("c").addPlayer(p);
						p.setDisplayName("§c" + p.getName());
					} else if (rangId == 6) {
						players.getScoreboard().getTeam("d").addPlayer(p);
						p.setDisplayName("§e" + p.getName());
					} else if (rangId == 5) {
						players.getScoreboard().getTeam("e").addPlayer(p);
						p.setDisplayName("§9" + p.getName());
					} else if (rangId == 4) {
						players.getScoreboard().getTeam("f").addPlayer(p);
						p.setDisplayName("§2" + p.getName());
					} else if (rangId == 3) {
						players.getScoreboard().getTeam("g").addPlayer(p);
						p.setDisplayName("§5" + p.getName());
					} else if (rangId == 2) {
						players.getScoreboard().getTeam("h").addPlayer(p);
						p.setDisplayName("§a" + p.getName());
					} else {
						players.getScoreboard().getTeam("i").addPlayer(p);
						p.setDisplayName("§b" + p.getName());
					}
				}
				p.setPlayerListName(RangSystem
						.getRangInStringWithColor(RangSystem.getRang(CloudyPlayer.getPlayer(p.getUniqueId()))).replace(
								RangSystem.getRangInString(RangSystem.getRang(CloudyPlayer.getPlayer(p.getUniqueId()))),
								"§l" + RangSystem
										.getRangInString(RangSystem.getRang(CloudyPlayer.getPlayer(p.getUniqueId())))
										.toUpperCase())
						+ " " + p.getDisplayName());

			}
		}
	}

	private int taskid = 0;

	public void vintexAnimation() {
		if (p.getScoreboard() != null) {
			final org.bukkit.scoreboard.Scoreboard board = p.getScoreboard();

			final Objective obj = board.getObjective("Scoreboard");

			taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
				int i = 0;

				@Override
				public void run() {
					i++;
					if (i >= 15 || obj == null) {
						obj.setDisplayName("§6§l» §e§lDUEL");
						Bukkit.getScheduler().cancelTask(taskid);
						taskid = 0;
						return;
					}
					if (i >= 5) {
						obj.setDisplayName("§e§l» §6§lDUEL");
					}
					if (i == 6) {
						obj.setDisplayName("§e§l» §e§lD§6§lUEL");
					}
					if (i == 7) {
						obj.setDisplayName("§e§l» §f§lD§e§lU§6§lEL");
					}
					if (i == 8) {
						obj.setDisplayName("§e§l» §6§lD§f§lU§e§lE§6§lL");
					}
					if (i == 9) {
						obj.setDisplayName("§e§l» §6§lDU§f§lE§e§lL");
					}
					if (i == 10) {
						obj.setDisplayName("§e§l» §6§lDUE§f§lL");
					}
					if (i == 11 || i == 12) {
						obj.setDisplayName("§e§l» §6§lDUEL");
					}
				}
			}, 0, 2);
		}
	}

	public static HashMap<Player, Scoreboard> getScoreboard() {
		return scoreboard;
	}

	public static void setScoreboard(HashMap<Player, Scoreboard> scoreboard) {
		Scoreboard.scoreboard = scoreboard;
	}

	public Player getP() {
		return p;
	}

	public void setP(Player p) {
		this.p = p;
	}

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
}