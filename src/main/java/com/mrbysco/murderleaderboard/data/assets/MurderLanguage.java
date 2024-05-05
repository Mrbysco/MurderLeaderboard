package com.mrbysco.murderleaderboard.data.assets;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class MurderLanguage extends LanguageProvider {
	public MurderLanguage(PackOutput packOutput) {
		super(packOutput, MurderLeaderboard.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addBlock(MurderRegistry.TOP_PLAYER, "Leaderboard Block");

		add("murderleaderboard.leaderboard.title", "Murder Leaderboard");
		add("murderleaderboard.leaderboard.search", "Search");
		add("murderleaderboard.leaderboard.close", "Close");
		add("murderleaderboard.leaderboard.murderer", "Murderer");
		add("murderleaderboard.leaderboard.killCount", "Kill Count");
		add("murderleaderboard.leaderboard.narration", "%s has murdered you %s times");
		add("murderleaderboard.blockentity.top_player", "Top Player");
		add("murderleaderboard.screen.rank_text", "Rank");
		add("murderleaderboard.toast.title", "%s takes %s");
		add("murderleaderboard.toast.subtitle", "Dethroning %s");

		add("murderleaderboard.networking.choose_rank.failed", "Failed to choose rank %s");
		add("murderleaderboard.networking.sync_kills.failed", "Failed to sync kills %s");

		add("murderleaderboard.command.add_kill", "Add kill for murderer %s to user %s");
		add("murderleaderboard.command.set_kill", "Set kills of %s for murderer %s to %s");
		add("murderleaderboard.command.remove_killer", "Remove murderer %s from user %s");
		add("murderleaderboard.command.clear", "Clear kills for user %s");
		add("murderleaderboard.command.invalid_username", "Invalid username '%s'. Username must be between 3-16 characters");
	}
}