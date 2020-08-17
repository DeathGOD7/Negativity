package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.SNEAK;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Sneak extends Cheat implements Listeners {

	public Sneak() {
		super(SNEAK, true, Materials.BLAZE_POWDER, CheatCategory.MOVEMENT, true, "sneack", "sneac");
	}
	
	@EventListener
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(checkActive("sneak-sprint")) {
			if (p.isSneaking() && p.isSprinting() && !p.isFlying() && np.booleans.get(SNEAK, "was-sneaking", false)) {
				if(!p.getPlayerVersion().isNewerOrEquals(Version.V1_14)) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(105 - (p.getPing() / 10)),
							"sneak-sprint", "Sneaking, sprinting and not flying");
					if(mayCancel && isSetBack()) {
						e.setCancelled(true);
						p.setSprinting(false);
					}
				}
			}
			np.booleans.set(SNEAK, "was-sneaking", p.isSneaking());
		}
	}
}