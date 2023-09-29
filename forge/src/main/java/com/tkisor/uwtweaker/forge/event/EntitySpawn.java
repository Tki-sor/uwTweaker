package com.tkisor.uwtweaker.forge.event;

import com.tkisor.uwtweaker.UwTweaker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = UwTweaker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntitySpawn {
    private static final UUID MODIFIER_ID_HEALTH = UUID.fromString("37346111-0b28-4416-8cab-df8226593b86");
    private static final UUID MODIFIER_ID_DAMAGE = UUID.fromString("9b5638c3-904c-410f-a9df-45c8afe7ea86");
    private static final String MODIFIER_NAME_HEALTH = "UwTweaker.HealthModifier";
    private static final String MODIFIER_NAME_DAMAGE = "UwTweaker.DamageModifier";

    @SubscribeEvent
    public static void onEntitySpawn(FinalizeSpawn event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        double attackDamageIncrease;
        if (entity instanceof Monster) {
            attackDamageIncrease = entity.getMaxHealth() / 12 + Math.log(Difficulty.getGameDifficultSize() * 2) * 0.025;
        }

        double maxHealthIncrease;
        double multiplier = Difficulty.difficultMultiplier();
        if (entity instanceof Monster) {
            maxHealthIncrease = multiplier * ((Math.exp(Math.log(Difficulty.getGameDifficultSize() * 3) * (1.35 + Math.log(entity.getMaxHealth()) / 3.7)) / 23333) / 1000 + entity.getMaxHealth() * 5 + Math.pow(Math.log(Difficulty.getGameDifficultSize() * 2), 4) + Difficulty.getGameDifficultSize() * 1.5 + Math.pow(Math.log(Difficulty.getGameDifficultSize() * 2), 5) * 5);
        } else {
            maxHealthIncrease = multiplier * (Math.log((Difficulty.getGameDifficultSize() / entity.getMaxHealth()) * 20) * 10 + entity.getMaxHealth() * 5 + Difficulty.getGameDifficultSize() * (1 + Math.log(entity.getMaxHealth()) * 3.5));
        }
        setMaxHealth(entity, maxHealthIncrease, AttributeModifier.Operation.ADDITION);
        
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side.isClient()) return;

    }

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerRespawnEvent event) {
        if (event.getEntity().isLocalPlayer()) return;
        if (event.getEntity() == null) return;
        playerHealth(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().isLocalPlayer()) return;
        if (event.getEntity() == null) return;
        playerHealth(event.getEntity());

//        PersistentData.get(event.getEntity().getServer()).putInt("UwTweaker", 233123);

    }

    public static void playerHealth(Player player) {
        if (player == null) return;
        switch (Difficulty.getGameDifficulty()) {
            case "easy" -> {
                setMaxHealth(player, 20, AttributeModifier.Operation.ADDITION);
            }
            case "normal" -> {
//                setMaxHealth(player, 20, AttributeModifier.Operation.ADDITION);
            }
            case "hard" -> {
                setMaxHealth(player, -10, AttributeModifier.Operation.ADDITION);
            }
            case "inferno" -> {
                setMaxHealth(player, -16, AttributeModifier.Operation.ADDITION);
            }
            default -> throw new IllegalStateException("Unexpected value: " + Difficulty.getGameDifficulty());
        }
    }

    /**
     * 代码引用自：
     * <a href="https://github.com/SilentChaos512/ScalingHealth">ScalingHealth Mod</a>
     * <p>
     * 遵循 MIT License
     */
    public static void setModifier(LivingEntity entity, Attribute attribute, UUID uuid, String name, double amount, AttributeModifier.Operation op) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        AttributeModifier mod = instance.getModifier(uuid);
        if (mod != null) instance.removeModifier(mod);
        instance.addPermanentModifier(new AttributeModifier(uuid, name, amount, op));
    }

    public static void setMaxHealth(LivingEntity entity, double amount, AttributeModifier.Operation op) {
        double oldMax = entity.getMaxHealth();
        setModifier(entity, Attributes.MAX_HEALTH, MODIFIER_ID_HEALTH, MODIFIER_NAME_HEALTH, amount, op);
        double newMax = entity.getMaxHealth();

        // Heal entity when increasing max health
        if (newMax > oldMax) {
            float healAmount = (float) (newMax - oldMax);
            entity.setHealth(entity.getHealth() + healAmount);
        } else if (entity.getHealth() > newMax) {
            entity.setHealth((float) newMax);
        }
    }

    public static void addAttackDamage(LivingEntity entity, double amount, AttributeModifier.Operation op) {
        setModifier(entity, Attributes.ATTACK_DAMAGE, MODIFIER_ID_DAMAGE, MODIFIER_NAME_DAMAGE, amount, op);
    }
}
