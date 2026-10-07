package io.github.kevinmoonglow.lunar_origins.item;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.client.CustomTooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Optional;

public class GnapGlasses extends Item implements ICurioItem {
    public GnapGlasses(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        ICurioItem.super.curioTick(slotContext, stack);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        IPowerContainer powers = IPowerContainer.get(slotContext.entity()).resolve().orElseThrow();

        powers.addPower(
                ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "dizzied_from_glasses"),
                ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "face_equip"));
        powers.addPower(
                ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "gnap_glasses"),
                ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "face_equip"));


        ICurioItem.super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        IPowerContainer powers = IPowerContainer.get(slotContext.entity()).resolve().orElseThrow();

        powers.removePower(
                ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "dizzied_from_glasses"),
                ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "face_equip")
        );
        powers.removePower(
                ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "gnap_glasses"),
                ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "face_equip")
        );

        ICurioItem.super.onUnequip(slotContext, newStack, stack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CustomTooltips.gnapGlassesTooltip(pStack, pTooltipComponents));
    }
}
