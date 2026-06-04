package me.flashbackfigurasinc.lua;

import me.flashbackfigurasinc.SyncStore;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaTypeDoc;

/**
 * Lua-глобал FlashbackSync для аватара.
 *   FlashbackSync.set(uuid, ключ, значение)  — пометить смену (запишется в реплей).
 *   FlashbackSync.get(uuid, ключ)            — прочитать (вживую: последний set; в реплее: проигранный).
 * uuid — строка player:getUUID(), чтобы метки не путались между игроками.
 */
@LuaWhitelist
@LuaTypeDoc(
        name = "FlashbackSync",
        value = "flashback_sync"
)
public class FlashbackSyncAPI {

    @LuaWhitelist
    public static void set(String uuid, String key, String value) {
        SyncStore.set(uuid, key, value);
    }

    @LuaWhitelist
    public static String get(String uuid, String key) {
        return SyncStore.get(uuid, key);
    }

    @Override
    public String toString() {
        return "FlashbackSyncAPI";
    }
}
