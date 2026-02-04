$(document).ready(function() {
  let events = [];
  
  init();
  
  function init() {
    loadEvents();
    $('#createEventForm').on('submit', handleCreate);
    $('#editEventForm').on('submit', handleEdit);
  }
  
  function loadEvents() {
    $.ajax({
      url: '/api/events',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          events = response.data;
          displayEvents(events);
        }
      }
    });
  }
  
  function displayEvents(events) {
    const container = $('#eventsList');
    container.empty();
    
    if (events.length === 0) {
      container.append('<div class="text-center py-4"><p class="text-muted">No events</p></div>');
      return;
    }
    
    events.forEach(evt => {
      const card = `
        <div class="event-card card mb-3">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div class="flex-grow-1">
                <h5>${evt.title}</h5>
                <p class="text-muted">${evt.description}</p>
                <div class="mb-2">
                  <span class="badge bg-primary">${evt.type}</span>
                  <span class="badge bg-secondary">${evt.location}</span>
                </div>
                <small class="text-muted">${formatDate(evt.startDate)} to ${formatDate(evt.endDate)}</small>
              </div>
              <div>
                <button class="btn btn-sm btn-outline-primary" onclick="editEvent(${evt.id})">
                  <i class="fa fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteEvent(${evt.id})">
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
      description: $('textarea[name="description"]').val(),
      startDate: $('input[name="startDate"]').val(),
      endDate: $('input[name="endDate"]').val(),
      location: $('input[name="location"]').val(),
      type: $('select[name="type"]').val()
    };
    
    $.ajax({
      url: '/api/events',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#createEventModal').modal('hide');
          $('#createEventForm')[0].reset();
          loadEvents();
        }
      }
    });
  }
  
  window.editEvent = function(id) {
    const evt = events.find(e => e.id === id);
    if (!evt) return;
    
    $('#editEventId').val(evt.id);
    $('#editEventTitle').val(evt.title);
    $('#editEventDescription').val(evt.description);
    $('#editEventStartDate').val(evt.startDate);
    $('#editEventEndDate').val(evt.endDate);
    $('#editEventLocation').val(evt.location);
    $('#editEventType').val(evt.type);
    $('#editEventModal').modal('show');
  };
  
  function handleEdit(e) {
    e.preventDefault();
    const id = $('#editEventId').val();
    const formData = {
      title: $('#editEventTitle').val(),
      description: $('#editEventDescription').val(),
      startDate: $('#editEventStartDate').val(),
      endDate: $('#editEventEndDate').val(),
      location: $('#editEventLocation').val(),
      type: $('#editEventType').val()
    };
    
    $.ajax({
      url: `/api/events/${id}`,
      method: 'PATCH',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#editEventModal').modal('hide');
          loadEvents();
        }
      }
    });
  }
  
  window.deleteEvent = function(id) {
    if (!confirm('Are you sure?')) return;
    
    $.ajax({
      url: `/api/events/${id}`,
      method: 'DELETE',
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          loadEvents();
        }
      }
    });
  };
  
  function formatDate(d) {
    return new Date(d).toLocaleDateString('en-IN');
  }
});