import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 生成占位美术。尺寸对齐原版素材：
 * 卡面 500x380、遗物 128x128、选角按钮 200x200、选角立绘 1920x1200、
 * 战斗立绘 468x201、肩部 1920x1136、尸体 512x512、事件 600x600。
 */
public class GenAssets {

    private static final String ROOT = "src/main/resources/zixiu/images/";

    public static void main(String[] args) throws Exception {
        // 卡面占位：手牌 250x190 + 大图 _p 500x380（BaseMod 由主图路径推导 _p）
        solid("cards/placeholder.png", 250, 190, new Color(58, 66, 84));
        solid("cards/placeholder_p.png", 500, 380, new Color(58, 66, 84));

        // 卡框：透明画布 + 居中的圆角卡片形状（几何取自原版/送葬人：512 下 301x421，起点 105,47）
        cardShape("cardui/bg_attack_512.png", 512, new Color(92, 38, 38));
        cardShape("cardui/bg_skill_512.png", 512, new Color(38, 58, 92));
        cardShape("cardui/bg_power_512.png", 512, new Color(62, 38, 92));
        cardShape("cardui/bg_attack_1024.png", 1024, new Color(92, 38, 38));
        cardShape("cardui/bg_skill_1024.png", 1024, new Color(38, 58, 92));
        cardShape("cardui/bg_power_1024.png", 1024, new Color(62, 38, 92));
        // 能量球占位：全透明，避免在卡面上画出一个多余的球
        transparent("cardui/card_orb.png", 64, 64);
        transparent("cardui/big_orb.png", 128, 128);
        transparent("cardui/small_orb.png", 64, 64);

        // 战斗立绘是真实美术（tools/KeyBlack 生成），本工具不再覆盖它
        // 肩部是 1920x1136 的大画布，原版几乎全透明，只画肩部那一小块。
        // 填成不透明纯色会在角色身后铺出一整块巨型矩形。
        transparent("char/shoulder.png", 1920, 1136);
        transparent("char/shoulder2.png", 1920, 1136);
        corpse("char/corpse.png", 512, 512);

        // 选角立绘与按钮都是真实美术，本工具不再生成（避免覆盖）

        // 事件图是真实美术，本工具不再覆盖

        // 遗物图标是真实美术（tools/SliceRelics 生成），本工具不再覆盖

        System.out.println("assets generated under " + ROOT);
    }

    private static void solid(String relative, int width, int height, Color color) throws Exception {
        solidAt(ROOT + relative, width, height, color);
    }

    private static void solidAt(String path, int width, int height, Color color) throws Exception {
        File file = new File(path);
        ensureParent(file);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    /** 卡框：圆角描边 + 羊皮纸底，外面透明。 */
    private static void cardFrame(String relative, int size, Color border, Color paper) throws Exception {
        File file = new File(ROOT + relative);
        ensureParent(file);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = Math.round(size * 0.06F);
        int inset = Math.round(size * 0.02F);
        g.setColor(border);
        g.fillRoundRect(0, 0, size, size, arc, arc);
        g.setColor(paper);
        g.fillRoundRect(inset, inset, size - inset * 2, size - inset * 2, arc, arc);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    /** 卡框：透明画布上画一张居中的圆角卡片，其余留透明。 */
    private static void cardShape(String relative, int canvas, Color color) throws Exception {
        File file = new File(ROOT + relative);
        ensureParent(file);
        float k = canvas / 512.0F;
        int x = Math.round(105 * k);
        int y = Math.round(47 * k);
        int w = Math.round(301 * k);
        int h = Math.round(421 * k);
        int arc = Math.round(28 * k);
        BufferedImage image = new BufferedImage(canvas, canvas, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillRoundRect(x, y, w, h, arc, arc);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    /** 秃笔：128 画布，中心 56px 画一支笔。 */
    private static void brushRelic(String path, Color handle, Color ink) throws Exception {
        File file = new File(path);
        ensureParent(file);
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 笔杆
        g.setColor(handle);
        g.fillRoundRect(60, 34, 8, 40, 4, 4);
        // 笔箍
        g.setColor(new Color(96, 96, 104));
        g.fillRect(58, 72, 12, 6);
        // 笔头
        g.setColor(ink);
        int[] xs = {58, 70, 64};
        int[] ys = {78, 78, 98};
        g.fillPolygon(xs, ys, 3);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    /** 选角按钮：仿原版斜角方块 + 摊开的书页。 */
    private static void charButton(String relative, int size) throws Exception {
        File file = new File(ROOT + relative);
        ensureParent(file);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int pad = Math.round(size * 0.07F);
        int arc = Math.round(size * 0.10F);
        g.setColor(new Color(32, 40, 54));
        g.fillRoundRect(pad, pad, size - pad * 2, size - pad * 2, arc, arc);
        g.setColor(new Color(92, 108, 134));
        g.fillRoundRect(pad + 5, pad + 5, size - (pad + 5) * 2, size - (pad + 5) * 2, arc, arc);
        g.setColor(new Color(146, 162, 190));
        g.fillRoundRect(pad + 11, pad + 11, size - (pad + 11) * 2, size - (pad + 11) * 2, arc, arc);

        int cx = size / 2;
        int cy = size / 2;
        int pageW = Math.round(size * 0.42F);
        int pageH = Math.round(size * 0.30F);
        g.setColor(new Color(228, 220, 200));
        g.fillRoundRect(cx - pageW / 2, cy - pageH / 2, pageW, pageH, 8, 8);
        g.setColor(new Color(72, 62, 50));
        g.drawLine(cx - pageW / 2 + 8, cy - pageH / 6, cx + pageW / 2 - 8, cy - pageH / 6);
        g.drawLine(cx - pageW / 2 + 8, cy, cx + pageW / 2 - 8, cy);
        g.drawLine(cx - pageW / 2 + 8, cy + pageH / 6, cx + pageW / 2 - 8, cy + pageH / 6);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    /** 能量球：简单的椭圆占位。 */
    private static void orb(String relative, int width, int height) throws Exception {
        File file = new File(ROOT + relative);
        ensureParent(file);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(210, 160, 60));
        g.fillOval(0, 0, width, height);
        g.setColor(new Color(250, 220, 140));
        g.fillOval(width / 6, height / 6, width * 2 / 3, height * 2 / 3);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    private static void transparent(String relative, int width, int height) throws Exception {
        File file = new File(ROOT + relative);
        ensureParent(file);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(image, "png", file);
    }

    /** 尸体：透明画布中间一个小小的人形。 */
    private static void corpse(String relative, int width, int height) throws Exception {
        File file = new File(ROOT + relative);
        ensureParent(file);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(52, 62, 78));
        g.fillOval(width / 2 - 70, height / 2 - 40, 140, 80);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    /** 遗物图标：128x128 透明画布，中心 56px 主体。 */
    private static void relicIcon(String path, Color color) throws Exception {
        File file = new File(path);
        ensureParent(file);
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillRoundRect(36, 36, 56, 56, 14, 14);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    /** 战斗立绘占位：一个简单的人形剪影，468x201，透明背景。 */
    private static void figure(String relative) throws Exception {
        File file = new File(ROOT + relative);
        ensureParent(file);
        BufferedImage image = new BufferedImage(468, 201, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(58, 66, 84));
        g.fillOval(180, 18, 108, 108);
        int[] xs = {150, 318, 348, 120};
        int[] ys = {150, 150, 201, 201};
        g.fillPolygon(xs, ys, 4);
        g.dispose();
        ImageIO.write(image, "png", file);
    }

    private static void ensureParent(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("cannot create " + parent);
        }
    }
}
