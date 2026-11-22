package zixiu.character;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbRed;
import zixiu.ZixiuMod;
import zixiu.cards.Jin;

import java.util.ArrayList;

public class ZixiuCharacter extends CustomPlayer {

    public static final String NAME = "箓生";
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
    private static final String SHOULDER_2 = "zixiu/images/char/shoulder2.png";
    private static final String SHOULDER_1 = "zixiu/images/char/shoulder.png";
    private static final String CORPSE_IMG = "zixiu/images/char/corpse.png";

    public ZixiuCharacter(String name, AbstractPlayer.PlayerClass playerClass) {
        super(name, playerClass, new EnergyOrbRed(), new StaticAnimation());
        initializeClass(CHAR_IMG, SHOULDER_2, SHOULDER_1, CORPSE_IMG, getLoadout(),
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
        return relics;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAME, "似乎很神秘的落魄书生，以一种奇怪的方式战斗，嘴里不断絮叨着「解箓」「成箓」。",
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

    /**
     * CustomPlayer 的默认实现只在 energyOrb 是 CustomEnergyOrb 时才返回贴图，否则直接抛异常。
     * 我们用官方的红球，所以这里要自己给。
     */
    @Override
    public Texture getEnergyImage() {
        return ImageMaster.ENERGY_RED_LAYER1;
    }

    @Override
    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> tmpPool) {
        CardLibrary.addCardsIntoPool(tmpPool, ZixiuEnums.ZIXIU_COLOR);
        return tmpPool;
    }

    @Override
    public String getVampireText() {
        return "字修端起砚台，问你要不要喝一口。";
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

    @Override
    public void doCharSelectScreenSelectEffect() {
        // 选角音效占位，定稿后替换
    }
}
