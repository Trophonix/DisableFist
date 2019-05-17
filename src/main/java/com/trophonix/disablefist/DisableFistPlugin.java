package com.trophonix.disablefist;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DisableFistPlugin extends JavaPlugin implements Listener {

  private boolean cancel;
  private boolean allEntities;
  private boolean disableForCreative;

  @Override public void onEnable() {
    saveDefaultConfig();
    load();
    getServer().getPluginManager().registerEvents(this, this);
  }

  private void load() {
    cancel = !Objects.requireNonNull(getConfig().getString("mode", "cancel"),
        "Failed to load mode option.").equalsIgnoreCase("nodamage");
    allEntities = getConfig().getBoolean("allEntities", false);
    disableForCreative = getConfig().getBoolean("disableForCreative", false);
  }

  @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length > 0 && (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload"))) {
      reloadConfig();
      load();
      sender.sendMessage(new String[]{
          ChatColor.DARK_GREEN + "Reloaded disablefist config.",
          ChatColor.GREEN + " cancel = " + cancel,
          ChatColor.GREEN + " allEntities = " + allEntities,
          ChatColor.GREEN + " disableForCreative = " + disableForCreative
      });
      return true;
    }
    sender.sendMessage(ChatColor.RED + "/disablefist rl");
    return true;
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent event) {
    if (!((allEntities || event.getEntity() instanceof Player) && event.getDamager() instanceof Player)) return;
    Player damager = (Player) event.getDamager();
    if (!disableForCreative && damager.getGameMode().equals(GameMode.CREATIVE)) return;
    ItemStack current = damager.getInventory().getItemInMainHand();
    if (current.getType().equals(Material.AIR)) {
      if (cancel) event.setCancelled(true);
      else event.setDamage(0);
    }
  }
}
