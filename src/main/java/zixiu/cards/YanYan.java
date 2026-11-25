package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.BurnPower;

public class YanYan extends ZixiuCard {

    public static final String ID = "YanYan";
    public static final String GLYPH = "焱";

    public YanYan() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.baseMagicNumber = 3;
        this.magicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 三把火：把已经在烧的敌人烧得更旺——身上灼烧层数翻倍（没灼烧的不管）
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster == null || monster.isDeadOrEscaped()) {
                continue;
            }
            AbstractPower burn = monster.getPower(BurnPower.POWER_ID);
            if (burn == null || burn.amount <= 0) {
                continue;
            }
            burn.amount *= 2;
            burn.updateDescription();
            burn.flash();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new YanYan();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }
}
