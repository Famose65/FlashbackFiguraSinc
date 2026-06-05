package me.flashbackfigurasinc.mixin;

import com.moulberry.flashback.Flashback;
import me.flashbackfigurasinc.anim.ExportAnimState;
import net.minecraft.client.render.GameRenderer;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ПРИЧИНА: Figura накладывает кадровые анимации в Avatar.applyAnimations(),
 * встроенной ТОЛЬКО в Minecraft.runTick. При ЭКСПОРТЕ Flashback кадр рисуется напрямую
 * через GameRenderer.render() БЕЗ runTick -> applyAnimations не зовётся -> анимации тела замерзают.
 *
 * ФИКС: цепляемся к GameRenderer.render (метод Minecraft, стабилен между версиями).
 * Срабатываем ТОЛЬКО когда идёт экспорт (Flashback.EXPORT_JOB != null) — тогда вручную
 * накладываем анимации перед отрисовкой кадра и сбрасываем после.
 * В обычной игре и в предпросмотре EXPORT_JOB == null -> миксин ничего не делает
 * (там анимации накладывает обычный runTick).
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    private void fbfs$applyFiguraAnimDuringExport(CallbackInfo ci) {
        if (Flashback.EXPORT_JOB != null) {
            ExportAnimState.exporting = true;
            AvatarManager.executeAll("flashbackExportApplyAnim", Avatar::applyAnimations);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void fbfs$clearFiguraAnimDuringExport(CallbackInfo ci) {
        if (ExportAnimState.exporting) {
            AvatarManager.executeAll("flashbackExportClearAnim", Avatar::clearAnimations);
            ExportAnimState.exporting = false;
        }
    }
}
