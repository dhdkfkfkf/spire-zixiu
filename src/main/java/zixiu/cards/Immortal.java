package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

/**
 * 仙：状态牌，用来解「凡庸」这类诅咒/状态。
 *
 * 0 费、可打出（原版「粘液」也是可打出的状态牌），打出后自身消耗，
 * 并把手上所有的状态牌与诅咒牌一起消耗掉——凡庸待在手里才有约束，
 * 升级后**不再消耗自身**（可以反复用来清场）。
 */
public class Immortal extends ZixiuCard {

    public static final String ID = "Immortal";
    public static final String GLYPH = "仙";

    public Immortal() {
        super(ID, 0, CardType.STATUS, CardRarity.SPECIAL, CardTarget.SELF);
        this.exhaust = true;
    }

    /**
     * 原版 canUpgrade() 对 STATUS / CURSE 一律返回 false，所以必须在子类里放开，
     * 否则卡牌库的「看升级版」、篝火强化、合字升级传递都碰不到这张牌。
     */
    @Override
    public boolean canUpgrade() {
        return !this.upgraded;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级后不再消耗自身
            this.exhaust = false;
            String upgradeDesc = strings(ID).UPGRADE_DESCRIPTION;
            if (upgradeDesc != null) {
                this.rawDescription = upgradeDesc;
                initializeDescription();
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractCard card : new ArrayList<AbstractCard>(p.hand.group)) {
            if (card == this) {
                continue;
            }
            if (card.type == CardType.STATUS || card.type == CardType.CURSE) {
                AbstractDungeon.actionManager.addToBottom(
                        new ExhaustSpecificCardAction(card, p.hand));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Immortal();
    }
}
