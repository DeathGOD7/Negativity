package com.elikill58.negativity.common.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;

public class UnbanCommand implements CommandListeners, TabListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if (arg.length == 0) {
			Messages.sendMessage(sender, "unban.help");
			return false;
		}

		OfflinePlayer cible = Adapter.getAdapter().getOfflinePlayer(arg[0]);
		if (!cible.hasPlayedBefore()) {
			Messages.sendMessage(sender, "invalid_player", "%arg%", arg[0]);
			return false;
		}

		if (!BanManager.isBanned(cible.getUniqueId())) {
			Messages.sendMessage(sender, "unban.not_banned", "%name%", cible.getName());
			return false;
		}

		Ban revokedBan = BanManager.revokeBan(cible.getUniqueId());
		if (revokedBan != null) {
			Messages.sendMessage(sender, "unban.well_unban", "%name%", cible.getName());
			return true;
		} else {
			// Tell the sender the revocation failed
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] arg, String prefix) {
		List<String> suggestions = new ArrayList<>();
		if (arg.length == 1) {
			// /nunban |
			for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
				if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
					suggestions.add(p.getName());
				}
			}
		}
		return suggestions;
	}
}