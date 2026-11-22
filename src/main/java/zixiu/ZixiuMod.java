package zixiu;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import zixiu.cards.Bathe;
import zixiu.cards.Belly;
import zixiu.cards.Benevolence;
import zixiu.cards.Beng;
import zixiu.cards.Bland;
import zixiu.cards.Blockade;
import zixiu.cards.Body;
import zixiu.cards.Bright;
import zixiu.cards.But;
import zixiu.cards.Can;
import zixiu.cards.Cause;
import zixiu.cards.Crystal;
import zixiu.cards.Da;
import zixiu.cards.Dai;
import zixiu.cards.Dawn;
import zixiu.cards.Dim;
import zixiu.cards.Drizzle;
import zixiu.cards.Er;
import zixiu.cards.Fen;
import zixiu.cards.Fine;
import zixiu.cards.Forever;
import zixiu.cards.Fu;
import zixiu.cards.Grove;
import zixiu.cards.Guard;
import zixiu.cards.Hollow;
import zixiu.cards.Huo;
import zixiu.cards.Immortal;
import zixiu.cards.Jie;
import zixiu.cards.Jin;
import zixiu.cards.Jiong;
import zixiu.cards.Kou;
import zixiu.cards.Kun;
import zixiu.cards.Lucky;
import zixiu.cards.Mie;
import zixiu.cards.Mound;
import zixiu.cards.Mu;
import zixiu.cards.Noon;
import zixiu.cards.Osmanthus;
import zixiu.cards.Pin;
import zixiu.cards.Prosper;
import zixiu.cards.Sen;
import zixiu.cards.Shou;
import zixiu.cards.Shu;
import zixiu.cards.Sky;
import zixiu.cards.Thicket;
import zixiu.cards.Trio;
import zixiu.cards.Wang;
import zixiu.cards.Whole;
import zixiu.cards.Xing;
import zixiu.cards.Yan;
import zixiu.cards.YanYan;
import zixiu.cards.Zao;
import zixiu.cards.Zhong;
import zixiu.character.ZixiuCharacter;
import zixiu.character.ZixiuEnums;
import zixiu.core.CardPlayHook;
import zixiu.core.TurnReset;
import zixiu.relics.HuoZheZi;
import zixiu.relics.HuoZiPan;
import zixiu.relics.JiuShuQian;
import zixiu.relics.ShuXia;
import zixiu.relics.TuBi;
import zixiu.relics.ZanYan;
import zixiu.relics.ZhuBi;
import zixiu.ui.HandActionButton;
import zixiu.ui.HandActions;
import zixiu.ui.ScreenToast;
import zixiu.ui.SplitPreview;

@SpireInitializer
public class ZixiuMod implements
        EditStringsSubscriber,
        EditCardsSubscriber,
        EditCharactersSubscriber,
        EditRelicsSubscriber,
        PostInitializeSubscriber {

    public static final String MOD_ID = "zixiu";

    public static String makeID(String id) {
        return MOD_ID + ":" + id;
    }

    public static String asset(String path) {
        return MOD_ID + "/images/" + path;
    }

    public static void initialize() {
        new ZixiuMod();
    }

    public ZixiuMod() {
        BaseMod.subscribe(this);
        BaseMod.subscribe(new HandActionButton("合", 300.0F,
                HandActions::inCombat,
                () -> HandActions.fusableCount() >= 2,
                HandActions::openFusion,
                null));
        BaseMod.subscribe(new HandActionButton("拆", 190.0F,
                HandActions::inCombat,
                () -> HandActions.splittableCount() >= 1,
                HandActions::openSplit,
                () -> HandActions.hasFreshWord() ? "墨迹未干" : null));
        BaseMod.subscribe(new TurnReset());
        BaseMod.subscribe(new CardPlayHook());
        BaseMod.subscribe(new ScreenToast());
        BaseMod.subscribe(new SplitPreview());
        BaseMod.addColor(ZixiuEnums.ZIXIU_COLOR,
                new Color(0.14F, 0.16F, 0.22F, 1.0F),
                asset("cardui/bg_attack_512.png"),
                asset("cardui/bg_skill_512.png"),
                asset("cardui/bg_power_512.png"),
                asset("cardui/card_orb.png"),
                asset("cardui/bg_attack_1024.png"),
                asset("cardui/bg_skill_1024.png"),
                asset("cardui/bg_power_1024.png"),
                asset("cardui/big_orb.png"),
                asset("cardui/small_orb.png"));
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, "zixiu/localization/CardStrings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "zixiu/localization/RelicStrings.json");
        BaseMod.loadCustomStringsFile(com.megacrit.cardcrawl.localization.PotionStrings.class,
                "zixiu/localization/PotionStrings.json");
        BaseMod.loadCustomStringsFile(com.megacrit.cardcrawl.localization.EventStrings.class,
                "zixiu/localization/EventStrings.json");
    }

    @Override
    public void receiveEditCards() {
        com.megacrit.cardcrawl.cards.AbstractCard[] cards = {
                // 基础牌与部件
                new Jin(), new Shou(), new Huo(), new Mu(), new Kou(),
                // 闭环样品
                new Dai(), new Xing(), new Kun(), new Shu(), new Sen(),
                // 焚
                new Yan(), new Can(), new Er(), new Zao(), new Da(), new Pin(),
                new YanYan(), new Beng(), new Fu(), new Wang(), new Zhong(),
                new Fen(), new Mie(), new Jie(), new Jiong(),
                // 润
                new Bathe(), new Blockade(), new Grove(), new Body(), new Guard(),
                new Drizzle(), new Thicket(), new Benevolence(), new Belly(),
                new Bland(), new Osmanthus(), new Hollow(), new Mound(),
                // 照
                new Bright(), new Prosper(), new Dawn(), new Lucky(), new Fine(),
                new But(), new Crystal(), new Noon(), new Cause(), new Immortal(),
                new Trio(), new Forever(), new Dim(), new Whole(), new Sky()
        };
        for (com.megacrit.cardcrawl.cards.AbstractCard card : cards) {
            BaseMod.addCard(card);
            com.megacrit.cardcrawl.unlock.UnlockTracker.markCardAsSeen(card.cardID);
        }
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(new ZixiuCharacter(ZixiuCharacter.NAME, ZixiuEnums.ZIXIU),
                asset("charSelect/button.png"),
                asset("charSelect/portrait.png"),
                ZixiuEnums.ZIXIU);
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelicToCustomPool(new TuBi(), ZixiuEnums.ZIXIU_COLOR);
        BaseMod.addRelicToCustomPool(new ZanYan(), ZixiuEnums.ZIXIU_COLOR);
        BaseMod.addRelicToCustomPool(new HuoZheZi(), ZixiuEnums.ZIXIU_COLOR);
        BaseMod.addRelicToCustomPool(new JiuShuQian(), ZixiuEnums.ZIXIU_COLOR);
        BaseMod.addRelicToCustomPool(new ShuXia(), ZixiuEnums.ZIXIU_COLOR);
        BaseMod.addRelicToCustomPool(new ZhuBi(), ZixiuEnums.ZIXIU_COLOR);
        BaseMod.addRelicToCustomPool(new HuoZiPan(), ZixiuEnums.ZIXIU_COLOR);
    }

    @Override
    public void receivePostInitialize() {
        com.badlogic.gdx.graphics.Color potionColor =
                new com.badlogic.gdx.graphics.Color(0.24F, 0.30F, 0.46F, 1.0F);
        BaseMod.addPotion(zixiu.potions.Ink.class, potionColor, potionColor, potionColor,
                makeID("Ink"), ZixiuEnums.ZIXIU);
        BaseMod.addPotion(zixiu.potions.Cinnabar.class, potionColor, potionColor, potionColor,
                makeID("Cinnabar"), ZixiuEnums.ZIXIU);
        BaseMod.addPotion(zixiu.potions.Rubbing.class, potionColor, potionColor, potionColor,
                makeID("Rubbing"), ZixiuEnums.ZIXIU);
        BaseMod.addEvent(zixiu.events.CuoZi.ID, zixiu.events.CuoZi.class, "Exordium");
        BaseMod.addEvent(zixiu.events.CanJuan.ID, zixiu.events.CanJuan.class, "Exordium");
    }
}
