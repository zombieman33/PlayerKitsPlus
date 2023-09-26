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
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case DIAMOND_HELMET:
            case NETHERITE_HELMET:
                return EquipmentSlot.HEAD;
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
            case ELYTRA:
                return EquipmentSlot.CHEST;
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case NETHERITE_LEGGINGS:
                return EquipmentSlot.LEGS;
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
            case NETHERITE_BOOTS:
                return EquipmentSlot.FEET;
            default:
                return null;
        }
    }

}
