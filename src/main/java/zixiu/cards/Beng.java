package zixiu.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import zixiu.core.FusionTracker;

public class Beng extends ZixiuCard {

    public static final String ID = "Beng";
    public static final String GLYPH = "崩";

    public Beng() {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.baseDamage = 11;
        this.baseMagicNumber = 3;
        this.magicNumber = 3;
    }

    @Override
    public void applyPowers() {
        // 原版「完美打击」的写法：临时把拆解加成并进基础伤害
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
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (!monster.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(new DamageAction((AbstractCreature) monster,
                        new DamageInfo(p, this.damage, this.damageTypeForTurn),
                        AbstractGameAction.AttackEffect.BLUNT_HEAVY));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Beng();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级：11 -> 14 伤害，每拆一张 3 -> 4
            upgradeDamage(3);
            upgradeMagicNumber(1);
        }
    }
}
