import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 从 3x3 素材表切出遗物图标：抠掉米色底、裁到主体、输出 128（主体 56）与 256（主体 200）。
 * 用法：java SliceRelics <素材表> <输出根目录>
 */
public class SliceRelics {

    /** row, col, relic id */
    private static final Object[][] CELLS = {
            {0, 0, "tubi"},
            {0, 1, "zanyan"},
            {0, 2, "huozhezi"},
            {1, 1, "zhubi"},
            {1, 2, "jiushuqian"},
            {2, 0, "shuxia"},
            {2, 2, "huozipan"},
    };

    public static void main(String[] args) throws Exception {
        BufferedImage sheet = ImageIO.read(new File(args[0]));
        String root = args[1];
        int cellW = sheet.getWidth() / 3;
        int cellH = sheet.getHeight() / 3;

        for (Object[] cell : CELLS) {
            int row = (Integer) cell[0];
            int col = (Integer) cell[1];
            String id = (String) cell[2];

            int x0 = col * cellW + 10;
            int y0 = row * cellH + 10;
            int w = cellW - 20;
            int h = cellH - 20;
            BufferedImage cellImg = sheet.getSubimage(x0, y0, w, h);

            BufferedImage keyed = keyBackground(cellImg);
            BufferedImage trimmed = trim(keyed);

            writeIcon(trimmed, root + "images/relics/" + id + ".png", 128, 56);
            writeIcon(trimmed, root + "images/largeRelics/" + id + ".png", 256, 200);
            writeOutline(trimmed, root + "images/relics/outline/" + id + ".png", 128, 56);
            System.out.println(id + " <- cell(" + row + "," + col + ") trimmed=" + trimmed.getWidth() + "x" + trimmed.getHeight());
        }
    }

    private static BufferedImage keyBackground(BufferedImage src) {
        int bg = src.getRGB(2, 2);
        int br = (bg >> 16) & 0xFF;
        int bgc = (bg >> 8) & 0xFF;
        int bb = bg & 0xFF;
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int dist = Math.abs(r - br) + Math.abs(g - bgc) + Math.abs(b - bb);
                boolean background = dist < 34 || (r > 238 && g > 232 && b > 220);
                out.setRGB(x, y, background ? 0 : (0xFF000000 | rgb));
            }
        }
        return out;
    }

    private static BufferedImage trim(BufferedImage src) {
        int minX = src.getWidth();
        int minY = src.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                if (((src.getRGB(x, y) >>> 24) & 0xFF) > 8) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return src;
        }
        return src.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private static void writeIcon(BufferedImage icon, String path, int canvas, int subject) throws Exception {
        BufferedImage out = new BufferedImage(canvas, canvas, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        float scale = Math.min(subject / (float) icon.getWidth(), subject / (float) icon.getHeight());
        int w = Math.max(1, Math.round(icon.getWidth() * scale));
        int h = Math.max(1, Math.round(icon.getHeight() * scale));
        g.drawImage(icon, (canvas - w) / 2, (canvas - h) / 2, w, h, null);
        g.dispose();
        ensureParent(path);
        ImageIO.write(out, "png", new File(path));
    }

    private static void writeOutline(BufferedImage icon, String path, int canvas, int subject) throws Exception {
        BufferedImage shape = new BufferedImage(icon.getWidth(), icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D sg = shape.createGraphics();
        sg.drawImage(icon, 0, 0, null);
        sg.dispose();
        for (int y = 0; y < shape.getHeight(); y++) {
            for (int x = 0; x < shape.getWidth(); x++) {
                int alpha = (shape.getRGB(x, y) >>> 24) & 0xFF;
                shape.setRGB(x, y, alpha == 0 ? 0 : (alpha << 24) | 0x506040);
            }
        }
        writeIcon(shape, path, canvas, subject);
    }

    private static void ensureParent(String path) {
        File parent = new File(path).getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("cannot create " + parent);
        }
    }
}
