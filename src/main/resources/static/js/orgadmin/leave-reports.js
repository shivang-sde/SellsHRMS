$(document).ready(function() {
  const orgId = window.APP.ORG_ID || $('#globalOrgId').val();

  $.getJSON(`/api/leaves/stats`, function(resp) {
    if (!resp.success) return showToast('error', 'Failed to load stats');

    const stats = resp.data;

    // Chart 1 — Leave Status Distribution
    const ctx1 = document.getElementById('leaveStatusChart');
    new Chart(ctx1, {
      type: 'pie',
      data: {
        labels: Object.keys(stats.statusCount || {}),
        datasets: [{
          data: Object.values(stats.statusCount || {}),
          backgroundColor: ['#2563eb','#10b981','#f59e0b','#ef4444']
        }]
      },
      options: { plugins: { legend: { position: 'bottom' } } }
    });

    // Chart 2 — Leave Type Usage
    const ctx2 = document.getElementById('leaveTypeChart');
    new Chart(ctx2, {
      type: 'bar',
      data: {
        labels: Object.keys(stats.typeWiseCount || {}),
        datasets: [{
          label: 'Leaves Availed',
          data: Object.values(stats.typeWiseCount || {}),
          backgroundColor: '#3b82f6'
        }]
      },
      options: {
        responsive: true,
        scales: { y: { beginAtZero: true } },
        plugins: { legend: { display: false } }
      }
    });
  });
});
