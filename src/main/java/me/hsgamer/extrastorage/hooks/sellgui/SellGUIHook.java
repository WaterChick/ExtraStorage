package me.hsgamer.extrastorage.hooks.sellgui;

import me.aov.sellgui.SellGUI;
import me.aov.sellgui.SellGUIAPI;
import me.aov.sellgui.SellGUIMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public final class SellGUIHook {

    private SellGUIMain sellPlugin;
    private SellGUIAPI sellApi;

    public void init() {
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("SellGUI")) {
                this.sellPlugin = (SellGUIMain) Bukkit.getPluginManager().getPlugin("SellGUI");
                if (this.sellPlugin != null) {
                    this.sellApi = new SellGUIAPI(this.sellPlugin);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "[ExtraStorage] Failed to hook into SellGUI", e);
            this.sellPlugin = null;
            this.sellApi = null;
        }
    }

    public boolean isAvailable() {
        return sellPlugin != null && sellPlugin.isEnabled() && sellApi != null;
    }

    public double getPrice(ItemStack item, Player player) {
        if (!isAvailable()) return 0.0;
        try {
            return sellApi.getPrice(item, player);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "[ExtraStorage] SellGUI API getPrice failed", e);
            return 0.0;
        }
    }

    public SellGUIMain getSellPlugin() {
        return sellPlugin;
    }

    public void sendSoldMessage(Player player, double totalPrice) {
        if (!isAvailable()) return;
        try {
            String soldMsg = sellPlugin.getLangConfig().getString("sold-message");
            if (soldMsg == null) return;

            if (sellPlugin.getConfig().getBoolean("round-places")) {
                String total = SellGUI.roundString(
                        String.valueOf(totalPrice),
                        sellPlugin.getConfig().getInt("places-to-round")
                );
                player.sendMessage(SellGUI.color(soldMsg.replaceAll("%total%", total)));
            } else {
                player.sendMessage(SellGUI.color(soldMsg.replaceAll("%total%", String.valueOf(totalPrice))));
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "[ExtraStorage] Failed to send SellGUI sold message", e);
        }
    }
}
