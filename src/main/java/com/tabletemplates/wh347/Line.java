package com.tabletemplates.wh347;

/** One computed WH-347 line (Rev. January 2025 columns 2 and 5-9). Immutable. */
public final class Line {
    private final WorkerType workerType;
    private final double stHours;
    private final double otHours;
    private final double totalHours;
    private final double stRate;
    private final double otRate;
    private final double gross;
    private final double fringeCredit;
    private final double cashInLieu;
    private final double grossProject;
    private final double grossAllWork;
    private final boolean grossAllWorkEntered;
    private final double totalDeductions;
    private final double netPay;
    private final double ratePlusFringe;
    private final Sanity sanity;

    Line(WorkerType workerType, double stHours, double otHours, double totalHours, double stRate, double otRate,
         double gross, double fringeCredit, double cashInLieu, double grossProject, double grossAllWork,
         boolean grossAllWorkEntered, double totalDeductions, double netPay, double ratePlusFringe, Sanity sanity) {
        this.workerType = workerType;
        this.stHours = stHours;
        this.otHours = otHours;
        this.totalHours = totalHours;
        this.stRate = stRate;
        this.otRate = otRate;
        this.gross = gross;
        this.fringeCredit = fringeCredit;
        this.cashInLieu = cashInLieu;
        this.grossProject = grossProject;
        this.grossAllWork = grossAllWork;
        this.grossAllWorkEntered = grossAllWorkEntered;
        this.totalDeductions = totalDeductions;
        this.netPay = netPay;
        this.ratePlusFringe = ratePlusFringe;
        this.sanity = sanity;
    }

    /** @return column 2, copied from the input */
    public WorkerType workerType() { return workerType; }

    /** @return column 5, straight-time hours for the week */
    public double stHours() { return stHours; }

    /** @return column 5, overtime hours for the week */
    public double otHours() { return otHours; }

    /** @return column 5, total hours (ST + OT) */
    public double totalHours() { return totalHours; }

    /** @return column 6A, ST line: base rate, $/h */
    public double stRate() { return stRate; }

    /** @return column 6A, OT line: base rate × 1.5, $/h */
    public double otRate() { return otRate; }

    /** @return wages ST × rate + OT × rate × 1.5 (excludes 6C) */
    public double gross() { return gross; }

    /** @return column 6B: total hours × total fringe $/h */
    public double fringeCredit() { return fringeCredit; }

    /** @return column 6C: total hours × cash-in-lieu $/h */
    public double cashInLieu() { return cashInLieu; }

    /** @return column 7A, gross earned on this project: wages + 6C */
    public double grossProject() { return grossProject; }

    /** @return column 7B, gross earned for all work: entered value, else 7A */
    public double grossAllWork() { return grossAllWork; }

    /** @return true when 7B was entered, false when it defaulted to 7A */
    public boolean grossAllWorkEntered() { return grossAllWorkEntered; }

    /** @return column 8, total deductions for all work */
    public double totalDeductions() { return totalDeductions; }

    /** @return column 9: 7B − 8 (the 6B fringe credit is never included) */
    public double netPay() { return netPay; }

    /** @return base rate + total fringe, $/h (utility value, not a form column) */
    public double ratePlusFringe() { return ratePlusFringe; }

    /** @return the sanity flag */
    public Sanity sanity() { return sanity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line l = (Line) o;
        return workerType == l.workerType && Double.compare(stHours, l.stHours) == 0
                && Double.compare(otHours, l.otHours) == 0 && Double.compare(totalHours, l.totalHours) == 0
                && Double.compare(stRate, l.stRate) == 0 && Double.compare(otRate, l.otRate) == 0
                && Double.compare(gross, l.gross) == 0 && Double.compare(fringeCredit, l.fringeCredit) == 0
                && Double.compare(cashInLieu, l.cashInLieu) == 0 && Double.compare(grossProject, l.grossProject) == 0
                && Double.compare(grossAllWork, l.grossAllWork) == 0 && grossAllWorkEntered == l.grossAllWorkEntered
                && Double.compare(totalDeductions, l.totalDeductions) == 0 && Double.compare(netPay, l.netPay) == 0
                && Double.compare(ratePlusFringe, l.ratePlusFringe) == 0 && sanity == l.sanity;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(workerType, stHours, otHours, totalHours, stRate, otRate, gross, fringeCredit,
                cashInLieu, grossProject, grossAllWork, grossAllWorkEntered, totalDeductions, netPay, ratePlusFringe,
                sanity);
    }

    @Override
    public String toString() {
        return "Line{workerType=" + workerType.name() + ", stHours=" + stHours + ", otHours=" + otHours
                + ", totalHours=" + totalHours + ", stRate=" + stRate + ", otRate=" + otRate + ", gross=" + gross
                + ", fringeCredit=" + fringeCredit + ", cashInLieu=" + cashInLieu + ", grossProject=" + grossProject
                + ", grossAllWork=" + grossAllWork + ", grossAllWorkEntered=" + grossAllWorkEntered
                + ", totalDeductions=" + totalDeductions + ", netPay=" + netPay + ", ratePlusFringe=" + ratePlusFringe
                + ", sanity=" + sanity + "}";
    }
}
