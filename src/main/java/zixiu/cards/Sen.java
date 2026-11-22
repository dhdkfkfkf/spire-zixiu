package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.SenPower;

public class Sen extends ZixiuCard {

    public static final String ID = "Sen";
    public static final String GLYPH = "森";

    public Sen() {
        super(ID, 2, CardType.POWER, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = SenPower.BLOCK;
        this.magicNumber = SenPower.BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new SenPower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Sen();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = "固有。每当你拆解一张牌，获得 6 点格挡。";
            initializeDescription();
        }
    }
}
