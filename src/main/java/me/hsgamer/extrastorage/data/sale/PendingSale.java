package me.hsgamer.extrastorage.data.sale;

import java.util.UUID;

public final class PendingSale {

    private final UUID playerUUID;
    private final String itemKey;
    private final int amount;
    private final double pricePerUnit;
    private final long timestamp;

    public PendingSale(UUID playerUUID, String itemKey, int amount, double pricePerUnit) {
        this.playerUUID = playerUUID;
        this.itemKey = itemKey;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isExpired(long timeoutMs) {
        return System.currentTimeMillis() - timestamp > timeoutMs;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getItemKey() {
        return itemKey;
    }

    public int getAmount() {
        return amount;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
