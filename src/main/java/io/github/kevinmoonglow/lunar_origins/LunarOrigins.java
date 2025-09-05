package io.github.kevinmoonglow.lunar_origins;

import com.mojang.logging.LogUtils;
import io.github.kevinmoonglow.lunar_origins.block.LunarOriginsBlocks;
import io.github.kevinmoonglow.lunar_origins.blockentity.LunarOriginsBlockEntities;
import io.github.kevinmoonglow.lunar_origins.effect.LunarOriginsEffects;
import io.github.kevinmoonglow.lunar_origins.enchantment.LunarOriginsEnchantments;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsCreativeTabs;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsItems;
import io.github.kevinmoonglow.lunar_origins.loot.LunarOriginsLoot;
import io.github.kevinmoonglow.lunar_origins.potion.LunarOriginsPotions;
import io.github.kevinmoonglow.lunar_origins.power.LunarOriginsPowers;
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
