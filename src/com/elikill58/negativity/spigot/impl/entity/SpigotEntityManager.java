package com.elikill58.negativity.spigot.impl.entity;

import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Entity;

public class SpigotEntityManager {

	public static com.elikill58.negativity.api.entity.Player getPlayer(Player p){
		NegativityPlayer np = NegativityPlayer.getCached(p.getUniqueId());
		if(np != null)
			return np.getPlayer();
		return NegativityPlayer.getNegativityPlayer(new SpigotPlayer(p)).getPlayer();
	}
	
	public static Entity getEntity(org.bukkit.entity.Entity bukkitEntity) {
		if(bukkitEntity == null)
			return null;
		switch (bukkitEntity.getType()) {
		case PLAYER:
			return getPlayer((Player) bukkitEntity);
		case IRON_GOLEM:
			return new SpigotIronGolem((IronGolem) bukkitEntity);
		default:
			return new SpigotEntity(bukkitEntity);
		}
	}

	public static CommandSender getExecutor(org.bukkit.command.CommandSender sender) {
		if(sender == null)
			return null;
		if(sender instanceof Player)
			return new SpigotPlayer((Player) sender);
		return new SpigotCommandSender(sender);
	}
}
