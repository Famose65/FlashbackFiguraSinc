package me.flashbackfigurasinc;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Сетевой пакет «метки»: (uuid игрока, ключ, значение) — всё строки.
 * Пишется в реплей через Flashback и принимается на воспроизведении.
 */
public record SyncPayload(String uuid, String key, String value) implements CustomPayload {

    public static final CustomPayload.Id<SyncPayload> ID =
            new CustomPayload.Id<>(Identifier.of("flashbackfigurasinc", "sync"));

    public static final PacketCodec<RegistryByteBuf, SyncPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SyncPayload::uuid,
            PacketCodecs.STRING, SyncPayload::key,
            PacketCodecs.STRING, SyncPayload::value,
            SyncPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
