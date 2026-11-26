package zixiu.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 快速结算「若干次 1 点伤害」。
 *
 * <p>和逐张 {@code DamageAction} 语义一致（每一次都是独立的伤害实例，会各自触发曝光 / 荆棘 / 格挡），
 * 但整段只占一个动作：吐 的金币次数动辄几十上百，排几十个 DamageAction 会把动作队列拖得很慢。
 */
public class GoldBarrageAction extends AbstractGameAction {

    private final AbstractPlayer source;
    private final int damage;
    private final DamageInfo.DamageType damageType;
    private final int hits;
    private boolean applied;

    public GoldBarrageAction(AbstractMonster target, AbstractPlayer source,
                             int damage, DamageInfo.DamageType damageType, int hits) {
        this.target = target;
        this.source = source;
        this.damage = damage;
        this.damageType = damageType;
        this.hits = hits;
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (!this.applied) {
            this.applied = true;
            for (int i = 0; i < this.hits; i++) {
                if (this.target == null || this.target.isDeadOrEscaped()) {
                    break;
                }
                this.target.damage(new DamageInfo(this.source, this.damage, this.damageType));
            }
        }
        tickDuration();
    }
}
