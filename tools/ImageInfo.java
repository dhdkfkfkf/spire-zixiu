import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ImageInfo {
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
            } else {
                System.out.println(args[i] + "  " + image.getWidth() + "x" + image.getHeight());
            }
        }
        zip.close();
    }
}
