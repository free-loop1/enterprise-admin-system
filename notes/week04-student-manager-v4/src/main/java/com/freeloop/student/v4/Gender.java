package com.freeloop.student.v4;

import java.util.Locale;

public enum Gender {
    MAN,
    WOMAN;

    public static Gender fromText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("性别不能为空");
        }
        return valueOf(text.trim().toUpperCase(Locale.ROOT));
    }
}
