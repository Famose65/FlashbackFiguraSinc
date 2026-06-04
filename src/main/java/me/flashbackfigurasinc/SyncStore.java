package me.flashbackfigurasinc;

import com.moulberry.flashback.Flashback;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Кэш меток (uuid → ключ → значение) + запись в реплей Flashback.
 *  - set(): обновляет кэш (для чтения вживую) и, если идёт запись, пишет пакет в реплей.
 *  - приёмник пакета (на воспроизведении) зовёт put() → кэш обновляется → аватар читает get().
 */
public final class SyncStore {

    private static final Map<String, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    private SyncStore() {}

    public static void put(String uuid, String key, String value) {
        if (uuid == null || key == null) return;
        CACHE.computeIfAbsent(uuid, u -> new ConcurrentHashMap<>()).put(key, value == null ? "" : value);
    }

    public static String get(String uuid, String key) {
        if (uuid == null || key == null) return null;
        Map<String, String> m = CACHE.get(uuid);
        return m == null ? null : m.get(key);
    }

    /** Вызывается из Lua FlashbackSync.set(): кэш + запись в реплей (если RECORDER активен). */
    public static void set(String uuid, String key, String value) {
        put(uuid, key, value);
        try {
            if (Flashback.RECORDER != null) {
                Flashback.RECORDER.writePacketAsync(
                        new CustomPayloadS2CPacket(new SyncPayload(uuid, key, value == null ? "" : value)),
                        NetworkPhase.PLAY);
            }
        } catch (Throwable ignored) {
            // Flashback не активен/недоступен — просто работаем по кэшу
        }
    }
}
