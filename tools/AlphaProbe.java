import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 统计图片透明度分布，用来判断卡框中间是否应该是透明的。
 * 用法：java AlphaProbe <jar> <entry...>
 */
public class AlphaProbe {

    public static void main(String[] args) throws Exception {
        ZipFile zip = new ZipFile(args[0]);
        for (int i = 1; i < args.length; i++) {
            ZipEntry entry = zip.getEntry(args[i]);
            if (entry == null) {
                System.out.println(args[i] + "  MISSING");
                continue;
            }
            InputStream in = zip.getInputStream(entry);
            BufferedImage image = ImageIO.read(in);
            in.close();
            if (image == null) {
                System.out.println(args[i] + "  NOT-AN-IMAGE");
                continue;
            }
            int w = image.getWidth();
            int h = image.getHeight();
            long centerAlpha = 0;
            long centerCount = 0;
            long borderAlpha = 0;
            long borderCount = 0;
            long transparent = 0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int a = (image.getRGB(x, y) >>> 24) & 0xFF;
                    if (a == 0) {
                        transparent++;
                    }
                    boolean center = x > w / 4 && x < w * 3 / 4 && y > h / 4 && y < h * 3 / 4;
                    if (center) {
                        centerAlpha += a;
                        centerCount++;
                    } else if (x < w / 20 || x > w * 19 / 20 || y < h / 20 || y > h * 19 / 20) {
                        borderAlpha += a;
                        borderCount++;
                    }
                }
            }
            System.out.println(args[i]
                    + "  " + w + "x" + h
                    + "  centerAlpha=" + (centerCount == 0 ? -1 : centerAlpha / centerCount)
                    + "  borderAlpha=" + (borderCount == 0 ? -1 : borderAlpha / borderCount)
                    + "  transparent=" + (100 * transparent / (w * h)) + "%");
        }
        zip.close();
    }
}
