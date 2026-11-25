package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.actions.PlayFromDrawPileAction;

/**
 * 未：来日未至，先把还没写出来的那一笔写两遍。
 *
 * 从抽牌堆挑一张牌，免费打出两次，然后那张牌进消耗堆；未自己也消耗，
 * 所以这是一张「一次性借牌」的牌。
 */
public class Wei extends ZixiuCard {

    public static final String ID = "Wei";
    public static final String GLYPH = "未";

    /** 打出几次。 */
    private static final int TIMES = 2;

    public Wei() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.ENEMY);
        this.exhaust = true;
        this.baseMagicNumber = TIMES;
        this.magicNumber = TIMES;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new PlayFromDrawPileAction(m, TIMES));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Wei();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }
}
