package net.vintex.duel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mcmonkey.sentinel.SentinelTrait;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;

public class Prio {

	private static List<Prio> prios = new ArrayList<Prio>();
	public static Map<Integer, Player> buyed = new HashMap<Integer, Player>();

	public static List<Prio> getPrios() {
		return prios;
	}

	private NPC npc;
	private Location found = null;
	private boolean arrived = true;
	private Prio prio;

	public Prio(Location loc) {
		NPCRegistry localNPCRegistry = CitizensAPI.getNPCRegistry();
		npc = localNPCRegistry.createNPC(EntityType.VILLAGER, "§7");
		npc.getNavigator().getDefaultParameters().speedModifier((float) 0.65);
		npc.spawn(loc);

		npc.addTrait(Equipment.class);
		npc.addTrait(Inventory.class);
		SentinelTrait sentinelTrait = new SentinelTrait();
		npc.addTrait(sentinelTrait);
		sentinelTrait.speed = 0.65;
		sentinelTrait.realistic = true;
		sentinelTrait.fightback = false;
		setArrived(true);

		ArmorStand armorStand = (ArmorStand) Bukkit.getWorld(Main.getMap()).spawnEntity(npc.getEntity().getLocation(),
				EntityType.ARMOR_STAND);
		armorStand.setVisible(false);
		armorStand.setInvulnerable(true);
		armorStand.setMarker(true);
		npc.getEntity().addPassenger(armorStand);

		// Bukkit.getPlayer("Symnatic").setGameMode(GameMode.CREATIVE);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (isArrived()) {
					findRoute(npc.getEntity().getLocation());
					setArrived(false);
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 2);

		prio = this;

		getPrios().add(this);
	}

	public static class pathLoc {
		int x, y, z;

		public pathLoc(int x, int y, int z, int first, Prio prio) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public Block nextBlock(int x, int x1, int x2, int z, int z1, int z2, int y) {
		for (int xn = x; xn <= (x1 >= 0 ? x2 : x1); xn++) {
			for (int zn = z; zn <= (z1 >= 0 ? z2 : z1); zn++) {
				Block block = new Location(npc.getEntity().getWorld(), xn, y, zn).getBlock();
				if (!block.isPassable()) {
					return block;
				}
			}
		}
		return null;
	}

	public void findRoute(final Location loc) {
		Location lastLoc = found == null ? loc : found;
		setFound(null);
		List<pathLoc> oks = new ArrayList<pathLoc>();
		int y = (int) loc.getY() - 1;
		if (!npc.getEntity().isOnGround()) {
			for (int yf = y; yf >= 0; yf--) {
				Location yfl = loc;
				yfl.setY(yf);
				if (!yfl.getBlock().isPassable()) {
					y = yf;
					break;
				}
			}
		}

		for (int x = (int) loc.getX() - 8; x <= (int) loc.getX() + 8; x++) {
			for (int z = (int) loc.getZ() - 8; z <= (int) loc.getZ() + 8; z++) {
				if (!new Location(loc.getWorld(), x, y, z).getBlock().isPassable()
						&& new Location(loc.getWorld(), x, y + 3, z).getBlock().isPassable()) {
					Vector vector = ((LivingEntity) npc.getEntity()).getEyeLocation().toVector()
							.subtract(new Location(loc.getWorld(), x, y, z).toVector());
					if (lastLoc.distance(new Location(loc.getWorld(), x, y, z)) >= 7
							&& vector.dot(((LivingEntity) npc.getEntity()).getEyeLocation().getDirection()) < -0.6) {
						oks.add(new pathLoc(x, y + 1, z, 0, this));
					}
				}
			}
		}
		if (oks.isEmpty()) {
			for (int x = (int) loc.getX() - 8; x <= (int) loc.getX() + 8; x++) {
				for (int z = (int) loc.getZ() - 8; z <= (int) loc.getZ() + 8; z++) {
					if (!new Location(loc.getWorld(), x, y, z).getBlock().isPassable()
							&& new Location(loc.getWorld(), x, y + 3, z).getBlock().isPassable()) {
						oks.add(new pathLoc(x, y + 1, z, 0, this));
					}
				}
			}
			if (oks.isEmpty())
				return;
		}
		int random = new Random().nextInt(oks.size());
		setFound(new Location(loc.getWorld(), oks.get(random).x, oks.get(random).y, oks.get(random).z));
		new pathLoc(oks.get(random).x, y + 1, oks.get(random).z, 1, this);
		final ArmorStand armorStand = (ArmorStand) getFound().getWorld().spawnEntity(getFound(),
				EntityType.ARMOR_STAND);
		armorStand.setCollidable(false);
		armorStand.setVisible(false);
		armorStand.setInvulnerable(true);
		npc.getNavigator().setTarget(armorStand, false);

		new BukkitRunnable() {
			int x = (int) npc.getEntity().getLocation().getX(), y = (int) npc.getEntity().getLocation().getY(),
					z = (int) npc.getEntity().getLocation().getZ();
			int tick = 0;
			int hits = 0;
			int cooldown = 12;

			@Override
			public void run() {
				if (!npc.isSpawned()) {
					cancel();
					npc.destroy();
					return;
				}
				if (buyed.containsKey(npc.getEntity().getEntityId())) {
					for (Player player : Main.getPlayers()) {
						if (player != buyed.get(npc.getEntity().getEntityId())) {
							npc.getNavigator().setTarget(player, true);
							cooldown++;
							if (cooldown >= 15) {
								cooldown = 0;
								if (npc.getEntity().getLocation().distance(player.getLocation()) <= 6) {
									Vector localVector = player.getLocation().toVector()
											.subtract(npc.getEntity().getLocation().toVector());
									localVector = localVector.normalize();
									localVector.setY(0);
									localVector.multiply(0.2D);

									player.setVelocity(player.getVelocity().add(localVector));
									player.damage(2);
									hits++;
									if (hits == 5) {
										cancel();
										player.spawnParticle(Particle.EXPLOSION_HUGE, npc.getEntity().getLocation(), 2);
										npc.despawn();
										Prio.getPrios().remove(prio);
									}
								}
							}
							break;
						}
					}
					return;
				}
				if (x == (int) npc.getEntity().getLocation().getX() && y == (int) npc.getEntity().getLocation().getY()
						&& z == (int) npc.getEntity().getLocation().getZ()) {
					tick++;
				} else {
					tick = 0;
				}
				x = (int) npc.getEntity().getLocation().getX();
				y = (int) npc.getEntity().getLocation().getY();
				z = (int) npc.getEntity().getLocation().getZ();

				npc.getNavigator().setTarget(armorStand, false);
				if (npc.getEntity().getLocation().distance(armorStand.getLocation()) <= 2.2 || tick >= 5
						|| npc.getEntity().getLocation().getY() <= getFound().getY() - 2
						|| (npc.getEntity().getLocation().getY() != armorStand.getLocation().getY())) {
					setArrived(true);
					npc.getNavigator().getPathStrategy().update();
					new BukkitRunnable() {
						@Override
						public void run() {
							armorStand.remove();
						}
					}.runTaskLater(Main.getPlugin(), 20);
					cancel();
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 2);
	}

	public NPC getNpc() {
		return npc;
	}

	public void setNpc(NPC npc) {
		this.npc = npc;
	}

	public Location getFound() {
		return found;
	}

	public void setFound(Location found) {
		this.found = found;
	}

	public boolean isArrived() {
		return arrived;
	}

	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}
}
