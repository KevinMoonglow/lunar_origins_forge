package io.github.kevinmoonglow.lunar_origins;

import com.mojang.logging.LogUtils;
import io.github.kevinmoonglow.lunar_origins.block.LunarOriginsBlocks;
import io.github.kevinmoonglow.lunar_origins.blockentity.LunarOriginsBlockEntities;
import io.github.kevinmoonglow.lunar_origins.effect.LunarOriginsEffects;
import io.github.kevinmoonglow.lunar_origins.enchantment.LunarOriginsEnchantments;
import io.github.kevinmoonglow.lunar_origins.entity.LunarOriginsEntities;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsCreativeTabs;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsItems;
import io.github.kevinmoonglow.lunar_origins.loot.LunarOriginsLoot;
import io.github.kevinmoonglow.lunar_origins.potion.LunarOriginsPotions;
import io.github.kevinmoonglow.lunar_origins.power.LunarOriginsPowers;
import io.github.kevinmoonglow.lunar_origins.power.ModifyBreathingPower;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LunarOrigins.MOD_ID)
@ParametersAreNonnullByDefault
public class LunarOrigins
{
    public static final String MOD_ID = "lunar_origins";
    private static final Logger LOGGER = LogUtils.getLogger();

    public LunarOrigins(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(LunarOriginsPowers.class);
        MinecraftForge.EVENT_BUS.register(ModifyBreathingPower.class);
        MinecraftForge.EVENT_BUS.register(LunarOriginsItems.class);
        MinecraftForge.EVENT_BUS.register(LunarOriginsLoot.class);

        LunarOriginsCreativeTabs.register(modEventBus);
        LunarOriginsBlocks.register(modEventBus);
        LunarOriginsItems.register(modEventBus);
        LunarOriginsEffects.register(modEventBus);
        LunarOriginsPotions.register(modEventBus);
        LunarOriginsPowers.register(modEventBus);
        LunarOriginsEnchantments.register(modEventBus);
        LunarOriginsLoot.register(modEventBus);
        LunarOriginsBlockEntities.register(modEventBus);
        LunarOriginsEntities.register(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LunarOriginsPotions.initPotionRecipes();
        LunarOriginsItems.initItems();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        /*
        var biomes = event.getServer().registryAccess().registryOrThrow(Registries.BIOME);

        LOGGER.info("== Biome Tags Loaded: ==");


        biomes.getTags().forEach(tag -> {
            LOGGER.info("{}", tag.getFirst().toString());
            tag.getSecond().forEach(x -> {
                LOGGER.info(":: {}", x.toString());
            });
        });
        LOGGER.info("========================");
         */
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
