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
        // magicNumber = 每张减多少费（1 → 升级 2）；只减一张成品牌
        int discount = this.magicNumber;
        for (AbstractCard card : p.hand.group) {
            if (CardComponents.glyphOf(card) != null && card.costForTurn > 0) {
                card.costForTurn = Math.max(0, card.costForTurn - discount);
                card.isCostModifiedForTurn = true;
                break;
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
            // 升级：一张 -1 -> 一张 -2
            upgradeMagicNumber(1);
        }
    }
}
