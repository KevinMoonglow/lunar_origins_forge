package io.github.kevinmoonglow.lunar_origins;

import com.mojang.logging.LogUtils;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.kevinmoonglow.lunar_origins.effect.LunarOriginsEffects;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsCreativeTabs;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsItems;
import io.github.kevinmoonglow.lunar_origins.potion.LunarOriginsPotions;
import io.github.kevinmoonglow.lunar_origins.power.LunarOriginsPowers;
import io.github.kevinmoonglow.lunar_origins.power.configuration.ModifyBreathingConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LunarOrigins.MOD_ID)
public class LunarOrigins
{
    public static final String MOD_ID = "lunar_origins";
    private static final Logger LOGGER = LogUtils.getLogger();

    public LunarOrigins(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        LunarOriginsCreativeTabs.register(modEventBus);
        LunarOriginsItems.register(modEventBus);
        LunarOriginsEffects.register(modEventBus);
        LunarOriginsPotions.register(modEventBus);
        LunarOriginsPowers.register(modEventBus);



        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LunarOriginsPotions.initPotionRecipes();
        LunarOriginsItems.initItems();
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }
    @SubscribeEvent
    public void onBreathingEvent(LivingBreatheEvent event) {
        LivingEntity player = event.getEntity();
        Level world = player.level();

        BlockPos eyePos = BlockPos.containing(player.getEyePosition());

        List<ModifyBreathingConfiguration> powers = IPowerContainer.getPowers(event.getEntity(), LunarOriginsPowers.MODIFY_BREATHING.get())
                .stream().map(x -> (ModifyBreathingConfiguration)x.get().getConfiguration())
                .sorted(Comparator.comparingInt(ModifyBreathingConfiguration::priority).reversed()).toList();

        Optional<ModifyBreathingConfiguration> prioritized = powers.stream().findFirst();
        if(prioritized.isPresent()) {
            ModifyBreathingConfiguration power = prioritized.orElseThrow();

            boolean isBreathableBlock = power.blockCondition().get().check(world, eyePos, () -> world.getBlockState(eyePos));
            boolean isBreathableEffect = power.breathingStatusEffects().getContent().stream().anyMatch(player::hasEffect);

            boolean canBreathe = isBreathableBlock || isBreathableEffect;
            boolean canRefillAir = isBreathableBlock;

            int loseInterval = power.loseAirInterval();
            int gainInterval = power.gainAirInterval();
            double baseLoseAmount = event.getConsumeAirAmount();
            double baseGainAmount = event.getRefillAirAmount();
            double loseAmount = baseLoseAmount;
            double gainAmount = baseGainAmount;

            event.setCanBreathe(canBreathe);
            if(canBreathe) {
                for (ConfiguredModifier<?> m : power.gainAirModifiers().getContent()) {
                    gainAmount = m.apply(player, baseGainAmount, gainAmount);
                }
                double refill = player.tickCount % gainInterval == 0 ? gainAmount : 0;
                event.setRefillAirAmount((int)refill);
                event.setCanRefillAir(canRefillAir);
            }
            else {
                for (ConfiguredModifier<?> m : power.loseAirModifiers().getContent()) {
                    loseAmount = m.apply(player, baseLoseAmount, loseAmount);
                }
                double consume = player.tickCount % loseInterval == 0 ? loseAmount : 0;
                event.setConsumeAirAmount((int) consume);
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)

        {
        }
    }
}
