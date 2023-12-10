package com.mrbysco.murderleaderboard.handler;

import com.mrbysco.murderleaderboard.config.MurderConfig;
import com.mrbysco.murderleaderboard.world.MurderData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class KillHandler {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof Player player) {
			LivingEntity credit = player.getKillCredit();
			if (credit != null) {
				countDeath(player, credit);
			}
		}
	}

	private void countDeath(Player player, LivingEntity killer) {
		CompoundTag persistentData = killer.getPersistentData();
		if (persistentData.isEmpty()) return;

		String killerName = persistentData.getString(MurderConfig.COMMON.nameKey.get());
		if (!killerName.isEmpty()) {
			Level level = player.level();
			MurderData data = MurderData.get(level);
			data.addKill(player.getGameProfile().getName(), killerName);
		}
	}

	@SubscribeEvent
	public void onDamage(LivingDamageEvent event) {
//		if (event.getEntity() instanceof Player player && !player.level().isClientSide) { //TODO: Only use while testing
//			DamageSource source = event.getSource();
//			if (source.getEntity() instanceof LivingEntity killer) {
//				CompoundTag persistentData = killer.getPersistentData();
//				if (persistentData.isEmpty()) return;
//
//				String killerName = persistentData.getString(MurderConfig.COMMON.nameKey.get());
//				if (!killerName.isEmpty()) {
//					Level level = player.level();
//					MurderData data = MurderData.get(level);
//					System.out.println(killerName);
//					data.addKill(player.getGameProfile().getName(), killerName);
//				}
//			}
//		}
	}
}
