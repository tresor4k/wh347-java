package com.tabletemplates.wh347;

import java.util.List;

/** A computed run: one project, one week. Immutable. */
public final class Run {
    private final List<Line> lines;
    private final FringeRates planTotals;
    private final double planTotalsSum;
    private final double fringeColumnSum;
    private final boolean reconciles;

    Run(List<Line> lines, FringeRates planTotals, double planTotalsSum, double fringeColumnSum, boolean reconciles) {
        this.lines = List.copyOf(lines);
        this.planTotals = planTotals;
        this.planTotalsSum = planTotalsSum;
        this.fringeColumnSum = fringeColumnSum;
        this.reconciles = reconciles;
    }

    /** @return one line per input worker, in input order (unmodifiable) */
    public List<Line> lines() { return lines; }

    /** @return crew totals paid to each plan: Σ total hours × plan rate */
    public FringeRates planTotals() { return planTotals; }

    /** @return sum of the 4 plan totals */
    public double planTotalsSum() { return planTotalsSum; }

    /** @return sum of column 6B over all lines */
    public double fringeColumnSum() { return fringeColumnSum; }

    /** @return true when |fringeColumnSum − planTotalsSum| &lt; 0.005 */
    public boolean reconciles() { return reconciles; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Run)) return false;
        Run r = (Run) o;
        return lines.equals(r.lines) && planTotals.equals(r.planTotals)
                && Double.compare(planTotalsSum, r.planTotalsSum) == 0
                && Double.compare(fringeColumnSum, r.fringeColumnSum) == 0 && reconciles == r.reconciles;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(lines, planTotals, planTotalsSum, fringeColumnSum, reconciles);
    }

    @Override
    public String toString() {
        return "Run{lines=" + lines + ", planTotals=" + planTotals + ", planTotalsSum=" + planTotalsSum
                + ", fringeColumnSum=" + fringeColumnSum + ", reconciles=" + reconciles + "}";
    }
}
