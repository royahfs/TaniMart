package com.example.tanimart.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyHelper {
    public static String formatRupiah(double value) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        return "Rp" + formatter.format(value);
    }
}
