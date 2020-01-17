package org.lds.math;

import org.lds.Util;

public class Fraction extends Number {
    public static Fraction parse(String value) {
        if (Util.isNullOrEmpty(value)) {
            return null;
        }
        int index = value.lastIndexOf('/');
        if (index < 0) {
            return new Fraction(Long.parseLong(value), 1);
        } else {
            return new Fraction(Long.parseLong(value.substring(0, index)), Long.parseLong(value.substring(index + 1)));
        }
    }

    private final long numerator;
    private final long denominator;

    public Fraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("/ by zero");
        }
        if (numerator == 0) {
            this.numerator = 0;
            this.denominator = 1;
        } else {
            long min = Math.min(Math.abs(numerator), Math.abs(denominator));
            long max = Math.max(Math.abs(numerator), Math.abs(denominator));
            long remainder;
            while ((remainder = max % min) > 0) {
                max = min;
                min = remainder;
            }
            if (denominator < 0) {
                min = -min;
            }
            this.numerator = numerator / min;
            this.denominator = denominator / min;
        }
    }

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public Fraction reciprocal() {
        return new Fraction(denominator, numerator);
    }

    @Override
    public String toString() {
        if (numerator == 0) {
            return "0";
        }
        if (denominator == 1) {
            return Long.toString(numerator / denominator);
        }
        return String.format("%d/%d", numerator, denominator);
    }

    @Override
    public int intValue() {
        return (int) longValue();
    }

    @Override
    public long longValue() {
        return numerator / denominator;
    }

    @Override
    public float floatValue() {
        return (float) numerator / denominator;
    }

    @Override
    public double doubleValue() {
        return (double) numerator / denominator;
    }

}
