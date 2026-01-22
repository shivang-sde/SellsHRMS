UNIVERSAL STRUCTURE

All work types share this generic structure:

Project
 ├── Epics        (Feature-level or theme)
 │    ├── Sprints  (Iteration or cycle)
 │    │    └── Tasks       (Units of work)
 │    │          └── Subtasks
 │    └── Milestones (Checkpoints / Deliverables)

Now, how that hierarchy behaves changes depending on the work type.

🧠 PER-WORK-TYPE MAPPING

🧱 1️⃣ Software Development

Goal: Build software products using Agile or Hybrid methodologies.

| Level         | Purpose                | Example                      |
| ------------- | ---------------------- | ---------------------------- |
| **Project**   | Full product or module | "HRMS Platform"              |
| **Epic**      | Major feature          | "Leave Management Module"    |
| **Sprint**    | 2-week dev cycle       | "Sprint 1 – Build Leave API" |
| **Task**      | Dev/Test item          | "Implement Leave Controller" |
| **Milestone** | Release checkpoint     | "Alpha Release Completed"    |

💡 Methodology: AGILE, SCRUM, or KANBAN


💻 2️⃣ IT Support
Goal: Manage incidents, requests, and maintenance tasks.

| Level         | Purpose                     | Example                             |
| ------------- | --------------------------- | ----------------------------------- |
| **Project**   | Service desk or system area | "Employee Helpdesk"                 |
| **Epic**      | Issue category              | "Email Setup Issues"                |
| **Sprint**    | Weekly support rotation     | "Support Week #2"                   |
| **Task**      | Ticket/Request              | "Reset Outlook password for Rakesh" |
| **Milestone** | SLA performance review      | "January SLA Report"                |

Methodology: KANBAN or LEAN (continuous flow)

🎨 3️⃣ Design
Goal: Manage creative and design deliverables.

| Level         | Purpose                           | Example                  |
| ------------- | --------------------------------- | ------------------------ |
| **Project**   | Creative campaign or brand design | "Rebranding 2026"        |
| **Epic**      | Asset type                        | "Logo Redesign"          |
| **Sprint**    | Review cycle                      | "Design Review Sprint 1" |
| **Task**      | Asset task                        | "Design logo mockups"    |
| **Milestone** | Delivery or approval              | "Brand kit approved"     |

💡 Methodology: SCRUM or WATERFALL

🤝 4️⃣ Customer Service
Goal: Track service requests and improvements.
| Level         | Purpose           | Example                    |
| ------------- | ----------------- | -------------------------- |
| **Project**   | Support operation | "Customer Feedback Portal" |
| **Epic**      | Complaint type    | "Refund Requests"          |
| **Sprint**    | Service cycle     | "Week 3 Support Cycle"     |
| **Task**      | Customer issue    | "Resolve complaint #123"   |
| **Milestone** | Service metric    | "Achieved 95% SLA for Q1"  |

Methodology: LEAN or KANBAN

🧍‍♀️ 5️⃣ HR
Goal: Track HR workflows and initiatives.

| Level         | Purpose        | Example                    |
| ------------- | -------------- | -------------------------- |
| **Project**   | HR process     | "New Employee Onboarding"  |
| **Epic**      | Sub-process    | "Document Verification"    |
| **Sprint**    | Timeline phase | "Week 1: Welcome + Setup"  |
| **Task**      | Activity       | "Collect ID proofs"        |
| **Milestone** | HR checkpoint  | "Employee Fully Onboarded" |

Methodology: WATERFALL or LEAN

📢 6️⃣ Marketing
Goal: Manage campaigns, assets, and performance tracking.

| Level         | Purpose     | Example                         |
| ------------- | ----------- | ------------------------------- |
| **Project**   | Campaign    | "Summer Hiring Campaign"        |
| **Epic**      | Channel     | "LinkedIn Ads"                  |
| **Sprint**    | Time period | "Campaign Phase 1 (Jan)"        |
| **Task**      | Action item | "Design LinkedIn carousel"      |
| **Milestone** | Goal metric | "1000 qualified leads achieved" |


💡 Methodology: AGILE or SCRUM

⚙️ 7️⃣ Operations

Goal: Manage day-to-day company processes.
| Level         | Purpose             | Example                     |
| ------------- | ------------------- | --------------------------- |
| **Project**   | Operational domain  | "Payroll Operations"        |
| **Epic**      | Process             | "Salary Disbursement"       |
| **Sprint**    | Month or cycle      | "January Payroll Cycle"     |
| **Task**      | Step or check       | "Generate payslips"         |
| **Milestone** | Periodic completion | "Payroll Completed for Jan" |

Methodology: LEAN or WATERFALL

💰 8️⃣ Finance

Goal: Handle budgeting, reports, and expenses.
| Level         | Purpose           | Example                     |
| ------------- | ----------------- | --------------------------- |
| **Project**   | Financial process | "Q1 Budget Planning"        |
| **Epic**      | Section           | "Departmental Budgets"      |
| **Sprint**    | Review window     | "Finance Review Week 1"     |
| **Task**      | Work item         | "Prepare HR cost breakdown" |
| **Milestone** | Goal              | "Q1 budget finalized"       |

💡 Methodology: WATERFALL or LEAN

🧾 9️⃣ Sales
Goal: Track pipelines, targets, and customer deals.
| Level         | Purpose        | Example                         |
| ------------- | -------------- | ------------------------------- |
| **Project**   | Sales campaign | "Corporate Subscriptions Drive" |
| **Epic**      | Lead category  | "Enterprise Clients"            |
| **Sprint**    | Sales period   | "Week 1 Target Cycle"           |
| **Task**      | Sales action   | "Demo call with ABC Corp"       |
| **Milestone** | Goal           | "Closed 5 enterprise deals"     |

Methodology: SCRUM or KANBAN
🧍 10️⃣ Other / Personal
Goal: Track personal or ad-hoc tasks.
| Level         | Purpose       | Example               |
| ------------- | ------------- | --------------------- |
| **Project**   | Personal goal | "Fitness Plan"        |
| **Epic**      | Sub-goal      | "Diet Management"     |
| **Sprint**    | Time period   | "Week 1 Routine"      |
| **Task**      | Daily task    | "Workout 30 min"      |
| **Milestone** | Achievement   | "Lost 2kg in 1 month" |

Methodology: OTHER (Flexible)
Project (Top-level initiative)
 ├── Epic (Feature / Category / Sub-process)
 │    ├── Sprint (Cycle / Iteration / Phase)
 │    │    ├── Task (Action item / Unit of work)
 │    │    │    └── SubTask (Detailed breakdown)
 │    └── Milestone (Goal / Deliverable / Achievement)


⚙️ In Your HRMS Context

This hierarchy makes your Work Management module universal:
Same core model works for any department.
You can adapt based on ProjectType:

```public enum ProjectType {
    SOFTWARE_DEVELOPMENT, IT_SUPPORT, DESIGN,
    CUSTOMER_SERVICE, HR, MARKETING, OPERATIONS,
    SALES, FINANCE, OTHER_PERSONAL
}```

You can toggle features:

Software → show Sprint/Epic

HR → show Milestone

IT Support → skip Epic/Sprint, just Project + Task

Marketing → show Epic (Channel), Milestone (Goal)