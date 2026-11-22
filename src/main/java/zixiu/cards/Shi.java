package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.core.FusionTracker;

/**
 * 仕：守得住才轮得到还手。本回合拆过字，就有余裕再摸一张。
 */
public class Shi extends ZixiuCard {

    public static final String ID = "Shi";
    public static final String GLYPH = "仕";

    public Shi() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.baseBlock = 7;
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        if (FusionTracker.splitsThisTurn() > 0) {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.magicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Shi();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(3);
        }
    }
}
