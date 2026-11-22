package zixiu.core;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import java.util.Properties;

/**
 * 模组设置。目前只有一项：九字遗物是否只出现在字修的角色池里。
 *
 * 注册遗物发生在模组加载时，所以这个开关改完要重启游戏才生效。
 */
public final class ZixiuConfig {

    private static final String FILE = "zixiuConfig";
    private static final String ISOLATION = "inscriptionIsolation";

    private static SpireConfig config;
    private static boolean isolation = true;

    private ZixiuConfig() {
    }

    public static void load() {
        try {
            Properties defaults = new Properties();
            defaults.setProperty(ISOLATION, "true");
            config = new SpireConfig("zixiu", FILE, defaults);
            isolation = config.getBool(ISOLATION);
        } catch (Exception e) {
            config = null;
            isolation = true;
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
}
