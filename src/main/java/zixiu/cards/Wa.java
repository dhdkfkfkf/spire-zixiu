package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

/** 哇：一声喊出来，全场都听见了。 */
public class Wa extends ZixiuCard {

    public static final String ID = "Wa";
    public static final String GLYPH = "哇";

    public Wa() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster == null || monster.isDeadOrEscaped()) {
                continue;
            }
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, p,
                    new WeakPower(monster, this.magicNumber, false), this.magicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Wa();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
