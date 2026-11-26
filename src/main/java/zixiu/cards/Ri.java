package zixiu.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.lexicon.Component;
import zixiu.powers.ExposurePower;

/**
 * 日：不升级只是晒一下（1 点伤害）；升级后才真正照出曝光（1 点伤害 + 1 层曝光）。
 */
public class Ri extends ComponentCard {

    public static final String ID = "Ri";

    public Ri() {
        super(ID, Component.RI, CardType.SKILL, CardTarget.ENEMY, 1, 0, 1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SHIELD));
        if (this.upgraded && m != null) {
            ExposurePower.apply(m, this.magicNumber);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Ri();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            String upgradeDesc = strings(ID).UPGRADE_DESCRIPTION;
            if (upgradeDesc != null) {
                this.rawDescription = upgradeDesc;
                initializeDescription();
            }
        }
    }
}
