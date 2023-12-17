package com.example.vhomestay.util.validation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Validation {
    public static LocalDate parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static String validateDecimalNumber(BigDecimal number) {
        if (number == null || number.compareTo(BigDecimal.ZERO) < 0) {
            return "0";
        }
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        try {
            return formatter.format(number);
        } catch (Exception e) {
            return "0";
        }
    }

}
