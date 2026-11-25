package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Fine extends ZixiuCard {

    public static final String ID = "Fine";
    public static final String GLYPH = "佳";

    public Fine() {
        super(ID, 0, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // magicNumber = 减费几张（1 → 升级 2 张），每张 -1 费
        int left = this.magicNumber;
        for (AbstractCard card : p.hand.group) {
            if (left <= 0) {
                break;
            }
            if (CardComponents.glyphOf(card) != null && card.costForTurn > 0) {
                card.costForTurn = Math.max(0, card.costForTurn - 1);
                card.isCostModifiedForTurn = true;
                left--;
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fine();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
