package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Dawn extends ZixiuCard {

    public static final String ID = "Dawn";
    public static final String GLYPH = "旦";

    public Dawn() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.baseMagicNumber = 2;
        this.magicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int draw = this.magicNumber;
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() <= 1) {
            draw += 1;
        }
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, draw));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Dawn();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
