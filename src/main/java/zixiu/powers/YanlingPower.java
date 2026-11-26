package zixiu.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;
import zixiu.relics.InscriptionRelic;
import zixiu.ui.ScreenToast;

/**
 * 九字真言·言灵。
 *
 * 九个字各有一次「言灵」，指针（amount = 1..9）决定接下来吃哪一个：
 * <ul>
 *   <li>没集齐：指针 = 已收集的字数，只在**回合开始**触发一次该字的言灵；</li>
 *   <li>集齐：指针从 1 开始，打出一张牌先按「组成单位数」推进 n，再触发新指针的言灵 ——
 *       指针自己往 1→9 轮转，不需要手动选。</li>
 * </ul>
 * 「组成单位数」= 这张牌拆开有几个部件（部件牌 1、单字 1、双字 2、三字 3）。
 */
public class YanlingPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("YanlingPower");
    public static final String STATE_NAME = "九字真言·言灵";

    public static final String[] NAMES = {"临", "兵", "斗", "者", "皆", "阵", "列", "在", "前"};
    private static final String[] EFFECTS = {
            "获得 3 点格挡。",
            "对随机敌人造成 9 点伤害。",
            "获得 1 点力量。",
            "回复 2 点生命。",
            "抽 1 张牌。",
            "给予所有敌人 1 层虚弱。",
            "获得 1 点敏捷。",
            "本回合下一张牌耗能减 1。",
            "将一张随机手牌本回合耗能变为 0。"
    };

    private static final int BLOCK = 3;
    private static final int BING_DAMAGE = 9;
    private static final int HEAL = 2;

    /** 「领悟」提示只弹一次。 */
    private static boolean announced;
    /** 「在」：下一张牌减 1 费的欠账。 */
    private static boolean discountPending;

    public YanlingPower(AbstractCreature owner, int index) {
        this.name = STATE_NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = clamp(index);
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Yanling");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        int i = clamp(this.amount);
        StringBuilder sb = new StringBuilder();
        sb.append("当前第 ").append(i).append(" 字「").append(NAMES[i - 1]).append("」：")
                .append(EFFECTS[i - 1])
                .append(InscriptionRelic.hasAll()
                        ? "（打出 n 单位牌推进 n，指针自动轮转）"
                        : "（每场战斗第一回合触发一次；集齐九字后出牌推进）");
        // 把 1~9 每个字的言灵都列出来，查起来不用翻文档
        sb.append(" NL ");
        for (int k = 0; k < NAMES.length; k++) {
            if (k > 0) {
                sb.append(" NL ");
            }
            sb.append(NAMES[k]).append("：").append(EFFECTS[k]);
        }
        this.description = sb.toString();
    }

    // ---- 生命周期 ----

    /** 有九字遗物就确保这个能力在场；没有就什么都不做。 */
    public static void ensure(AbstractPlayer player) {
        if (player == null) {
            return;
        }
        int owned = InscriptionRelic.ownedCount();
        announceIfComplete();
        if (owned <= 0) {
            return;
        }
        AbstractPower existing = player.getPower(POWER_ID);
        if (existing instanceof YanlingPower) {
            YanlingPower yanling = (YanlingPower) existing;
            // 没集齐时指针跟着「已收集字数」走（中途拿到新字也能立刻对上）
            if (!InscriptionRelic.hasAll()) {
                yanling.amount = owned;
            }
            yanling.updateDescription();
            return;
        }
        // 没集齐时指针固定在「已收集字数」，集齐后从「临」开始
        int start = InscriptionRelic.hasAll() ? 1 : owned;
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(player, player, new YanlingPower(player, start), start));
    }

    /** 本场战斗是否已经触发过「未集齐」的那一次。 */
    private static boolean triggeredThisCombat;

    /** 战斗开始时清空。 */
    public static void resetCombatTrigger() {
        triggeredThisCombat = false;
    }

    /** 回合开始：集齐了每回合触发；没集齐只在本次战斗的第一回合触发一次。 */
    public static void onPlayerTurnStart(AbstractPlayer player) {
        ensure(player);
        if (player == null) {
            return;
        }
        AbstractPower power = player.getPower(POWER_ID);
        if (!(power instanceof YanlingPower)) {
            return;
        }
        if (!InscriptionRelic.hasAll()) {
            if (triggeredThisCombat) {
                return;
            }
            triggeredThisCombat = true;
        }
        ((YanlingPower) power).trigger();
    }

    /** 打出一张牌之后：集齐了才推进并再触发一次。 */
    public static void onCardPlayed(AbstractPlayer player, AbstractCard card) {
        if (player == null || !InscriptionRelic.hasAll()) {
            return;
        }
        ensure(player);
        AbstractPower power = player.getPower(POWER_ID);
        if (!(power instanceof YanlingPower)) {
            return;
        }
        YanlingPower yanling = (YanlingPower) power;
        yanling.advance(Math.max(1, CardComponents.expand(card).size()));
        yanling.trigger();
    }

    /** 「在」的减费结算：打出耗能 ≥1 的牌时返还 1 点能量。 */
    public static boolean consumeDiscount(AbstractCard card) {
        if (!discountPending || card == null || card.costForTurn < 1) {
            return false;
        }
        discountPending = false;
        return true;
    }

    /** 求解器镜像用：「在」的减费欠账。 */
    public static boolean discountPending() {
        return discountPending;
    }

    /** 求解器镜像用：写回减费欠账。 */
    public static void setDiscountPending(boolean pending) {
        discountPending = pending;
    }

    /** 当前指针（1..9），没有这个能力时返回 0。 */
    public static int index(AbstractPlayer player) {
        if (player == null) {
            return 0;
        }
        AbstractPower power = player.getPower(POWER_ID);
        return power instanceof YanlingPower ? clamp(power.amount) : 0;
    }

    /** 求解器镜像用：写回指针（没有这个能力就什么都不做）。 */
    public static void setIndex(AbstractPlayer player, int index) {
        if (player == null) {
            return;
        }
        AbstractPower power = player.getPower(POWER_ID);
        if (power instanceof YanlingPower) {
            power.amount = clamp(index);
            power.updateDescription();
        }
    }

    private static void announceIfComplete() {
        if (!InscriptionRelic.hasAll()) {
            announced = false;
            return;
        }
        if (announced) {
            return;
        }
        announced = true;
        ScreenToast.show("九字真言·言灵：你领悟了九字真言的力量");
    }

    private void advance(int units) {
        this.amount = ((clamp(this.amount) - 1 + units) % NAMES.length) + 1;
        updateDescription();
    }

    // ---- 言灵本体 ----

    private void trigger() {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null) {
            return;
        }
        this.flash();
        switch (clamp(this.amount)) {
            case 1:
                addToBot(new GainBlockAction(player, player, BLOCK));
                break;
            case 2:
                AbstractMonster monster = randomMonster();
                if (monster != null) {
                    addToBot(new DamageAction(monster,
                            new DamageInfo(player, BING_DAMAGE, DamageInfo.DamageType.THORNS),
                            AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }
                break;
            case 3:
                addToBot(new ApplyPowerAction(player, player,
                        new StrengthPower(player, 1), 1));
                break;
            case 4:
                addToBot(new HealAction(player, player, HEAL));
                break;
            case 5:
                addToBot(new DrawCardAction(player, 1));
                break;
            case 6:
                applyWeakToAll(player);
                break;
            case 7:
                addToBot(new ApplyPowerAction(player, player,
                        new DexterityPower(player, 1), 1));
                break;
            case 8:
                discountPending = true;
                break;
            case 9:
                freeRandomHandCard();
                break;
            default:
                break;
        }
    }

    private static void applyWeakToAll(AbstractPlayer player) {
        if (AbstractDungeon.getMonsters() == null) {
            return;
        }
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster == null || monster.isDeadOrEscaped()) {
                continue;
            }
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, player,
                    new WeakPower(monster, 1, false), 1));
        }
    }

    private static AbstractMonster randomMonster() {
        if (AbstractDungeon.getMonsters() == null) {
            return null;
        }
        return AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
    }

    private static void freeRandomHandCard() {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null || player.hand.group.isEmpty()) {
            return;
        }
        AbstractCard card = player.hand.group.get(
                AbstractDungeon.cardRandomRng.random(player.hand.group.size() - 1));
        card.setCostForTurn(0);
        card.flash();
    }

    private static int clamp(int index) {
        if (index < 1) {
            return 1;
        }
        if (index > NAMES.length) {
            return NAMES.length;
        }
        return index;
    }
}
