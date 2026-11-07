package net.diamondlocator.item;

import net.diamondlocator.DiamondLocator;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, DiamondLocator.MOD_ID);

    // Локатор Алмазов
    public static final RegistryObject<Item> DIAMOND_LOCATOR = ITEMS.register("diamond_locator",
            () -> new DiamondLocatorItem(new Item.Properties().stacksTo(1)));

    // НОВЫЙ: Локатор Незерита (Древних обломков)
    public static final RegistryObject<Item> NETHERITE_LOCATOR = ITEMS.register("netherite_locator",
            () -> new NetheriteLocatorItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}