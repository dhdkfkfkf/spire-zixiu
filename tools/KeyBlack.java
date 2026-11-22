import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 把纯黑背景抠成透明，并裁到主体包围盒（留一点边）。
 * 用法：java KeyBlack <输入图> <输出 png> [黑阈值]
 */
public class KeyBlack {

    public static void main(String[] args) throws Exception {
        BufferedImage src = ImageIO.read(new File(args[0]));
        if (src == null) {
            throw new IllegalArgumentException("not an image: " + args[0]);
        }
        int threshold = args.length > 2 ? Integer.parseInt(args[2]) : 14;
        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage keyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int minX = w;
        int minY = h;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                if (r < threshold && g < threshold && b < threshold) {
                    keyed.setRGB(x, y, 0);
                } else {
                    keyed.setRGB(x, y, 0xFF000000 | rgb);
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

        int margin = 4;
        int x0 = Math.max(0, minX - margin);
        int y0 = Math.max(0, minY - margin);
        int x1 = Math.min(w - 1, maxX + margin);
        int y1 = Math.min(h - 1, maxY + margin);
        BufferedImage out = keyed.getSubimage(x0, y0, x1 - x0 + 1, y1 - y0 + 1);
        if (args.length > 3) {
            int targetHeight = Integer.parseInt(args[3]);
            int targetWidth = Math.max(1, Math.round(out.getWidth() * (targetHeight / (float) out.getHeight())));
            BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                    java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(out, 0, 0, targetWidth, targetHeight, null);
            g.dispose();
            out = scaled;
        }

        File target = new File(args[1]);
        File parent = target.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("cannot create " + parent);
        }
        ImageIO.write(out, "png", target);
        System.out.println("wrote " + args[1] + " " + out.getWidth() + "x" + out.getHeight()
                + " (from " + w + "x" + h + ", threshold " + threshold + ")");
    }
}
