package io.github.kevinmoonglow.lunar_origins.item;

import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class DivingHelmet extends GlassBowl {
    public DivingHelmet(ArmorMaterial pMaterial, Type pType, Properties pProperties, int[] sealantDrainSteps) {
        super(pMaterial, pType, pProperties, sealantDrainSteps);
        FULL_KEY = "item.lunar_origins.diving_helmet.full";
    }

    @Override
    public boolean isCracked(ItemStack stack) {
        return false;
    }
}
