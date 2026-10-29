package com.chaossmp.chaositems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Color;

import java.util.List;

public final class ChaosItems extends JavaPlugin implements Listener {

    private NamespacedKey energyKey;
    private NamespacedKey recipeKey;

    @Override
    public void onEnable() {
        energyKey = new NamespacedKey(this, "energy_drink");
        recipeKey = new NamespacedKey(this, "energy_recipe");

        Bukkit.getPluginManager().registerEvents(this, this);

        registerEnergyDrinkRecipe();

        getLogger().info("ChaosItems включён!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChaosItems выключён!");
    }

    private ItemStack createEnergyDrink() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setColor(Color.LIME);
        meta.setDisplayName(ChatColor.GREEN + "⚡ Энергетик");

        meta.setLore(List.of(
                ChatColor.GRAY + "Напиток ChaosSMP",
                ChatColor.WHITE + "Эффект:",
                ChatColor.GREEN + "Скорость II — 3 минуты",
                "",
                ChatColor.YELLOW + "Выпей, чтобы ускориться!"
        ));

        meta.getPersistentDataContainer().set(
                energyKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        meta.addCustomEffect(
                new PotionEffect(
                        PotionEffectType.SPEED,
                        20 * 180,
                        1,
                        false,
                        true,
                        true
                ),
                true
        );

        item.setItemMeta(meta);
        return item;
    }

    private void registerEnergyDrinkRecipe() {
        ItemStack result = createEnergyDrink();

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);

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

        getLogger().info("Рецепт энергетика зарегистрирован!");
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() != Material.POTION) {
            return;
        }

        if (!(item.getItemMeta() instanceof PotionMeta meta)) {
            return;
        }

        Byte marker = meta.getPersistentDataContainer().get(
                energyKey,
                PersistentDataType.BYTE
        );

        if (marker == null || marker != (byte) 1) {
            return;
        }

        Player player = event.getPlayer();

        player.sendMessage(
                ChatColor.GREEN + "⚡ Ты выпил энергетик! Скорость II на 3 минуты."
        );
    }
}
