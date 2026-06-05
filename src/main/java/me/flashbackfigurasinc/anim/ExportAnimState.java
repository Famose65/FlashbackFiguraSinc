package me.flashbackfigurasinc.anim;

/**
 * Общий флаг: идёт ли прямо сейчас экспорт кадра Flashback.
 * Ставится в MixinExportJob (вокруг отрисовки кадра экспорта),
 * читается в MixinTimeController (чтобы продвигать время анимаций по таймлайну экспорта,
 * а не возвращать 0 как в паузе).
 */
public class ExportAnimState {
    public static volatile boolean exporting = false;

    private ExportAnimState() {}
}
