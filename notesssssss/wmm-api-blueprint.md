BASE ROUTE 

All APIs will live under:
/api/work

Submodules:
/api/work/projects
/api/work/tasks
/api/work/epics
/api/work/sprints
/api/work/milestones
/api/work/members
/api/work/comments
/api/work/attachments
/api/work/activities
/api/work/labels


Each API will:
Be scoped to organisationId
Use consistent JSON response format

**2️⃣ PROJECT MANAGEMENT** 
| UI Screen          | Backend Endpoint                                                  | DTO                 | Notes                           |
| ------------------ | ----------------------------------------------------------------- | ------------------- | ------------------------------- |
| Project List       | `/api/work/projects`                                              | ProjectDTO          | Filter by type/status           |
| Project Detail     | `/api/work/projects/{id}`                                         | ProjectDTO          | Include members/tasks summary   |
| Task Board         | `/api/work/tasks?projectId={id}`                                  | TaskDTO[]           | For Kanban view                 |
| Task Detail        | `/api/work/tasks/{id}`                                            | TaskDTO             | Includes comments & attachments |
| Add Comment        | `/api/work/tasks/{id}/comments`                                   | TaskCommentDTO      | Inline replies                  |
| Upload File        | `/api/work/attachments/upload`                                    | TaskAttachmentDTO   | FileUploadHelper handles        |
| Epic/Sprint Boards | `/api/work/projects/{id}/epics` `/api/work/projects/{id}/sprints` | EpicDTO / SprintDTO | For Agile view                  |
| Milestone Timeline | `/api/work/projects/{id}/milestones`                              | MilestoneDTO        | For Waterfall tracking          |
| My Tasks           | `/api/work/my-tasks`                                              | TaskDTO             | Personal to-do list             |
| Activity Log       | `/api/work/tasks/{id}/activities`                                 | TaskActivityDTO     | Audit history                   |

DTO Used: ProjectDTO

**PROJECT MEMBERS (Team)**
| Method     | Endpoint                                  | Description                          | Payload / Params   |
| ---------- | ----------------------------------------- | ------------------------------------ | ------------------ |
| **GET**    | `/api/work/projects/{projectId}/members`  | Get all project members              | `orgId`            |
| **POST**   | `/api/work/projects/{projectId}/members`  | Add member                           | `ProjectMemberDTO` |
| **PUT**    | `/api/work/projects/members/{memberId}`   | Update member details or role        | `ProjectMemberDTO` |
| **DELETE** | `/api/work/projects/members/{memberId}`   | Remove (soft delete) member          | `orgId`            |
| **GET**    | `/api/work/members/employee/{employeeId}` | Get all projects employee belongs to | `orgId`            |
DTO Used: ProjectMemberDTO


***4️⃣ EPICS***
| Method     | Endpoint                               | Description                   | Payload / Params |
| ---------- | -------------------------------------- | ----------------------------- | ---------------- |
| **GET**    | `/api/work/projects/{projectId}/epics` | List epics for a project      | `orgId`          |
| **GET**    | `/api/work/epics/{id}`                 | Get epic details (with tasks) | `orgId`          |
| **POST**   | `/api/work/projects/{projectId}/epics` | Create new epic               | `EpicDTO`        |
| **PUT**    | `/api/work/epics/{id}`                 | Update epic info              | `EpicDTO`        |
| **DELETE** | `/api/work/epics/{id}`                 | Delete epic                   | `orgId`          |

DTO Used: EpicDTO

**5️⃣ SPRINTS**
| Method     | Endpoint                                 | Description                | Payload / Params |
| ---------- | ---------------------------------------- | -------------------------- | ---------------- |
| **GET**    | `/api/work/projects/{projectId}/sprints` | List sprints for a project | `orgId`          |
| **GET**    | `/api/work/sprints/{id}`                 | Get sprint detail          | `orgId`          |
| **POST**   | `/api/work/projects/{projectId}/sprints` | Create sprint              | `SprintDTO`      |
| **PUT**    | `/api/work/sprints/{id}`                 | Update sprint              | `SprintDTO`      |
| **DELETE** | `/api/work/sprints/{id}`                 | Delete sprint              | `orgId`          |
| **POST**   | `/api/work/sprints/{id}/complete`        | Mark sprint completed      | `orgId`          |

DTO Used SprintDTO;

6️⃣ MILESTONES
| Method     | Endpoint                                    | Description           | Payload / Params |
| ---------- | ------------------------------------------- | --------------------- | ---------------- |
| **GET**    | `/api/work/projects/{projectId}/milestones` | List milestones       | `orgId`          |
| **GET**    | `/api/work/milestones/{id}`                 | Get milestone details | `orgId`          |
| **POST**   | `/api/work/projects/{projectId}/milestones` | Create milestone      | `MilestoneDTO`   |
| **PUT**    | `/api/work/milestones/{id}`                 | Update milestone      | `MilestoneDTO`   |
| **DELETE** | `/api/work/milestones/{id}`                 | Delete milestone      | `orgId`          |

DTO Used MilestoneDTO

TASKS (Universal — handles independent & project tasks)
| Method     | Endpoint                        | Description                                             | Payload / Params                                         |
| ---------- | ------------------------------- | ------------------------------------------------------- | -------------------------------------------------------- |
| **GET**    | `/api/work/tasks`               | List tasks for organisation (supports filters)          | `orgId`, `projectId`, `assigneeId`, `status`             |
| **GET**    | `/api/work/tasks/{id}`          | Get task details (with comments, attachments, subtasks) | `orgId`                                                  |
| **POST**   | `/api/work/tasks`               | Create task                                             | `TaskDTO` (supports null `projectId` for personal tasks) |
| **PUT**    | `/api/work/tasks/{id}`          | Update task                                             | `TaskDTO`                                                |
| **DELETE** | `/api/work/tasks/{id}`          | Soft delete                                             | `orgId`                                                  |
| **POST**   | `/api/work/tasks/{id}/status`   | Change status only                                      | `{status: "IN_PROGRESS"}`                                |
| **POST**   | `/api/work/tasks/{id}/subtasks` | Create subtask                                          | `TaskDTO`                                                |
| **GET**    | `/api/work/tasks/{id}/subtasks` | List subtasks                                           | `orgId`                                                  |
DTO Used: TaskDTO

8️⃣ COMMENTS
| Method     | Endpoint                            | Description               | Payload / Params |
| ---------- | ----------------------------------- | ------------------------- | ---------------- |
| **GET**    | `/api/work/tasks/{taskId}/comments` | Get all comments for task | `orgId`          |
| **POST**   | `/api/work/tasks/{taskId}/comments` | Add new comment           | `TaskCommentDTO` |
| **PUT**    | `/api/work/comments/{id}`           | Edit comment              | `TaskCommentDTO` |
| **DELETE** | `/api/work/comments/{id}`           | Delete comment            | `orgId`          |
DTO Used: CommentsDTO

9️⃣ ATTACHMENTS
| Method     | Endpoint                                          | Description                                       | Payload / Params                                       |
| ---------- | ------------------------------------------------- | ------------------------------------------------- | ------------------------------------------------------ |
| **POST**   | `/api/work/attachments/upload`                    | Upload files (task, comment, subtask, or generic) | Multipart + query: `orgId`, `contextType`, `contextId` |
| **GET**    | `/api/work/attachments/{contextType}/{contextId}` | Get all attachments for given entity              | `contextType = task/comment`                           |
| **DELETE** | `/api/work/attachments/{id}`                      | Delete attachment                                 | `orgId`                                                |

DTO Used: AttachementsDTO

**🔟 TASK ACTIVITY / AUDIT LOG**
| Method  | Endpoint                                    | Description                        | Payload / Params |
| ------- | ------------------------------------------- | ---------------------------------- | ---------------- |
| **GET** | `/api/work/tasks/{taskId}/activities`       | Get change/activity log for a task | `orgId`          |
| **GET** | `/api/work/projects/{projectId}/activities` | Project-level activity feed        | `orgId`          |
DTO Used: TaskActivityDTO

**1️⃣1️⃣ TASK LABEL**
| Method     | Endpoint                | Description     | Payload / Params |
| ---------- | ----------------------- | --------------- | ---------------- |
| **GET**    | `/api/work/labels`      | List all labels | `orgId`          |
| **POST**   | `/api/work/labels`      | Create label    | `TaskLabelDTO`   |
| **DELETE** | `/api/work/labels/{id}` | Delete label    | `orgId`          |

**1️⃣2️⃣ PERSONAL TASKS (Independent)**
| Method   | Endpoint                  | Description                                 | Payload / Params      |
| -------- | ------------------------- | ------------------------------------------- | --------------------- |
| **GET**  | `/api/work/my-tasks`      | Get logged-in user’s own tasks (no project) | `orgId`, session user |
| **POST** | `/api/work/my-tasks`      | Create self-task (projectId=null)           | `TaskDTO`             |
| **PUT**  | `/api/work/my-tasks/{id}` | Update self-task                            | `TaskDTO`             |



API DESIGN PRINCIPLES

✅ Consistency: All routes use plural nouns and /{id} for entity operations.
✅ Security: Each request validated via orgId + logged-in employeeId.
✅ Reusability: All Task endpoints support both project and independent contexts.
✅ Extensibility: Adding new methodologies or types requires no endpoint change — only metadata.
✅ DTO Standardization: Each response wraps in:
{
  "success": true,
  "data": { ... },
  "message": "optional message"
}

FRONTEND INTEGRATION MAP
| UI Screen          | Backend Endpoint                                                  | DTO                 | Notes                           |
| ------------------ | ----------------------------------------------------------------- | ------------------- | ------------------------------- |
| Project List       | `/api/work/projects`                                              | ProjectDTO          | Filter by type/status           |
| Project Detail     | `/api/work/projects/{id}`                                         | ProjectDTO          | Include members/tasks summary   |
| Task Board         | `/api/work/tasks?projectId={id}`                                  | TaskDTO[]           | For Kanban view                 |
| Task Detail        | `/api/work/tasks/{id}`                                            | TaskDTO             | Includes comments & attachments |
| Add Comment        | `/api/work/tasks/{id}/comments`                                   | TaskCommentDTO      | Inline replies                  |
| Upload File        | `/api/work/attachments/upload`                                    | TaskAttachmentDTO   | FileUploadHelper handles        |
| Epic/Sprint Boards | `/api/work/projects/{id}/epics` `/api/work/projects/{id}/sprints` | EpicDTO / SprintDTO | For Agile view                  |
| Milestone Timeline | `/api/work/projects/{id}/milestones`                              | MilestoneDTO        | For Waterfall tracking          |
| My Tasks           | `/api/work/my-tasks`                                              | TaskDTO             | Personal to-do list             |
| Activity Log       | `/api/work/tasks/{id}/activities`                                 | TaskActivityDTO     | Audit history                   |


Entities: Project, Epic, Sprint, Milestone, Task, TaskComment, TaskAttachment, TaskLabel, TaskActivity, ProjectMember
✅ Services: Already outlined (CRUD + validations)
✅ DTOs: Defined for all entities
✅ Mappers: Built for clean object transformation
✅ Repositories: Contain all queries
✅ Helper: FileUploadHelper for file operations
✅ Endpoints: Listed exhaustively for backend+frontend integration