import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 输出不透明区域的包围盒，用来复刻卡框的卡片形状。
 */
public class AlphaBox {

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
            int w = image.getWidth();
            int h = image.getHeight();
            int minX = w;
            int minY = h;
            int maxX = -1;
            int maxY = -1;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int a = (image.getRGB(x, y) >>> 24) & 0xFF;
                    if (a > 128) {
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
            System.out.println(args[i] + "  canvas=" + w + "x" + h
                    + "  box=(" + minX + "," + minY + ") " + (maxX - minX + 1) + "x" + (maxY - minY + 1));
        }
        zip.close();
    }
}
