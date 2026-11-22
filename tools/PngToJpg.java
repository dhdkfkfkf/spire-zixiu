import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/** PNG → JPG（RGB 白底），用于创意工坊封面。用法：java PngToJpg <in> <out.jpg> */
public class PngToJpg {

    public static void main(String[] args) throws Exception {
        BufferedImage src = ImageIO.read(new File(args[0]));
        if (src == null) {
            throw new IllegalArgumentException("not an image: " + args[0]);
        }
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        ImageIO.write(out, "jpg", new File(args[1]));
        System.out.println("wrote " + args[1] + " " + out.getWidth() + "x" + out.getHeight());
    }
}
