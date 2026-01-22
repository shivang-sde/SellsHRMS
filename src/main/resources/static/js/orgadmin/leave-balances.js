$(document).ready(function() {
  let allBalances = [];
  let filteredBalances = [];
  const orgId = window.APP.ORG_ID;

  // Initialize
  init();

  function init() {
    loadLeaveBalances();
    loadFilters();
    setupEventListeners();
  }

  function setupEventListeners() {
    // Search and filter
    $('#employeeSearch').on('keyup', applyFilters);
    $('#departmentFilter').on('change', applyFilters);
    $('#leaveTypeFilter').on('change', applyFilters);
    $('#financialYearFilter').on('change', applyFilters);

    // Table row click
    $('#balancesTableBody').on('click', 'tr', function() {
      const balanceId = $(this).data('id');
      if (balanceId) {
        showBalanceDetails(balanceId);
      }
    });
  }

  // Load leave balances
  function loadLeaveBalances() {
    $.ajax({
      url: `/api/leaves/org/${orgId}/balances`,
      method: 'GET',
      success: function(response) {
        if (response.success) {
          allBalances = response.data;
          filteredBalances = allBalances;
          displayBalances(filteredBalances);
          updateSummary(filteredBalances);
        }
      },
      error: function(xhr) {
        $('#balancesTableBody').html(`
          <tr>
            <td colspan="12" class="text-center text-danger py-4">
              Error loading leave balances. Please try again.
            </td>
          </tr>
        `);
      }
    });
  }

  function displayBalances(balances) {
    const tbody = $('#balancesTableBody');
    tbody.empty();

    if (balances.length === 0) {
      tbody.append(`
        <tr>
          <td colspan="12" class="text-center py-5">
            <i class="fa fa-inbox fa-3x text-muted mb-3"></i>
            <p class="text-muted">No leave balance records found</p>
          </td>
        </tr>
      `);
      $('#recordCount').text('0 records');
      return;
    }

    balances.forEach(balance => {
      const balanceValue = balance.closingBalance;
      const balanceClass = balanceValue > 5 ? 'balance-positive' : 
                           balanceValue > 0 ? 'balance-low' : 'balance-negative';

      const row = `
        <tr data-id="${balance.id}" data-employee="${balance.employeeName.toLowerCase()}" 
            data-department="${balance.departmentName.toLowerCase()}" 
            data-leavetype="${balance.leaveTypeName.toLowerCase()}">
          <td>
            <strong>${balance.employeeName}</strong>
          </td>
          <td>${balance.employeeCode}</td>
          <td>${balance.departmentName}</td>
          <td>${balance.leaveTypeName}</td>
          <td>
            <span class="badge ${balance.isPaid ? 'bg-success' : 'bg-secondary'} badge-sm">
              ${balance.isPaid ? 'Paid' : 'Unpaid'}
            </span>
          </td>
          <td>${balance.openingBalance.toFixed(1)}</td>
          <td>${balance.accrued.toFixed(1)}</td>
          <td class="text-danger">${balance.availed.toFixed(1)}</td>
          <td class="${balanceClass}">${balanceValue.toFixed(1)}</td>
          <td>${balance.carriedForward.toFixed(1)}</td>
          <td>${balance.encashed.toFixed(1)}</td>
          <td><small>${balance.leaveYear}</small></td>
        </tr>
      `;
      tbody.append(row);
    });

    $('#recordCount').text(`${balances.length} records`);
  }

  function updateSummary(balances) {
    const uniqueEmployees = new Set(balances.map(b => b.employeeId)).size;
    const totalAllocated = balances.reduce((sum, b) => sum + b.closingBalance, 0);
    const totalAvailed = balances.reduce((sum, b) => sum + b.availed, 0);
    const totalRemaining = totalAllocated - totalAvailed;

    $('#totalEmployees').text(uniqueEmployees);
    $('#totalAllocated').text(totalAllocated.toFixed(1));
    $('#totalAvailed').text(totalAvailed.toFixed(1));
    $('#totalRemaining').text(totalRemaining.toFixed(1));
  }

  // Load filters
  function loadFilters() {
    // Load leave types for filter
    $.ajax({
      url: `/api/leave-type/org/${orgId}`,
      method: 'GET',
      success: function(response) {
        const select = $('#leaveTypeFilter');
        response.forEach(type => {
          select.append(`<option value="${type.name}">${type.name}</option>`);
        });
      }
    });

    // Extract unique departments from balances
    // This will be populated after balances are loaded
    setTimeout(() => {
      const departments = [...new Set(allBalances.map(b => b.departmentName))];
      const select = $('#departmentFilter');
      departments.forEach(dept => {
        if (dept && dept !== '-') {
          select.append(`<option value="${dept}">${dept}</option>`);
        }
      });
    }, 500);
  }

  // Apply filters
  function applyFilters() {
    const searchTerm = $('#employeeSearch').val().toLowerCase();
    const department = $('#departmentFilter').val().toLowerCase();
    const leaveType = $('#leaveTypeFilter').val().toLowerCase();

    filteredBalances = allBalances.filter(balance => {
      const matchesSearch = searchTerm === '' || 
        balance.employeeName.toLowerCase().includes(searchTerm) ||
        balance.employeeCode.toLowerCase().includes(searchTerm);
      
      const matchesDepartment = department === '' || 
        balance.departmentName.toLowerCase() === department;
      
      const matchesLeaveType = leaveType === '' || 
        balance.leaveTypeName.toLowerCase() === leaveType;

      return matchesSearch && matchesDepartment && matchesLeaveType;
    });

    displayBalances(filteredBalances);
    updateSummary(filteredBalances);
  }

  // Show balance details
  function showBalanceDetails(balanceId) {
    const balance = allBalances.find(b => b.id === balanceId);
    if (!balance) return;

    $('#detailEmployeeName').text(balance.employeeName);
    $('#detailEmployeeCode').text(balance.employeeCode);
    $('#detailLeaveType').html(`
      ${balance.leaveTypeName} 
      <span class="badge ${balance.isPaid ? 'bg-success' : 'bg-secondary'} ms-2">
        ${balance.isPaid ? 'Paid' : 'Unpaid'}
      </span>
    `);
    $('#detailFY').text(balance.financialYear);
    $('#detailOpening').text(balance.openingBalance.toFixed(1));
    $('#detailAccrued').text(balance.accrued.toFixed(1));
    $('#detailAvailed').text(balance.availed.toFixed(1));
    $('#detailBalance').text((balance.closingBalance - balance.availed).toFixed(1));
    $('#detailCarriedForward').text(balance.carriedForward.toFixed(1));
    $('#detailEncashed').text(balance.encashed.toFixed(1));

    $('#balanceDetailsModal').modal('show');
  }

  // Export to Excel
  window.exportToExcel = function() {
    if (filteredBalances.length === 0) {
      showToast('error', 'No data to export');
      return;
    }

    // Create CSV content
    let csv = 'Employee Name,Employee Code,Department,Leave Type,Type,Opening Balance,Accrued,Availed,Balance,Carried Forward,Encashed,Financial Year\n';
    
    filteredBalances.forEach(balance => {
      const balanceValue = balance.closingBalance - balance.availed;
      csv += `"${balance.employeeName}","${balance.employeeCode}","${balance.departmentName}","${balance.leaveTypeName}",`;
      csv += `"${balance.isPaid ? 'Paid' : 'Unpaid'}",${balance.openingBalance},${balance.accrued},${balance.availed},`;
      csv += `${balanceValue},${balance.carriedForward},${balance.encashed},"${balance.financialYear}"\n`;
    });

    // Create download link
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `leave_balances_${new Date().toISOString().split('T')[0]}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    showToast('success', 'Leave balances exported successfully');
  };
});