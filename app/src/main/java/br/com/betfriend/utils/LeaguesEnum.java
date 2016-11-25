package br.com.betfriend.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum LeaguesEnum {

    BRAZILIAN_SERIE_A(34, 0),

    PREMIER_LEAGUE(7, 1),

    ITALY_SERIE_A(10, 2);

    private Integer id;

    private Integer dialogPosition;

    private static final Map<Integer, LeaguesEnum> lookup = new HashMap<Integer, LeaguesEnum>();

    static {
        for (LeaguesEnum s : EnumSet.allOf(LeaguesEnum.class)) {
            lookup.put(s.dialogPosition(), s);
        }
    }

    private LeaguesEnum(int id, int dialogPosition) {
        this.id = id;
        this.dialogPosition = dialogPosition;
    }

    public Integer id() {
        return this.id;
    }

    public Integer dialogPosition() {
        return this.dialogPosition;
    }

    public static LeaguesEnum get(final Integer dialogPosition) {
        return lookup.get(dialogPosition);
    }

}
