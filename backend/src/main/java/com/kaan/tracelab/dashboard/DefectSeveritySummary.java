package com.kaan.tracelab.dashboard;

public record DefectSeveritySummary(
        long low,
        long medium,
        long high,
        long critical
) {
}