// apiClient.js - Centralized API communication uti
const organisationId = window.APP.ORG_ID;
const employeeId = window.APP.EMPLOYEE_ID;

const apiClient = {
    baseURL: window.APP.CONTEXT_PATH + '/api',
    async request(endpoint, options = {}) {
        const config = {
            headers: { 'Content-Type': 'application/json', ...options.headers },
            ...options
        };
        const response = await fetch(this.baseURL + endpoint, config);
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || 'Request failed');
        return data.data;
    },
    get(endpoint) { return this.request(endpoint, { method: 'GET' }); },
    post(endpoint, body) { return this.request(endpoint, { method: 'POST', body: JSON.stringify(body) }); },
    put(endpoint, body) { return this.request(endpoint, { method: 'PUT', body: JSON.stringify(body) }); },
    delete(endpoint) { return this.request(endpoint, { method: 'DELETE' }); },
    upload(endpoint, formData) {
        return fetch(this.baseURL + endpoint, { method: 'POST', body: formData })
            .then(res => res.json())
            .then(data => {
                if (!data || data.status !== 'OK') throw new Error(data.message || 'Upload failed');
                return data.data;
            });
    },
    // File upload with FormData
    async upload(endpoint, formData) {
        try {
            const response = await fetch(this.baseURL + endpoint, {
                method: 'POST',
                body: formData // Don't set Content-Type for FormData
            });

            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message || 'Upload failed');
            }
            return data.data;
        } catch (error) {
            console.error('Upload Error:', error);
            showToast(error.message || 'Upload failed', 'error');
            throw error;
        }
    }
};

// Dashboard API
const dashboardAPI = {
    getMyWork(orgId, empId) {
        return apiClient.get(`/dashboard/my-work?organisationId=${orgId}&employeeId=${empId}`);
    },

    getUpcomingReminders(orgId, empId, daysAhead = 3) {
        return apiClient
            .get(`/api/tasks/reminders/upcoming?organisationId=${orgId}&employeeId=${empId}&daysAhead=${daysAhead}`)
            .then(response => response.data.data || []); // Assuming your API response structure is { data: [...] }
    }
};


// PROJECT API
const projectAPI = {
    create(project) {
        return apiClient.post(`/projects?organisationId=${organisationId}&createdById=${employeeId}`, project);
    },
    update(id, project) {
        return apiClient.put(`/projects/${id}?organisationId=${organisationId}&employeeId=${employeeId}`, project);
    },
    getById(id) {
        return apiClient.get(`/projects/${id}?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
    delete(id) {
        return apiClient.delete(`/projects/${id}?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
    getByOrganisation() {
        return apiClient.get(`/projects?organisationId=${organisationId}`);
    },
    getByStatus(status) {
        return apiClient.get(`/projects/status/${status}?organisationId=${organisationId}`);
    },
    getByType(type) {
        return apiClient.get(`/projects/type/${type}?organisationId=${organisationId}`);
    },
    getByEmployee(empId = employeeId) {
        return apiClient.get(`/projects/employee/${empId}?organisationId=${organisationId}`);
    },
    search(keyword) {
        return apiClient.get(`/projects/search?organisationId=${organisationId}&keyword=${keyword}`);
    }
};


// Project Members API
const memberAPI = {
    add(member) {
        return apiClient.post(
            `/projects/members?organisationId=${organisationId}&actorId=${employeeId}`,
            member
        );
    },
    update(id, member) {
        return apiClient.put(
            `/projects/members/${id}?organisationId=${organisationId}&actorId=${employeeId}`,
            member
        );
    },
    remove(id) {
        return apiClient.delete(
            `/projects/members/${id}?organisationId=${organisationId}&actorId=${employeeId}`
        );
    },
    getById(id) {
        return apiClient.get(`/projects/members/${id}?organisationId=${organisationId}`);
    },
    getByProject(projectId) {
        return apiClient.get(`/projects/members/project/${projectId}?organisationId=${organisationId}`);
    },
    getByEmployee(empId = employeeId) {
        return apiClient.get(`/projects/members/employee/${empId}?organisationId=${organisationId}`);
    }
};


const taskAPI = {
    create(task) {
        return apiClient.post(`/tasks?organisationId=${organisationId}&reporterId=${employeeId}`, task);
    },
    update(id, task) {
        return apiClient.put(`/tasks/${id}?organisationId=${organisationId}&employeeId=${employeeId}`, task);
    },
    getById(id) {
        return apiClient.get(`/tasks/${id}?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
    delete(id) {
        return apiClient.delete(`/tasks/${id}?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
    getByProject(projectId) {
        return apiClient.get(`/tasks/project/${projectId}?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
    addAttachment(id, formData) {
        return apiClient.upload(`/tasks/${id}/attachments?employeeId=${employeeId}`, formData);
    },
    getAttachments(id) {
        return apiClient.get(`/tasks/${id}/attachments?organisationId=${organisationId}`);
    },
    getActivities(id) {
        return apiClient.get(`/tasks/${id}/activities`);
    },
    getMyTasks(empId) {
    return apiClient.get(`/tasks/self?organisationId=${organisationId}&employeeId=${empId}`);
  },

  create(task) {
    return apiClient.post(`/tasks?organisationId=${organisationId}&reporterId=${employeeId}`, task);
  },

  update(id, task) {
    return apiClient.put(`/tasks/${id}?organisationId=${organisationId}&employeeId=${employeeId}`, task);
  },

  delete(id) {
    return apiClient.delete(`/tasks/${id}?organisationId=${organisationId}&employeeId=${employeeId}`);
  },

  addAttachment(id, formData) {
    return apiClient.upload(`/tasks/${id}/attachments?employeeId=${employeeId}`, formData);
  },
};

// Ticket API
const ticketAPI = {
    create(ticket) {
        return apiClient.post(`/tickets?organisationId=${organisationId}&createdById=${employeeId}`, ticket);
    },
    update(id, ticket) {
        return apiClient.put(`/tickets/${id}?organisationId=${organisationId}&employeeId=${employeeId}`, ticket);
    },
    getById(id) {
        return apiClient.get(`/tickets/${id}?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
    delete(id) {
        return apiClient.delete(`/tickets/${id}?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
   
    // Status update now uses query params instead of request body
    updateStatus(ticketId, status) {
        return apiClient.put(`/tickets/${ticketId}/status?status=${encodeURIComponent(status)}&employeeId=${employeeId}`);
    },

    //  Assign tickets — now purely query params
    assign(ticketId, assigneeIds) {
        const query = assigneeIds.map(id => `assigneeIds=${id}`).join('&');
        return apiClient.put(`/tickets/${ticketId}/assign?${query}&managerId=${employeeId}`);
    },
     getByStatus(status) {
        return apiClient.get(`/tickets/status/${encodeURIComponent(status)}?organisationId=${organisationId}`);
    },
    // Get delayed tickets (already correct)
    getDelayed() {
        return apiClient.get(`/tickets/delayed?organisationId=${organisationId}`);
    },

    getByProject(projectId) {
        return apiClient.get(`/tickets/project/${projectId}?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
    getByEmployee(empId = employeeId) {
        return apiClient.get(`/tickets/employee/${empId}?organisationId=${organisationId}`);
    },
    getIndependent() {
        return apiClient.get(`/tickets/independent?organisationId=${organisationId}&employeeId=${employeeId}`);
    },
    search(keyword) {
        return apiClient.get(`/tickets/search?organisationId=${organisationId}&keyword=${keyword}`);
    },
    addAttachments(ticketId, formData) {
    return apiClient.post(`/tickets/${ticketId}/attachments?employeeId=${window.APP.EMPLOYEE_ID}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
   },
    getAttachments(id) {
        return apiClient.get(`/tickets/${id}/attachments?organisationId=${organisationId}`);
    },
    getActivities(id) {
        return apiClient.get(`/tickets/${id}/activities`);
    }
};

const employeeAPI = {
  getSubordinates(managerId = employeeId) {
    console.log(managerId)
    return apiClient.get(`/employees/subordinates?managerId=${managerId}&organisationId=${organisationId}`);
  }
};
