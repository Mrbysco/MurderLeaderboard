package com.mrbysco.murderleaderboard.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;

public class LeaderboardCommands {
	public static void initializeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("murderleaderboard");

		root.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(Commands.literal("add")
						.then(Commands.argument("user", StringArgumentType.word())
								.suggests((cs, builder) -> SharedSuggestionProvider.suggest(cs.getSource().getOnlinePlayerNames(), builder))
								.then(Commands.argument("murderer", StringArgumentType.word())
										.executes(LeaderboardCommands::addKill)
								)
						)
				)
				.then(Commands.literal("remove_killer")
						.then(Commands.argument("user", StringArgumentType.word())
								.suggests((cs, builder) -> SharedSuggestionProvider.suggest(cs.getSource().getOnlinePlayerNames(), builder))
								.then(Commands.argument("murderer", StringArgumentType.word())
										.executes(LeaderboardCommands::removeKiller)
								)
						)
				)
				.then(Commands.literal("set")
						.then(Commands.argument("user", StringArgumentType.word())
								.suggests((cs, builder) -> SharedSuggestionProvider.suggest(cs.getSource().getOnlinePlayerNames(), builder))
								.then(Commands.argument("murderer", StringArgumentType.word())
										.then(Commands.argument("count", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
												.executes(LeaderboardCommands::setKill)
										)
								)
						)
				)
				.then(Commands.literal("clear")
						.then(Commands.argument("user", StringArgumentType.word())
								.suggests((cs, builder) -> SharedSuggestionProvider.suggest(cs.getSource().getOnlinePlayerNames(), builder))
								.executes(LeaderboardCommands::clear)
						)
				)
		;

		dispatcher.register(root);
	}

	private static int addKill(CommandContext<CommandSourceStack> ctx) {
		String user = StringArgumentType.getString(ctx, "user");
		String murderer = StringArgumentType.getString(ctx, "murderer");
		if (!StringUtil.isValidPlayerName(murderer)) {
			ctx.getSource().sendFailure(Component.translatable("murderleaderboard.command.invalid_username", murderer));
			return 0;
		}
		ServerLevel overworld = ctx.getSource().getServer().overworld();

		MurderData data = MurderData.get(overworld);
		data.addKill(user, murderer);

		ctx.getSource().sendSuccess(() -> Component.translatable("murderleaderboard.command.add_kill", murderer, user), true);

		return 0;
	}

	private static int setKill(CommandContext<CommandSourceStack> ctx) {
		String user = StringArgumentType.getString(ctx, "user");
		String murderer = StringArgumentType.getString(ctx, "murderer");
		if (!StringUtil.isValidPlayerName(murderer)) {
			ctx.getSource().sendFailure(Component.translatable("murderleaderboard.command.invalid_username", murderer));
			return 0;
		}
		int killCount = IntegerArgumentType.getInteger(ctx, "count");
		ServerLevel overworld = ctx.getSource().getServer().overworld();

		MurderData data = MurderData.get(overworld);
		data.setKill(user, murderer, killCount);

		ctx.getSource().sendSuccess(() -> Component.translatable("murderleaderboard.command.set_kill", user, murderer, killCount), true);

		return 0;
	}

	private static int removeKiller(CommandContext<CommandSourceStack> ctx) {
		String user = StringArgumentType.getString(ctx, "user");
		String murderer = StringArgumentType.getString(ctx, "murderer");
		if (!StringUtil.isValidPlayerName(murderer)) {
			ctx.getSource().sendFailure(Component.translatable("murderleaderboard.command.invalid_username", murderer));
			return 0;
		}
		ServerLevel overworld = ctx.getSource().getServer().overworld();

		MurderData data = MurderData.get(overworld);
		data.removeKiller(user, murderer);

		ctx.getSource().sendSuccess(() -> Component.translatable("murderleaderboard.command.remove_killer", murderer, user), true);

		return 0;
	}

	private static int clear(CommandContext<CommandSourceStack> ctx) {
		String user = StringArgumentType.getString(ctx, "user");
		ServerLevel overworld = ctx.getSource().getServer().overworld();

		MurderData data = MurderData.get(overworld);
		data.clearKillers(user);

		ctx.getSource().sendSuccess(() -> Component.translatable("murderleaderboard.command.clear", user), true);

		return 0;
	}
}
