package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Benevolence extends ZixiuCard {

    public static final String ID = "Benevolence";
    public static final String GLYPH = "仁";

    public Benevolence() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = 5;
        this.magicNumber = 5;
        // 满血时改给的格挡，用 !B! 显示，也跟着升级走
        this.baseBlock = 5;
        this.block = 5;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.currentHealth >= p.maxHealth) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        } else {
            AbstractDungeon.actionManager.addToBottom(new HealAction(p, p, this.magicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Benevolence();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 治疗 5 → 6，满血格挡 5 → 6
            upgradeMagicNumber(1);
            upgradeBlock(1);
        }
    }
}
