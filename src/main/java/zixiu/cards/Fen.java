package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.BurnPower;

/**
 * 焚：把火铺开。对所有敌人施加灼烧（原「焱」的效果）。
 */
public class Fen extends ZixiuCard {

    public static final String ID = "Fen";
    public static final String GLYPH = "焚";

    public Fen() {
        super(ID, 1, CardType.SKILL, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.baseMagicNumber = 3;
        this.magicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster == null || monster.isDeadOrEscaped()) {
                continue;
            }
            BurnPower.apply(monster, this.magicNumber);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fen();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 3 层 -> 5 层（与当年焱一致）
            upgradeMagicNumber(2);
        }
    }
}
