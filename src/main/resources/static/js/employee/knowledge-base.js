$(document).ready(function() {
  let subjects = [];
  let currentSubjectId = null;
  let currentTopics = [];

  init();

  function init() {
    loadSubjects();
    setupEventListeners();
  }

  function setupEventListeners() {
    $('#createSubjectForm').on('submit', handleCreateSubject);
    $('#editSubjectForm').on('submit', handleEditSubject);
    $('#createTopicForm').on('submit', handleCreateTopic);
    $('#editTopicForm').on('submit', handleEditTopic);
  }

  // ==================== Subjects ====================

  function loadSubjects() {
    $.ajax({
      url: '/api/knowledge-base/subjects',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          subjects = response.data;
          displaySubjects(subjects);
        }
      },
      error: function(xhr) {
        $('#subjectsGrid').html(`
          <div class="col-12"><div class="alert alert-danger">Error loading subjects</div></div>
        `);
      }
    });
  }

  function displaySubjects(subjects) {
    const grid = $('#subjectsGrid');
    grid.empty();

    if (subjects.length === 0) {
      grid.append(`
        <div class="col-12">
          <div class="card border-0 shadow-sm text-center py-5">
            <div class="card-body">
              <i class="fa fa-book fa-4x text-muted mb-3"></i>
              <h5>No Subjects Found</h5>
              <p class="text-muted">Create your first subject to organize knowledge</p>
              <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createSubjectModal">
                <i class="fa fa-plus me-2"></i>Create Subject
              </button>
            </div>
          </div>
        </div>
      `);
      return;
    }

    subjects.forEach(subject => {
      const card = `
        <div class="col-md-4 mb-4">
          <div class="card border-0 shadow-sm subject-card" onclick="viewSubject(${subject.id})">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="card-title mb-0">${subject.title}</h5>
                <div class="dropdown" onclick="event.stopPropagation()">
                  <button class="btn btn-sm btn-light" data-bs-toggle="dropdown">
                    <i class="fa fa-ellipsis-v"></i>
                  </button>
                  <ul class="dropdown-menu">
                    <li><a class="dropdown-item" href="#" onclick="event.preventDefault(); editSubject(${subject.id})">
                      <i class="fa fa-edit me-2"></i>Edit
                    </a></li>
                    <li><a class="dropdown-item text-danger" href="#" onclick="event.preventDefault(); deleteSubject(${subject.id})">
                      <i class="fa fa-trash me-2"></i>Delete
                    </a></li>
                  </ul>
                </div>
              </div>
              <p class="text-muted small mb-3">${subject.description || 'No description'}</p>
              <div class="d-flex justify-content-between align-items-center">
                <span class="badge bg-primary">${subject.topicCount} Topics</span>
                <small class="text-muted">${formatDate(subject.createdAt)}</small>
              </div>
            </div>
          </div>
        </div>
      `;
      grid.append(card);
    });
  }

  function handleCreateSubject(e) {
    e.preventDefault();
    const formData = {
      title: $('#createSubjectForm input[name="title"]').val(),
      description: $('#createSubjectForm textarea[name="description"]').val()
    };

    $.ajax({
      url: '/api/knowledge-base/subjects',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#createSubjectModal').modal('hide');
          $('#createSubjectForm')[0].reset();
          loadSubjects();
        }
      },
      error: function(xhr) {
        showToast('error', 'Failed to create subject');
      }
    });
  }

  window.editSubject = function(subjectId) {
    const subject = subjects.find(s => s.id === subjectId);
    if (!subject) return;

    $('#editSubjectId').val(subject.id);
    $('#editSubjectTitle').val(subject.title);
    $('#editSubjectDescription').val(subject.description);
    $('#editSubjectModal').modal('show');
  };

  function handleEditSubject(e) {
    e.preventDefault();
    const subjectId = $('#editSubjectId').val();
    const formData = {
      title: $('#editSubjectTitle').val(),
      description: $('#editSubjectDescription').val()
    };

    $.ajax({
      url: `/api/knowledge-base/subjects/${subjectId}`,
      method: 'PATCH',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#editSubjectModal').modal('hide');
          loadSubjects();
        }
      },
      error: function(xhr) {
        showToast('error', 'Failed to update subject');
      }
    });
  }

  window.deleteSubject = function(subjectId) {
    if (!confirm('Are you sure you want to delete this subject? All topics will be deleted.')) {
      return;
    }

    $.ajax({
      url: `/api/knowledge-base/subjects/${subjectId}`,
      method: 'DELETE',
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          loadSubjects();
        }
      },
      error: function(xhr) {
        showToast('error', 'Failed to delete subject');
      }
    });
  };

  window.viewSubject = function(subjectId) {
    currentSubjectId = subjectId;
    loadSubjectWithTopics(subjectId);
    $('#viewSubjectModal').modal('show');
  };

  function loadSubjectWithTopics(subjectId) {
    $.ajax({
      url: `/api/knowledge-base/subjects/${subjectId}/with-topics`,
      method: 'GET',
      success: function(response) {
        if (response.success) {
          const data = response.data;
          $('#viewSubjectTitle').text(data.title);
          $('#viewSubjectDescription').text(data.description || 'No description');
          currentTopics = data.topics;
          displayTopics(data.topics);
        }
      },
      error: function(xhr) {
        $('#topicsList').html('<div class="alert alert-danger">Error loading topics</div>');
      }
    });
  }

  function displayTopics(topics) {
    const container = $('#topicsList');
    container.empty();

    if (topics.length === 0) {
      container.append(`
        <div class="text-center py-4">
          <i class="fa fa-file-alt fa-3x text-muted mb-3"></i>
          <p class="text-muted">No topics yet. Click "Add Topic" to create one.</p>
        </div>
      `);
      return;
    }

    topics.forEach(topic => {
      const item = `
        <div class="topic-item p-3 mb-2 border rounded">
          <div class="d-flex justify-content-between align-items-start">
            <div class="flex-grow-1">
              <h6 class="mb-1">${topic.title}</h6>
              <p class="text-muted small mb-2">${topic.content.substring(0, 150)}${topic.content.length > 150 ? '...' : ''}</p>
              ${topic.attachmentUrl ? `<a href="${topic.attachmentUrl}" target="_blank" class="small"><i class="fa fa-paperclip me-1"></i>Attachment</a>` : ''}
            </div>
            <div class="ms-3">
              <button class="btn btn-sm btn-outline-primary me-1" onclick="editTopic(${topic.id})">
                <i class="fa fa-edit"></i>
              </button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteTopic(${topic.id})">
                <i class="fa fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      `;
      container.append(item);
    });
  }

  // ==================== Topics ====================

  window.openCreateTopicModal = function() {
    $('#createTopicSubjectId').val(currentSubjectId);
    $('#viewSubjectModal').modal('hide');
    $('#createTopicModal').modal('show');
  };

  function handleCreateTopic(e) {
    e.preventDefault();
    const formData = {
      subjectId: parseInt($('#createTopicSubjectId').val()),
      title: $('#createTopicForm input[name="title"]').val(),
      content: $('#createTopicForm textarea[name="content"]').val(),
      attachmentUrl: $('#createTopicForm input[name="attachmentUrl"]').val() || null
    };

    $.ajax({
      url: '/api/knowledge-base/topics',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#createTopicModal').modal('hide');
          $('#createTopicForm')[0].reset();
          $('#viewSubjectModal').modal('show');
          loadSubjectWithTopics(currentSubjectId);
        }
      },
      error: function(xhr) {
        showToast('error', 'Failed to create topic');
      }
    });
  }

  window.editTopic = function(topicId) {
    const topic = currentTopics.find(t => t.id === topicId);
    if (!topic) return;

    $('#editTopicId').val(topic.id);
    $('#editTopicSubjectId').val(topic.subjectId);
    $('#editTopicTitle').val(topic.title);
    $('#editTopicContent').val(topic.content);
    $('#editTopicAttachment').val(topic.attachmentUrl || '');
    $('#viewSubjectModal').modal('hide');
    $('#editTopicModal').modal('show');
  };

  function handleEditTopic(e) {
    e.preventDefault();
    const topicId = $('#editTopicId').val();
    const formData = {
      subjectId: parseInt($('#editTopicSubjectId').val()),
      title: $('#editTopicTitle').val(),
      content: $('#editTopicContent').val(),
      attachmentUrl: $('#editTopicAttachment').val() || null
    };

    $.ajax({
      url: `/api/knowledge-base/topics/${topicId}`,
      method: 'PATCH',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#editTopicModal').modal('hide');
          $('#viewSubjectModal').modal('show');
          loadSubjectWithTopics(currentSubjectId);
        }
      },
      error: function(xhr) {
        showToast('error', 'Failed to update topic');
      }
    });
  }

  window.deleteTopic = function(topicId) {
    if (!confirm('Are you sure you want to delete this topic?')) {
      return;
    }

    $.ajax({
      url: `/api/knowledge-base/topics/${topicId}`,
      method: 'DELETE',
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          loadSubjectWithTopics(currentSubjectId);
        }
      },
      error: function(xhr) {
        showToast('error', 'Failed to delete topic');
      }
    });
  };

  function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
  }
});