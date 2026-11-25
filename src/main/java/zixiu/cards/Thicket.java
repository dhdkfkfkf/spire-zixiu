package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.lexicon.Component;

/**
 * 丛：一丛草里什么都有。往手里塞两张随机部件牌（10 种部件里随机，可能重复）。
 */
public class Thicket extends ZixiuCard {

    public static final String ID = "Thicket";
    public static final String GLYPH = "丛";

    public Thicket() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = 3;
        this.magicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Component[] pool = Component.values();
        for (int i = 0; i < this.magicNumber; i++) {
            Component component = pool[AbstractDungeon.cardRandomRng.random(pool.length - 1)];
            AbstractCard card = CardComponents.createComponent(component);
            if (card != null) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, false));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Thicket();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级：3 张 → 4 张，并且 1 费 → 0 费
            upgradeMagicNumber(1);
            upgradeBaseCost(0);
        }
    }
}
