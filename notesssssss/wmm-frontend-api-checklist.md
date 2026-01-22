**DASHBOARD (Global Overview)**
Purpose: Unified view of user’s work: assigned projects, personal tasks, and upcoming deadlines

Components
| Component                   | API Endpoint                                                           | DTO                 | Notes                             |
| --------------------------- | ---------------------------------------------------------------------- | ------------------- | --------------------------------- |
| **Active Projects Summary** | `GET /api/work/projects?orgId={orgId}`                                 | `ProjectDTO[]`      | Filter by `status=IN_PROGRESS`    |
| **My Tasks Widget**         | `GET /api/work/my-tasks?orgId={orgId}`                                 | `TaskDTO[]`         | Group by `status`                 |
| **Upcoming Deadlines**      | `GET /api/work/tasks?orgId={orgId}&assigneeId={empId}&dueDate<today+7` | `TaskDTO[]`         | Use due date filter               |
| **Team Activity Feed**      | `GET /api/work/projects/{projectId}/activities`                        | `TaskActivityDTO[]` | Show most recent 10 changes       |
| **Quick Add Task Button**   | `POST /api/work/my-tasks`                                              | `TaskDTO`           | Create personal task (no project) |


**🏗️ 2️⃣ PROJECT LIST PAGE**
components

| Component                         | API Endpoint                                 | DTO            | Notes                                                    |
| --------------------------------- | -------------------------------------------- | -------------- | -------------------------------------------------------- |
| **Project Table / Grid**          | `GET /api/work/projects?orgId={orgId}`       | `ProjectDTO[]` | Supports search & filters                                |
| **Search Box**                    | `GET /api/work/projects/search?keyword={kw}` | `ProjectDTO[]` | Instant search                                           |
| **Create Project Button (Modal)** | `POST /api/work/projects`                    | `ProjectDTO`   | Form fields → name, dept, manager, type, etc.            |
| **Edit / Update Project**         | `PUT /api/work/projects/{id}`                | `ProjectDTO`   | On modal submit                                          |
| **Delete / Archive Project**      | `DELETE /api/work/projects/{id}`             | —              | Confirm before delete                                    |
| **Filter Dropdowns**              | Same `GET /api/work/projects`                | —              | Filter by `ProjectStatus`, `ProjectType`, `DepartmentId` |


**PROJECT DETAIL PAGE**
🔹 Components
| Component                        | API Endpoint                                   | DTO                  | Notes                          |
| -------------------------------- | ---------------------------------------------- | -------------------- | ------------------------------ |
| **Header (Project Info)**        | `GET /api/work/projects/{id}`                  | `ProjectDTO`         | Show name, manager, progress   |
| **Member List / Manage Members** | `GET /api/work/projects/{id}/members`          | `ProjectMemberDTO[]` | Show avatars, roles            |
| **Add Member Button**            | `POST /api/work/projects/{id}/members`         | `ProjectMemberDTO`   | Modal → select employee & role |
| **Remove Member**                | `DELETE /api/work/projects/members/{memberId}` | —                    | Confirm removal                |
| **Epics Tab**                    | `GET /api/work/projects/{id}/epics`            | `EpicDTO[]`          | For Agile view                 |
| **Sprints Tab**                  | `GET /api/work/projects/{id}/sprints`          | `SprintDTO[]`        | For Scrum view                 |
| **Milestones Tab**               | `GET /api/work/projects/{id}/milestones`       | `MilestoneDTO[]`     | For Waterfall view             |
| **Task Tab / Kanban Board**      | `GET /api/work/tasks?projectId={id}`           | `TaskDTO[]`          | Task cards grouped by status   |
| **Add Task Button**              | `POST /api/work/tasks`                         | `TaskDTO`            | Auto attach projectId          |
| **Activity Feed Sidebar**        | `GET /api/work/projects/{id}/activities`       | `TaskActivityDTO[]`  | Project-level change log       |

**🗂️ 4️⃣ TASK BOARD / LIST VIEW**
| Component                     | API Endpoint                         | DTO                       | Notes                                    |
| ----------------------------- | ------------------------------------ | ------------------------- | ---------------------------------------- |
| **Task Board Data**           | `GET /api/work/tasks?projectId={id}` | `TaskDTO[]`               | Kanban lanes by status                   |
| **Drag-drop (Status Update)** | `POST /api/work/tasks/{id}/status`   | `{status: "IN_PROGRESS"}` | Trigger on drop                          |
| **Task Modal / Detail Panel** | `GET /api/work/tasks/{id}`           | `TaskDTO`                 | Includes subtasks, comments, attachments |
| **Create Task (inline add)**  | `POST /api/work/tasks`               | `TaskDTO`                 | Same endpoint as detail page             |
| **Subtask Section**           | `GET /api/work/tasks/{id}/subtasks`  | `TaskDTO[]`               | Show under parent                        |
| **Add Subtask**               | `POST /api/work/tasks/{id}/subtasks` | `TaskDTO`                 | parentTaskId auto-assigned               |

***📝 5️⃣ TASK DETAIL MODAL***
🔹 Tabs and Calls
| Tab                   | API Endpoint                                                        | DTO                   | Notes                                |
| --------------------- | ------------------------------------------------------------------- | --------------------- | ------------------------------------ |
| **Overview**          | `GET /api/work/tasks/{id}`                                          | `TaskDTO`             | Title, Description, Status, Priority |
| **Update Info**       | `PUT /api/work/tasks/{id}`                                          | `TaskDTO`             | Inline editable fields               |
| **Comments**          | `GET /api/work/tasks/{id}/comments`                                 | `TaskCommentDTO[]`    | Show threaded list                   |
| **Add Comment**       | `POST /api/work/tasks/{id}/comments`                                | `TaskCommentDTO`      | Real-time append                     |
| **Attachments**       | `GET /api/work/attachments/task/{id}`                               | `TaskAttachmentDTO[]` | Show thumbnails                      |
| **Upload Attachment** | `POST /api/work/attachments/upload?contextType=task&contextId={id}` | Multipart             | Uses `FileUploadHelper`              |
| **Activity Log**      | `GET /api/work/tasks/{id}/activities`                               | `TaskActivityDTO[]`   | Show recent changes                  |
| **Subtasks**          | `GET /api/work/tasks/{id}/subtasks`                                 | `TaskDTO[]`           | Checkable items                      |

**👥 6️⃣ PERSONAL TASKS (My Tasks)**
components 
| Component                      | API Endpoint                           | DTO         | Notes                 |
| ------------------------------ | -------------------------------------- | ----------- | --------------------- |
| **My Task Board / To-Do List** | `GET /api/work/my-tasks?orgId={orgId}` | `TaskDTO[]` | Grouped by status     |
| **Add Personal Task**          | `POST /api/work/my-tasks`              | `TaskDTO`   | No projectId required |
| **Update Task / Status**       | `PUT /api/work/my-tasks/{id}`          | `TaskDTO`   | Self-update           |
| **Delete Task**                | `DELETE /api/work/my-tasks/{id}`       | —           | Soft delete only      |


**7️⃣ EPICS, SPRINTS, MILESTONES (Simplified Agile)**
components
| Component               | API Endpoint                              | DTO                  | Notes                  |
| ----------------------- | ----------------------------------------- | -------------------- | ---------------------- |
| **Epics List**          | `GET /api/work/projects/{id}/epics`       | `EpicDTO[]`          | For Agile board header |
| **Create Epic**         | `POST /api/work/projects/{id}/epics`      | `EpicDTO`            | Modal form             |
| **Sprints List**        | `GET /api/work/projects/{id}/sprints`     | `SprintDTO[]`        | For Scrum cycle view   |
| **Start Sprint Button** | `PUT /api/work/sprints/{id}`              | `{status: "ACTIVE"}` | Activate sprint        |
| **Complete Sprint**     | `POST /api/work/sprints/{id}/complete`    | —                    | Close sprint           |
| **Milestone Timeline**  | `GET /api/work/projects/{id}/milestones`  | `MilestoneDTO[]`     | Gantt-like display     |
| **Add Milestone**       | `POST /api/work/projects/{id}/milestones` | `MilestoneDTO`       | Dialog add form        |

**FILE UPLOAD & ATTACHMENT HANDLING**
components
| Component                  | API Endpoint                                                           | DTO                   | Notes                        |
| -------------------------- | ---------------------------------------------------------------------- | --------------------- | ---------------------------- |
| **Attach File to Task**    | `POST /api/work/attachments/upload?contextType=task&contextId={id}`    | `TaskAttachmentDTO`   | Use multipart form data      |
| **Attach File to Comment** | `POST /api/work/attachments/upload?contextType=comment&contextId={id}` | `TaskAttachmentDTO`   | Optional description         |
| **List Attachments**       | `GET /api/work/attachments/{contextType}/{contextId}`                  | `TaskAttachmentDTO[]` | Preview/download             |
| **Delete Attachment**      | `DELETE /api/work/attachments/{id}`                                    | —                     | Requires uploader/admin role |

**🧾 9️⃣ REPORTING & METRICS**
components
| Component                   | API Endpoint                                         | DTO                                          | Notes                     |
| --------------------------- | ---------------------------------------------------- | -------------------------------------------- | ------------------------- |
| **Project Summary Widget**  | `GET /api/work/projects/{id}/summary`                | JSON `{totalTasks, completedTasks, members}` | Top card view             |
| **Employee Workload Chart** | `GET /api/work/tasks?orgId={orgId}&groupBy=assignee` | Aggregated                                   | Simple bar chart          |
| **Project Progress Chart**  | `GET /api/work/tasks?projectId={id}&groupBy=status`  | Aggregated                                   | Pie chart or progress bar |




**1️⃣0️⃣ SYSTEM BEHAVIORS**
components
| Trigger             | Frontend Action                | API Call                              | Notes |
| ------------------- | ------------------------------ | ------------------------------------- | ----- |
| Project created     | Refresh project list           | `GET /api/work/projects`              |       |
| Member added        | Refresh members & tasks        | `GET /api/work/projects/{id}/members` |       |
| Task status changed | Update Kanban lane             | `POST /api/work/tasks/{id}/status`    |       |
| File uploaded       | Re-render attachments list     | `GET /api/work/attachments/task/{id}` |       |
| Comment added       | Append comment                 | `GET /api/work/tasks/{id}/comments`   |       |
| Sprint completed    | Refresh sprint & epic progress | `GET /api/work/projects/{id}/epics`   |       |



**✅ 1️⃣1️⃣ NON-FUNCTIONAL CHECKLIST**
| Concern               | Implementation Guidance                                        |
| --------------------- | -------------------------------------------------------------- |
| **Authentication**    | Use existing HRMS session / token                              |
| **Authorization**     | Only project members may update project tasks                  |
| **Error Handling**    | Return JSON `{success:false, message: "..."}`                  |
| **Pagination**        | Apply to project & task lists (limit=20 default)               |
| **Sorting & Filters** | Backend supports query params (`sortBy`, `status`, `priority`) |
| **Caching**           | Client-side cache last fetched tasks for faster reload         |
| **Upload Size Limit** | Max 10 MB per file (as per `application.yml`)                  |

**🧭 1️⃣2️⃣ DEVELOPER HANDOFF SUMMARY**
✅ 12 core API modules (Projects, Tasks, Members, Epics, Sprints, Milestones, Comments, Attachments, Activities, Labels, Personal Tasks, Reports)
✅ All endpoints defined for REST consumption
✅ Each UI component mapped to 1–2 backend APIs
✅ Reusable Task modal & File upload helper integration
✅ Ready for Postman collection / Swagger generation

