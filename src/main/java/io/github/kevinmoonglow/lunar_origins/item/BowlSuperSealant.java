package io.github.kevinmoonglow.lunar_origins.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BowlSuperSealant extends Item {
    public BowlSuperSealant(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack stack, @NotNull Slot slot, @NotNull ClickAction clickType, @NotNull Player player) {
        if (clickType == ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack target = slot.getItem();
            Item targetItem = target.getItem();
            if (targetItem instanceof GlassBowl) {
                boolean result = ((GlassBowl) targetItem).applySuperSealant(target);
                if (result) {
                    stack.shrink(1);
                }
                return true;
            }
        }

        return super.overrideStackedOnOther(stack, slot, clickType, player);
    }
}
