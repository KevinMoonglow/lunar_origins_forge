package io.github.kevinmoonglow.lunar_origins.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BowlSealant extends Item {
    public static final int SEALANT_AMOUNT = 12000;
    public BowlSealant(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickType, Player player) {
        if (clickType == ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack target = slot.getItem();
            Item targetItem = target.getItem();
            if (targetItem instanceof GlassBowl) {
                boolean result = ((GlassBowl) targetItem).applySealant(target, SEALANT_AMOUNT);
                if (result) {
                    stack.shrink(1);
                }
                return true;
            }
        }

        return super.overrideStackedOnOther(stack, slot, clickType, player);
    }
}
