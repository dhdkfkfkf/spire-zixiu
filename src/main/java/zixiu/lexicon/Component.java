package zixiu.lexicon;

/**
 * 汉字的最小构件。共 10 种，既是部件牌的取值，也是配方键的元素。
 */
public enum Component {
    REN('人'), KOU('口'), MU('木'), YUE('月'), TU('土'),
    YI('一'), SHUI('水'), HUO('火'), SHAN('山'), RI('日');

    private final char glyph;

    Component(char glyph) {
        this.glyph = glyph;
    }

    public char glyph() {
        return glyph;
    }

    /**
     * 异体归并：亻→人、氵→水、灬→火、士→土、囗→口。
     * 卡面美术保留原字形，机制层只认归并后的部件。
     */
    public static Component fromVariant(char c) {
        switch (c) {
            case '亻':
                return REN;
            case '氵':
                return SHUI;
            case '灬':
                return HUO;
            case '士':
                return TU;
            case '囗':
                return KOU;
            default:
                break;
        }
        for (Component component : values()) {
            if (component.glyph == c) {
                return component;
            }
        }
        return null;
    }
}
