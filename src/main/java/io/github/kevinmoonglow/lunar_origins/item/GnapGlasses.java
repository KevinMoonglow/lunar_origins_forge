package io.github.kevinmoonglow.lunar_origins.item;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
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
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.lunar_origins.gnap_glasses.tooltip.1").withStyle(ChatFormatting.GRAY));

        if(pLevel != null && pLevel.isClientSide()) {
            if(pStack.getItem() == LunarOriginsItems.GNAP_GLASSES.get()) {
                Player player = Minecraft.getInstance().player;
                Optional<IPowerContainer> optional = IPowerContainer.get(player).resolve();
                if(optional.isPresent()) {
                    IPowerContainer powers = optional.get();

                    boolean waterFocus = powers.hasPower(ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "gnaporeon/water_focus"));

                    if (waterFocus) {
                        pTooltipComponents.add(Component.translatable("item.lunar_origins.gnap_glasses.tooltip.needed").withStyle(ChatFormatting.GRAY));
                    } else {
                        pTooltipComponents.add(Component.translatable("item.lunar_origins.gnap_glasses.tooltip.uncomfortable").withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }
    }
}
