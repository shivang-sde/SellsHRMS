| AttendanceStatus       | Paid | Affects LOP? | Notes                                      |
| ---------------------- | ---- | ------------ | ------------------------------------------ |
| PRESENT                | ✅    | ❌            | Fully paid                                 |
| HALF_DAY               | ✅/½  | ✅/½          | Half LOP if not covered                    |
| ON_LEAVE (Paid type)   | ✅    | ❌            | If leaveType.isPaid=true                   |
| ON_LEAVE (Unpaid type) | ❌    | ✅            | If leaveType.isPaid=false                  |
| ABSENT                 | ❌    | ✅            | Full LOP                                   |
| HOLIDAY                | ✅    | ❌            | Always paid (company policy)               |
| WEEK_OFF               | ✅    | ❌            | Always paid                                |
| SHORT_DAY              | ❌    | ✅            | Optional — treat as LOP if not regularized |
| WFH / ON_DUTY          | ✅    | ❌            | Treated as paid day                        |
