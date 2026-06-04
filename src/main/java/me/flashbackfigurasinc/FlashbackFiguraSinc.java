package me.flashbackfigurasinc;

import me.flashbackfigurasinc.lua.plugins.FlashbackSyncPlugin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashbackFiguraSinc implements ClientModInitializer {

    public static final String MODID = "flashbackfigurasinc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        // 1) Lua-API для аватара (регистрирует глобал FlashbackSync)
        new FlashbackSyncPlugin();

        // 2) тип сетевого пакета (clientbound) — чтобы Flashback мог его записать и проиграть
        PayloadTypeRegistry.playS2C().register(SyncPayload.ID, SyncPayload.CODEC);

        // 3) приёмник: на воспроизведении Flashback вливает пакет → обновляем кэш
        ClientPlayNetworking.registerGlobalReceiver(SyncPayload.ID, (payload, context) ->
                SyncStore.put(payload.uuid(), payload.key(), payload.value()));

        LOGGER.info("FlashbackFiguraSinc loaded");
    }
}
