package net.diamondlocator;

import com.mojang.logging.LogUtils;
import net.diamondlocator.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DiamondLocator.MOD_ID)
public class DiamondLocator {
    public static final String MOD_ID = "diamond_locator";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DiamondLocator() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);

        // Регистрируем метод для добавления предметов в креативные вкладки
        modEventBus.addListener(this::addCreative);
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
        event.accept(ModItems.DIAMOND_LOCATOR.get());
        event.accept(ModItems.NETHERITE_LOCATOR.get()); // Добавляем незеритовый искатель
    }
}
}