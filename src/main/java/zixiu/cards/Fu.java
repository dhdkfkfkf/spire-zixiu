package zixiu.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Fu extends ZixiuCard {

    public static final String ID = "Fu";
    public static final String GLYPH = "夫";

    public Fu() {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = 16;
        this.baseMagicNumber = 26;
        this.magicNumber = 26;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int damage = this.damage;
        if (m != null && m.currentHealth * 2 <= m.maxHealth) {
            damage = this.magicNumber;
        }
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                new DamageInfo(p, damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fu();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 两个数值都强化：主伤害 16→20，残血替换伤害 26→30
            upgradeDamage(4);
            upgradeMagicNumber(4);
        }
    }
}
