🧮 2. DASHBOARD METRICS & DATA SOURCES
| Metric / Widget                       | Tables Involved                                            | Description                                   |
| ------------------------------------- | ---------------------------------------------------------- | --------------------------------------------- |
| **Average Employee Attendance (%)**   | `tbl_attendance_summary`, `tbl_employee`                   | `(PRESENT days / total working days) * 100`   |
| **Attendance Over Time (line chart)** | `tbl_attendance_summary`                                   | Monthly average attendance %                  |
| **Reasons for Absenteeism (pie)**     | `tbl_attendance_summary`, `tbl_leave_type`                 | Count of `ON_LEAVE` grouped by leave type     |
| **Number of Days Missed**             | `tbl_attendance_summary`                                   | Count of `ABSENT` + `ON_LEAVE` days           |
| **Late Arrivals (bar)**               | `tbl_attendance_summary`                                   | Count where `is_late = 1`, grouped monthly    |
| **Days Missed by Department**         | `tbl_attendance_summary`, `tbl_employee`, `tbl_department` | Total absent days grouped by department       |
| **Average Weekly Hours**              | `tbl_punch_in_out`, `tbl_employee`, `tbl_department`       | Average of `work_hours` grouped by department |
