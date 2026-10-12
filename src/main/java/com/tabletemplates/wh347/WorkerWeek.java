package com.tabletemplates.wh347;

import java.util.Arrays;
import java.util.Objects;

/**
 * One worker's week on one project: the input of one WH-347 line. Immutable; build it with {@link #builder()}.
 */
public final class WorkerWeek {
    private final double baseRate;
    private final FringeRates fringe;
    private final double[] stByDay;
    private final double[] otByDay;
    private final Deductions deductions;
    private final double cashInLieuRate;
    private final Double grossAllWork;
    private final WorkerType workerType;

    private WorkerWeek(Builder b) {
        this.baseRate = b.baseRate;
        this.fringe = b.fringe;
        this.stByDay = b.stByDay.clone();
        this.otByDay = b.otByDay.clone();
        this.deductions = b.deductions;
        this.cashInLieuRate = b.cashInLieuRate;
        this.grossAllWork = b.grossAllWork;
        this.workerType = b.workerType;
    }

    /** @return a builder with every field at zero, no 7B entered and {@link WorkerType#UNSET} */
    public static Builder builder() {
        return new Builder();
    }

    /** @return a builder initialized with the values of this week */
    public Builder toBuilder() {
        Builder b = new Builder();
        b.baseRate = baseRate;
        b.fringe = fringe;
        b.stByDay = stByDay.clone();
        b.otByDay = otByDay.clone();
        b.deductions = deductions;
        b.cashInLieuRate = cashInLieuRate;
        b.grossAllWork = grossAllWork;
        b.workerType = workerType;
        return b;
    }

    /** @return hourly base rate actually paid, $/h (column 6A, ST line) */
    public double baseRate() { return baseRate; }

    /** @return hourly fringe credit per plan (column 6B is derived from it) */
    public FringeRates fringe() { return fringe; }

    /** @return a copy of the straight-time hours per day, 7 days of the payroll week */
    public double[] stByDay() { return stByDay.clone(); }

    /** @return a copy of the overtime hours per day, 7 days of the payroll week */
    public double[] otByDay() { return otByDay.clone(); }

    /** @return itemized deductions for all work (column 8) */
    public Deductions deductions() { return deductions; }

    /** @return cash paid in lieu of fringe benefits, $/h (column 6C); zero means none */
    public double cashInLieuRate() { return cashInLieuRate; }

    /** @return gross earned for all work, all projects (column 7B), or {@code null} when not entered (7B = 7A) */
    public Double grossAllWork() { return grossAllWork; }

    /** @return column 2; {@link WorkerType#UNSET} means not entered */
    public WorkerType workerType() { return workerType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkerWeek)) return false;
        WorkerWeek w = (WorkerWeek) o;
        return Double.compare(baseRate, w.baseRate) == 0 && fringe.equals(w.fringe)
                && Arrays.equals(stByDay, w.stByDay) && Arrays.equals(otByDay, w.otByDay)
                && deductions.equals(w.deductions) && Double.compare(cashInLieuRate, w.cashInLieuRate) == 0
                && Objects.equals(grossAllWork, w.grossAllWork) && workerType == w.workerType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseRate, fringe, Arrays.hashCode(stByDay), Arrays.hashCode(otByDay),
                deductions, cashInLieuRate, grossAllWork, workerType);
    }

    @Override
    public String toString() {
        return "WorkerWeek{baseRate=" + baseRate + ", fringe=" + fringe + ", stByDay=" + Arrays.toString(stByDay)
                + ", otByDay=" + Arrays.toString(otByDay) + ", deductions=" + deductions
                + ", cashInLieuRate=" + cashInLieuRate + ", grossAllWork=" + grossAllWork
                + ", workerType=" + workerType.name() + "}";
    }

    /** Builder for {@link WorkerWeek}. */
    public static final class Builder {
        private double baseRate;
        private FringeRates fringe = FringeRates.NONE;
        private double[] stByDay = new double[7];
        private double[] otByDay = new double[7];
        private Deductions deductions = Deductions.NONE;
        private double cashInLieuRate;
        private Double grossAllWork;
        private WorkerType workerType = WorkerType.UNSET;

        private Builder() {
        }

        private static double[] week(double[] days) {
            Objects.requireNonNull(days, "days");
            if (days.length != 7) {
                throw new IllegalArgumentException("expected 7 days, got " + days.length);
            }
            return days.clone();
        }

        /**
         * @param v hourly base rate actually paid, $/h (column 6A, ST line)
         * @return this builder
         */
        public Builder baseRate(double v) { baseRate = v; return this; }

        /**
         * @param v hourly fringe credit per plan
         * @return this builder
         */
        public Builder fringe(FringeRates v) { fringe = Objects.requireNonNull(v, "fringe"); return this; }

        /**
         * @param days straight-time hours per day, exactly 7 values
         * @return this builder
         * @throws IllegalArgumentException when {@code days} does not hold 7 values
         */
        public Builder stByDay(double... days) { stByDay = week(days); return this; }

        /**
         * @param days overtime hours per day, exactly 7 values (the ST/OT split is entered, not computed)
         * @return this builder
         * @throws IllegalArgumentException when {@code days} does not hold 7 values
         */
        public Builder otByDay(double... days) { otByDay = week(days); return this; }

        /**
         * @param v itemized deductions for all work (column 8)
         * @return this builder
         */
        public Builder deductions(Deductions v) { deductions = Objects.requireNonNull(v, "deductions"); return this; }

        /**
         * @param v cash paid in lieu of fringe benefits, $/h (column 6C); zero means none
         * @return this builder
         */
        public Builder cashInLieuRate(double v) { cashInLieuRate = v; return this; }

        /**
         * @param v gross earned for all work, all projects (column 7B); {@code null} means 7B = 7A
         * @return this builder
         */
        public Builder grossAllWork(Double v) { grossAllWork = v; return this; }

        /**
         * @param v column 2
         * @return this builder
         */
        public Builder workerType(WorkerType v) { workerType = Objects.requireNonNull(v, "workerType"); return this; }

        /** @return the immutable week */
        public WorkerWeek build() {
            return new WorkerWeek(this);
        }
    }
}
