package zixiu.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;

import java.util.List;

/**
 * 选牌界面右侧的提示面板（合字预知 / 拆字预知共用）。
 *
 * 位置就是战斗界面右边那块空白竖条：x 70%~97.5%，从 76% 高度往下排。
 * 字号比正文放大 1.2 倍，正文用「描两遍」的方式做一点加粗，行距跟着放大到 40。
 */
public final class PreviewPanel {

    private static final Color TITLE_COLOR = new Color(0.86F, 0.73F, 0.42F, 1.0F);
    private static final Color BODY_COLOR = new Color(0.95F, 0.92F, 0.85F, 1.0F);

    private static final float PAD = 22.0F;
    private static final float TITLE_GAP = 12.0F;
    /** 整体往下挪一个字的高度（行距也是 40，正好一格）。 */
    private static final float DROP = 40.0F;

    private PreviewPanel() {
    }

    public static void draw(SpriteBatch sb, String title, List<String> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        float x = Settings.WIDTH * 0.700F;
        float w = Settings.WIDTH * 0.275F;
        float top = Settings.HEIGHT * 0.760F - DROP * Settings.scale;
        float pad = PAD * Settings.scale;
        float wrap = w - pad * 2.0F;
        float lineH = 40.0F * Settings.scale;

        StringBuilder body = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                body.append(" NL ");
            }
            body.append(items.get(i));
        }
        String bodyText = body.toString();

        BitmapFont bodyFont = FontHelper.tipBodyFont;
        BitmapFont titleFont = FontHelper.tipHeaderFont;

        float titleH = title == null || title.isEmpty()
                ? 0.0F
                : FontHelper.getSmartHeight(titleFont, title, wrap, lineH);
        float bodyH = FontHelper.getSmartHeight(bodyFont, bodyText, wrap, lineH);
        float height = pad * 2.0F + titleH
                + (titleH > 0.0F ? TITLE_GAP * Settings.scale : 0.0F) + bodyH;

        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.68F));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, top - height, w, height);
        sb.setColor(Color.WHITE);

        float y = top - pad;
        if (titleH > 0.0F) {
            FontHelper.renderSmartText(sb, titleFont, title,
                    x + pad, y, wrap, lineH, TITLE_COLOR);
            y -= titleH + TITLE_GAP * Settings.scale;
        }
        FontHelper.renderSmartText(sb, bodyFont, bodyText,
                x + pad, y, wrap, lineH, BODY_COLOR);
    }

    /** 把卡面描述里的 !D!/!B!/!M! 换成当前数值，去掉自定义关键词前缀与换行符。 */
    public static String describe(AbstractCard card) {
        if (card == null) {
            return "";
        }
        CardStrings strings = CardCrawlGame.languagePack.getCardStrings(card.cardID);
        String desc = strings == null ? "" : strings.DESCRIPTION;
        if (card.upgraded && strings != null
                && strings.UPGRADE_DESCRIPTION != null && !strings.UPGRADE_DESCRIPTION.isEmpty()) {
            desc = strings.UPGRADE_DESCRIPTION;
        }
        if (desc == null) {
            return "";
        }
        // 用 base* 而不是 damage/block：刚 new 出来的牌还没跑 applyPowers，
        // damage / block 此时是 -1，预览里就会满屏「-1」。
        desc = desc.replace("!D!", String.valueOf(card.baseDamage))
                .replace("!B!", String.valueOf(card.baseBlock))
                .replace("!M!", String.valueOf(card.baseMagicNumber))
                .replace("zixiu:", "")
                .replace(" NL ", "，")
                .replace(" 。", "。")
                .replace(" ，", "，");
        return desc.replaceAll("[ ]{2,}", " ").trim();
    }
}
