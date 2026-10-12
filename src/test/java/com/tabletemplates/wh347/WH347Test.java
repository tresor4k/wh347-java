// Parity with the Go package wh347 (wh347_test.go), itself in parity with the
// Rust crate wh347 0.1.0 (tests/engine.rs). Every case and expected value is
// copied from wh347_test.go; tools/crosscheck.mjs verifies it one by one.
package com.tabletemplates.wh347;

import static com.tabletemplates.wh347.WH347.computeLine;
import static com.tabletemplates.wh347.WH347.computeRun;
import static com.tabletemplates.wh347.WH347.roundCents;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class WH347Test {
    static WorkerWeek johnMason() {
        return WorkerWeek.builder()
                .baseRate(30)
                .fringe(FringeRates.NONE.withHealthWelfare(4).withPension(2))
                .stByDay(8, 8, 8, 8, 8, 0, 0)
                .otByDay(4, 0, 0, 0, 0, 0, 0)
                .deductions(Deductions.NONE.withOther(310))
                .build();
    }

    static WorkerWeek worker() {
        return WorkerWeek.builder()
                .baseRate(20)
                .fringe(FringeRates.NONE.withHealthWelfare(1).withPension(1).withVacation(0.5).withTraining(0.25))
                .stByDay(8, 8, 8, 8, 8, 0, 0)
                .build();
    }

    static WorkerWeek with(WorkerWeek w, Consumer<WorkerWeek.Builder> f) {
        WorkerWeek.Builder b = w.toBuilder();
        f.accept(b);
        return b.build();
    }

    /** Same check as the Go helper {@code near} (vitest toBeCloseTo(expected, 2)): |a − e| &lt; 0.005. */
    static void near(String field, double actual, double expected) {
        assertTrue(Math.abs(actual - expected) < 0.005, field + ": expected " + expected + ", got " + actual);
    }

    /** Exact comparison, like Go {@code ==} (0.0 == -0.0). */
    static void eq(String field, double actual, double expected) {
        if (actual != expected) fail(field + ": expected " + expected + ", got " + actual);
    }

    static void eq(String field, int actual, int expected) {
        if (actual != expected) fail(field + ": expected " + expected + ", got " + actual);
    }

    static void eq(String field, boolean actual, boolean expected) {
        if (actual != expected) fail(field + ": expected " + expected + ", got " + actual);
    }

    static void eq(String field, Object actual, Object expected) {
        if (!Objects.equals(actual, expected)) fail(field + ": expected " + expected + ", got " + actual);
    }

    static DynamicTest checkLine(String name, WorkerWeek in, Consumer<Line> check) {
        return DynamicTest.dynamicTest(name, () -> check.accept(computeLine(in)));
    }

    static DynamicTest checkRun(String name, List<WorkerWeek> in, Consumer<Run> check) {
        return DynamicTest.dynamicTest(name, () -> check.accept(computeRun(in)));
    }

    static DynamicTest checkTotal(String name, FringeRates in, double want) {
        return DynamicTest.dynamicTest(name, () -> eq("Total", in.total(), want));
    }

    static DynamicTest checkRound(double in, double want) {
        return DynamicTest.dynamicTest(String.valueOf(in), () -> eq("RoundCents", roundCents(in), want));
    }

    @TestFactory
    Stream<DynamicTest> testComputeLine() {
        return Stream.of(
            // --- parity.test.ts ---
            checkLine("SC-WH-1 gross ST+OT", johnMason(), l -> {
                near("STHours", l.stHours(), 40);
                near("OTHours", l.otHours(), 4);
                near("Gross", l.gross(), 1380);
            }),
            checkLine("SC-WH-3 net is cash pay", johnMason(), l -> {
                near("NetPay", l.netPay(), 1070);
            }),
            // --- edge-cases.test.ts ---
            checkLine("zero hours", with(worker(), w -> w.stByDay(0, 0, 0, 0, 0, 0, 0)), l -> {
                eq("Gross", l.gross(), 0);
                eq("FringeCredit", l.fringeCredit(), 0);
                eq("NetPay", l.netPay(), 0);
                eq("Sanity", l.sanity(), Sanity.OK);
            }),
            checkLine("overtime only", with(worker(), w -> w
                    .stByDay(0, 0, 0, 0, 0, 0, 0)
                    .otByDay(0, 0, 0, 0, 0, 6, 4)), l -> {
                eq("STHours", l.stHours(), 0);
                eq("OTHours", l.otHours(), 10);
                near("Gross", l.gross(), 300);
                near("FringeCredit", l.fringeCredit(), 27.5);
            }),
            checkLine("negative net is review", with(worker(), w -> w.deductions(Deductions.NONE.withFederalTax(2000))), l -> {
                near("Gross", l.gross(), 800);
                near("NetPay", l.netPay(), -1200);
                eq("Sanity", l.sanity(), Sanity.REVIEW);
            }),
            checkLine("itemized deductions sum", with(worker(), w -> w
                    .deductions(Deductions.NONE.withFica(61.2).withFederalTax(90).withStateTax(38.5).withOther(10.3))), l -> {
                near("TotalDeductions", l.totalDeductions(), 200);
                near("NetPay", l.netPay(), 600);
            }),
            checkLine("hours threshold 112 ok", with(worker(), w -> w
                    .stByDay(16, 16, 16, 16, 16, 16, 16)), l -> {
                eq("STHours", l.stHours(), 112);
                eq("Sanity", l.sanity(), Sanity.OK);
            }),
            checkLine("hours threshold 112 over", with(worker(), w -> w
                    .stByDay(16, 16, 16, 16, 16, 16, 16)
                    .otByDay(0.25, 0, 0, 0, 0, 0, 0)), l -> {
                eq("Sanity", l.sanity(), Sanity.REVIEW);
            }),
            checkLine("negative rate is review", with(worker(), w -> w
                    .baseRate(-1)
                    .stByDay(1, 0, 0, 0, 0, 0, 0)), l -> {
                eq("Sanity", l.sanity(), Sanity.REVIEW);
            }),
            checkLine("rate plus fringe", worker(), l -> {
                near("RatePlusFringe", l.ratePlusFringe(), 22.75);
            }),
            checkLine("rev2025 defaults neutral", worker(), l -> {
                eq("CashInLieu", l.cashInLieu(), 0);
                eq("GrossProject", l.grossProject(), l.gross());
                eq("GrossAllWork", l.grossAllWork(), l.grossProject());
                eq("GrossAllWorkEntered", l.grossAllWorkEntered(), false);
                near("NetPay", l.netPay(), 800);
                eq("WorkerType", l.workerType(), WorkerType.UNSET);
                near("OTRate", l.otRate(), 30);
            }),
            checkLine("col 6C cash in lieu", with(worker(), w -> w
                    .cashInLieuRate(2)
                    .otByDay(2, 0, 0, 0, 0, 0, 0)), l -> {
                near("CashInLieu", l.cashInLieu(), 84);
                near("GrossProject", l.grossProject(), 944);
                near("GrossAllWork", l.grossAllWork(), 944);
                near("FringeCredit", l.fringeCredit(), 42*2.75);
                near("NetPay", l.netPay(), 944);
                eq("Sanity", l.sanity(), Sanity.OK);
            }),
            checkLine("col 7B entered more", with(worker(), w -> w
                    .grossAllWork(1500.0)
                    .deductions(Deductions.NONE.withFederalTax(200))), l -> {
                eq("GrossAllWorkEntered", l.grossAllWorkEntered(), true);
                eq("GrossAllWork", l.grossAllWork(), 1500);
                near("NetPay", l.netPay(), 1300);
                eq("Sanity", l.sanity(), Sanity.OK);
            }),
            checkLine("col 7B entered less", with(worker(), w -> w.grossAllWork(700.0)), l -> {
                eq("Sanity", l.sanity(), Sanity.REVIEW);
            }),
            checkLine("col 7B entered zero", with(worker(), w -> w
                    .stByDay(0, 0, 0, 0, 0, 0, 0)
                    .grossAllWork(0.0)), l -> {
                eq("GrossAllWorkEntered", l.grossAllWorkEntered(), true);
                eq("Sanity", l.sanity(), Sanity.OK);
            }),
            checkLine("col 2 registered apprentice", with(worker(), w -> w.workerType(WorkerType.REGISTERED_APPRENTICE)), l -> {
                eq("WorkerType", l.workerType(), WorkerType.REGISTERED_APPRENTICE);
            }),
            checkLine("col 2 journeyworker", with(worker(), w -> w.workerType(WorkerType.JOURNEYWORKER)), l -> {
                eq("WorkerType", l.workerType(), WorkerType.JOURNEYWORKER);
            })
        );
    }

    @TestFactory
    Stream<DynamicTest> testComputeRun() {
        return Stream.of(
            checkRun("SC-WH-2 fringe 6B reconciles", List.of(johnMason()), r -> {
                near("FringeCredit", r.lines().get(0).fringeCredit(), 264);
                eq("Sanity", r.lines().get(0).sanity(), Sanity.OK);
                near("PlanTotals.HealthWelfare", r.planTotals().healthWelfare(), 176);
                near("PlanTotalsSum", r.planTotalsSum(), 264);
                near("FringeColumnSum", r.fringeColumnSum(), 264);
                eq("Reconciles", r.reconciles(), true);
            }),
            checkRun("empty crew", List.of(), r -> {
                eq("len(Lines)", r.lines().size(), 0);
                eq("FringeColumnSum", r.fringeColumnSum(), 0);
                eq("PlanTotalsSum", r.planTotalsSum(), 0);
                eq("Reconciles", r.reconciles(), true);
            }),
            checkRun("zero fringe reconciles", List.of(with(worker(), w -> w.fringe(FringeRates.NONE))), r -> {
                eq("FringeCredit", r.lines().get(0).fringeCredit(), 0);
                eq("PlanTotalsSum", r.planTotalsSum(), 0);
                eq("Reconciles", r.reconciles(), true);
            }),
            checkRun("multi worker reconciliation", List.of(
                    worker(),
                    with(worker(), w -> w.fringe(FringeRates.NONE.withPension(3.33).withVacation(1.11))),
                    with(worker(), w -> w
                            .fringe(FringeRates.NONE)
                            .otByDay(2, 2, 2, 2, 2, 0, 0))
            ), r -> {
                eq("Reconciles", r.reconciles(), true);
                near("FringeColumnSum", r.fringeColumnSum(), r.planTotalsSum());
            })
        );
    }

    @TestFactory
    Stream<DynamicTest> testFringeRatesTotal() {
        return Stream.of(
            checkTotal("derived", FringeRates.NONE.withHealthWelfare(4).withPension(2), 6),
            checkTotal("zero", FringeRates.NONE, 0)
        );
    }

    @TestFactory
    Stream<DynamicTest> testRoundCents() {
        return Stream.of(
            checkRound(1069.999999999, 1070),
            checkRound(0.005, 0.01),
            checkRound(-1.005, -1),
            checkRound(-0.125, -0.12), // half toward +Inf, like JS Math.round
            checkRound(2.675, 2.68) // same as node: Math.round(2.675 * 100) / 100
        );
    }

    @Test
    void testExampleComputeLine() {
        WorkerWeek week = WorkerWeek.builder()
                .baseRate(30)
                .fringe(FringeRates.NONE.withHealthWelfare(4).withPension(2))
                .stByDay(8, 8, 8, 8, 8, 0, 0)
                .otByDay(4, 0, 0, 0, 0, 0, 0)
                .deductions(Deductions.NONE.withOther(310))
                .build();
        Line l = computeLine(week);
        eq("Output", l.totalHours(), 44);
        eq("Output", l.otRate(), 45);
        eq("Output", roundCents(l.fringeCredit()), 264);
        eq("Output", roundCents(l.grossProject()), 1380);
        eq("Output", roundCents(l.netPay()), 1070);
        eq("Output", l.sanity().toString(), "ok");

        Run r = computeRun(List.of(week));
        eq("Output", roundCents(r.planTotals().healthWelfare()), 176);
        eq("Output", r.reconciles(), true);
    }
}
