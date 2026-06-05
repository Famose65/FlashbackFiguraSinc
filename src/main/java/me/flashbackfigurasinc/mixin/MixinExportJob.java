package me.flashbackfigurasinc.mixin;

import me.flashbackfigurasinc.anim.ExportAnimState;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ПРИЧИНА: Figura накладывает кадровые анимации в Avatar.applyAnimations(),
 * которая встроена ТОЛЬКО в Minecraft.runTick (каждый кадр обычной игры).
 * Flashback при ЭКСПОРТЕ рисует кадр через ExportJob.render() -> gameRenderer.render(),
 * НЕ вызывая runTick -> applyAnimations не зовётся -> анимации тела замерзают.
 * (Глаза живут, т.к. их двигает скрипт в events.tick, а minecraft.tick() при экспорте зовётся.)
 *
 * ФИКС: перед отрисовкой кадра экспорта вручную накладываем анимации Figura, после — сбрасываем
 * (как делает runTick HEAD/RETURN в обычной игре). Срабатывает ТОЛЬКО в коде экспорта Flashback —
 * на обычную игру не влияет.
 */
@Mixin(targets = "com.moulberry.flashback.exporting.ExportJob", remap = false)
public class MixinExportJob {

    @Inject(method = "render", at = @At("HEAD"))
    private static void fbfs$applyFiguraAnim(CallbackInfo ci) {
        ExportAnimState.exporting = true;
        AvatarManager.executeAll("flashbackExportApplyAnim", Avatar::applyAnimations);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private static void fbfs$clearFiguraAnim(CallbackInfo ci) {
        AvatarManager.executeAll("flashbackExportClearAnim", Avatar::clearAnimations);
        ExportAnimState.exporting = false;
    }
}
