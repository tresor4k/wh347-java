/**
 * Computes one line and one week of the U.S. Department of Labor WH-347 certified payroll form, revised version
 * (Rev. January 2025), used on Davis-Bacon and Related Acts contracts.
 *
 * <p>Given a worker's week (base rate, fringe rates per plan, straight-time and overtime hours per day, cash in
 * lieu of fringe, deductions), {@link com.tabletemplates.wh347.WH347#computeLine} returns columns 5 to 9 of the
 * form and {@link com.tabletemplates.wh347.WH347#computeRun} adds the per-plan fringe totals for the crew and a
 * reconciliation check of column 6B. The package has no dependencies and no I/O.
 *
 * <p>Arithmetic is plain {@code double} with no intermediate rounding;
 * {@link com.tabletemplates.wh347.WH347#roundCents} rounds to the cent (halves toward +Infinity) and is meant for
 * display only. The ST/OT split is entered by the user, not computed from a daily or weekly threshold.
 *
 * <p>Not an official DOL tool, not legal advice; you remain responsible for your certified payrolls. The package
 * does not check wage determinations, fill the Statement of Compliance (page 2), or produce the form itself.
 *
 * <p>DOL instructions for the WH-347: <a href="https://www.dol.gov/agencies/whd/forms/wh347">dol.gov/agencies/whd/forms/wh347</a>
 *
 * <p>The same calculations are available as a hosted tool at
 * <a href="https://tabletemplates.com/certified-payroll-software/">tabletemplates.com/certified-payroll-software/</a>.
 */
package com.tabletemplates.wh347;
