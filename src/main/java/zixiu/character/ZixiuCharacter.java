package zixiu.character;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;

import com.megacrit.cardcrawl.screens.CharSelectInfo;
import zixiu.ZixiuMod;
import zixiu.cards.Jin;
import zixiu.ui.ZixiuEnergyOrb;

import java.util.ArrayList;

public class ZixiuCharacter extends CustomPlayer {

    public static final String NAME = "砚秋";
    public static final int STARTING_HP = 70;
    public static final int MAX_HP = 70;
    public static final int STARTING_GOLD = 99;
    public static final int CARD_DRAW = 5;
    public static final int ENERGY_PER_TURN = 3;
    public static final int ASCENSION_MAX_HP_LOSS = 5;

    /**
     * 静态战斗立绘由 AbstractPlayer.render 按图片原始像素绘制，
     * 所以尺寸控制在 PNG 上（tools/KeyBlack 的 targetHeight），这里不参与。
     */

    private static final String CHAR_IMG = "zixiu/images/char/char.png";
    /**
     * 原版有两个肩像槽位（shoulder2 用在篝火，shoulder 在篝火 UI 隐藏时顶上），
     * 都只服务于「篝火」这一处；砚秋只留「坐在火边」那张，两个槽位共用它。
     * 死亡图是 corpse.png，胜利统计屏原版自己就是黑底（不画肩像）。
     */
    private static final String SHOULDER = "zixiu/images/char/shoulder2.png";
    private static final String CORPSE_IMG = "zixiu/images/char/corpse.png";

    public ZixiuCharacter(String name, AbstractPlayer.PlayerClass playerClass) {
        super(name, playerClass, new ZixiuEnergyOrb(), new StaticAnimation());
        initializeClass(CHAR_IMG, SHOULDER, SHOULDER, CORPSE_IMG, getLoadout(),
                20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> deck = new ArrayList<String>();
        for (int i = 0; i < 4; i++) {
            deck.add(ZixiuMod.makeID("Jin"));
        }
        for (int i = 0; i < 3; i++) {
            deck.add(ZixiuMod.makeID("Shou"));
        }
        deck.add(ZixiuMod.makeID("Huo"));
        deck.add(ZixiuMod.makeID("Mu"));
        deck.add(ZixiuMod.makeID("Kou"));
        // TODO(04 验证期): 森 暂放进起始牌组，方便测「拆解触发」。06–08 完成后移除。
        deck.add(ZixiuMod.makeID("Sen"));
        return deck;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> relics = new ArrayList<String>();
        relics.add(ZixiuMod.makeID("TuBi"));
        relics.add(ZixiuMod.makeID(zixiu.relics.LianShu.ID));
        return relics;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAME,
                // renderSmartText 按空格切词、遇到单独的 "NL" 才换行（纯中文不会自动折行）
                "旧家出身的公子，家道中落后只剩一支笔、一方砚。 NL "
                        + "他不修仙、不炼丹，只信手里的字：拆开是道理，合上是命。",
                STARTING_HP, MAX_HP, 0, STARTING_GOLD, CARD_DRAW,
                this, getStartingRelics(), getStartingDeck(), false);
    }

    @Override
    public String getTitle(AbstractPlayer.PlayerClass playerClass) {
        return NAME;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return ZixiuEnums.ZIXIU_COLOR;
    }

    @Override
    public Color getCardRenderColor() {
        return new Color(0.62F, 0.72F, 0.86F, 1.0F);
    }

    @Override
    public Color getCardTrailColor() {
        return new Color(0.62F, 0.72F, 0.86F, 1.0F);
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new Jin();
    }

    @Override
    public String getLeaderboardCharacterName() {
        return NAME;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return ASCENSION_MAX_HP_LOSS;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    @Override
    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> tmpPool) {
        CardLibrary.addCardsIntoPool(tmpPool, ZixiuEnums.ZIXIU_COLOR);
        return tmpPool;
    }

    @Override
    public String getVampireText() {
        return "砚秋端起砚台，问你要不要喝一口。";
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL,
                AbstractGameAction.AttackEffect.SLASH_VERTICAL,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        };
    }

    @Override
    public Color getSlashAttackColor() {
        return new Color(0.62F, 0.72F, 0.86F, 1.0F);
    }

    @Override
    public String getSpireHeartText() {
        return "你提笔，在心上写下一个字。";
    }

    @Override
    public AbstractPlayer newInstance() {
        return new ZixiuCharacter(NAME, ZixiuEnums.ZIXIU);
    }

    @Override
    public String getLocalizedCharacterName() {
        return NAME;
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_DAGGER_6";
    }

    /**
     * 通关动画。图片放在下面这几个路径，四张都齐了才启用；缺一张就退回原版结尾。
     * 尺寸全部 1920×1200（和原版 images/scenes/* 一致）。
     */
    private static final String CUTSCENE_BG = "zixiu/images/scenes/yanqiu_bg.jpg";
    private static final String[] CUTSCENE_PANELS = {
            "zixiu/images/scenes/yanqiu1.png",
            "zixiu/images/scenes/yanqiu2.png",
            "zixiu/images/scenes/yanqiu3.png"
    };

    /**
     * 每格的音效（CutscenePanel 的第二个参数就是音效 key，不是文本）。
     * 全是纸张系：轻纸声 → 纸页翻飞 → 书页落下。
     */
    private static final String[] CUTSCENE_SFX = {
            "CARD_SELECT",
            "CARD_DRAW_8",
            "CARD_OBTAIN"
    };

    @Override
    public com.badlogic.gdx.graphics.Texture getCutsceneBg() {
        return exists(CUTSCENE_BG)
                ? com.megacrit.cardcrawl.helpers.ImageMaster.loadImage(CUTSCENE_BG)
                : null;
    }

    @Override
    public java.util.List<com.megacrit.cardcrawl.cutscenes.CutscenePanel> getCutscenePanels() {
        for (String path : CUTSCENE_PANELS) {
            if (!exists(path)) {
                return null;
            }
        }
        java.util.ArrayList<com.megacrit.cardcrawl.cutscenes.CutscenePanel> panels =
                new java.util.ArrayList<com.megacrit.cardcrawl.cutscenes.CutscenePanel>();
        for (int i = 0; i < CUTSCENE_PANELS.length; i++) {
            panels.add(new com.megacrit.cardcrawl.cutscenes.CutscenePanel(
                    CUTSCENE_PANELS[i], CUTSCENE_SFX[i]));
        }
        return panels;
    }

    private static boolean exists(String path) {
        return com.badlogic.gdx.Gdx.files != null
                && com.badlogic.gdx.Gdx.files.internal(path).exists();
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        // 选角音效占位，定稿后替换
    }
}
