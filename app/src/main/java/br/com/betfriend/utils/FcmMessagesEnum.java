package br.com.betfriend.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import br.com.betfriend.R;

public enum FcmMessagesEnum {

    NEW_MATCHES(100, R.string.app_name, R.string.fcm_new_matches),

    NEW_BET_INVITE(101, R.string.fcm_new_bet_invite, R.string.fcm_touch_to_see),

    BET_ACCEPTED(102, R.string.app_name, R.string.fcm_bet_accepted);

    private Integer code;

    private int title;

    private int messageBody;


    private static final Map<Integer, FcmMessagesEnum> lookup = new HashMap<Integer, FcmMessagesEnum>();

    static {
        for (FcmMessagesEnum s : EnumSet.allOf(FcmMessagesEnum.class)) {
            lookup.put(s.code(), s);
        }
    }

    FcmMessagesEnum(int code, int title, int messageBody) {
        this.code = code;
        this.title = title;
        this.messageBody = messageBody;
    }

    public Integer code() {
        return this.code;
    }

    public int title() {
        return this.title;
    }

    public int messageBody() {
        return this.messageBody;
    }

    public static FcmMessagesEnum get(final Integer code) {
        return lookup.get(code);
    }
}
