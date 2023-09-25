package me.zombieman.playerkitsplus.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    public static ItemStack createItem(ItemStack item, String name,  boolean enchanted, @Nullable String... lore) {
        ItemMeta meta = item.getItemMeta();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        meta.displayName(miniMessage.deserialize(name).style(style -> style.decoration(TextDecoration.ITALIC, false)));

        if (lore != null) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(miniMessage.deserialize(line).style(style -> style.decoration(TextDecoration.ITALIC, false)));
            }
            meta.lore(loreComponents);
        }
        if (enchanted) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }
}
