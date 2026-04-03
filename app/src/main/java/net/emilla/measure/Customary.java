package net.emilla.measure;

enum Customary {;
    public static final int INCHES_IN_INCH = 1;
    public static final int INCHES_IN_FOOT = INCHES_IN_INCH * 12;
    public static final int INCHES_IN_YARD = INCHES_IN_FOOT * 3;
    public static final int INCHES_IN_MILE = INCHES_IN_YARD * 1_760;

    public static final int TEASPOONS_IN_TEASPOON = 1;
    public static final int TEASPOONS_IN_TABLESPOON = TEASPOONS_IN_TEASPOON * 3;
    public static final int TEASPOONS_IN_FLUID_OUNCE = TEASPOONS_IN_TABLESPOON * 2;
    public static final int TEASPOONS_IN_CUP = TEASPOONS_IN_FLUID_OUNCE * 8;
    public static final int TEASPOONS_IN_PINT = TEASPOONS_IN_CUP * 2;
    public static final int TEASPOONS_IN_QUART = TEASPOONS_IN_PINT * 2;
    public static final int TEASPOONS_IN_GALLON = TEASPOONS_IN_QUART * 4;

    public static final int OUNCES_IN_OUNCE = 1;
    public static final int OUNCES_IN_POUND = OUNCES_IN_OUNCE * 16;
    public static final int OUNCES_IN_TON = OUNCES_IN_POUND * 2_000;
}
