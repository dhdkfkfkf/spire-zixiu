import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 生成九字遗物、残碑、模组徽章与九字碑事件图。
 *
 * 用法：java GenInscriptionArt <仓库根目录>
 */
public final class GenInscriptionArt {

    private static final String[] GLYPHS = {"临", "兵", "斗", "者", "皆", "阵", "列", "前", "行"};
    private static final String[] IDS = {"lin", "bing", "dou", "zhe", "jie", "zhen", "lie", "qian", "xing"};

    private GenInscriptionArt() {
    }

    public static void main(String[] args) throws Exception {
        File root = new File(args.length > 0 ? args[0] : ".");
        File relicDir = new File(root, "src/main/resources/images/relics");
        File outlineDir = new File(root, "src/main/resources/images/relics/outline");
        File uiDir = new File(root, "src/main/resources/zixiu/images/ui");
        File eventDir = new File(root, "src/main/resources/images/events");
        relicDir.mkdirs();
        outlineDir.mkdirs();
        uiDir.mkdirs();
        eventDir.mkdirs();

        for (int i = 0; i < GLYPHS.length; i++) {
            write(new File(relicDir, IDS[i] + ".png"), relic(GLYPHS[i], false));
            write(new File(outlineDir, IDS[i] + ".png"), relic(GLYPHS[i], true));
        }
        write(new File(relicDir, "canbei.png"), relic("碑", false));
        write(new File(outlineDir, "canbei.png"), relic("碑", true));

        write(new File(uiDir, "badge.png"), badge());
        write(new File(eventDir, "jiuzibei.png"), event());
        System.out.println("generated inscription art");
    }

    private static void write(File file, BufferedImage image) throws Exception {
        ImageIO.write(image, "png", file);
    }

    private static Font font(int size) {
        Font font = new Font("Microsoft YaHei", Font.BOLD, size);
        if (!font.canDisplay('临')) {
            font = new Font("SimSun", Font.BOLD, size);
        }
        if (!font.canDisplay('临')) {
            font = new Font("Serif", Font.BOLD, size);
        }
        return font;
    }

    /** 128×128，主体压在中心约 56px 内。 */
    private static BufferedImage relic(String glyph, boolean outlineOnly) {
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (!outlineOnly) {
            g.setColor(new Color(232, 214, 178, 255));
            g.fill(new Ellipse2D.Float(34f, 34f, 60f, 60f));
            g.setColor(new Color(150, 116, 74, 255));
            g.setStroke(new BasicStroke(2.4f));
            g.draw(new Ellipse2D.Float(34f, 34f, 60f, 60f));
            g.setColor(new Color(178, 62, 48, 255));
            g.fill(new RoundRectangle2D.Float(88f, 88f, 12f, 12f, 3f, 3f));
            g.setColor(new Color(58, 42, 30));
            g.setFont(font(46));
        } else {
            g.setColor(Color.WHITE);
            g.setFont(font(46));
        }

        FontMetrics metrics = g.getFontMetrics();
        int width = metrics.stringWidth(glyph);
        int x = (128 - width) / 2;
        int y = (128 - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(glyph, x, y);
        g.dispose();
        return image;
    }

    /** 128×128 模组徽章：墨底 + 朱砂字。 */
    private static BufferedImage badge() {
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(28, 24, 20, 255));
        g.fill(new RoundRectangle2D.Float(2f, 2f, 124f, 124f, 18f, 18f));
        g.setColor(new Color(196, 72, 54, 255));
        g.setFont(font(78));
        FontMetrics metrics = g.getFontMetrics();
        String glyph = "字";
        g.drawString(glyph, (128 - metrics.stringWidth(glyph)) / 2,
                (128 - metrics.getHeight()) / 2 + metrics.getAscent());
        g.dispose();
        return image;
    }

    /** 600×600 事件图：碑面上竖排九字。 */
    private static BufferedImage event() {
        BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setPaint(new GradientPaint(0, 0, new Color(52, 46, 40), 600, 600, new Color(22, 20, 18)));
        g.fillRect(0, 0, 600, 600);
        g.setColor(new Color(88, 78, 66));
        g.setStroke(new BasicStroke(6f));
        g.drawRoundRect(90, 60, 420, 480, 26, 26);

        g.setColor(new Color(226, 212, 182));
        g.setFont(font(42));
        FontMetrics metrics = g.getFontMetrics();
        for (int i = 0; i < GLYPHS.length; i++) {
            int x = 300 - metrics.stringWidth(GLYPHS[i]) / 2;
            int y = 108 + i * 46 + metrics.getAscent();
            g.drawString(GLYPHS[i], x, y);
        }
        g.setColor(new Color(178, 62, 48));
        g.fill(new RoundRectangle2D.Float(500f, 500f, 34f, 34f, 6f, 6f));
        g.dispose();
        return image;
    }
}
