
| Category                 | Purpose                                                                                         | Who defines it                           | Affects whom             | Frequency of change                         |
| ------------------------ | ----------------------------------------------------------------------------------------------- | ---------------------------------------- | ------------------------ | ------------------------------------------- |
| **Statutory Components** | Social security / government-mandated contributions (e.g., EPF, ESI, PhilHealth, SSS, Pag-IBIG) | Usually *labour or social security laws* | Both Employer & Employee | Usually constant, may change % rates yearly |
| **Income Tax Slabs**     | Income tax on *annual earnings*                                                                 | *Tax department / finance ministry*      | Only Employee            | Usually updated each financial year         |

| Country          | Statutory Components                                                                                            | Income Tax                                                  |
| ---------------- | --------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------- |
| 🇮🇳 India       | EPF (Employee Provident Fund), ESI (Employee State Insurance), PT (Professional Tax), LWF (Labour Welfare Fund) | Income Tax Slabs under Finance Act (5%, 10%, 20%, 30% etc.) |
| 🇵🇭 Philippines | SSS (Social Security System), PhilHealth, Pag-IBIG Fund                                                         | Withholding Tax Table (progressive monthly tax bands)       |


Statutory = benefit contributions (retirement, insurance, housing)
Income Tax = personal income taxation

Organisation
 ├── Country → IN
 │    ├── Statutory Components → [EPF, ESI, PT]
 │    ├── Income Tax Slab → FY2026-27
 │    └── Payroll Settings
 └── Country → PH
      ├── Statutory Components → [SSS, PhilHealth, Pag-IBIG]
      ├── Income Tax Slab → Withholding Tax 2026
      └── Payroll Settings

When PayRun executes:
Engine fetches all active components:
SalaryStructure → Earnings + Deductions.
Engine applies:
Statutory Rules → EPF, ESI, SSS.
IncomeTax Rules → Based on taxable annual salary.
Engine combines results → SalarySlip with:
Employee Deductions (Statutory + Tax)
Employer Contributions (Statutory only)
Net Pay


┌──────────────────────────────┐        ┌──────────────────────────────┐
│   Statutory Component        │        │   Income Tax Slab            │
│  (EPF, SSS, PhilHealth)      │        │  (Taxable Income Brackets)   │
├──────────────────────────────┤        ├──────────────────────────────┤
│  Has Employer + Employee %   │        │  Has progressive % rates     │
│  Based on salary component   │        │  Based on total taxable pay  │
│  Deducted monthly            │        │  Deducted monthly/yearly     │
│  Benefit / Social scheme     │        │  Govt income taxation        │
└──────────────────────────────┘        └──────────────────────────────┘

✅ 8️⃣ Design Conclusion

| Layer                         | Purpose                                            | Example Data                         |
| ----------------------------- | -------------------------------------------------- | ------------------------------------ |
| **StatutoryComponent / Rule** | Configurable per-country social contribution rules | EPF 12% EE + 12% ER up to ₹15k       |
| **IncomeTaxSlab / Rule**      | Progressive tax brackets for income tax            | 0–2.5L: 0%; 2.5L–5L: 5%; 5L–10L: 10% |
| **PayRun Engine**             | Combines both deductions logically                 | Salary Slip includes both sets       |


A Statutory Component (like EPF, SSS, or PhilHealth) can have multiple rule versions over time.
Each rule represents how that component behaves during a specific effective period (financial year, or government update).

So the relationship is:

`One Statutory Component (EPF)
 ├── Rule v1: Jan 2024 - Mar 2024 (12% EE + 12% ER)
 ├── Rule v2: Apr 2024 - Mar 2025 (12% EE + 13% ER)
 └── Rule v3: Apr 2025 - present (12% EE + 13% ER, new ceiling)
`

**🧭 UNIVERSAL PAYROLL MODULE — FINALIZED CLASS RELATION DIAGRAM**

(Aligned with your existing HRMS entities — Employee, Organisation, Department, etc.)
(Country-aware, multi-tenant, future-proof)

1. ORGANISATIONAL & MASTER CONTEXT

`
Organisation
 ├── country (e.g., "IN", "PH")
 ├── organisationPolicy (Attendance, Work hours, Payroll settings)
 ├── workLocations → [OrgWorkLocation]
 ├── salaryComponents → [SalaryComponent]
 ├── statutoryComponents → [StatutoryComponent]
 ├── incomeTaxSlabs → [IncomeTaxSlab]
 └── payRuns → [PayRun]

`

2. 💰 SALARY COMPONENTS & STRUCTURES
`
SalaryComponent
 ├── id, name, abbreviation (e.g., BASIC, HRA, PF)
 ├── type → EARNING / DEDUCTION / REIMBURSEMENT
 ├── calculationType → FIXED / FORMULA / PERCENTAGE
 ├── formula / condition (dynamic EL expressions)
 ├── dependsOnPaymentDays / includeInTotal / isStatistical
 ├── isTaxApplicable / isFlexibleBenefit / maxFlexibleBenefitAmount
 ├── groupType (EARNING_GROUP / DEDUCTION_GROUP)
 ├── organisation, countryCode
 └── [M:N] SalaryStructure.components
`
SalaryStructure
`
SalaryStructure
 ├── name
 ├── frequency → MONTHLY / WEEKLY / BIWEEKLY
 ├── type → GENERAL / TIMESHEET / FLEXIBLE
 ├── leaveEncashmentRate, maxBenefits
 ├── [M:N] components → SalaryComponent
 ├── organisation, countryCode
 └── [1:N] EmployeeSalaryAssignment
`
EmployeeSalaryAssignment
`
EmployeeSalaryAssignment
 ├── employee
 ├── salaryStructure
 ├── taxSlab → IncomeTaxSlab
 ├── basePay, variablePay
 ├── effectiveFrom, effectiveTo
 ├── [1:N] salarySlips
 └── active
`

3. 📊 STATUTORY FRAMEWORK
`
StatutoryComponent
 ├── code, name (e.g., EPF, SSS, PhilHealth)
 ├── organisation, countryCode, stateCode
 ├── description
 ├── isActive
 └── [1:N] statutoryRules
`
StatutoryRule
`
StatutoryRule
 ├── statutoryComponent
 ├── effectiveFrom, effectiveTo
 ├── employeeContributionPercent, employerContributionPercent
 ├── minApplicableSalary, maxApplicableSalary
 ├── deductionCycle → MONTHLY / QUARTERLY / YEARLY / CUSTOM
 ├── includeInCTC, applyProRata
 ├── additionalConfig (JSON logic)
 ├── countryCode, stateCode
 └── active
`
Relation:
One StatutoryComponent → Many StatutoryRules (versioned over time)

4. INCOME TAX FRAMEWORK
`
IncomeTaxSlab
 ├── name, countryCode
 ├── effectiveFrom, effectiveTo
 ├── allowTaxExemption, standardExemptionLimit
 └── [1:N] rules → IncomeTaxRule
`
`
IncomeTaxRule
 ├── taxSlab
 ├── minIncome, maxIncome, deductionPercent
 ├── condition (optional EL/JSON)
 └── active
`
Relation:
One IncomeTaxSlab → Many IncomeTaxRules (progressive tax bands)

5. 💼  PAYROLL EXECUTION FLOW
`
PayRun
 ├── organisation
 ├── startDate, endDate
 ├── status → READY / APPROVED / COMPLETED
 ├── totalGross, totalDeduction, totalNet
 └── [1:N] salarySlips
`
`
SalarySlip
 ├── employee
 ├── assignment → EmployeeSalaryAssignment
 ├── payRun
 ├── fromDate, toDate
 ├── workingDays, paymentDays, lopDays
 ├── grossPay, totalDeductions, netPay
 └── [1:N] components → SalarySlipComponent
`
`
SalarySlipComponent
 ├── salarySlip
 ├── salaryComponent
 ├── amount
 └── calculationLog
`

**🔗 RELATION MATRIX (Compact Overview)**
| Entity                   | Related Entities                                           | Relation Type | Notes                                  |
| ------------------------ | ---------------------------------------------------------- | ------------- | -------------------------------------- |
| Organisation             | SalaryComponent, StatutoryComponent, IncomeTaxSlab, PayRun | 1→N           | Country + org-level scoping            |
| SalaryComponent          | SalaryStructure                                            | M→N           | Components reused across structures    |
| SalaryStructure          | EmployeeSalaryAssignment                                   | 1→N           | One structure → many employees         |
| EmployeeSalaryAssignment | SalarySlip                                                 | 1→N           | Tracks payroll versioning per employee |
| StatutoryComponent       | StatutoryRule                                              | 1→N           | Versioning per effective period        |
| IncomeTaxSlab            | IncomeTaxRule                                              | 1→N           | Defines tax bracket logic              |
| PayRun                   | SalarySlip                                                 | 1→N           | Batch run → multiple slips             |

***6. COUNTRY-AWARE, MULTI-TENANT DESIGN PRINCIPLES**
| Aspect                    | Implementation                                                                                               |
| ------------------------- | ------------------------------------------------------------------------------------------------------------ |
| **Multi-country support** | All master entities (`SalaryComponent`, `StatutoryComponent`, `TaxSlab`) carry `countryCode` and `stateCode` |
| **Org isolation**         | `organisation_id` FK ensures tenant separation                                                               |
| **Effective dates**       | Every rule/slab/version carries `effectiveFrom`/`effectiveTo` for time-based validity                        |
| **Override model**        | Org can override national defaults — e.g. local PF %                                                         |
| **Dynamic computation**   | `formula` and `condition` stored as string (to be parsed by Payroll Engine)                                  |

**⚙️ 7. PAYROLL ENGINE FLOW (Operational Logic)**
Fetch Eligible Employees → active, assigned salary structure
Load Components → from SalaryStructure
Apply Formulas → Evaluate based on base pay, conditions
Apply Statutory Rules → Filter by country/state and pay cycle
Apply Income Tax Slabs → Determine applicable deduction %
Aggregate Results → Gross, Deductions, Employer Contributions
Generate SalarySlip + Components → Persist final computed results
Link to PayRun → for summary & approvals

EXAMPLE DATA SNAPSHOT (India)
StatutoryComponent: "EPF"
 ├─ Rule v1: (Employee 12%, Employer 12%, monthly, ≤ ₹15,000)
 └─ Rule v2: (Employee 12%, Employer 13%, monthly, > ₹15,000)

IncomeTaxSlab FY2025–26:
 ├─ Rule1: 0–2.5L → 0%
 ├─ Rule2: 2.5–5L → 5%
 ├─ Rule3: 5–10L → 20%
 └─ Rule4: 10L+ → 30%



Overview: Payroll Engine Responsibilities
| Step                                     | Description                                      | Handled By                           |
| ---------------------------------------- | ------------------------------------------------ | ------------------------------------ |
| 1️⃣ Fetch active employees & assignments | All active employees in org with valid structure | `EmployeeSalaryAssignmentRepository` |
| 2️⃣ Compute component-wise salary        | Formula or fixed-based                           | Internal helper                      |
| 3️⃣ Compute statutory deductions         | EPF, ESI, etc.                                   | `StatutoryComputationEngine`         |
| 4️⃣ Compute tax (TDS)                    | Annual projection logic                          | `TaxComputationEngine`               |
| 5️⃣ Create salary slip                   | `SalarySlip`, `SalarySlipComponent`              |                                      |
| 6️⃣ Update pay run totals                | Gross, deductions, net                           | `PayRunRepository`                   |
hwy


Relationship Between Base Pay, Components & Assignment
| Layer                     | Entity                     | Purpose                                                                          |
| ------------------------- | -------------------------- | -------------------------------------------------------------------------------- |
| **Employee-specific**     | `EmployeeSalaryAssignment` | Holds employee’s `basePay` and `variablePay` (per-employee control).             |
| **Organisation-specific** | `SalaryStructure`          | Defines the layout — which components exist and how they are calculated.         |
| **Component-specific**    | `SalaryComponent`          | Defines computation rule (formula, type, etc.) and now stores computed `amount`. |

So, the runtime calculation engine will follow this rule:

For each SalaryComponent in the assigned SalaryStructure,
get its amount as follows:
If calculationType = FIXED: use amount (directly entered)
If calculationType = FORMULA: evaluate formula
If calculationType = PERCENTAGE: apply on basePay or referenced component

If base pay not entered itself while assigning salary structure to an employee, a component BASIC is created automatically…
if (assignment.getBasePay() == null) {
    SalaryComponent basic = salaryComponentRepository.findByAbbreviationAndOrganisationId("BASIC", orgId)
        .orElseThrow(() -> new RuntimeException("BASIC component missing for organisation"));
    assignment.setBasePay(basic.getAmount());
}
Every structure must have one anchor earning component, usually BASIC.

All other dependent components (like HRA, PF, etc.) derive from it.

**Formula Evaluation Context (Now Becomes Cleaner)**
| Variable      | Source                                                   |
| ------------- | -------------------------------------------------------- |
| `BASE`        | `assignment.getBasePay()` or `component("BASIC").amount` |
| `COMP:<ABBR>` | computed component’s `amount`                            |
| `ORG:...`     | organisation configuration (optional future feature)     |
| `VARPAY`      | `assignment.getVariablePay()`                            |

{
  "BASE": 40000,
  "VARPAY": 5000,
  "COMP:BASIC": 40000,
  "COUNTRY": "IN",
  "STATE": "MH"
}

COMP:BASIC * 0.4

**9️⃣ To Summarize Your Decision (and Why It’s Great)**
| Design Decision                                        | Benefit                                                  |
| ------------------------------------------------------ | -------------------------------------------------------- |
| Added `amount` field in `SalaryComponent`              | Allows direct fixed values and persists computed results |
| Auto-create BASIC if basePay missing                   | Keeps payroll consistent                                 |
| Use `EmployeeSalaryAssignment.basePay` per employee    | Enables employee-level salary differentiation            |
| Reference dependencies via abbreviation (`COMP:BASIC`) | Clean, readable, country-agnostic formulas               |
| Context-aware formula evaluation                       | Consistent across all payroll runs                       |
| Clear user guidance and validation                     | Prevents errors in configuration                         |


🧩 1️⃣ Overview of the Computation Flow
Step-by-step process every time payroll runs for an organisation:
| Phase                     | Description                                                                     |
| ------------------------- | ------------------------------------------------------------------------------- |
| 1️⃣ Gather Data           | Load all active employees + their `EmployeeSalaryAssignment` + salary structure |
| 2️⃣ Build Context         | Add base pay, variable pay, country, state, etc. to evaluation context          |
| 3️⃣ Evaluate Components   | Use `FormulaExpressionEvaluator` to compute each component amount               |
| 4️⃣ Compute Totals        | Gross = sum(earnings), Deductions = sum(deductions), Net = Gross − Deductions   |
| 5️⃣ Apply Tax & Statutory | Invoke respective engines (placeholders for now)                                |
| 6️⃣ Generate Salary Slip  | Create `SalarySlip` and `SalarySlipComponent` entries                           |
| 7️⃣ Persist + Return DTOs | Save all data and return results for display/export                             |


🧱 4️⃣ Placeholder Hooks (Phase 2)

When we extend this engine, we’ll plug in:
TaxComputationEngine.applyTaxDeductions(slipDTO, context)
StatutoryComputationEngine.applyContributions(slipDTO, context)
Those will use the same evaluator + context for contribution rules.
We’ll integrate the TaxComputationEngine next (using the same formula evaluator).

Then StatutoryComputationEngine (EPF, ESI, SSS etc., country-aware).

Then optionally build a UI-guided “Formula Builder” using the hints and syntax we discussed.

**🧠 2️⃣ In Data Model Terms**
Let’s map this understanding to your entities.
| Entity                        | Purpose                                      |
| ----------------------------- | -------------------------------------------- |
| `SalaryComponent`             | Logical definition of any earning/deduction  |
| `SalaryStructure`             | Groups components for a structure            |
| `EmployeeSalaryAssignment`    | Binds structure + base pay to employee       |
| `PayRun`                      | Tracks each payroll cycle                    |
| `SalarySlip`                  | Per-employee per-run result                  |
| `SalarySlipComponent`         | Breakdown of each earning/deduction          |
| `StatutoryComponent` + `Rule` | Defines country/state-specific PF, ESI, etc. |
| `IncomeTaxSlab` + `Rule`      | Defines income tax logic                     |

So, from a database perspective, everything (even taxes) ends up as a SalarySlipComponent
— because that’s how the employee sees their pay.

🧩 3️⃣ Runtime Integration Flow (Execution Layer)

Here’s how it all fits together during computation 👇

🔹 Step 1 – Payroll Engine

Loads structure components.

Computes fixed/formula/percentage earnings and deductions.

Produces preliminary gross and deductions (before tax/statutory).

Passes control to Tax & Statutory Engines.

🔹 Step 2 – StatutoryComputationEngine

For the given org + employee + country/state:

Finds applicable statutory rules (EPF, ESI, etc.).

Computes employer/employee contributions.

Returns a list of computed deductions (e.g., { name: "EPF", amount: 4800 }).

These are injected into SalarySlipComponent as type DEDUCTION.

🔹 Step 3 – TaxComputationEngine

Reads gross annualized income → applies tax slab logic.

Calculates monthly TDS.

Returns another deduction component { name: "TDS", amount: 1200 }.

🔹 Step 4 – Finalization

The payroll engine aggregates all deductions:

totalDeductions = manualDeductions + statutory + tax
netPay = gross - totalDeductions


Inserts all into SalarySlipComponent.
✅ So even though the logic lives in separate engines,
the final persistence model unifies everything under payslip deductions.

🧱 4️⃣ Example Data Flow (India)


5️⃣ Architectural Integration — Engine Chaining
In code terms, your final PayrollCalculationServiceImpl pipeline will look like this:
SalarySlipDTO slip = basePayrollEngine.compute(assignment, payRun);
List<SalarySlipComponentDTO> statutoryDeductions =
    statutoryComputationEngine.applyStatutoryDeductions(slip, context);

List<SalarySlipComponentDTO> taxDeductions =
    taxComputationEngine.applyTaxDeductions(slip, context);
// Merge all components into one list
slip.getComponents().addAll(statutoryDeductions);
slip.getComponents().addAll(taxDeductions);

// Recompute totals
slip = calculateTotals(slip);
