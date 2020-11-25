package com.elikill58.negativity.sponge.impl.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SprintData;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.SpongePotionEffectType;
import com.elikill58.negativity.sponge.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge.impl.inventory.SpongePlayerInventory;
import com.elikill58.negativity.sponge.impl.item.SpongeItemStack;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge.impl.location.SpongeWorld;
import com.elikill58.negativity.sponge.utils.LocationUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.support.ViaVersionSupport;
import com.flowpowered.math.vector.Vector3d;

public class SpongePlayer extends Player {

	private final org.spongepowered.api.entity.living.player.Player p;
	private Version playerVersion;

	public SpongePlayer(org.spongepowered.api.entity.living.player.Player p) {
		this.p = p;
		this.playerVersion = loadVersion();
	}
	
	private Version loadVersion() {
		return Negativity.viaVersionSupport ? ViaVersionSupport.getPlayerVersion(this) : Version.getVersion();
	}

	@Override
	public UUID getUniqueId() {
		return p.getUniqueId();
	}

	@Override
	public void sendMessage(String msg) {
		p.sendMessage(Text.of(msg));
	}

	@Override
	public boolean isOnGround() {
		return p.isOnGround();
	}

	@Override
	public boolean isOp() {
		return p.hasPermission("*");
	}

	@Override
	public boolean hasElytra() {
		return p.get(Keys.IS_ELYTRA_FLYING).orElse(false);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(p, (org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) entity.getLocation().getDefault());
	}

	@Override
	public float getWalkSpeed() {
		return (float) (double) p.get(Keys.WALKING_SPEED).get();
	}

	@Override
	public double getHealth() {
		return p.getOrCreate(HealthData.class).get().health().get();
	}

	@Override
	public float getFallDistance() {
		return p.getOrCreate(FallDistanceData.class).get().fallDistance().get();
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(p.gameMode().get().getName());
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		switch (gameMode) {
		case ADVENTURE:
			p.gameMode().set(GameModes.ADVENTURE);
			break;
		case CREATIVE:
			p.gameMode().set(GameModes.CREATIVE);
			break;
		case CUSTOM:
			p.gameMode().set(GameModes.NOT_SET);
			break;
		case SPECTATOR:
			p.gameMode().set(GameModes.SPECTATOR);
			break;
		case SURVIVAL:
			p.gameMode().set(GameModes.SURVIVAL);
			break;
		}
	}

	@Override
	public void damage(double amount) {
		p.damage(amount, DamageSource.builder().type(DamageTypes.CUSTOM).build());
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(p.getLocation());
	}

	@Override
	public int getPing() {
		return p.getConnection().getLatency();
	}

	@Override
	public World getWorld() {
		return new SpongeWorld(p.getWorld());
	}

	@Override
	public String getName() {
		return p.getName();
	}

	@Override
	public boolean hasPermission(String perm) {
		return p.hasPermission(perm);
	}

	@Override
	public Version getPlayerVersion() {
		return playerVersion == Version.HIGHER ? (playerVersion = loadVersion()) : playerVersion;
	}

	@Override
	public void kick(String reason) {
		p.kick(Text.of(reason));
	}

	@Override
	public int getLevel() {
		return p.get(Keys.EXPERIENCE_LEVEL).get();
	}
	
	@Override
	public double getFoodLevel() {
		return p.get(Keys.FOOD_LEVEL).get();
	}

	@Override
	public boolean getAllowFlight() {
		return p.get(Keys.CAN_FLY).orElse(false);
	}

	@Override
	public Entity getVehicle() {
		return SpongeEntityManager.getEntity(p.getVehicle().orElse(null));
	}
	
	@Override
	public ItemStack getItemInHand() {
		Optional<org.spongepowered.api.item.inventory.ItemStack> opt = p.getItemInHand(HandTypes.MAIN_HAND);
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public boolean isFlying() {
		return p.getOrCreate(FlyingData.class).get().flying().get();
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		(channelId.equalsIgnoreCase("fml") ? SpongeNegativity.fmlChannel : SpongeNegativity.channel).sendTo(p, (chan) -> chan.writeByteArray(writeMessage));
	}

	@Override
	public boolean isSleeping() {
		return p.getOrCreate(SleepingData.class).get().sleeping().get();
	}

	@Override
	public boolean isSneaking() {
		return p.getOrCreate(SneakingData.class).get().sneaking().get();
	}

	@Override
	public double getEyeHeight() {
		return Utils.getPlayerHeadHeight(p);
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		List<org.spongepowered.api.effect.potion.PotionEffect> potionEffects = p.getOrNull(Keys.POTION_EFFECTS);
		if (potionEffects == null) {
			return false;
		}
		for (org.spongepowered.api.effect.potion.PotionEffect effect : potionEffects) {
			if (effect.getType().getId().equalsIgnoreCase(type.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<org.spongepowered.api.effect.potion.PotionEffect> effects = p.getOrNull(Keys.POTION_EFFECTS);
		if (effects == null) {
			return Collections.emptyList();
		}
		return effects.stream()
			.map(this::createPotionEffect)
			.collect(Collectors.toList());
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		return p.get(Keys.POTION_EFFECTS).flatMap(effects -> {
			for (org.spongepowered.api.effect.potion.PotionEffect effect : effects) {
				if (effect.getType().getId().equalsIgnoreCase(type.getId())) {
					return Optional.of(createPotionEffect(effect));
				}
			}
			return Optional.empty();
		});
	}
	
	private PotionEffect createPotionEffect(org.spongepowered.api.effect.potion.PotionEffect effect) {
		return new PotionEffect(PotionEffectType.forId(effect.getType().getId()), effect.getDuration(), effect.getAmplifier());
	}
	
	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		p.transform(Keys.POTION_EFFECTS, effects -> {
			org.spongepowered.api.effect.potion.PotionEffect effect =
				org.spongepowered.api.effect.potion.PotionEffect.of(SpongePotionEffectType.getEffect(type), amplifier, duration);
			if (effects == null) {
				return Collections.singletonList(effect);
			}
			effects.add(effect);
			return effects;
		});
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		p.transform(Keys.POTION_EFFECTS, effects -> {
			if (effects != null) {
				effects.removeIf(effect -> effect.getType().getId().equals(type.getId()));
				return effects;
			}
			return Collections.emptyList();
		});
	}

	@Override
	public String getIP() {
		return p.getConnection().getAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return p.isOnline();
	}

	@Override
	public void setSneaking(boolean b) {
		p.getOrCreate(SneakingData.class).get().sneaking().set(b);
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isSprinting() {
		return p.getOrCreate(SprintData.class).get().sprinting().get();
	}

	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void teleport(Location loc) {
		p.setLocation((org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) loc.getDefault());
	}

	@Override
	public boolean isInsideVehicle() {
		return p.getVehicle().isPresent();
	}

	@Override
	public float getFlySpeed() {
		return (float) (double) p.get(Keys.FLYING_SPEED).get();
	}

	@Override
	public void setSprinting(boolean b) {
		p.getOrCreate(SprintData.class).get().sprinting().set(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		p.getNearbyEntities(x).forEach((entity) -> list.add(SpongeEntityManager.getEntity(entity)));
		return list;
	}

	@Override
	public boolean isSwimming() {
		if (!isSprinting())
			return false;
		Location loc = getLocation().clone();
		if (loc.getBlock().getType().getId().contains("WATER"))
			return true;
		if (loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER"))
			return true;
		return false;
	}

	@Override
	public ItemStack getItemInOffHand() {
		Optional<org.spongepowered.api.item.inventory.ItemStack> opt = p.getItemInHand(HandTypes.OFF_HAND);
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}

	@Override
	public Vector getVelocity() {
		Vector3d vel = p.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public PlayerInventory getInventory() {
		return new SpongePlayerInventory(p);
	}
	
	@Override
	public boolean hasOpenInventory() {
		return p.getOpenInventory().isPresent() && p.getOpenInventory().get().getArchetype().equals(InventoryArchetypes.CHEST);
	}

	@Override
	public Inventory getOpenInventory() {
		return p.getOpenInventory().isPresent() ? null : new SpongeInventory(p.getOpenInventory().get());
	}

	@Override
	public void openInventory(Inventory inv) {
		p.openInventory((org.spongepowered.api.item.inventory.Inventory) inv.getDefault());
	}

	@Override
	public void closeInventory() {
		Task.builder().execute(() -> p.closeInventory()).submit(SpongeNegativity.getInstance());
	}

	@Override
	public void updateInventory() {
		
	}

	@Override
	public void setAllowFlight(boolean b) {
		p.offer(Keys.CAN_FLY, b);
	}

	@Override
	public void showPlayer(Player p) {
		// TODO implement showPlayer
	}
	
	@Override
	public void hidePlayer(Player p) {
		// TODO implement hidePlayer
	}

	@Override
	public void setVelocity(Vector vel) {
		p.setVelocity(new Vector3d(vel.getX(), vel.getY(), vel.getZ()));
	}

	@Override
	public Object getDefault() {
		return p;
	}
	
	@Override
	public Location getEyeLocation() {
		Vector3d vec = p.getProperty(EyeLocationProperty.class).map(EyeLocationProperty::getValue).orElse(p.getRotation());
		return new SpongeLocation(new SpongeWorld(p.getWorld()), vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = p.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return 0;
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return p.getConnection().getVirtualHost();
	}
}
