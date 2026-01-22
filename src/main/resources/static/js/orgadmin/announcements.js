$(document).ready(function() {
  let announcements = [];
  
  init();
  
  function init() {
    loadAnnouncements();
    $('#createAnnouncementForm').on('submit', handleCreate);
    $('#editAnnouncementForm').on('submit', handleEdit);
  }
  
  function loadAnnouncements() {
    $.ajax({
      url: '/api/announcements',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          announcements = response.data;
          displayAnnouncements(announcements);
        }
      }
    });
  }
  
  function displayAnnouncements(announcements) {
    const container = $('#announcementsList');
    container.empty();
    
    if (announcements.length === 0) {
      container.append('<div class="text-center py-4"><p class="text-muted">No announcements</p></div>');
      return;
    }
    
    announcements.forEach(ann => {
      const card = `
        <div class="announcement-card card mb-3">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div class="flex-grow-1">
                <h5>${ann.title}</h5>
                <p class="text-muted">${ann.message}</p>
                <small class="text-muted">Valid until: ${ann.validUntil ? formatDateTime(ann.validUntil) : 'No expiry'}</small>
              </div>
              <div>
                <button class="btn btn-sm btn-outline-primary" onclick="editAnnouncement(${ann.id})">
                  <i class="fa fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-${ann.isActive ? 'warning' : 'success'}" onclick="toggleStatus(${ann.id})">
                  <i class="fa fa-${ann.isActive ? 'eye-slash' : 'eye'}"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteAnnouncement(${ann.id})">
                  <i class="fa fa-trash"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      `;
      container.append(card);
    });
  }
  
  function handleCreate(e) {
    e.preventDefault();
    const formData = {
      title: $('input[name="title"]').val(),
      message: $('textarea[name="message"]').val(),
      validUntil: $('input[name="validUntil"]').val() || null
    };
    
    $.ajax({
      url: '/api/announcements',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#createAnnouncementModal').modal('hide');
          $('#createAnnouncementForm')[0].reset();
          loadAnnouncements();
        }
      }
    });
  }
  
  window.editAnnouncement = function(id) {
    const ann = announcements.find(a => a.id === id);
    if (!ann) return;
    
    $('#editAnnouncementId').val(ann.id);
    $('#editTitle').val(ann.title);
    $('#editMessage').val(ann.message);
    $('#editValidUntil').val(ann.validUntil ? ann.validUntil.slice(0, 16) : '');
    $('#editAnnouncementModal').modal('show');
  };
  
  function handleEdit(e) {
    e.preventDefault();
    const id = $('#editAnnouncementId').val();
    const formData = {
      title: $('#editTitle').val(),
      message: $('#editMessage').val(),
      validUntil: $('#editValidUntil').val() || null
    };
    
    $.ajax({
      url: `/api/announcements/${id}`,
      method: 'PATCH',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#editAnnouncementModal').modal('hide');
          loadAnnouncements();
        }
      }
    });
  }
  
  window.toggleStatus = function(id) {
    $.ajax({
      url: `/api/announcements/${id}/toggle`,
      method: 'PATCH',
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          loadAnnouncements();
        }
      }
    });
  };
  
  window.deleteAnnouncement = function(id) {
    if (!confirm('Are you sure?')) return;
    
    $.ajax({
      url: `/api/announcements/${id}`,
      method: 'DELETE',
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          loadAnnouncements();
        }
      }
    });
  };
  
  function formatDateTime(dt) {
    return new Date(dt).toLocaleString('en-IN');
  }
});