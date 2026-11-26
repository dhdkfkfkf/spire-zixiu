package zixiu.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 末：一木到头。对面剩得越少，这一下越狠——目标每失去 10% 生命，伤害 +M。
 */
public class Mo extends ZixiuCard {

    public static final String ID = "Mo";
    public static final String GLYPH = "末";

    public Mo() {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 6;
        this.baseMagicNumber = 3;
        this.magicNumber = 3;
    }

    /** 目标掉了几个「10%」：满血 0，剩 90% 算 1 档，剩 0% 算 10 档。 */
    private int hpBonus(AbstractMonster mo) {
        if (mo == null || mo.maxHealth <= 0) {
            return 0;
        }
        int missing = mo.maxHealth - Math.max(0, mo.currentHealth);
        int steps = (int) Math.floor(missing * 10.0 / mo.maxHealth);
        if (steps < 0) {
            steps = 0;
        } else if (steps > 10) {
            steps = 10;
        }
        return steps * this.magicNumber;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        // 原版「完美打击」的写法：临时把血线加成并进基础伤害，力量/易伤照常参与
        int realBaseDamage = this.baseDamage;
        this.baseDamage = realBaseDamage + hpBonus(mo);
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // this.damage 已含血线加成（useCard 里会先调 calculateCardDamage）
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mo();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级：6 -> 8 伤害，每 10% 3 -> 4
            upgradeDamage(2);
            upgradeMagicNumber(1);
        }
    }
}
