package com.tabletemplates.wh347;

/** Result of the per-line sanity check. */
public enum Sanity {
    /** No anomaly detected. */
    OK("ok"),
    /** Net pay &lt; 0, hours &gt; 112, base rate &lt; 0, or entered 7B &lt; 7A. */
    REVIEW("review");

    private final String label;

    Sanity(String label) {
        this.label = label;
    }

    /** @return "ok" or "review" */
    @Override
    public String toString() {
        return label;
    }
}
