package zixiu.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;
import zixiu.relics.InscriptionRelic;

import java.util.List;

/**
 * 九字碑：碑上刻着「临兵斗者皆阵列前行」，拓下一个字带走。
 *
 * 只有字修会遇到，每局最多两次，集齐九字后不再出现。
 */
public class JiuZiBei extends AbstractImageEvent {

    public static final String ID = ZixiuMod.makeID("JiuZiBei");
    private static final EventStrings STRINGS = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] FALLBACK = {
            "碑上刻着九个字：临、兵、斗、者、皆、阵、列、前、行。拓得下来的只有你没记住的那个。",
            "拓下一个字",
            "以血换字：失去 8 点生命，拓下两个字",
            "离开",
            "你把碑面按在纸上，拓下了一个字。",
            "你的血渗进碑缝，两个字浮了出来。",
            "你退开一步，没有动碑。",
            "[离开]"
    };
    private static final String[] D = STRINGS != null && STRINGS.DESCRIPTIONS != null
            && STRINGS.DESCRIPTIONS.length >= 8 ? STRINGS.DESCRIPTIONS : FALLBACK;

    private static final int MAX_VISITS = 2;
    private static int visits = 0;

    private int screen = 0;

    public JiuZiBei() {
        super("九字碑", D[0], "images/events/jiuzibei.png");
        visits++;
        this.imageEventText.setDialogOption(D[1]);
        this.imageEventText.setDialogOption(D[2]);
        this.imageEventText.setDialogOption(D[3]);
    }

    /**
     * 能不能进这一幕的事件表。
     *
     * 注意：BaseMod 是在**地牢构造时**判定这个条件的，那一刻 {@code actNum} 往往还是 0，
     * 所以这里不能写 {@code actNum > 0}（写了第一幕就永远进不了表）。幕数交给
     * {@code AddEventParams.dungeonIDs} 控制。
     */
    public static boolean canSpawn() {
        boolean ok = visits < MAX_VISITS && !InscriptionRelic.hasAll();
        org.apache.logging.log4j.LogManager.getLogger(JiuZiBei.class)
                .info("[zixiu] JiuZiBei table check act=" + AbstractDungeon.actNum
                        + " dungeon=" + AbstractDungeon.id + " visits=" + visits + " -> " + ok);
        return ok;
    }

    /** 新开一局时把「遇到过几次」清零。 */
    public static void resetVisits() {
        visits = 0;
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (this.screen != 0) {
            openMap();
            return;
        }
        this.screen = 1;

        String result;
        if (buttonPressed == 0) {
            grant(1);
            result = D[4];
        } else if (buttonPressed == 1) {
            AbstractDungeon.player.damage(
                    new com.megacrit.cardcrawl.cards.DamageInfo(null, 8,
                            com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
            grant(2);
            result = D[5];
        } else {
            result = D[6];
        }

        this.imageEventText.updateBodyText(result);
        this.imageEventText.updateDialogOption(0, D[7]);
        this.imageEventText.clearRemainingOptions();
    }

    private void grant(int count) {
        List<String> missing = InscriptionRelic.missingIds();
        for (int i = 0; i < count && !missing.isEmpty(); i++) {
            int index = AbstractDungeon.cardRandomRng.random(missing.size() - 1);
            AbstractRelic relic = InscriptionRelic.make(missing.remove(index));
            if (relic != null) {
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                        (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F, relic);
            }
        }
    }
}
