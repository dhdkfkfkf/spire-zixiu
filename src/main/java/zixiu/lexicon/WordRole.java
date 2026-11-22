package zixiu.lexicon;

/**
 * 一个字在配方表里的归属。
 *
 * PLAYABLE  可打出，也是合法的拼合结果
 * SEED      字胚：拼得出、打不出，只作为更大字的构件
 * JUNK      废字：能拼出来但没有正面用途，不进选字面板
 * NO_RECIPE 无配方：拼不出来，但可能拆得开（如 淼）
 */
public enum WordRole {
    PLAYABLE, SEED, JUNK, NO_RECIPE
}
