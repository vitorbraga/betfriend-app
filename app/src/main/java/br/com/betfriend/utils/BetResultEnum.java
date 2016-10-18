package br.com.betfriend.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import br.com.betfriend.R;

/**
 * Created by vitorbr on 14/09/16.
 */
public enum BetResultEnum {

    UNDEFINED(1, "-", R.drawable.rounded_corner_grey),

    WIN(2, "Win", R.drawable.rounded_corner_green),

    LOSS(3, "Loss", R.drawable.rounded_corner_red),

    DRAW(4, "Draw", R.drawable.rounded_corner_grey);

    private Integer id;

    private String label;

    private int color;


    private static final Map<Integer, BetResultEnum> lookup = new HashMap<Integer, BetResultEnum>();

    static {
        for (BetResultEnum s : EnumSet.allOf(BetResultEnum.class)) {
            lookup.put(s.id(), s);
        }
    }

    private BetResultEnum(int id, String label, int color) {
        this.id = id;
        this.label = label;
        this.color = color;
    }

    public Integer id() {
        return this.id;
    }

    public String label() {
        return this.label;
    }

    public int color() {
        return this.color;
    }

    public static BetResultEnum get(final Integer id) {
        return lookup.get(id);
    }


}
