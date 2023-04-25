package com.nure.tariffmanage;

import java.sql.Date;

public record TopUpsHistory(double amount, Date date) implements Comparable<TopUpsHistory> {

    @Override
    public int compareTo(TopUpsHistory o) {
        if ((date.equals(o.date))) return 0;
        else if (date.after(o.date)) return 1;
        else return -1;

    }
}
