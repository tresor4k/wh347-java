package com.tabletemplates.wh347;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Entry points: {@link #computeLine}, {@link #computeRun} and {@link #roundCents}. */
public final class WH347 {
    /** Tolerance for cent-level comparisons (7B vs 7A check, 6B reconciliation). */
    public static final double RECONCILIATION_TOLERANCE = 0.005;

    /** Overtime multiplier applied to the base rate (column 6A, OT line). */
    public static final double OT_MULTIPLIER = 1.5;

    /** Sanity threshold: more weekly hours than this is flagged {@link Sanity#REVIEW}. */
    public static final double MAX_SANE_WEEK_HOURS = 112.0;

    private WH347() {
    }

    private static double sumWeek(double[] days) {
        double s = 0.0;
        for (double d : days) {
            s += d;
        }
        return s;
    }

    /**
     * Computes one WH-347 line. No intermediate rounding: use {@link #roundCents} for display only.
     *
     * <p>Java never fuses a multiply and an add into one FMA operation (only {@code Math.fma} does), so the
     * results are bit-identical to the Go package and the Rust crate.
     *
     * @param w the worker's week
     * @return the computed line
     */
    public static Line computeLine(WorkerWeek w) {
        Objects.requireNonNull(w, "w");
        double baseRate = w.baseRate();
        double st = sumWeek(w.stByDay());
        double ot = sumWeek(w.otByDay());
        double hours = st + ot;
        double fringeRate = w.fringe().total();
        double otRate = baseRate * OT_MULTIPLIER;
        double gross = st * baseRate + (ot * baseRate) * OT_MULTIPLIER;
        double cashInLieu = hours * w.cashInLieuRate();
        double grossProject = gross + cashInLieu;
        Double entered = w.grossAllWork();
        double grossAllWork = entered != null ? entered : grossProject;
        double deductions = w.deductions().total();
        double net = grossAllWork - deductions;
        Sanity sanity = Sanity.OK;
        if (net < 0 || hours > MAX_SANE_WEEK_HOURS || baseRate < 0
                || grossAllWork < grossProject - RECONCILIATION_TOLERANCE) {
            sanity = Sanity.REVIEW;
        }
        return new Line(w.workerType(), st, ot, hours, baseRate, otRate, gross, hours * fringeRate, cashInLieu,
                grossProject, grossAllWork, entered != null, deductions, net, baseRate + fringeRate, sanity);
    }

    /**
     * Computes a full run: all lines, per-plan fringe totals and the 6B reconciliation.
     *
     * @param workers the crew, one week per worker
     * @return the computed run
     */
    public static Run computeRun(List<WorkerWeek> workers) {
        Objects.requireNonNull(workers, "workers");
        List<Line> lines = new ArrayList<>(workers.size());
        double hw = 0.0, pension = 0.0, vacation = 0.0, training = 0.0, fringeColumnSum = 0.0;
        for (WorkerWeek w : workers) {
            Line l = computeLine(w);
            lines.add(l);
            double h = l.totalHours();
            FringeRates f = w.fringe();
            hw += h * f.healthWelfare();
            pension += h * f.pension();
            vacation += h * f.vacation();
            training += h * f.training();
            fringeColumnSum += l.fringeCredit();
        }
        FringeRates planTotals = new FringeRates(hw, pension, vacation, training);
        double planTotalsSum = planTotals.total();
        boolean reconciles = Math.abs(fringeColumnSum - planTotalsSum) < RECONCILIATION_TOLERANCE;
        return new Run(lines, planTotals, planTotalsSum, fringeColumnSum, reconciles);
    }

    /**
     * Rounds to cents, halves toward +Infinity (same as JavaScript {@code Math.round(n * 100) / 100} and the Go
     * {@code RoundCents}). For display and snapshots only, never inside the calculation.
     *
     * @param n the amount
     * @return the amount rounded to the cent
     */
    public static double roundCents(double n) {
        double x = n * 100;
        double f = Math.floor(x);
        if (x - f >= 0.5) {
            f++;
        }
        return f / 100;
    }
}
