package su.nightexpress.excellentcrates.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.hooks.impl.ItemsAdderHook;
import su.nightexpress.nightcore.config.FileConfig;

/**
 * Port of ExcellentCrates 6.2.0 CustomItemProvider: a custom plugin item saved by its ID,
 * so changes made in the item plugin's config are always reflected.
 * Same config layout as upstream: Type / Handler / ItemId / Amount.
 */
public class CustomItemRef {

    private static final String TYPE_CUSTOM = "CUSTOM";

    private final String handlerName;
    private final String itemId;
    private final int    amount;

    public CustomItemRef(@NotNull String handlerName, @NotNull String itemId, int amount) {
        this.handlerName = handlerName.toLowerCase();
        this.itemId = itemId;
        this.amount = Math.max(1, amount);
    }

    @Nullable
    public static CustomItemRef fromItem(@NotNull ItemStack itemStack) {
        String itemId = ItemsAdderHook.getItemId(itemStack);
        if (itemId == null) return null;

        return new CustomItemRef(ItemsAdderHook.HANDLER, itemId, itemStack.getAmount());
    }

    @Nullable
    public static CustomItemRef read(@NotNull FileConfig config, @NotNull String path) {
        if (!TYPE_CUSTOM.equalsIgnoreCase(config.getString(path + ".Type", ""))) return null;

        String handlerName = config.getString(path + ".Handler");
        String itemId = config.getString(path + ".ItemId");
        if (handlerName == null || itemId == null) return null;

        return new CustomItemRef(handlerName, itemId, config.getInt(path + ".Amount", 1));
    }

    public static void write(@NotNull FileConfig config, @NotNull String path, @Nullable CustomItemRef ref) {
        config.remove(path);
        if (ref == null) return;

        config.set(path + ".Type", TYPE_CUSTOM);
        config.set(path + ".Handler", ref.handlerName);
        config.set(path + ".ItemId", ref.itemId);
        config.set(path + ".Amount", ref.amount);
    }

    public boolean isValid() {
        return this.handlerName.equals(ItemsAdderHook.HANDLER) && ItemsAdderHook.isValidId(this.itemId);
    }

    /**
     * @return Fresh item from the item plugin, or null when it's not available (not loaded yet, removed, plugin missing).
     */
    @Nullable
    public ItemStack createItemStack() {
        if (!this.handlerName.equals(ItemsAdderHook.HANDLER)) return null;

        return ItemsAdderHook.createItem(this.itemId, this.amount);
    }

    /**
     * @return Item from the item plugin, or a copy of the given fallback (last saved NBT) when it can't be created.
     */
    @NotNull
    public static ItemStack resolve(@Nullable CustomItemRef ref, @NotNull ItemStack fallback) {
        if (ref != null) {
            ItemStack itemStack = ref.createItemStack();
            if (itemStack != null) return itemStack;
        }
        return new ItemStack(fallback);
    }

    @NotNull
    public String getHandlerName() {
        return this.handlerName;
    }

    @NotNull
    public String getItemId() {
        return this.itemId;
    }

    public int getAmount() {
        return this.amount;
    }
}
