package me.flashbackfigurasinc.mixin;

import me.flashbackfigurasinc.anim.ExportAnimState;
import net.minecraft.client.MinecraftClient;
import org.figuramc.figura.animation.TimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Figura.TimeController.getDiff() в паузе возвращает 0 (и анимации не двигаются).
 * При экспорте Flashback игра "на паузе" + время идёт не в реальном времени, а по таймлайну.
 * Поэтому во время экспорта возвращаем дельту таймлайна экспорта (кол-во тиков на кадр * 0.05с),
 * чтобы анимации продвигались на правильную величину. Вне экспорта — поведение Figura не трогаем.
 */
@Mixin(value = TimeController.class, remap = false)
public class MixinTimeController {

    @Inject(method = "getDiff", at = @At("HEAD"), cancellable = true, remap = false)
    private void fbfs$exportTimelineDiff(CallbackInfoReturnable<Float> cir) {
        if (!ExportAnimState.exporting) {
            return;
        }
        // deltaTicks этого кадра экспорта (Flashback заранее выставляет его в deltaTracker)
        float deltaTicks = MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration();
        cir.setReturnValue(deltaTicks * 0.05f); // 1 тик = 0.05 секунды
    }
}
