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
 * 从：一人跟着一人。接上连书之后，这一下更重。
 *
 * 和「二」区分开：「二」是固定两段连击，「从」是一段、按本回合连书层数加伤。
 */
public class Cong extends ZixiuCard {

    public static final String ID = "Cong";
    public static final String GLYPH = "从";

    public Cong() {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 4;
        this.baseMagicNumber = 2;
        this.magicNumber = 2;
    }

    @Override
    public void applyPowers() {
        // 和崩同一套写法：临时把连书加成并进基础伤害，卡面预览才准
        int realBaseDamage = this.baseDamage;
        this.baseDamage = realBaseDamage + chainBonus();
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage = realBaseDamage + chainBonus();
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    private int chainBonus() {
        return FusionTracker.chain() * this.magicNumber;
    }

    /** 连书层数会在回合内变化，手牌里的数字要跟着走（只在本层数变化时重算一次）。 */
    private int lastChain = Integer.MIN_VALUE;

    @Override
    public void update() {
        super.update();
        if (AbstractDungeon.player == null) {
            return;
        }
        if (!AbstractDungeon.player.hand.contains(this)) {
            return;
        }
        int chain = FusionTracker.chain();
        if (chain != lastChain) {
            lastChain = chain;
            applyPowers();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // this.damage 已含连书加成
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Cong();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
            // 升级后每层连书的加伤 2 → 3
            upgradeMagicNumber(1);
        }
    }
}
