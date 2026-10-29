package com.chaossmp.chaositems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public final class ChaosItems extends JavaPlugin implements Listener {

private NamespacedKey energyKey;
private NamespacedKey superAppleKey;
private NamespacedKey energyRecipeKey;
private NamespacedKey superAppleRecipeKey;

@Override
public void onEnable() {
    energyKey = new NamespacedKey(this, "energy_drink");
    superAppleKey = new NamespacedKey(this, "super_apple");

    energyRecipeKey = new NamespacedKey(this, "energy_recipe");
    superAppleRecipeKey = new NamespacedKey(this, "super_apple_recipe");

    Bukkit.getPluginManager().registerEvents(this, this);

    registerEnergyDrinkRecipe();
    registerSuperAppleRecipe();

    getLogger().info("ChaosItems enabled!");
}

@Override
public void onDisable() {
    getLogger().info("ChaosItems disabled.");
}

// ЭНЕРГЕТИК

private ItemStack createEnergyDrink() {
    ItemStack item = new ItemStack(Material.POTION);
    PotionMeta meta = (PotionMeta) item.getItemMeta();

    meta.setColor(Color.LIME);
    meta.setDisplayName(ChatColor.GREEN + "⚡ Энергетик");

    meta.setLore(List.of(
            ChatColor.GRAY + "Напиток ChaosSMP",
            ChatColor.WHITE + "Эффект:",
            ChatColor.GREEN + "Скорость III — 3 минуты",
            "",
            ChatColor.YELLOW + "Выпей, чтобы ускориться!"
    ));

    meta.getPersistentDataContainer().set(
            energyKey,
            PersistentDataType.BYTE,
            (byte) 1
    );

    item.setItemMeta(meta);
    return item;
}

private void registerEnergyDrinkRecipe() {
    ShapedRecipe recipe = new ShapedRecipe(
            energyRecipeKey,
            createEnergyDrink()
    );

    recipe.shape(
            " S ",
            "CGR",
            " E "
    );

    recipe.setIngredient('S', Material.SUGAR);
    recipe.setIngredient('C', Material.CACTUS);
    recipe.setIngredient('G', Material.GLASS_BOTTLE);
    recipe.setIngredient('R', Material.REDSTONE);
    recipe.setIngredient('E', Material.EMERALD);

    Bukkit.addRecipe(recipe);
    getLogger().info("Energy drink recipe registered!");
}

// SUPER APPLE

private ItemStack createSuperApple() {
    ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
    ItemMeta meta = item.getItemMeta();

    meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Super Apple");

    meta.setLore(List.of(
            ChatColor.DARK_PURPLE + "Легендарный предмет ChaosSMP",
            "",
            ChatColor.YELLOW + "Поглощение V — 2 минуты",
            ChatColor.GREEN + "Регенерация III — 2 минуты",
            ChatColor.BLUE + "Сопротивление II — 5 минут",
            ChatColor.RED + "Огнестойкость — 10 минут",
            ChatColor.DARK_RED + "Сила II — 3 минуты",
            ChatColor.AQUA + "Скорость II — 3 минуты"
    ));

    meta.getPersistentDataContainer().set(
            superAppleKey,
            PersistentDataType.BYTE,
            (byte) 1
    );

    item.setItemMeta(meta);
    return item;
}

private void registerSuperAppleRecipe() {
    ShapedRecipe recipe = new ShapedRecipe(
            superAppleRecipeKey,
            createSuperApple()
    );

    recipe.shape(
            "GGG",
            "GAG",
            "GGG"
    );

    recipe.setIngredient('G', Material.GOLD_BLOCK);
    recipe.setIngredient('A', Material.APPLE);

    Bukkit.addRecipe(recipe);
    getLogger().info("Super Apple recipe registered!");
}

// ОБРАБОТКА ИСПОЛЬЗОВАНИЯ ПРЕДМЕТОВ

@EventHandler
public void onItemConsume(PlayerItemConsumeEvent event) {
    ItemStack item = event.getItem();

    if (item.getType() == Material.POTION
            && item.getItemMeta() instanceof PotionMeta meta) {

        Byte marker = meta.getPersistentDataContainer().get(
                energyKey,
                PersistentDataType.BYTE
        );

        if (marker != null && marker == (byte) 1) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            consumeOneItem(player, event.getHand());

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SPEED,
                    20 * 180,
                    2,
                    false,
                    true,
                    true
            ));

            player.getInventory().addItem(
                    new ItemStack(Material.GLASS_BOTTLE)
            );

            player.sendMessage(
                    ChatColor.GREEN
                            + "⚡ Вы выпили энергетик! Скорость III на 3 минуты."
            );
            return;
        }
    }

    if (item.getType() == Material.GOLDEN_APPLE
            && item.getItemMeta() != null) {

        Byte marker = item.getItemMeta()
                .getPersistentDataContainer()
                .get(superAppleKey, PersistentDataType.BYTE);

        if (marker != null && marker == (byte) 1) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            consumeOneItem(player, event.getHand());

            player.setFoodLevel(20);
            player.setSaturation(20);

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.ABSORPTION,
                    20 * 120,
                    4,
                    false,
                    true,
                    true
            ));

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    20 * 120,
                    2,
                    false,
                    true,
                    true
            ));

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.RESISTANCE,
                    20 * 300,
                    1,
                    false,
                    true,
                    true
            ));

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.FIRE_RESISTANCE,
                    20 * 600,
                    0,
                    false,
                    true,
                    true
            ));

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.STRENGTH,
                    20 * 180,
                    1,
                    false,
                    true,
                    true
            ));

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SPEED,
                    20 * 180,
                    1,
                    false,
                    true,
                    true
            ));

            player.sendMessage(
                    ChatColor.GOLD + "" + ChatColor.BOLD
                            + "SUPER APPLE "
                            + ChatColor.GREEN
                            + "Вы получили все эффекты!"
            );
        }
    }
}

private void consumeOneItem(Player player, EquipmentSlot hand) {
    ItemStack heldItem = hand == EquipmentSlot.OFF_HAND
            ? player.getInventory().getItemInOffHand()
            : player.getInventory().getItemInMainHand();

    if (heldItem.getAmount() > 1) {
        heldItem.setAmount(heldItem.getAmount() - 1);
    } else {
        if (hand == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(null);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }
}

}
