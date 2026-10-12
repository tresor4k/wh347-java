package com.tabletemplates.wh347;

/** Fringe benefit rates in $/hour, one per plan. Immutable. */
public final class FringeRates {
    /** All four plan rates at zero. */
    public static final FringeRates NONE = new FringeRates(0, 0, 0, 0);

    private final double healthWelfare;
    private final double pension;
    private final double vacation;
    private final double training;

    /**
     * Creates fringe rates.
     *
     * @param healthWelfare health and welfare plan, $/h
     * @param pension pension plan, $/h
     * @param vacation vacation / holiday plan, $/h
     * @param training training plan, $/h
     */
    public FringeRates(double healthWelfare, double pension, double vacation, double training) {
        this.healthWelfare = healthWelfare;
        this.pension = pension;
        this.vacation = vacation;
        this.training = training;
    }

    /** @return health and welfare plan, $/h */
    public double healthWelfare() { return healthWelfare; }

    /** @return pension plan, $/h */
    public double pension() { return pension; }

    /** @return vacation / holiday plan, $/h */
    public double vacation() { return vacation; }

    /** @return training plan, $/h */
    public double training() { return training; }

    /**
     * @param v health and welfare plan, $/h
     * @return a copy with this rate replaced
     */
    public FringeRates withHealthWelfare(double v) { return new FringeRates(v, pension, vacation, training); }

    /**
     * @param v pension plan, $/h
     * @return a copy with this rate replaced
     */
    public FringeRates withPension(double v) { return new FringeRates(healthWelfare, v, vacation, training); }

    /**
     * @param v vacation / holiday plan, $/h
     * @return a copy with this rate replaced
     */
    public FringeRates withVacation(double v) { return new FringeRates(healthWelfare, pension, v, training); }

    /**
     * @param v training plan, $/h
     * @return a copy with this rate replaced
     */
    public FringeRates withTraining(double v) { return new FringeRates(healthWelfare, pension, vacation, v); }

    /** @return the total hourly fringe credit: the sum of the 4 plan rates */
    public double total() {
        return healthWelfare + pension + vacation + training;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FringeRates)) return false;
        FringeRates f = (FringeRates) o;
        return Double.compare(healthWelfare, f.healthWelfare) == 0 && Double.compare(pension, f.pension) == 0
                && Double.compare(vacation, f.vacation) == 0 && Double.compare(training, f.training) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(new double[] {healthWelfare, pension, vacation, training});
    }

    @Override
    public String toString() {
        return "FringeRates{healthWelfare=" + healthWelfare + ", pension=" + pension
                + ", vacation=" + vacation + ", training=" + training + "}";
    }
}
