🧱 Notes on Future Integration (Phase 2)

When you’re ready to add EmployeeTaxDeclaration and EmployeeTaxProof:

Replace getProvisionalDeclarations() with:
EmployeeTaxDeclaration decl = declarationRepo.findActiveForYear(employee.getId(), fiscalYear);
double declared = decl != null ? decl.getApprovedOrDeclaredAmount() : 0.0;
return declared;
Recompute taxable income with real proof data once verified.

Add caching of monthly TDS across pay runs.

Key Design Points

Fully multi-country, multi-tenant via countryCode + organisationId

Easy extension for flat-rate countries or progressive slabs

Plug-and-play integration with payroll engine (the one you built earlier)

Phase-2 ready for investment proofs, rebates, rebalance mid-year


Example Calculation
| Parameter                                    | Value                       |
| -------------------------------------------- | --------------------------- |
| Gross monthly                                | ₹80,000                     |
| Annual gross                                 | ₹9,60,000                   |
| Standard deduction                           | ₹50,000                     |
| Provisional investment (phase-2 placeholder) | ₹1,50,000                   |
| Taxable income                               | ₹7,60,000                   |
| Tax rules                                    | 5% (2.5–5L), 10% (5–10L)    |
| Annual tax                                   | ₹10,000 + ₹26,000 = ₹36,000 |
| Monthly TDS                                  | ₹3,000                      |


👨‍💼 Example 2: Philippine Employee

| Parameter          | Value                                 |
| ------------------ | ------------------------------------- |
| Country            | `PH`                                  |
| Gross monthly      | ₱40,000                               |
| Annual gross       | ₱480,000                              |
| Standard exemption | ₱90,000                               |
| Taxable            | ₱390,000                              |
| TRAIN law brackets | ₱250k → 0%, next ₱140k @15% = ₱21,000 |
| Monthly TDS        | ₱1,750                                |
