package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.ZaoPower;

/**
 * 灶：一灶火，拆下来的字都是柴。
 *
 * 本回合每拆一张牌，对所有敌人施加 1 层灼烧；回合结束熄火。
 */
public class Zao extends ZixiuCard {

    public static final String ID = "Zao";
    public static final String GLYPH = "灶";

    public Zao() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new ZaoPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Zao();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级：每次拆解 1 层 -> 2 层
            upgradeMagicNumber(1);
        }
    }
}
