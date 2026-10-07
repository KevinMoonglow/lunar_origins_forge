package io.github.kevinmoonglow.lunar_origins.client;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = LunarOrigins.MOD_ID, value = Dist.CLIENT)
public class CustomTooltips {
    public static void gnapGlassesTooltip(ItemStack stack, List<Component> components) {
        Player player = Minecraft.getInstance().player;
        Optional<IPowerContainer> optional = IPowerContainer.get(player).resolve();
        if (optional.isPresent()) {
            IPowerContainer powers = optional.get();

            boolean waterFocus = powers.hasPower(ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "gnaporeon/water_focus"));
            components.add(Component.translatable("item.lunar_origins.gnap_glasses.tooltip.1").withStyle(ChatFormatting.GRAY));

            if(waterFocus)
                components.add(Component.translatable("item.lunar_origins.gnap_glasses.tooltip.needed").withStyle(ChatFormatting.GRAY));
            else
                components.add(Component.translatable("item.lunar_origins.gnap_glasses.tooltip.uncomfortable").withStyle(ChatFormatting.GRAY));
        }
    }
}
