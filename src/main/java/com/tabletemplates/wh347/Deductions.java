package com.tabletemplates.wh347;

/** Itemized weekly deductions in $ for all work (column 8). They are entered, never computed. Immutable. */
public final class Deductions {
    /** All four deductions at zero. */
    public static final Deductions NONE = new Deductions(0, 0, 0, 0);

    private final double fica;
    private final double federalTax;
    private final double stateTax;
    private final double other;

    /**
     * Creates itemized deductions.
     *
     * @param fica FICA withholding
     * @param federalTax federal income tax withholding
     * @param stateTax state income tax withholding
     * @param other other permissible deductions
     */
    public Deductions(double fica, double federalTax, double stateTax, double other) {
        this.fica = fica;
        this.federalTax = federalTax;
        this.stateTax = stateTax;
        this.other = other;
    }

    /** @return FICA withholding */
    public double fica() { return fica; }

    /** @return federal income tax withholding */
    public double federalTax() { return federalTax; }

    /** @return state income tax withholding */
    public double stateTax() { return stateTax; }

    /** @return other permissible deductions */
    public double other() { return other; }

    /**
     * @param v FICA withholding
     * @return a copy with this deduction replaced
     */
    public Deductions withFica(double v) { return new Deductions(v, federalTax, stateTax, other); }

    /**
     * @param v federal income tax withholding
     * @return a copy with this deduction replaced
     */
    public Deductions withFederalTax(double v) { return new Deductions(fica, v, stateTax, other); }

    /**
     * @param v state income tax withholding
     * @return a copy with this deduction replaced
     */
    public Deductions withStateTax(double v) { return new Deductions(fica, federalTax, v, other); }

    /**
     * @param v other permissible deductions
     * @return a copy with this deduction replaced
     */
    public Deductions withOther(double v) { return new Deductions(fica, federalTax, stateTax, v); }

    /** @return the column 8 total: the sum of the 4 itemized deductions */
    public double total() {
        return fica + federalTax + stateTax + other;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deductions)) return false;
        Deductions d = (Deductions) o;
        return Double.compare(fica, d.fica) == 0 && Double.compare(federalTax, d.federalTax) == 0
                && Double.compare(stateTax, d.stateTax) == 0 && Double.compare(other, d.other) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(new double[] {fica, federalTax, stateTax, other});
    }

    @Override
    public String toString() {
        return "Deductions{fica=" + fica + ", federalTax=" + federalTax
                + ", stateTax=" + stateTax + ", other=" + other + "}";
    }
}
