package com.sellspark.SellsHRMS.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * Response payload for GET /api/attendance/pre-check.
 *
 * <p>The front-end uses this single response to decide the initial badge state
 * and whether the Punch-In button should be enabled, avoiding 3 separate AJAX
 * calls (holiday, leave, shift-window).
 *
 * <p>Priority order enforced on front-end:
 * <ol>
 *   <li>Holiday          → badge = HOLIDAY,    button disabled
 *   <li>On-Leave         → badge = ON_LEAVE,   button disabled
 *   <li>Week-Off         → badge = WEEK_OFF,   button disabled
 *   <li>Before shift     → badge = NOT_PUNCHED, button disabled, reason=BEFORE_SHIFT
 *   <li>After shift      → badge = NOT_PUNCHED, button disabled, reason=AFTER_SHIFT
 *   <li>All clear        → badge = NOT_PUNCHED, button enabled
 * </ol>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PunchPreCheckResponse {

    // ── Holiday ────────────────────────────────────────────────
    private boolean holiday;
    private String  holidayName;          // null when not a holiday
    private String  holidayType;          // null when not a holiday

    // ── Leave ──────────────────────────────────────────────────
    private boolean onLeave;

    // ── Week-off ───────────────────────────────────────────────
    private boolean weekOff;
    private String  weekOffDay;           // e.g. "SUNDAY"

    // ── Shift / Office window ──────────────────────────────────
    /** Office open time (from org policy), ISO-8601 local time string "HH:mm" */
    private String officeStart;           // e.g. "10:00"

    /** Office close time (from org policy) */
    private String officeClosed;          // e.g. "19:00"

    /** Grace minutes allowed after officeStart before marking "late" */
    private int    lateGraceMinutes;

    /**
     * Whether the current server time falls inside the punch-in window.
     * i.e.  officeStart - some_pre_buffer  ≤  now  ≤  officeClosed
     */
    private boolean withinShiftWindow;

    /**
     * One of: BEFORE_SHIFT | WITHIN_SHIFT | AFTER_SHIFT
     */
    private String shiftStatus;

    // ── Combined "can punch in" verdict ───────────────────────
    /** True only when no holiday + no leave + no week-off + within shift */
    private boolean canPunchIn;
}
