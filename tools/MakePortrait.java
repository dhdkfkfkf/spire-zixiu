import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 把任意横图转换成选角立绘规格：1920x1200，等比缩放 + 上下补边，不拉伸。
 * 用法：java MakePortrait <输入图> <输出 png>
 */
public class MakePortrait {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1200;

    public static void main(String[] args) throws Exception {
        BufferedImage src = ImageIO.read(new File(args[0]));
        if (src == null) {
            throw new IllegalArgumentException("not an image: " + args[0]);
        }

        int drawWidth = WIDTH;
        int drawHeight = (int) Math.round(src.getHeight() * (WIDTH / (double) src.getWidth()));
        int offsetY = (HEIGHT - drawHeight) / 2;

        BufferedImage out = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(new Color(10, 10, 12));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.drawImage(src, 0, offsetY, drawWidth, drawHeight, null);
        g.dispose();

        File target = new File(args[1]);
        File parent = target.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("cannot create " + parent);
        }
        ImageIO.write(out, "png", target);
        System.out.println("wrote " + args[1] + " (" + WIDTH + "x" + HEIGHT + ", image " + drawWidth + "x" + drawHeight + ")");
    }
}
