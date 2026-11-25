package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;
import zixiu.core.FusionTracker;
import zixiu.lexicon.Component;
import zixiu.lexicon.ZixiuLexicon;

import java.util.List;

/**
 * 连书：血条下的标记，显示当前的连书数，并写一行「上一字：森（木，木，木）」——
 * 连书要靠玩家记住上一张字，光看手牌太累了，干脆放在 buff 里。
 *
 * 真正的状态在 {@link zixiu.core.FusionTracker}，这里只负责显示——连书断了就把自己摘掉，
 * 图标不会留下一个「连书 0」。
 */
public class ChainPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("ChainPower");

    public ChainPower(AbstractCreature owner) {
        this.name = "连书";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Chain");
        updateDescription();
    }

    /** 让图标等于当前连书数；连书为 0 时把自己移除。 */
    public static void sync() {
        if (AbstractDungeon.player == null) {
            return;
        }
        int chain = FusionTracker.chain();
        AbstractPower existing = AbstractDungeon.player.getPower(POWER_ID);
        if (chain <= 0) {
            if (existing != null) {
                AbstractDungeon.actionManager.addToBottom(
                        new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, POWER_ID));
            }
            return;
        }
        if (existing instanceof ChainPower) {
            existing.amount = chain;
            existing.updateDescription();
        } else {
            ChainPower power = new ChainPower(AbstractDungeon.player);
            power.amount = chain;
            power.updateDescription();
            AbstractDungeon.player.powers.add(power);
        }
    }

    @Override
    public void updateDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("连书 ×").append(this.amount)
                .append("：每接一张与上一张共享部件的成品牌，对所有敌人造成 ")
                .append(2 * this.amount).append(" 点伤害。可以跨回合续上。");
        String anchor = anchorText();
        if (!anchor.isEmpty()) {
            sb.append(" NL 上一字：").append(anchor);
        }
        this.description = sb.toString();
    }

    /** 「森（木，木，木）」：把上一张成品牌的字形展开成部件；拿不到字就返回空串。 */
    public static String anchorText() {
        String glyph = FusionTracker.lastWordGlyph();
        if (glyph == null) {
            return "";
        }
        List<Component> parts = ZixiuLexicon.partsOf(glyph);
        StringBuilder sb = new StringBuilder(glyph);
        if (!parts.isEmpty()) {
            sb.append('（');
            for (int i = 0; i < parts.size(); i++) {
                if (i > 0) {
                    sb.append('，');
                }
                sb.append(parts.get(i).glyph());
            }
            sb.append('）');
        }
        return sb.toString();
    }
}
