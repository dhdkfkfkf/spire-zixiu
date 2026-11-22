package zixiu.character;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;

/**
 * 向游戏内枚举注入字修自己的取值。ModTheSpire 在类加载时处理这些 @SpireEnum。
 */
public final class ZixiuEnums {

    @SpireEnum
    public static AbstractPlayer.PlayerClass ZIXIU;

    @SpireEnum
    public static AbstractCard.CardColor ZIXIU_COLOR;

    /** 名字必须与 CardColor 一致：BaseMod 会用 CardColor.name() 去 valueOf LibraryType。 */
    @SpireEnum(name = "ZIXIU_COLOR")
    public static CardLibrary.LibraryType ZIXIU_LIBRARY;

    private ZixiuEnums() {
    }
}
