package com.tkisor.uwtweaker.forge.event;

import com.tkisor.uwtweaker.UwTweaker;
import com.tkisor.uwtweaker.util.PersistentData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;
import java.util.Random;

@Mod.EventBusSubscriber(modid = UwTweaker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DifficultyEvent {
    private static MinecraftServer server = null;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntitySpawn(MobSpawnEvent.FinalizeSpawn event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;
    }

    // 生物死亡
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (server == null) return;
        if (event.getEntity() == null) return;
        LivingEntity entity = event.getEntity();

        double random;
        if (entity instanceof Monster) {
            random = new Random().nextDouble() * 0.0005;
        } else {
            random = new Random().nextDouble() * 0.0003;
        }
        Difficulty.addGameDifficultSize(random);

    }

    private static boolean isDifficultySync = false;
    private static int saveDifficultySize = 0;
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    protected static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.getServer() == null) return;
        if (event.side.isClient()) return;
        if (event.phase == TickEvent.Phase.END) {
            saveDifficultySize++;
            // 防止过于频繁保存
            if (saveDifficultySize >= 10) {
                saveDifficultySize = 0;
                server = event.getServer();
                saveDifficulty();
            }
        }

        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player == null) return;
            DecimalFormat decimalFormat = new DecimalFormat("#.####");
            player.sendSystemMessage(Component.literal("难度："+ decimalFormat.format(Difficulty.getGameDifficultSize())), true);

        });

        // 初始化难度
        if (!isDifficultySync) {
            double size = PersistentData.get(event.getServer()).getDouble("DifficultSize");
            if (size == 0) {
                PersistentData.get(event.getServer()).putDouble("DifficultSize", 0.0001);
            }
            double difficultSize = PersistentData.get(event.getServer()).getDouble("DifficultSize");
            Difficulty.setGameDifficultSize(difficultSize);
            server = event.getServer();
            isDifficultySync = true;
        }

        // 每10秒增加难度
        if (event.getServer().getTickCount() % 200 == 0) {
            double random = new Random().nextDouble() * 0.001;
            Difficulty.addGameDifficultSize(random);
        }

    }

    public static void saveDifficulty() {
        try {
            PersistentData.get(server).putDouble("DifficultSize", Difficulty.getGameDifficultSize());
        } catch (Exception ignored) {
        }
    }
}
