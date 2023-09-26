package me.zombieman.playerkitsplus.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArmorManager {

    private static final Set<Material> ARMOR_MATERIALS = new HashSet<>(Arrays.asList(
            Material.ELYTRA, Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
            Material.DIAMOND_HELMET, Material.NETHERITE_HELMET, Material.LEATHER_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
            Material.NETHERITE_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS,
            Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
            Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
            Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
    ));

    public static boolean isArmor(ItemStack item) {
        return ARMOR_MATERIALS.contains(item.getType());
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
            case LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE, ELYTRA:
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
