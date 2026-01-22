$(document).ready(function () {
  const orgId = window.APP.ORG_ID || $("#globalOrgId").val();
  const employeeId = window.APP.EMPLOYEE_ID;
  if (!orgId) return console.warn("No ORG_ID in session");

       loadUpcomingReminders();

  // --------------------------
  // 1️⃣ Load main dashboard data
  // --------------------------
  $.getJSON(`/api/dashboard/org/${orgId}`, function (response) {
    const data = response.data || {};
    console.log("Dashboard data:", data);

    renderList("#birthdaysContainer", data.birthdays, e => `${e.fullName || (e.firstName + " " + e.lastName)}`);
    renderList("#anniversaryContainer", data.anniversaries, e => `${e.fullName || (e.firstName + " " + e.lastName)}`);
    renderList("#holidayContainer", data.holidays, h => `${h.holidayName} - ${formatDate(h.holidayDate)}`);

    // Combine Events + Announcements for one card
    const combined = [];
    if (Array.isArray(data.events))
      combined.push(...data.events.map(ev => ({
        label: `<i class="fa fa-calendar-day text-primary"></i> ${ev.title} - ${formatDate(ev.startDate)}`
      })));
    if (Array.isArray(data.announcements))
      combined.push(...data.announcements.map(an => ({
        label: `<i class="fa fa-bullhorn text-warning"></i> <b>${an.title}</b> - ${an.message}`
      })));
    renderList("#eventsContainer", combined, i => i.label);
  }).fail(() => showToast("error", "Failed to load dashboard data"));

async function loadUpcomingReminders() {
    const container = $('#remindersContainer');
    container.html('<div class="text-muted small">Loading...</div>');

    try {
        const res = await fetch(`${window.APP.CONTEXT_PATH}/api/tasks/reminders/upcoming?organisationId=${organisationId}&employeeId=${employeeId}`);
        const data = await res.json();
        const reminders = data?.data || [];

        if (!reminders.length) {
            container.html('<div class="text-muted small">No reminders in next 3 days</div>');
            return;
        }

        const html = reminders.map(r => `
            <div class="border-bottom mb-2 pb-1">
                <div class="fw-bold text-truncate" title="${r.title}">${r.title}</div>
                <div class="small text-muted text-truncate" title="${r.description || '-'}">
                    ${r.description || '-'}
                </div>
                <div class="small text-primary mt-1">
                    <i class="fas fa-clock me-1"></i>${formatDateTime(r.reminderAt)}
                </div>
            </div>
        `).join('');

        container.html(html);

    } catch (err) {
        console.error('Failed to load reminders', err);
        container.html('<div class="text-danger small">Failed to load reminders</div>');
    }
}

// Helper function to format LocalDateTime string from backend
function formatDateTime(dtString) {
    if (!dtString) return '-';
    const dt = new Date(dtString);
    return dt.toLocaleString('en-IN', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}




  // --------------------------
  // 2️⃣ Load Knowledge Base
  // --------------------------
  // loadKnowledgeBase(); 

//   function loadKnowledgeBase() {
//   const kbContainer = $("#knowledgeContainer");
//   kbContainer.html('<p class="text-center text-muted small mb-0"><i class="fa fa-spinner fa-spin"></i> Loading...</p>');

//   $.getJSON(`/api/kb/org/${orgId}/dashboard`, function (response) {
//     const subjects = response.data || response || [];
//     console.log("KB Dashboard:", subjects);
//     kbContainer.empty();

//     if (!subjects || subjects.length === 0) {
//       kbContainer.html('<p class="text-muted small mb-0">No knowledge base subjects found.</p>');
//       return;
//     }

//     const list = $('<ul class="list-group list-group-flush small"></ul>');
//     subjects.slice(0, 3).forEach(s => {
//       const item = $(`
//         <li class="list-group-item">
//           <div class="fw-bold text-primary">${s.title}</div>
//           <div class="text-muted small">${s.description || ''}</div>
//           <ul class="small ps-3 mt-1 mb-0">
//             ${(s.recentTopics && s.recentTopics.length > 0)
//               ? s.recentTopics.slice(0, 3).map(t => `
//                   <li>
//                     ${t.title}
//                     ${t.hasAttachment ? `<i class="fa fa-paperclip text-secondary ms-1"></i>` : ''}
//                   </li>
//                 `).join('')
//               : '<li class="text-muted">No topics yet.</li>'}
//           </ul>
//           <div class="mt-1 text-end">
//             <a href="#" class="text-decoration-none view-topics" data-id="${s.id}">
//               View All <i class="fa fa-chevron-right small"></i>
//             </a>
//           </div>
//         </li>
//       `);
//       list.append(item);
//     });

//     kbContainer.append(list);
//   }).fail(() => {
//     kbContainer.html('<p class="text-muted small mb-0">Failed to load knowledge base.</p>');
//   });
// }


  // --------------------------
  // 3️⃣ Load Topics when clicked
  // --------------------------
//    

  // function loadTopics(subjectId) {
  //   const kbContainer = $("#knowledgeContainer");
  //   kbContainer.html('<p class="text-center text-muted small mb-0"><i class="fa fa-spinner fa-spin"></i> Loading topics...</p>');

  //   $.getJSON(`/api/kb/org/${orgId}/subjects/${subjectId}/with-topics`, function (response) {
  //     const subject = response.data || response;
  //     kbContainer.empty();

  //     kbContainer.append(`
  //       <div class="mb-2">
  //         <button class="btn btn-sm btn-outline-secondary mb-2" id="kbBackBtn">
  //           <i class="fa fa-arrow-left"></i> Back
  //         </button>
  //         <h6 class="fw-bold mb-1">${subject.title}</h6>
  //         <p class="small text-muted">${subject.description || ''}</p>
  //       </div>
  //     `);

  //     if (!subject.topics || subject.topics.length === 0) {
  //       kbContainer.append('<p class="text-muted small mb-0">No topics available.</p>');
  //       return;
  //     }

  //     const list = $('<ul class="list-group list-group-flush small"></ul>');
  //     subject.topics.forEach(t => {
  //       list.append(`
  //         <li class="list-group-item">
  //           <div class="fw-semibold">${t.title}</div>
  //           <div class="text-muted">${t.content ? t.content.substring(0, 80) + '...' : ''}</div>
  //           ${t.attachmentUrl ? `<a href="${t.attachmentUrl}" target="_blank" class="small text-decoration-none">
  //             <i class="fa fa-paperclip"></i> View Attachment
  //           </a>` : ''}
  //         </li>
  //       `);
  //     });

  //     kbContainer.append(list);
  //     $("#kbBackBtn").click(() => loadKnowledgeBase());
  //   }).fail(() => {
  //     $("#knowledgeContainer").html('<p class="text-muted small mb-0">Failed to load topics.</p>');
  //   });
  // }

  // --------------------------
  // Helpers
  // --------------------------
  function renderList(container, items, mapFn) {
    const el = $(container);
    el.empty();
    if (!items || items.length === 0) {
      el.html('<p class="text-muted small mb-0">No upcoming items.</p>');
      return;
    }
    const list = $('<ul class="list-group list-group-flush small"></ul>');
    items.forEach(i => list.append(`<li class="list-group-item">${mapFn(i)}</li>`));
    el.append(list);
  }

  function formatDate(d) {
    if (!d) return "";
    return new Date(d).toLocaleDateString("en-GB", { day: "2-digit", month: "short" });
  }
});
