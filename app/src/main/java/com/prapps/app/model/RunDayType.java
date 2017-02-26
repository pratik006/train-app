package com.prapps.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RunDayType {
    SUN("S", 1), MON("M", 2),TUE("Tu", 3),WED("W", 4),THU("Th", 5),FRI("F", 6),SAT("Sa", 7), DAILY("D", -1);

    private String runDay;
    private int dayOfWeek;

    RunDayType(String runDay, int dayOfWeek) {
        this.runDay = runDay;
        this.dayOfWeek = dayOfWeek;
    }

    public String getRunDay() {
        return runDay;
    }

    public void setRunDay(String runDay) {
        this.runDay = runDay;
    }

    @JsonValue
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public static RunDayType getByRunDay(String runDay) {
        for (RunDayType type : RunDayType.values()) {
            if (runDay.equals(type.runDay)) {
                return type;
            }
        }

        return RunDayType.DAILY;
    }

    public static RunDayType getByDayOfWeek(int dayOfWeek) {
        for (RunDayType type : RunDayType.values()) {
            if (dayOfWeek == type.dayOfWeek) {
                return type;
            }
        }

        return RunDayType.DAILY;
    }
}

