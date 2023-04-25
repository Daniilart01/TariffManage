package com.nure.tariffmanage;

import java.sql.Date;

public record TariffUsageHistory(String tariffName, Date beginingDate, Date endDate) implements Comparable<TariffUsageHistory> {

    @Override
    public int compareTo(TariffUsageHistory o) {
        if ((beginingDate.equals(o.beginingDate))) return 0;
        else if (beginingDate.after(o.beginingDate)) return 1;
        else return -1;

    }
}
