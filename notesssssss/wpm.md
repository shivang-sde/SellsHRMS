**1️⃣ Core Entities & Their Roles**

| Entity             | Purpose                                                                                                                                                                                      |
| ------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Project**        | The top-level container for work. Represents initiatives in your HRMS (e.g., Payroll Module, Summer Campaign). Has metadata: type, methodology, status, priority, dates, manager, team lead. |
| **ProjectMember**  | Tracks employees involved in a project. Holds role, allocation, join/leave dates. Essential for resource management and audit.                                                               |
| **Epic**           | Higher-level work chunk within a project (Agile “feature” or initiative). Contains multiple **Tasks**.                                                                                       |
| **Milestone**      | Defines major checkpoints within a project. Tasks can be linked to milestones for tracking completion.                                                                                       |
| **Sprint**         | Time-boxed iteration (Agile/Scrum). Tasks can belong to sprints.                                                                                                                             |
| **Task**           | The main work unit. Can belong to Project, Epic, Sprint, Milestone. Can have sub-tasks. Has assignee, reporter, status, priority, due dates, and metrics.                                    |
| **TaskComment**    | Comments by employees on a task (discussion thread).                                                                                                                                         |
| **TaskAttachment** | Files associated with a task or comment (docs, images, etc.).                                                                                                                                |
| **TaskLabel**      | Labels/tags to categorize tasks. Many-to-many with Task.                                                                                                                                     |
| **TaskActivity**   | Audit/logging entity. Tracks all actions performed on tasks: status change, assignee change, comments, attachments, sprint/milestone linking.                                                |


Entity Relationships (Logical)

Project
 ├─ ProjectMember (1 project ↔ many employees)
 ├─ Task (independent or linked to Epic/Sprint/Milestone)
 ├─ Epic ─ Task
 ├─ Sprint ─ Task
 └─ Milestone ─ Task

Task
 ├─ SubTasks (Task ↔ Task)
 ├─ TaskComment ─ TaskAttachment
 ├─ TaskAttachment
 ├─ TaskLabel (Many-to-Many)
 └─ TaskActivity (audit trail)


Key points:

Task is central — almost everything connects here.

ProjectMember links employees to Project → optional for task assignment (can enforce that only members are assigned tasks).

Epic, Sprint, Milestone → containers/organizers of tasks.

TaskActivity logs everything for traceability.

TaskAttachment and TaskComment → auxiliary but linked to tasks for collaboration.

3️⃣ Business Logic Flow

Let’s go step by step through typical project workflows:
3.1 Project Creation

Admin/Manager creates a Project.

Assigns:

projectManager and projectTeamLead

ProjectType and Methodology

Start/end dates, priority, and status (PLANNING by default)

Optionally adds ProjectMembers with:

Role (Developer, QA, etc.)

Allocation %

Join date

Business rules:
Only employees in the organization can be added as members.
Cannot activate project without at least one manager or lead.

3.2 Epic & Milestone Setup

Create Epic within a project to define features/modules.

Create Milestone to mark checkpoints.

Business rules:

Epic is optional for non-Agile projects.
Milestones can exist without tasks initially.
Task linking to Epic/Milestone automatically aggregates progress.

3 Sprint Planning

Sprints exist only in Agile/Scrum projects.

Tasks can be assigned to a sprint.

Sprint has status: PLANNED, ACTIVE, COMPLETED, CANCELLED.

Business rules:
Only active project members can be assigned tasks.
Start/End date of sprint should be within project’s start/end dates.

3.4 Task Lifecycle

Creation

Assign:
assignee (employee)
reporter (creator)
Optional: Epic, Sprint, Milestone
Automatically logs TASK_CREATED in TaskActivity.

Updating
Changing fields triggers TaskActivity (status change, priority, assignee, links to epic/milestone/sprint).
Can attach comments or attachments.

Sub-tasks
parentTask establishes hierarchy.
Progress aggregation possible (sum of sub-tasks’ completion for parent task).
Completion
Task moves to DONE (or equivalent) → triggers activity log.
Can automatically update Milestone progress if all tasks linked are complete.

3.5 Comments & Attachments

Comments:
Added by employees assigned to the task or project members.
Can contain multiple attachments.
Each addition logged in TaskActivity as COMMENT_ADDED.

Attachments:
Can be attached directly to task or comment.
Logged in TaskActivity as ATTACHMENT_UPLOADED.

3.6 Activity Logging (Audit)

Every significant action on a task is logged in TaskActivity:
Task created/updated
Status/priority changed
Assignee changed
Sub-task created
Comment added
Attachment uploaded
Links to Epic/Sprint/Milestone
Optionally stores oldValue and newValue for reporting.

3.7 Reporting & Queries

Typical queries:
Active tasks per project / per employee.
Tasks by status, priority, milestone, or sprint.
Project progress % (tasks completed / total tasks).
Sprint progress (tasks in ACTIVE/REVIEW/DONE).

Resource utilization (via ProjectMember.allocationPercentage and tasks assigned).
Audit logs (TaskActivity) for compliance and reporting.

4️⃣ Multi-Tenancy Considerations

Organisation exists in almost all entities (Project, ProjectMember, Task).
Every query should filter by organisation_id to ensure tenant isolation.
ProjectMember helps enforce that only users in a project (and org) can access tasks.

5️⃣ Entity Access / CRUD Logic

Repositories should support:
ProjectRepository
Find by org, status, type, projectManager, projectMember
ProjectMemberRepository
Find members by project, role, employee
EpicRepository
Tasks linked to Epic
MilestoneRepository
Tasks linked to Milestone
SprintRepository
Tasks in sprint
TaskRepository
Filter by project, assignee, reporter, status, milestone, sprint, epic
TaskActivityRepository
Filter by task, employee, activity type, org

TaskCommentRepository
Comments per task
TaskAttachmentRepository
Attachments per task or comment
TaskLabelRepository
Tasks per label

6️⃣ Summary of Business Logic Flow

Project Setup
Project → Members, Epics, Milestones, Sprints
Task Creation

Task → links to Epic/Sprint/Milestone, assigns assignee
Logs activity
Task Updates
Status, priority, assignee changes → log activity
Comments and attachments → log activity

Progress Tracking
Epic / Milestone / Sprint track progress from tasks
Project tracks overall completion

Resource & Audit
ProjectMember → role and allocation
TaskActivity → action audit trail
Reporting
Filter by organization, project, employee, milestone, sprint, status, etc.

✅ Conclusion

Project = container for all work
ProjectMember = resource + role + allocation
Epic / Milestone / Sprint = organizing structures
Task = work unit
TaskComment / TaskAttachment = collaboration
TaskActivity = audit trail
Together, they form a flexible, scalable multi-tenant HRMS work management module with full traceability and reporting.



🧩 1️⃣ What’s the purpose of “Project Members”?

The ProjectMember entity isn’t just a join table — it defines who is authorized to participate in that project and what role they play.

So when a project is created:

You (the creator / project manager) can invite or add employees into ProjectMember.

This list becomes the authorization boundary for all future work in that project.

Think of it like:

“Only people on the project team can create, assign, or comment on tasks within this project.”

🧠 2️⃣ Why not allow everyone in the organisation to do project tasks?

Because in a multi-tenant HRMS, you’ll have hundreds of employees in one org.
Allowing everyone to touch every task would:

Make data chaotic (no clear ownership)

Break access control

Hurt reporting (“who’s working on what?”)

Violate confidentiality between departments (e.g. HR vs Finance vs IT Support)

Hence, ProjectMember acts as the permission boundary.

⚙️ 3️⃣ So what’s the right business rule?
✅ Golden Rule

Only employees who are listed as ProjectMember can:
Be assigned to a task (assignee)
Be a task reporter (creator)
Comment, upload attachments, or update tasks within that project

🧾 4️⃣ Roles — Do we need them?
Yes — and here’s why.
Different people perform different functions within a project:
| Role                      | Typical Permissions                                 |
| ------------------------- | --------------------------------------------------- |
| **Project Manager**       | Create/edit project, manage members, close project  |
| **Team Lead**             | Create/edit tasks, assign tasks to team members     |
| **Developer/Contributor** | Work on assigned tasks, comment, upload attachments |
| **QA/Reviewer**           | Change task status (e.g., “In Review”, “Done”)      |
| **Viewer/Stakeholder**    | Read-only access, no modifications                  |


5️⃣ How to enforce this in your logic layer
In your Service Layer / TaskServiceImpl

When someone tries to:

Create a task

Update a task

Comment on a task

Upload an attachment

You check:

if (!projectMemberRepository.existsByProjectIdAndEmployeeIdAndIsActiveTrue(task.getProject().getId(), currentUser.getId())) {
    throw new AccessDeniedException("You are not a member of this project");
}




