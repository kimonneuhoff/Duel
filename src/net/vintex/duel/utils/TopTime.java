package net.vintex.duel.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import de.realjongi.cloudy.spigot.cloudyutils.CloudyPlayer;

public class TopTime {

	public static Map<Player, TopTime> players = new HashMap<Player, TopTime>();
	private Map<String, String> topTimes = new HashMap<String, String>();
	private Player p;

	public TopTime(Player p) {
		this.p = p;

		String rawCach = CloudyPlayer.getPlayer(p.getUniqueId()).getData("superJumpTopTimes");

		if (rawCach.contains("/")) {
			if (!rawCach.contains("//")) {
				topTimes.put(rawCach.split("/")[0], rawCach.split("/")[1] + "/" + rawCach.split("/")[2]);
			} else {
				String cached[] = rawCach.split("//");
				int size = RandomMap.allmaps.size();
				for (int i = 0; i < size; i++) {
					if (cached[i] == null || !cached[i].contains("/"))
						return;
					String map = cached[i].split("/")[0];
					topTimes.put(map, cached[i].split("/")[1] + "/" + cached[i].split("/")[2]);
				}
			}
		}

		players.put(p, this);
	}

	public void saveNewTopTime(String map, int m, int s) {
		topTimes.put(map, m + "/" + s);
	}

	public void convertTopTimes() {
		String allTopTimes = null;
		for (String map : topTimes.keySet()) {
			if (allTopTimes == null) {
				allTopTimes = map + "/" + topTimes.get(map);
			} else {
				allTopTimes = allTopTimes + "//" + map + "/" + topTimes.get(map);
			}
		}
		try {
			CloudyPlayer.getPlayer(p.getUniqueId()).setData("superJumpTopTimes", allTopTimes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean isTopTime(String map, int m, int s) {
		if (topTimes.get(map) != null) {
			String old = "";
			String latest = "";

			if (Integer.valueOf(topTimes.get(map).split("/")[0]) <= 9) {
				old = old + 0 + topTimes.get(map).split("/")[0];
			} else {
				old = old + topTimes.get(map).split("/")[0];
			}
			if (Integer.valueOf(topTimes.get(map).split("/")[1]) <= 9) {
				old = old + 0 + topTimes.get(map).split("/")[1];
			} else {
				old = old + topTimes.get(map).split("/")[1];
			}

			if (m <= 9) {
				latest = latest + 0 + m;
			} else {
				latest = latest + m;
			}
			if (s <= 9) {
				latest = latest + 0 + s;
			} else {
				latest = latest + s;
			}

			if (Integer.valueOf(latest) < Integer.valueOf(old)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public String getTopTimeAsString(String map, String m, String s) {
		if (topTimes.get(map) != null) {
			String top = "";
			if (Integer.valueOf(m) <= 9) {
				top = top + "0" + m;
			} else {
				top = top + m;
			}
			if (Integer.valueOf(s) <= 9) {
				top = top + ":0" + s;
			} else {
				top = top + ":" + s;
			}
			return top;
		} else {
			return "Keine";
		}
	}

	public static Map<Player, TopTime> getPlayers() {
		return players;
	}

	public static void setPlayers(Map<Player, TopTime> players) {
		TopTime.players = players;
	}

	public Map<String, String> getTopTimes() {
		return topTimes;
	}

	public void setTopTimes(Map<String, String> topTimes) {
		this.topTimes = topTimes;
	}

	public Player getP() {
		return p;
	}

	public void setP(Player p) {
		this.p = p;
	}
}
