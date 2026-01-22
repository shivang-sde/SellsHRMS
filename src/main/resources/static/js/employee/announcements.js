$(document).ready(function() {
  loadAnnouncements();
  
  function loadAnnouncements() {
    $.ajax({
      url: '/api/announcements/active',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          displayAnnouncements(response.data);
        }
      }
    });
  }
  
  function displayAnnouncements(announcements) {
    const container = $('#announcementsList');
    container.empty();
    
    if (announcements.length === 0) {
      container.append(`
        <div class="text-center py-5">
          <i class="fa fa-bullhorn fa-4x text-muted mb-3"></i>
          <h5>No Active Announcements</h5>
        </div>
      `);
      return;
    }
    
    announcements.forEach(ann => {
      const card = `
        <div class="announcement-card card mb-3">
          <div class="card-body">
            <h5><i class="fa fa-bullhorn text-primary me-2"></i>${ann.title}</h5>
            <p class="text-muted">${ann.message}</p>
            <small class="text-muted">Posted: ${formatDateTime(ann.createdAt)}</small>
            ${ann.validUntil ? `<br><small class="text-muted">Valid until: ${formatDateTime(ann.validUntil)}</small>` : ''}
          </div>
        </div>
      `;
      container.append(card);
    });
  }
  
  function formatDateTime(dt) {
    return new Date(dt).toLocaleString('en-IN');
  }
});