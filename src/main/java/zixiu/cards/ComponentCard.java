package zixiu.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import zixiu.lexicon.Component;
import zixiu.powers.ExposurePower;

/**
 * 部件牌：0 费、效果微弱，同时是拼合的材料。 rarity 为 SPECIAL，不进奖励池。
 */
public abstract class ComponentCard extends ZixiuCard {

    protected final Component component;

    protected ComponentCard(String localId, Component component, CardType type, CardTarget target,
                            int damage, int block, int magic) {
        super(localId, 0, type, CardRarity.SPECIAL, target);
        this.component = component;
        this.baseDamage = damage;
        this.baseBlock = block;
        this.magicNumber = magic;
        this.baseMagicNumber = magic;
    }

    public Component component() {
        return component;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        switch (component) {
            case HUO:
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                        new DamageInfo(p, this.damage, this.damageTypeForTurn),
                        AbstractGameAction.AttackEffect.FIRE));
                break;
            case MU:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
                break;
            case KOU:
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.magicNumber));
                AbstractDungeon.actionManager.addToBottom(new DiscardAction(p, p, 1, false));
                break;
            case REN:
                for (int i = 0; i < this.magicNumber; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                            new DamageInfo(p, this.damage, this.damageTypeForTurn),
                            AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                break;
            case YUE:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
                AbstractDungeon.actionManager.addToBottom(new HealAction(p, p, this.magicNumber));
                break;
            case TU:
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
                break;
            case YI:
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.magicNumber));
                break;
            case SHUI:
                AbstractDungeon.actionManager.addToBottom(new HealAction(p, p, this.magicNumber));
                break;
            case SHAN:
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p,
                        new WeakPower(m, this.magicNumber, false), this.magicNumber));
                break;
            case RI:
                ExposurePower.apply(m, this.magicNumber);
                break;
            default:
                break;
        }
    }
}
