package io.github.kevinmoonglow.lunar_origins.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Goggles extends ArmorItem {
    public Goggles(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new GlassBowlPowerProvider();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(entity.isAlive()) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if(slotId != EquipmentSlot.HEAD.getIndex()) {
                CompoundTag nbt = stack.getOrCreateTag();
                nbt.putBoolean("waterEyeLevel", livingEntity.isUnderWater());
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putBoolean("waterEyeLevel", player.isUnderWater());
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        Component s = super.getName(stack);
        CompoundTag nbt = stack.getOrCreateTag();
        if(nbt.getBoolean("waterEyeLevel")) {
            return Component.translatable("item.lunar_origins.goggles.flooded");
        }
        else return s;
    }
}
