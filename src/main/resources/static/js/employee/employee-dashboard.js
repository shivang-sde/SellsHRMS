//employee-dashboard.js 
document.addEventListener("DOMContentLoaded", () => {
    const employeeId = document.getElementById("loggedEmployeeId").value;

    if (!employeeId) {
        showToast("error", "Invalid Session: Employee ID Missing");
        return;
    }

    loadEmployeeProfile(employeeId);
});


async function loadEmployeeProfile(id) {
    const container = document.getElementById("empDashboardContainer");

    try {
        const res = await fetch(`/api/employees/${id}`);
        if (!res.ok) throw new Error("Failed to load employee details");

        const emp = await res.json();

        container.innerHTML = `
            <div class="row">

                <div class="col-md-4">
                    <div class="card p-3 shadow-sm">
                        <h5 class="mb-2">Profile</h5>
                        <p><strong>Name:</strong> ${emp.fullName}</p>
                        <p><strong>Email:</strong> ${emp.email}</p>
                        <p><strong>Phone:</strong> ${emp.phone}</p>
                        <p><strong>Status:</strong> 
                            <span class="badge bg-${emp.status === 'ACTIVE' ? 'success' : 'secondary'}">
                                ${emp.status}
                            </span>
                        </p>
                    </div>
                </div>

                <div class="col-md-8">
                    <div class="card p-3 shadow-sm">
                        <h5 class="mb-3">Organisation Info</h5>

                        <p><strong>Organisation:</strong> ${emp.organisation}</p>
                        <p><strong>Department:</strong> ${emp.department}</p>
                        <p><strong>Designation:</strong> ${emp.designation}</p>

                        <hr>
                        <a class="btn btn-primary" href="/employee/profile">View Detailed Profile</a>
                    </div>
                </div>

            </div>
        `;
    } catch (err) {
        container.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
    }
}
