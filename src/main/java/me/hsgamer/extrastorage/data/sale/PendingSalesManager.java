package me.hsgamer.extrastorage.data.sale;

import me.hsgamer.extrastorage.ExtraStorage;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PendingSalesManager {

    private final Map<String, PendingSale> pendingSales = new ConcurrentHashMap<>();
    private final long timeoutMs;
    private BukkitTask cleanupTask;

    public PendingSalesManager(long timeoutSeconds, long cleanupIntervalSeconds) {
        this.timeoutMs = timeoutSeconds * 1000L;
    }

    public void startCleanup(Plugin plugin, long cleanupIntervalSeconds) {
        this.cleanupTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::cleanupExpired, cleanupIntervalSeconds * 20L, cleanupIntervalSeconds * 20L);
    }

    public void stop() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
        pendingSales.clear();
    }

    private static String makeKey(UUID playerUUID, String itemKey) {
        return playerUUID.toString() + ":" + itemKey;
    }

    public boolean isPending(UUID playerUUID, String itemKey) {
        String key = makeKey(playerUUID, itemKey);
        PendingSale sale = pendingSales.get(key);
        if (sale == null) return false;
        if (sale.isExpired(timeoutMs)) {
            pendingSales.remove(key);
            return false;
        }
        return true;
    }

    public void addPending(UUID playerUUID, String itemKey, int amount, double pricePerUnit) {
        String key = makeKey(playerUUID, itemKey);
        pendingSales.put(key, new PendingSale(playerUUID, itemKey, amount, pricePerUnit));
    }

    public PendingSale confirmAndRemove(UUID playerUUID, String itemKey) {
        String key = makeKey(playerUUID, itemKey);
        PendingSale sale = pendingSales.remove(key);
        if (sale == null) return null;
        if (sale.isExpired(timeoutMs)) return null;
        return sale;
    }

    public void clearPlayer(UUID playerUUID) {
        String prefix = playerUUID.toString() + ":";
        pendingSales.keySet().removeIf(key -> key.startsWith(prefix));
    }

    private void cleanupExpired() {
        Iterator<Map.Entry<String, PendingSale>> it = pendingSales.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().isExpired(timeoutMs)) {
                it.remove();
            }
        }
    }
}
