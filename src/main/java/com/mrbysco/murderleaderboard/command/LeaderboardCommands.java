package com.mrbysco.murderleaderboard.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerLevel;

public class LeaderboardCommands {
	public static void initializeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("murderleaderboard");

		root.requires((source) -> source.hasPermission(2))
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

	@SuppressWarnings("SameReturnValue")
	private static int addKill(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		String user = StringArgumentType.getString(ctx, "user");
		String murderer = StringArgumentType.getString(ctx, "murderer");
		ServerLevel overworld = ctx.getSource().getServer().overworld();

		MurderData data = MurderData.get(overworld);
		data.addKill(user, murderer);

		return 0;
	}

	@SuppressWarnings("SameReturnValue")
	private static int setKill(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		String user = StringArgumentType.getString(ctx, "user");
		String murderer = StringArgumentType.getString(ctx, "murderer");
		int killCount = IntegerArgumentType.getInteger(ctx, "count");
		ServerLevel overworld = ctx.getSource().getServer().overworld();

		MurderData data = MurderData.get(overworld);
		data.setKill(user, murderer, killCount);

		return 0;
	}

	@SuppressWarnings("SameReturnValue")
	private static int removeKiller(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		String user = StringArgumentType.getString(ctx, "user");
		String murderer = StringArgumentType.getString(ctx, "murderer");
		ServerLevel overworld = ctx.getSource().getServer().overworld();

		MurderData data = MurderData.get(overworld);
		data.removeKiller(user, murderer);

		return 0;
	}

	@SuppressWarnings("SameReturnValue")
	private static int clear(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		String user = StringArgumentType.getString(ctx, "user");
		ServerLevel overworld = ctx.getSource().getServer().overworld();

		MurderData data = MurderData.get(overworld);
		data.clearKillers(user);

		return 0;
	}
}
