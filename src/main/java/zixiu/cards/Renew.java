package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.actions.GoldBarrageAction;

/**
 * 吐：财大气粗，有多少钱就吐多少口。
 *
 * <p>每有 10 金币造成 1 点伤害（升级后每 5 金币 1 点）；每次都是独立的 1 点伤害实例。
 */
public class Renew extends ZixiuCard {

    public static final String ID = "Renew";
    public static final String GLYPH = "吐";

    public Renew() {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 1;
        this.baseMagicNumber = 10;
        this.magicNumber = 10;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int hits = p.gold / this.magicNumber;
        if (hits > 0 && m != null) {
            AbstractDungeon.actionManager.addToBottom(new GoldBarrageAction(
                    m, p, this.damage, this.damageTypeForTurn, hits));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Renew();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(-5);
        }
    }
}
