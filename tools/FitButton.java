import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 把满幅图缩进原版选角按钮的内容区：200x200 画布、四周 25px 透明。
 * 用法：java FitButton <输入> <输出>
 */
public class FitButton {

    public static void main(String[] args) throws Exception {
        BufferedImage src = ImageIO.read(new File(args[0]));
        int canvas = 200;
        int inset = 25;
        BufferedImage out = new BufferedImage(canvas, canvas, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, inset, inset, canvas - inset * 2, canvas - inset * 2, null);
        g.dispose();
        ImageIO.write(out, "png", new File(args[1]));
        System.out.println("wrote " + args[1] + " (" + canvas + "x" + canvas + ", content " + (canvas - inset * 2) + ")");
    }
}
