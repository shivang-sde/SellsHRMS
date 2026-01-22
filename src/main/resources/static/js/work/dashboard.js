// dashboard.js
$(document).ready(function() {
    loadDashboardData();
});

async function loadDashboardData() {
    try {
        const data = await dashboardAPI.getMyWork(window.APP.ORG_ID, window.APP.EMPLOYEE_ID);
        
        // Update statistics
        updateStats(data);
        
        // Render sections
        renderActiveProjects(data.activeProjects || []);
        renderAssignedTasks(data.assignedTasks || []);
        renderMyTickets(data.assignedTickets || []);
        
    } catch (error) {
        console.error('Failed to load dashboard:', error);
        showToast('Failed to load dashboard data', 'error');
    }
}

function updateStats(data) {
    const activeProjects = data.activeProjects?.length || 0;
    const pendingTasks = data.assignedTasks?.filter(t => t.status !== 'COMPLETED').length || 0;
    const openTickets = data.assignedTickets?.filter(t => t.status === 'OPEN').length || 0;
    const completedToday = data.completedToday || 0;

    $('#activeProjectsCount').text(activeProjects);
    $('#pendingTasksCount').text(pendingTasks);
    $('#openTicketsCount').text(openTickets);
    $('#completedTodayCount').text(completedToday);
}

function renderActiveProjects(projects) {
    const container = $('#activeProjectsList');
    
    if (projects.length === 0) {
        container.html(`
            <div class="text-center text-muted py-4">
                <i class="fas fa-folder-open fa-3x mb-3 opacity-50"></i>
                <p>No active projects</p>
            </div>
        `);
        return;
    }

    const html = projects.slice(0, 5).map(project => `
        <a href="${window.APP.CONTEXT_PATH}/work/projects/${project.id}" 
           class="list-group-item list-group-item-action border-0 py-3">
            <div class="d-flex justify-content-between align-items-start">
                <div class="flex-grow-1">
                    <h6 class="mb-1">${project.name}</h6>
                    <p class="text-muted mb-1 small">${project.description || 'No description'}</p>
                    <div class="d-flex gap-2 align-items-center">
                        ${getStatusBadge(project.status)}
                        <small class="text-muted">
                            <i class="fas fa-calendar-alt me-1"></i>${formatDate(project.startDate)}
                        </small>
                    </div>
                </div>
                <i class="fas fa-chevron-right text-muted"></i>
            </div>
        </a>
    `).join('');

    container.html(html);
}

function renderAssignedTasks(tasks) {
    const container = $('#assignedTasksList');
    
    if (tasks.length === 0) {
        container.html(`
            <div class="text-center text-muted py-4">
                <i class="fas fa-tasks fa-3x mb-3 opacity-50"></i>
                <p>No assigned tasks</p>
            </div>
        `);
        return;
    }

    const html = tasks.slice(0, 5).map(task => `
        <a href="${window.APP.CONTEXT_PATH}/work/tasks/${task.id}" 
           class="list-group-item list-group-item-action border-0 py-3">
            <div class="d-flex justify-content-between align-items-start">
                <div class="flex-grow-1">
                    <h6 class="mb-1">${task.title}</h6>
                    <div class="d-flex gap-2 align-items-center flex-wrap">
                        ${getStatusBadge(task.status)}
                        ${getPriorityBadge(task.priority)}
                        <small class="text-muted">
                            <i class="fas fa-clock me-1"></i>Due: ${formatDate(task.dueDate)}
                        </small>
                    </div>
                </div>
                <i class="fas fa-chevron-right text-muted"></i>
            </div>
        </a>
    `).join('');

    container.html(html);
}

function renderMyTickets(tickets) {
    const tbody = $('#ticketsTable tbody');
    
    if (tickets.length === 0) {
        tbody.html(`
            <tr>
                <td colspan="6" class="text-center text-muted py-4">
                    <i class="fas fa-ticket-alt fa-3x mb-3 opacity-50"></i>
                    <p>No assigned tickets</p>
                </td>
            </tr>
        `);
        return;
    }

    const html = tickets.slice(0, 5).map(ticket => `
        <tr>
            <td><strong>#${ticket.id}</strong></td>
            <td>
                <a href="${window.APP.CONTEXT_PATH}/work/tickets/${ticket.id}" 
                   class="text-decoration-none">
                    ${ticket.title}
                </a>
            </td>
            <td>${getPriorityBadge(ticket.priority)}</td>
            <td>${getStatusBadge(ticket.status)}</td>
            <td>${formatDate(ticket.createdDate)}</td>
            <td>
                <a href="${window.APP.CONTEXT_PATH}/work/tickets/${ticket.id}" 
                   class="btn btn-sm btn-outline-primary">
                    <i class="fas fa-eye"></i>
                </a>
            </td>
        </tr>
    `).join('');

    tbody.html(html);
}

async function loadUpcomingReminders() {
    try {
        const reminders = await dashboardAPI.getUpcomingReminders(window.APP.ORG_ID, window.APP.EMPLOYEE_ID, 3);

        const container = $('#upcomingRemindersList');

        if (!reminders || reminders.length === 0) {
            container.html(`
                <div class="text-center text-muted py-4">
                    <i class="fas fa-bell fa-3x mb-3 opacity-50"></i>
                    <p>No upcoming reminders in next 3 days</p>
                </div>
            `);
            return;
        }

        const html = reminders.slice(0, 5).map(task => `
            <a href="${window.APP.CONTEXT_PATH}/work/tasks/${task.id}" 
               class="list-group-item list-group-item-action border-0 py-3">
                <div class="d-flex justify-content-between align-items-start">
                    <div class="flex-grow-1">
                        <h6 class="mb-1">${task.title}</h6>
                        <div class="d-flex gap-2 align-items-center flex-wrap">
                            ${getStatusBadge(task.status)}
                            ${getPriorityBadge(task.priority)}
                            <small class="text-muted">
                                <i class="fas fa-clock me-1"></i>Due: ${formatDate(task.dueDate)}
                            </small>
                        </div>
                    </div>
                    <i class="fas fa-chevron-right text-muted"></i>
                </div>
            </a>
        `).join('');

        container.html(html);

    } catch (error) {
        console.error('Failed to load upcoming reminders:', error);
        $('#upcomingRemindersList').html(`
            <div class="text-center text-danger py-4">
                <p>Error loading reminders</p>
            </div>
        `);
    }
}


// $(document).ready(function () {
//   loadTicketsDashboard();
// });

// async function loadTicketsDashboard() {
//   try {
//     const data = await ticketAPI.getAllTickets(window.APP.ORG_ID); // Or filter by project
//     renderTicketDashboard(data);
//   } catch (e) {
//     console.error("Error loading tickets:", e);
//   }
// }

function renderTicketDashboard(tickets) {
  // Clear old containers
  $("#notStartedContainer, #inProgressContainer, #onHoldContainer, #completedContainer, #delayedContainer").empty();

  tickets.forEach(ticket => {
    const card = buildTicketCard(ticket);

    const today = new Date();

    switch (ticket.status) {
      case "OPEN":
        $("#notStartedContainer").append(card);
        break;
      case "IN_PROGRESS":
        $("#inProgressContainer").append(card);
        break;
      case "ON_HOLD":
        $("#onHoldContainer").append(card);
        break;
      case "COMPLETED":
        $("#completedContainer").append(card);
        break;
      default:
        break;
    }

    // Delayed detection
    if (ticket.status !== "COMPLETED" && ticket.endDate && new Date(ticket.endDate) < today) {
      $("#delayedContainer").append(card);
    }
  });
}

function buildTicketCard(ticket) {
  const start = ticket.actualStartDate ? `Started: ${ticket.actualStartDate}` : "";
  const end = ticket.actualCompletionDate ? `Completed: ${ticket.actualCompletionDate}` : "";

  return `
    <div class="border p-2 mb-2 rounded small shadow-sm">
      <div class="fw-bold">${ticket.title}</div>
      <div class="text-muted">${ticket.projectName || "No Project"}</div>
      <div class="d-flex justify-content-between mt-1">
        <small>Status: ${ticket.status}</small>
        <small>${start}</small>
      </div>
      <small>${end}</small>
    </div>
  `;
}
