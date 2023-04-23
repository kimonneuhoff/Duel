package net.vintex.duel;

public enum Game {
	none(""), classic("Classic"), bomblobbers("BombLobbers"), buildwars("BuildWars"), levitation("Levitation"),
	sneakyassasins("SneakyAssasins"), superjump("SuperJump"), woolblock("WoolBlock"), aura("Aura"), spleef("Spleef"), knockdown("KnockDown"), bowduel("BowDuel");

	private final String friendly;

	private Game(String name) {
		friendly = name;
	}

	public String getFriendly() {
		return friendly;
	}
}
