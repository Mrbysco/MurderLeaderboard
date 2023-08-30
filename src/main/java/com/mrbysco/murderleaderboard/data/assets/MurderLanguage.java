package com.mrbysco.murderleaderboard.data.assets;

import com.mrbysco.murderleaderboard.MurderLeaderboard;
import com.mrbysco.murderleaderboard.registry.MurderRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class MurderLanguage extends LanguageProvider {
	public MurderLanguage(DataGenerator gen) {
		super(gen, MurderLeaderboard.MOD_ID, "en_us");
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
	}
}