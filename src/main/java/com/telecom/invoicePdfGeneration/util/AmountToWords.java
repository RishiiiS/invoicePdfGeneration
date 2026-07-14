package com.telecom.invoicePdfGeneration.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AmountToWords {

    private static final String[] units = { "", "One", "Two", "Three", "Four",
            "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
            "Eighteen", "Nineteen" };

    private static final String[] tens = { 
            "",         // 0
            "",         // 1
            "Twenty",   // 2
            "Thirty",   // 3
            "Forty",    // 4
            "Fifty",    // 5
            "Sixty",    // 6
            "Seventy",  // 7
            "Eighty",   // 8
            "Ninety"    // 9
    };

    public static String convert(BigDecimal amount) {
        if (amount == null) {
            return "Zero Dollars";
        }
        
        long dollars = amount.longValue();
        int cents = amount.subtract(new BigDecimal(dollars))
                          .multiply(new BigDecimal(100))
                          .setScale(0, RoundingMode.HALF_UP)
                          .intValue();

        if (dollars == 0 && cents == 0) {
            return "Zero Dollars Only";
        }

        StringBuilder result = new StringBuilder();

        if (dollars > 0) {
            result.append(convertNumber(dollars)).append(" Dollars");
        }

        if (cents > 0) {
            if (dollars > 0) {
                result.append(" and ");
            }
            result.append(convertNumber(cents)).append(" Cents");
        }

        result.append(" Only");
        return result.toString();
    }

    private static String convertNumber(long n) {
        if (n < 0) {
            return "Minus " + convertNumber(-n);
        }

        if (n < 20) {
            return units[(int) n];
        }

        if (n < 100) {
            return tens[(int) (n / 10)] + ((n % 10 != 0) ? " " : "") + units[(int) (n % 10)];
        }

        if (n < 1000) {
            return units[(int) (n / 100)] + " Hundred" + ((n % 100 != 0) ? " " : "") + convertNumber(n % 100);
        }

        if (n < 1000000) {
            return convertNumber(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " : "") + convertNumber(n % 1000);
        }

        if (n < 1000000000) {
            return convertNumber(n / 1000000) + " Million" + ((n % 1000000 != 0) ? " " : "") + convertNumber(n % 1000000);
        }

        return convertNumber(n / 1000000000) + " Billion" + ((n % 1000000000 != 0) ? " " : "") + convertNumber(n % 1000000000);
    }
}
