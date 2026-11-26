package zixiu.core;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import java.util.Properties;

/**
 * 模组设置。
 *
 * <ul>
 *   <li>九字遗物是否只出现在字修的角色池里（模组加载时读取，改完重启生效）；</li>
 *   <li>新华字典（卡牌图鉴）是否启用，立即生效；</li>
 *   <li>拆合规则方案（1 / 2），在选角界面的按钮上切换，立即生效。</li>
 * </ul>
 */
public final class ZixiuConfig {

    private static final String FILE = "zixiuConfig";
    private static final String ISOLATION = "inscriptionIsolation";
    private static final String LIBRARY = "cardLibrary";
    private static final String FUSION_MODE = "fusionMode";

    private static SpireConfig config;
    private static boolean isolation = true;
    /** 「新华字典」卡牌图鉴：关掉之后顶栏不再出「典」，也不接管任何输入。 */
    private static boolean library = true;
    /** 1 = 产出虚无、材料进弃牌堆；2 = 产出保留、材料消耗（进消耗堆）。 */
    private static int fusionMode = 1;

    private ZixiuConfig() {
    }

    public static void load() {
        try {
            Properties defaults = new Properties();
            defaults.setProperty(ISOLATION, "true");
            defaults.setProperty(LIBRARY, "true");
            defaults.setProperty(FUSION_MODE, "1");
            config = new SpireConfig("zixiu", FILE, defaults);
            isolation = config.getBool(ISOLATION);
            library = config.getBool(LIBRARY);
            fusionMode = config.getInt(FUSION_MODE);
        } catch (Exception e) {
            config = null;
            isolation = true;
            library = true;
            fusionMode = 1;
        }
    }

    public static boolean isolation() {
        return isolation;
    }

    public static void setIsolation(boolean value) {
        isolation = value;
        if (config == null) {
            return;
        }
        try {
            config.setBool(ISOLATION, value);
            config.save();
        } catch (Exception ignored) {
            // 保存失败只影响下次启动时的默认值，不影响本次运行。
        }
    }

    // ---- 新华字典 ----

    public static boolean libraryEnabled() {
        return library;
    }

    public static void setLibraryEnabled(boolean value) {
        library = value;
        if (config == null) {
            return;
        }
        try {
            config.setBool(LIBRARY, value);
            config.save();
        } catch (Exception ignored) {
            // 保存失败只影响下次启动时的默认值，不影响本次运行。
        }
    }

    // ---- 拆合规则 ----

    /** 1 = 产出虚无、材料进弃牌堆；2 = 产出保留、材料消耗（进消耗堆）。 */
    public static int fusionMode() {
        return fusionMode == 2 ? 2 : 1;
    }

    public static void setFusionMode(int value) {
        fusionMode = value == 2 ? 2 : 1;
        if (config == null) {
            return;
        }
        try {
            config.setInt(FUSION_MODE, fusionMode);
            config.save();
        } catch (Exception ignored) {
            // 同上。
        }
    }

    /** 方案 1 ↔ 方案 2，返回切换后的方案号。 */
    public static int toggleFusionMode() {
        setFusionMode(fusionMode() == 1 ? 2 : 1);
        return fusionMode();
    }

    /** 拆/合用的牌是否进消耗堆（方案 2）；否则进弃牌堆（方案 1）。 */
    public static boolean exhaustsMaterials() {
        return fusionMode() == 2;
    }

    /** 拆/合产出的牌是否虚无且消耗（方案 1）；否则保留（方案 2）。 */
    public static boolean transientResults() {
        return fusionMode() == 1;
    }

    public static String fusionModeLabel() {
        return fusionMode() == 1
                ? "方案 1：产出虚无·消耗 · 材料入弃牌堆"
                : "方案 2：产出保留 · 材料消耗";
    }
}
