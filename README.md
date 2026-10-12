# wh347 (Java)

Java port of the Go package
[`codeberg.org/tresor2k/tabletemplates-go/wh347`](https://pkg.go.dev/codeberg.org/tresor2k/tabletemplates-go/wh347),
itself a port of the Rust crate [`wh347`](https://crates.io/crates/wh347).
The test suite replays every case of the Go tests with the same expected values.

Calculations for one line and one week of the U.S. Department of Labor
**WH-347 certified payroll form, revised version (Rev. January 2025)**, used on
Davis-Bacon and Related Acts contracts.

The previous version of the form carries an OMB expiration date of 09/30/2026.

Given a worker's week (base rate, fringe rates per plan, straight-time and overtime
hours per day, cash in lieu of fringe, deductions), the package computes columns 5
to 9 of the form, per-plan fringe totals for the crew, and a reconciliation check
of column 6B. It has no runtime dependencies and no I/O. Java 11 or later.

- Step-by-step guide to the form: <https://tabletemplates.com/how-to-fill-out-wh-347/>
- Hosted tool that produces the WH-347 from the same calculations (LeaveSheet):
  <https://tabletemplates.com/certified-payroll-software/>

## Install

Maven:

```xml
<dependency>
  <groupId>com.tabletemplates</groupId>
  <artifactId>wh347</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'com.tabletemplates:wh347:0.1.0'
```

Clojure (`deps.edn`):

```clojure
com.tabletemplates/wh347 {:mvn/version "0.1.0"}
```

## Example

```java
import com.tabletemplates.wh347.*;
import java.util.List;

WorkerWeek week = WorkerWeek.builder()
        .baseRate(30)
        .fringe(FringeRates.NONE.withHealthWelfare(4).withPension(2))
        .stByDay(8, 8, 8, 8, 8, 0, 0)
        .otByDay(4, 0, 0, 0, 0, 0, 0)
        .deductions(Deductions.NONE.withOther(310))
        .build();

Line line = WH347.computeLine(week);
System.out.println(line.totalHours());                      // 44.0   column 5
System.out.println(line.otRate());                          // 45.0   column 6A, OT line
System.out.println(WH347.roundCents(line.fringeCredit()));  // 264.0  column 6B
System.out.println(WH347.roundCents(line.grossProject()));  // 1380.0 column 7A
System.out.println(WH347.roundCents(line.netPay()));        // 1070.0 column 9
System.out.println(line.sanity());                          // ok

Run run = WH347.computeRun(List.of(week));
System.out.println(WH347.roundCents(run.planTotals().healthWelfare())); // 176.0
System.out.println(run.reconciles());                                   // true
```

## Columns of the revised form

| Column | Content | In this package |
|---|---|---|
| 1A | Worker entry number | not handled (identity) |
| 1B / 1C / 1D | Last name / first name / middle initial | not handled (identity) |
| 1E | Worker identifying number (e.g. last 4 digits of the SSN) | not handled (identity) |
| 2 | "J" journeyworker or "RA" registered apprentice | `WorkerWeek.workerType()`, copied to `Line.workerType()` |
| 3 | Labor classification | not handled |
| 4 | Hours worked each day, ST and OT lines | `WorkerWeek.stByDay()`, `WorkerWeek.otByDay()` |
| 5 | Total hours for the week | `Line.stHours()`, `Line.otHours()`, `Line.totalHours()` |
| 6A | Hourly rate paid, ST and OT lines (excluding cash in lieu of fringe) | `Line.stRate()`, `Line.otRate()` = base rate × 1.5 |
| 6B | Total fringe benefit credit | `Line.fringeCredit()` = total hours × total hourly fringe credit |
| 6C | Payment in lieu of fringe benefits | `Line.cashInLieu()` = total hours × `cashInLieuRate` |
| 7A | Gross amount earned, this project | `Line.grossProject()` = ST × rate + OT × rate × 1.5 + 6C |
| 7B | Gross amount earned, all work | `Line.grossAllWork()` = entered value (`WorkerWeek.grossAllWork()` non-null), else 7A |
| 8 | Deductions for all work | `Line.totalDeductions()` = sum of the itemized deductions |
| 9 | Net payment for all work | `Line.netPay()` = 7B − 8 (the 6B fringe credit is never included) |

`Line.sanity()` is `Sanity.REVIEW` when the net pay is negative, the week exceeds 112 hours,
the base rate is negative, or an entered 7B is lower than 7A.

`WH347.computeRun` adds, for the crew, the total paid to each fringe plan (Σ total hours × plan
rate), their sum, the sum of column 6B, and `reconciles()`: true when the two sums differ by
less than 0.005.

Arithmetic is plain `double` with no intermediate rounding; `WH347.roundCents` rounds to the
cent (halves toward +∞, like the Go `RoundCents`) and is meant for display only. The ST/OT
split is entered by the user, not computed from a daily or weekly threshold.

All public types are immutable (`WorkerWeek` is built with `WorkerWeek.builder()`), with
`equals`, `hashCode` and `toString`.

## Parity with the Go package

`tools/crosscheck.mjs` (Node, no dependencies) compares
`src/test/java/com/tabletemplates/wh347/WH347Test.java` with the Go `wh347_test.go`: case
names, input values and expected values, one by one.

```sh
node tools/crosscheck.mjs path/to/wh347_test.go
mvn verify
```

## Scope and disclaimer

Not an official DOL tool, not legal advice; you remain responsible for your certified payrolls.
The package does not check wage determinations, fill the Statement of Compliance
(page 2), or produce the form itself.

- DOL instructions for the WH-347: <https://www.dol.gov/agencies/whd/forms/wh347>
- Guide: <https://tabletemplates.com/how-to-fill-out-wh-347/>
- The same calculations are available as a hosted tool at
  <https://tabletemplates.com/certified-payroll-software/>.

## License

MIT, see [LICENSE](LICENSE). Published by TableTemplates.
