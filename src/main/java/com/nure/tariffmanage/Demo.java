package com.nure.tariffmanage;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Demo {
    public static void main(String[] args) {
        Date date = new Date(123,3,25);
        System.out.println(date.toLocalDate().equals(LocalDate.now()));
    }
}
