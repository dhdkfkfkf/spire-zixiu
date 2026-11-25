package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.BurnPower;
import zixiu.powers.ExposurePower;

/**
 * 晶：棱面反光，照得更细。施加曝光；目标已经在烧，就多反两张牌回来（原「淼」的效果）。
 */
public class Crystal extends ZixiuCard {

    public static final String ID = "Crystal";
    public static final String GLYPH = "晶";

    public Crystal() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 被炅换成灼烧的话，这一发就等于给目标上了灼烧，抽牌照算
        boolean becomesBurn = ExposurePower.apply(m, this.magicNumber);
        if (m != null && (becomesBurn || m.hasPower(BurnPower.POWER_ID))) {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 2));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Crystal();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 曝光 1 层 -> 2 层
            upgradeMagicNumber(1);
        }
    }
}
