package zixiu.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.core.FusionTracker;

/**
 * 出：两山叠成出路。本回合拆过几张，这一击就多几段力。
 */
public class Chu extends ZixiuCard {

    public static final String ID = "Chu";
    public static final String GLYPH = "出";

    public Chu() {
        super(ID, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY);
        this.baseDamage = 14;
        this.baseMagicNumber = 6;
        this.magicNumber = 5;
    }

    @Override
    public void applyPowers() {
        // 原版「完美打击」的写法：临时把拆解加成并进基础伤害，力量/易伤照常参与
        int realBaseDamage = this.baseDamage;
        this.baseDamage = realBaseDamage + splitBonus();
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage = realBaseDamage + splitBonus();
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    private int splitBonus() {
        return FusionTracker.splitsThisTurn() * this.magicNumber;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // this.damage 已含拆解加成
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SMASH));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Chu();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(4);
        }
    }
}
