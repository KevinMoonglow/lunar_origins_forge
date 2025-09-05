package io.github.kevinmoonglow.lunar_origins.item;

import io.github.apace100.apoli.util.PowerGrantingItem;
import io.github.apace100.apoli.util.StackPowerUtil;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class GogglesPowerProvider  implements PowerGrantingItem, ICapabilityProvider {
    private final LazyOptional<PowerGrantingItem> holder = LazyOptional.of(() -> this);
    private final List<StackPowerUtil.StackPower> POWERS;

    GogglesPowerProvider() {
        POWERS = new LinkedList<>();

        StackPowerUtil.StackPower nightVisionGogglesPower = new StackPowerUtil.StackPower();
        nightVisionGogglesPower.powerId = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "night_vision_for_goggles");
        nightVisionGogglesPower.slot = EquipmentSlot.HEAD;
        nightVisionGogglesPower.isHidden = true;
        nightVisionGogglesPower.isNegative = false;
        POWERS.add(nightVisionGogglesPower);
    }

    @Override
    public @NotNull Collection<StackPowerUtil.StackPower> getPowers(ItemStack itemStack, EquipmentSlot equipmentSlot) {
        return POWERS.stream().filter(stackPower -> stackPower.slot == equipmentSlot).toList();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return ApoliCapabilities.POWER_GRANTING_ITEM.orEmpty(cap, holder);
    }
}
