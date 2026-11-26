package zixiu.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.lexicon.Component;
import zixiu.powers.BurnPower;

/**
 * 火：不升级只是一点火星（1 点伤害）；升级后才点着（1 点伤害 + 1 层灼烧）。
 */
public class Huo extends ComponentCard {

    public static final String ID = "Huo";

    public Huo() {
        super(ID, Component.HUO, CardType.ATTACK, CardTarget.ENEMY, 1, 0, 1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.FIRE));
        if (this.upgraded && m != null) {
            BurnPower.apply(m, this.magicNumber);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Huo();
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
