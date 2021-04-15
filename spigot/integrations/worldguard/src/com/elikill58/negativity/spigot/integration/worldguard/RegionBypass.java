package com.elikill58.negativity.spigot.integration.worldguard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.bypass.BypassChecker;

public class RegionBypass implements BypassChecker {

	@Override
	public boolean hasBypass(Player p, Cheat c) {
		return WorldGuardSupport.isInAreas(p.getLocation(), getRegions());
	}
	
	private final Set<String> cheats = new HashSet<>();
	private final List<String> regions;
	
	public RegionBypass(Configuration section) {
		for(String possibleCheats : section.getStringList("cheats")) {
			if(possibleCheats.equalsIgnoreCase("all")) {
				cheats.addAll(Cheat.getCheatKeys());
			} else {
				Cheat cheat = Cheat.fromString(possibleCheats);
				if(cheat == null)
					Adapter.getAdapter().getLogger().info("Cannot find the cheat " + possibleCheats + " in region bypass");
				else
					cheats.add(cheat.getKey());
			}
		}
		
		regions = section.getStringList("regions");
	}
	
	public List<String> getRegions() {
		return regions;
	}
}