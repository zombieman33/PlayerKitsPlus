package me.zombieman.playerkitsplus.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArmorManager {

    public static boolean isArmor(ItemStack item) {
        Material itemType = item.getType();
        return itemType == Material.LEATHER_HELMET || itemType == Material.CHAINMAIL_HELMET ||
                itemType == Material.IRON_HELMET || itemType == Material.DIAMOND_HELMET ||
                itemType == Material.NETHERITE_HELMET || itemType == Material.LEATHER_CHESTPLATE ||
                itemType == Material.CHAINMAIL_CHESTPLATE || itemType == Material.IRON_CHESTPLATE ||
                itemType == Material.DIAMOND_CHESTPLATE || itemType == Material.NETHERITE_CHESTPLATE ||
                itemType == Material.LEATHER_LEGGINGS || itemType == Material.CHAINMAIL_LEGGINGS ||
                itemType == Material.IRON_LEGGINGS || itemType == Material.DIAMOND_LEGGINGS ||
                itemType == Material.NETHERITE_LEGGINGS || itemType == Material.LEATHER_BOOTS ||
                itemType == Material.CHAINMAIL_BOOTS || itemType == Material.IRON_BOOTS ||
                itemType == Material.DIAMOND_BOOTS || itemType == Material.NETHERITE_BOOTS;
    }

    public static void equipArmor(Player player, ItemStack item) {
        EquipmentSlot slot = getArmorSlot(item);
        ItemStack currentArmor = player.getInventory().getItem(slot);

        if (currentArmor == null || currentArmor.getType().isAir()) {
            player.getInventory().setItem(slot, item);
        } else {
            player.getInventory().addItem(item);
        }
    }

    public static EquipmentSlot getArmorSlot(ItemStack item) {
        Material itemType = item.getType();
        switch (itemType) {
            case LEATHER_HELMET, CHAINMAIL_HELMET, IRON_HELMET, DIAMOND_HELMET, NETHERITE_HELMET:
                return EquipmentSlot.HEAD;
            case LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE:
                return EquipmentSlot.CHEST;
            case LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS:
                return EquipmentSlot.LEGS;
            case LEATHER_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS:
                return EquipmentSlot.FEET;
            default:
                return null;
        }
    }
}
