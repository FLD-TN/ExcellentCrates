package su.nightexpress.excellentcrates.hooks.impl;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.hooks.HookId;
import su.nightexpress.nightcore.util.Plugins;

/**
 * Port of nightcore 2.8+ ItemsAdderItemProvider / ItemsAdderItem (used by ExcellentCrates 6.2+ "save by Item ID").
 * All ItemsAdder classes are referenced here only, so the plugin works without ItemsAdder installed.
 */
public class ItemsAdderHook {

    public static final String HANDLER = "itemsadder";

    public static boolean isAvailable() {
        return Plugins.isInstalled(HookId.ITEMS_ADDER);
    }

    @Nullable
    public static String getItemId(@NotNull ItemStack itemStack) {
        if (!isAvailable() || itemStack.getType().isAir()) return null;

        CustomStack customStack = CustomStack.byItemStack(itemStack);
        return customStack == null ? null : customStack.getNamespacedID();
    }

    public static boolean isValidId(@NotNull String itemId) {
        return isAvailable() && CustomStack.isInRegistry(itemId);
    }

    /**
     * @return Location of the furniture entity that owns this block (furniture hitbox), or null if it's not a furniture block.
     */
    @Nullable
    public static Location getFurnitureLocation(@NotNull Block block) {
        if (!isAvailable()) return null;

        CustomFurniture furniture = CustomFurniture.byAlreadySpawned(block);
        if (furniture == null || furniture.getEntity() == null) return null;

        return furniture.getEntity().getLocation();
    }

    /**
     * @return Location of the furniture if the entity is an ItemsAdder furniture, otherwise null.
     */
    @Nullable
    public static Location getFurnitureLocation(@NotNull Entity entity) {
        if (!isAvailable()) return null;

        CustomFurniture furniture = CustomFurniture.byAlreadySpawned(entity);
        if (furniture == null || furniture.getEntity() == null) return null;

        return furniture.getEntity().getLocation();
    }

    @Nullable
    public static ItemStack createItem(@NotNull String itemId, int amount) {
        if (!isAvailable()) return null;

        CustomStack customStack = CustomStack.getInstance(itemId);
        if (customStack == null) return null;

        ItemStack itemStack = customStack.getItemStack();
        itemStack.setAmount(Math.max(1, amount));
        return itemStack;
    }
}
