1. **The core concept — Leave Type configuration drives how balances are created and grow**

When an HR creates a Leave Type (like “Casual Leave”, “Earned Leave”, “Sick Leave”), the attributes in your LeaveType entity tell the system how that leave behaves across the year/month

| Field                                                  | Meaning                                                              | Affects Balance?                      |
| ------------------------------------------------------ | -------------------------------------------------------------------- | ------------------------------------- |
| `annualLimit`                                          | Total leave units per year (e.g., 24 days/year)                      | ✅ sets _opening_ & _closing_ balance |
| `accrualMethod`                                        | How leaves are earned over time                                      | ✅ drives how balance grows           |
| `accrualRate`                                          | How many leaves accrue per period (usually monthly)                  | ✅ monthly increment logic            |
| `carryForwardAllowed` / `carryForwardLimit`            | Can unused leaves move to next year?                                 | ✅ affects next year's opening        |
| `validityDays`                                         | For special/comp-off leave — how long it remains valid before expiry | ✅ checked before applying            |
| `maxConsecutiveDays`                                   | Max number of days that can be applied at once                       | ✅ validated during apply             |
| `availableDuringProbation` / `allowDuringNoticePeriod` | Whether new/joining employees can use it                             | ✅ validated during apply             |

So, LeaveType defines the policy rules, and EmployeeLeaveBalance stores how that policy applies per employee per leave year.

**⚙️ 2. The key fields controlling accrual**
AccrualMethod (ENUM)
Defines when and how leaves are granted over time:

| AccrualMethod | Meaning                                                           | Example behavior                  |
| ------------- | ----------------------------------------------------------------- | --------------------------------- |
| `ANNUAL`      | All leaves credited at the start of the leave year                | 24 leaves on 1st Jan              |
| `MONTHLY`     | Credited gradually every month                                    | 2 per month for 12 months         |
| `PRO_RATA`    | Credited proportionally based on joining date or service duration | If joined mid-year, gets half     |
| `NONE`        | No auto accrual; manually managed                                 | For special leaves, LOP, comp-off |

accrualRate

Defines how many leaves to credit per accrual cycle.

Examples:
If accrualMethod = MONTHLY and accrualRate = 2.0, → employee gets 2 days/month.
If accrualMethod = ANNUAL and accrualRate = 24.0, → full 24 credited once per year.
This is the field your accrueMonthlyLeaves() function uses.

validityDays
Applies mostly to Comp-off or Special Leaves.
It defines how long after accrual the leave can be taken.

validityDays = 60

If a Comp-off was accrued on 1 Feb, it must be used before 1 Apr (60 days).

🧩 4️⃣ UI / Frontend Form Inter-dependency Rules

This is very important to avoid invalid configurations.
Let’s define smart frontend logic so HR users can’t make conflicting configurations.

| Field                                       | Depends on                                                    | UI Behavior             |
| ------------------------------------------- | ------------------------------------------------------------- | ----------------------- |
| `accrualRate`                               | `accrualMethod` ≠ NONE                                        | disable if `NONE`       |
| `carryForwardAllowed` / `carryForwardLimit` | `accrualMethod == MONTHLY`                                    | disable otherwise       |
| `carryForwardLimit`                         | enabled only if `carryForwardAllowed` checked                 |                         |
| `validityDays`                              | relevant only if `accrualMethod == NONE` (Comp-Off / Special) | disable otherwise       |
| `annualLimit`                               | required if `accrualMethod` = ANNUAL or MONTHLY               | required, else optional |
| `maxConsecutiveDays`                        | always optional, but can have tooltip “policy check”          | —                       |

| Rule                      | Description                                         |
| ------------------------- | --------------------------------------------------- |
| **Carry Forward**         | Applies only if `accrualMethod = MONTHLY`           |
| **Carry Forward Limit**   | Must ≤ `accrualRate`                                |
| **Annual accrual usage**  | Optionally pro-rated monthly in validation          |
| **Max consecutive days**  | Always limits per-application duration              |
| **Validity days**         | Only relevant for “Comp-Off” (accrualMethod = NONE) |
| **Front-end enforcement** | Disable unrelated fields dynamically                |
| **Back-end enforcement**  | Validate dependent fields logically                 |

Example valid configurations

| Name         | AccrualMethod | AccrualRate | CarryForwardAllowed | CarryForwardLimit | ValidityDays | Notes                                                          |
| ------------ | ------------- | ----------- | ------------------- | ----------------- | ------------ | -------------------------------------------------------------- |
| Earned Leave | MONTHLY       | 2           | ✅                  | 1                 | —            | Monthly accrual + limited carry forward                        |
| Casual Leave | ANNUAL        | 24          | ❌                  | —                 | —            | Annual credited, full available but limited by max consecutive |
| Comp-Off     | NONE          | —           | ❌                  | —                 | 60           | Manual leave, valid for 60 days                                |

Notes & Recommendations

| Behavior        | How handled                                                  |
| --------------- | ------------------------------------------------------------ |
| Monthly accrual | Uses accrualRate × monthsElapsed                             |
| Carry-forward   | Adds 1 per past month capped by carryForwardLimit            |
| Annual accrual  | Divides annualLimit by 12 to simulate per-month availability |
| Max consecutive | Enforced in same validation                                  |
| Pro-rata        | Placeholder (can extend for joining-date based logic)        |
| No cron         | Works dynamically using `LocalDate.now()` and months elapsed |
| Reusability     | Can inject this class in any service (Spring `@Component`)   |
