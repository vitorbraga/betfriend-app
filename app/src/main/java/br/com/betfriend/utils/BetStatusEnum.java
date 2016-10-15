package br.com.betfriend.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitorbr on 14/09/16.
 */
public enum BetStatusEnum {

    AWAITING_ACCEPTANCE(1),

    ACCEPTED(2),

    REFUSED(3),

    CANCELED(4);

    private Integer id;

    private static final Map<Integer, BetStatusEnum> lookup = new HashMap<Integer, BetStatusEnum>();

    static {
        for (BetStatusEnum s : EnumSet.allOf(BetStatusEnum.class)) {
            lookup.put(s.id(), s);
        }
    }

    private BetStatusEnum(int id) {
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }

    public static BetStatusEnum get(final Integer id) {
        return lookup.get(id);
    }

}
