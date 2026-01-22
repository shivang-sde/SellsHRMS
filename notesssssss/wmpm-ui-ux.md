🧩 OVERVIEW
Work Management Module in HRMS

The module will be a universal, methodology-aware system that can adapt to:

Agile/Scrum (Epics, Sprints, Tasks, Subtasks, Milestones)
Kanban (Continuous flow boards)
Waterfall (Sequential Gantt-like projects)
Ad-hoc / Independent Tasks (personal or manager-assigned)

⚙️ CORE UX STRUCTURE
| Section                         | Purpose                                           | Main UI Components              |
| ------------------------------- | ------------------------------------------------- | ------------------------------- |
| **1. Dashboard**                | Unified overview of all projects & tasks          | Cards, Charts, Filters          |
| **2. Project Management**       | Create & manage projects (per department or type) | Project List + Detail View      |
| **3. Task Management**          | Task boards, lists, timelines                     | Kanban board / Table view       |
| **4. Sprint & Epic Management** | Agile/Iterative workflows                         | Sprint Planning Board           |
| **5. Milestone Tracking**       | Goal checkpoints                                  | Timeline view / Milestone chart |
| **6. Task Collaboration**       | Comments, attachments, subtasks                   | Comment thread, File viewer     |
| **7. My Tasks (Personal)**      | Employee’s own assignments                        | To-do board, Filters, Quick add |
| **8. Reports**                  | Team performance and workload                     | Charts, Filters, Summaries      |


***DASHBOARD FLOW***
For all employees:

Components:
🔹 My Tasks Summary
Pie chart → To Do / In Progress / Review / Done
“Quick Add Task” button
🔹 Active Projects
List or card grid showing user’s involved projects
🔹 Upcoming Deadlines
Tasks due soon
🔹 Team Activity Feed
Recent comments, attachments, or task updates
🔹 Filter by Department / Project Type / Role

**🏗️ 2️⃣ PROJECT MANAGEMENT FLOW**

A. Project List View

Sidebar Filters:

Methodology (Agile, Kanban, Waterfall, etc.)

Department

Status

Manager

Main Table Columns:
| Project | Type | Methodology | Manager | Department | Progress | Status | Actions |
|----------|-------|--------------|----------|-------------|-----------|----------|
| HRMS Platform | Software Development | Scrum | John Doe | IT | 65% | In Progress | View / Edit|

Actions:

➕ Create Project (opens modal / form)
📋 Export CSV / Excel
🧩 Filter by ProjectType (Software, HR, Sales, etc.)

B. Project Detail Page (Dynamic Layout)
The project detail adjusts based on ProjectMethodology.
| Methodology                     | Primary Views                 | Example UI                                                         |
| ------------------------------- | ----------------------------- | ------------------------------------------------------------------ |
| **Agile / Scrum**               | Epics → Sprints → Tasks Board | Jira-style board with drag-drop columns (To Do, In Progress, Done) |
| **Kanban**                      | Continuous Board              | ClickUp-style status lanes                                         |
| **Waterfall / Sequential**      | Gantt Chart Timeline          | MS Project-style timeline                                          |
| **Ad-hoc / Continuous**         | Simple Task List              | Table view with quick add                                          |
| **Process-based (Ops/Support)** | Ticket queue                  | Table with SLA and tags                                            |



TASK MANAGEMENT FLOW

Tasks are the heart of everything.
The UI will have three interchangeable views:
🧱 Board View (Kanban) → for Agile teams
📋 List View → for HR / Finance sequential tasks
📆 Calendar View → for Ops / Support & HR events

A. Task Board (Kanban)
Columns = TaskStatus (BACKLOG, TO_DO, IN_PROGRESS, REVIEW, DONE)
Cards show:
Title, Assignee avatar, Priority color, Due date
Drag-drop to update status
Right-click → “Add Subtask” or “Log Time”

Task Card Hover Actions
| Icon | Action                       |
| ---- | ---------------------------- |
| 🧩   | View Epic/Sprint association |
| 💬   | Open comments                |
| 📎   | Attach file                  |
| ⏱    | Log work time                |


Task Detail Modal (Reusable Everywhere)
Clicking a task opens a right-side modal (like Jira or ClickUp):
Tabs:
Overview
Title, Description, Priority, Status
Reporter / Assignee (dropdowns)
Related Epic/Sprint/Milestone
Subtasks
Nested list with quick checkboxes
Comments
Threaded discussion
Inline file attachments
Attachments
File thumbnails / download links
Activity
Timeline of changes (from TaskActivity entity)

C. Creating / Assigning Tasks

| Scenario                            | Supported? | Example                                      |
| ----------------------------------- | ---------- | -------------------------------------------- |
| Employee creates personal task      | ✅          | “Prepare appraisal self-review” (no project) |
| Manager assigns task to team member | ✅          | “Design payslip layout” assigned to Designer |
| Task created by HR, assigned to IT  | ✅          | “Set up new employee’s laptop”               |
| Reporter ≠ Assignee ≠ Reviewer      | ✅          | HR creates task, IT executes, Admin reviews  |
| Subtask under parent                | ✅          | “Write unit tests” under “Implement API”     |

The system will handle all through your entity relationships (project, reporter, assignee, parentTask).

.

🚀 4️⃣ EPIC, SPRINT, & MILESTONE FLOW
Epics
Represent large initiatives (e.g., “Payroll Module”)
Have sub-view showing related sprints & tasks
Visual progress bar = completed tasks / total tasks
Sprints
Belong to a project (Scrum only)
Sprint board → “Active Sprint” view with backlog, story points
Create / Close sprint button
Milestones
Appear on timeline
Tasks can be linked → milestone completion triggers progress update

USER ROLES & ACCESS LOGIC 
| Role                        | Permissions         | Typical Actions                                 |
| --------------------------- | ------------------- | ----------------------------------------------- |
| **Admin / Project Manager** | Full CRUD           | Create projects, assign members, set priorities |
| **Team Lead**               | Manage team tasks   | Assign tasks, update progress                   |
| **Member**                  | Own tasks           | Update status, comment, upload attachments      |
| **Viewer / HR**             | Read-only           | View task/project progress                      |
| **Individual Contributor**  | Personal tasks only | Manage own To-Do list                           |

💡 6️⃣ INDEPENDENT / PERSONAL TASKS FLOW
Sometimes, employees create personal tasks not tied to any project.

Flow:
From dashboard → “My Tasks”
“+ New Task” → opens simplified form:
Title, Description, Priority, Due Date

Stored with:
project = null
reporter = employee
assignee = employee

Displayed in:
Personal board (To Do, In Progress, Done)
No epics/sprints visible

Still supports:
Attachments, comments, subtasks
This makes it universal — same backend, flexible UI.

📎 7️⃣ ATTACHMENT UX FLOW

When adding a file:
User clicks “📎 Attach file”
FileUploadHelper is triggered
Preview appears inline (image/pdf)
Metadata (file type, uploader, upload time)
System handles both user and system-generated uploads

📊 8️⃣ REPORTS & ANALYTICS
By Project:
Tasks by status (bar chart)
Workload by employee (heatmap)
Burn-down chart for Scrum

By Department:
Active vs completed projects
Time to completion trends

By Employee:
Task efficiency
Overdue vs on-time tasks

EDGE CASE FLOWS
| Case                           | Behavior                                                                  |
| ------------------------------ | ------------------------------------------------------------------------- |
| Project deleted → tasks remain | Tasks become “orphaned” with `project=null` but `organisationId` retained |
| Employee leaves org            | Their tasks auto-unassigned or reassigned to PM                           |
| Multi-department collaboration | Shared project with members across departments                            |
| Temporary contractors          | Limited task visibility only for assigned projects                        |
| Milestone delayed              | Auto-update `status=DELAYED`, notify PM                                   |

TECHNICAL FRONT-END WIRING (if JSP + REST)
| Page                  | REST Endpoint        | Description                             |
| --------------------- | -------------------- | --------------------------------------- |
| `/work/projects`      | `/api/projects`      | Project list                            |
| `/work/projects/{id}` | `/api/projects/{id}` | Project detail (with tasks)             |
| `/work/tasks`         | `/api/tasks`         | Task list / personal board              |
| `/work/tasks/{id}`    | `/api/tasks/{id}`    | Task detail modal                       |
| `/work/sprints`       | `/api/sprints`       | Sprint management                       |
| `/work/milestones`    | `/api/milestones`    | Milestone tracking                      |
| `/work/comments`      | `/api/comments`      | Task collaboration                      |
| `/work/attachments`   | `/api/attachments`   | File uploads (using `FileUploadHelper`) |

