import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 从大图里裁一个正方形并缩放到目标尺寸，用来做选角按钮。
 * 用法：java CropSquare <输入> <输出> <中心x> <中心y> <裁切边长> <输出边长>
 */
public class CropSquare {

    public static void main(String[] args) throws Exception {
        BufferedImage src = ImageIO.read(new File(args[0]));
        int cx = Integer.parseInt(args[2]);
        int cy = Integer.parseInt(args[3]);
        int size = Integer.parseInt(args[4]);
        int out = Integer.parseInt(args[5]);

        int x = Math.max(0, Math.min(src.getWidth() - size, cx - size / 2));
        int y = Math.max(0, Math.min(src.getHeight() - size, cy - size / 2));
        BufferedImage crop = src.getSubimage(x, y, size, size);

        BufferedImage target = new BufferedImage(out, out, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(crop, 0, 0, out, out, null);
        g.dispose();

        File file = new File(args[1]);
        ImageIO.write(target, "png", file);
        System.out.println("wrote " + args[1] + " from (" + x + "," + y + ") " + size + "px");
    }
}
