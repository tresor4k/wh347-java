package com.tabletemplates.wh347;

/** Column 2 of the revised form. {@link #UNSET} means not entered (printed blank, never defaulted to "J"). */
public enum WorkerType {
    /** Not entered. */
    UNSET(""),
    /** "J" — journeyworker. */
    JOURNEYWORKER("J"),
    /** "RA" — registered apprentice. */
    REGISTERED_APPRENTICE("RA");

    private final String code;

    WorkerType(String code) {
        this.code = code;
    }

    /** @return the form code: "J", "RA", or "" when not entered */
    public String code() {
        return code;
    }

    /** @return the form code, same as {@link #code()} */
    @Override
    public String toString() {
        return code;
    }
}
