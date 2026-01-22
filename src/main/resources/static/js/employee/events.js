$(document).ready(function() {
  loadUpcomingEvents();
  
  function loadUpcomingEvents() {
    $.ajax({
      url: '/api/events/upcoming',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          displayEvents(response.data);
        }
      }
    });
  }
  
  function displayEvents(events) {
    const container = $('#eventsList');
    container.empty();
    
    if (events.length === 0) {
      container.append(`
        <div class="text-center py-5">
          <i class="fa fa-calendar fa-4x text-muted mb-3"></i>
          <h5>No Upcoming Events</h5>
        </div>
      `);
      return;
    }
    
    events.forEach(evt => {
      const card = `
        <div class="event-card card mb-3">
          <div class="card-body">
            <div class="d-flex align-items-start">
              <div class="date-badge text-center me-3">
                <div class="badge bg-primary p-3">
                  <div style="font-size: 24px;">${new Date(evt.startDate).getDate()}</div>
                  <div>${new Date(evt.startDate).toLocaleString('en', {month: 'short'})}</div>
                </div>
              </div>
              <div class="flex-grow-1">
                <h5>${evt.title}</h5>
                <p class="text-muted">${evt.description}</p>
                <div class="mb-2">
                  <span class="badge bg-primary">${evt.type}</span>
                  <span class="badge bg-secondary"><i class="fa fa-map-marker-alt me-1"></i>${evt.location}</span>
                </div>
                <small class="text-muted">
                  ${formatDate(evt.startDate)} to ${formatDate(evt.endDate)}
                </small>
              </div>
            </div>
          </div>
        </div>
      `;
      container.append(card);
    });
  }
  
  function formatDate(d) {
    return new Date(d).toLocaleDateString('en-IN', {day: '2-digit', month: 'short', year: 'numeric'});
  }
});