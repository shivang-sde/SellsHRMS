**Universal Multi-Country Payroll Module Architecture**
Payroll Module
├── Dashboard
│    ├─ Net Pay Summary
│    ├─ Pay Date
│    ├─ Active Employees
│    └─ Deduction Summary (EPF, ESI, TDS, etc.)
├── Pay Runs
│    ├─ Run Payroll
│    └─ Payroll History
├── Approvals
│    ├─ Reimbursement Approvals
│    ├─ Proof of Investments
│    └─ Salary Revision Approvals
├── Taxes & Forms
│    ├─ Country-specific TDS / Tax Liabilities
│    ├─ Challans
│    ├─ Form 16, Form 24Q (India)
│    └─ Country-specific statutory forms
├── Settings / Organisation Profile
│    ├─ Work Locations
│    ├─ Payroll Config per Country
│    └─ Tax & Statutory Config per Location / State
└── Salary Components
     ├─ Earnings
     │    ├─ Name
     │    ├─ Type
     │    ├─ Calculation Type (Fixed / Formula)
     │    ├─ Formula / Value
     │    ├─ Consider for EPF / ESI / Tax
     ├─ Deductions
     │    ├─ Name
     │    ├─ Type
     │    ├─ Calculation Type (Fixed / Formula)
     │    └─ Consider for statutory / tax
     └─ Reimbursements
