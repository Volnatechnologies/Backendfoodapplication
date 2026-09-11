package com.caloryhive.business.staff.entity.enums;

public enum StaffStatus {
    ACTIVE,
    CLOCKED_IN,
    OFF_DUTY,
    LATE,
    ABSENT,
    ON_BREAK,
    ON_LEAVE;

    public boolean isActiveOrClockedIn() {
        return this == ACTIVE || this == CLOCKED_IN;
    }
}
