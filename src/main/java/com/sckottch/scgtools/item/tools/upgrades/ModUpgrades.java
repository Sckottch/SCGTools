package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import com.sckottch.scgtools.utility.tags.ModTags;
import net.minecraft.tags.ItemTags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModUpgrades {
    public static DeferredRegister<ToolUpgrade> UPGRADES =
        DeferredRegister.create(ModRegistries.UPGRADE_REGISTRY_KEY, SCGTools.MOD_ID);
    
    public static final Supplier<ToolUpgrade> MENDING =
        UPGRADES.register("mending", () -> new MendingUpgrade(ToolTier.FLINT, "Mending", ModTags.Items.EVOLVING_ITEM));
    
    public static final Supplier<ToolUpgrade> ENCHANTMENT_I =
        UPGRADES.register("enchantment_i", () -> new EnchantmentUpgrade(ToolTier.NETHERITE, "Enchantment I", ModTags.Items.EVOLVING_ITEM));
    
    public static final Supplier<ToolUpgrade> ENCHANTMENT_II =
        UPGRADES.register("enchantment_ii", () -> new EnchantmentUpgrade(ToolTier.DRACONIC, "Enchantment II", ModTags.Items.EVOLVING_ITEM));
    
    public static final Supplier<ToolUpgrade> HAMMER =
        UPGRADES.register("hammer", () -> new HammerUpgrade(ToolTier.IRON, "Hammer", ModTags.Items.MINING_TOOL));
    
    public static final Supplier<ToolUpgrade> QUICK_DRAW =
        UPGRADES.register("quick_draw", () -> new QuickDrawUpgrade(ToolTier.IRON, "Quick Draw", ModTags.Items.WEAPON));
    
    public static final Supplier<ToolUpgrade> VEIN_MINE =
        UPGRADES.register("vein_mine", () -> new VeinMineUpgrade(ToolTier.DIAMOND, "Vein Mine", ItemTags.PICKAXES));
    
    public static final Supplier<ToolUpgrade> CHAINSAW =
        UPGRADES.register("chainsaw", () -> new ChainsawUpgrade(ToolTier.IRON, "Chainsaw", ItemTags.AXES));
    
    public static final Supplier<ToolUpgrade> TELEPORT =
        UPGRADES.register("teleport", () -> new TeleportUpgrade(ToolTier.DIAMOND, "Teleport", ItemTags.SWORDS));

    public static final Supplier<ToolUpgrade> FERTILIZER =
        UPGRADES.register("fertilizer", () -> new FertilizerUpgrade(ToolTier.IRON, "Fertilizer", ItemTags.HOES));

    public static final Supplier<ToolUpgrade> COMPACTOR =
        UPGRADES.register("compactor", () -> new CompactorUpgrade(ToolTier.IRON, "Compactor", ItemTags.SHOVELS));

    public static void register(IEventBus eventBus) {
        UPGRADES.register(eventBus);
    }
}
