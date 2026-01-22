document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("createOrgForm");
    const orgId = extractOrgIdFromUrl();
    const isEditMode = !!orgId;

    if (isEditMode) {
        document.querySelector("h3").textContent = "Edit Organisation";
        document.querySelector("button[type='submit']").textContent = "Save Changes";
        loadOrganisation(orgId);
        disableAdminFields();
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const payload = {
            name: val("orgName"),
            domain: val("orgDomain"),
            logoUrl: val("logoUrl"),
            contactEmail: val("orgEmail"),
            contactPhone: val("orgPhone"),
            address: val("orgAddress"),
            country: val("orgCountry"),
            pan: val("orgPan"),
            tan: val("orgTan"),
            validity: val("validity"),
            maxEmployees: parseInt(val("maxEmployees") || 0),
        };

      

        // Only include admin data if creating a new org
        if (!isEditMode) {
            payload.adminFullName = val("adminFullName");
            payload.adminEmail = val("adminEmail");
            payload.adminPassword = val("adminPassword");
            console.log("org, admin create mode payload data", payload);
        }


        try {
            const url = isEditMode
                ? `/api/superadmin/organisation/${orgId}`
                : `/api/superadmin/organisation`;

            const method = isEditMode ? "PUT" : "POST";

            const res = await fetch(url, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                showToast("error", isEditMode ? "Failed to update organisation" : "Failed to create organisation");
                throw new Error("Request failed");
            }

            showToast("success", isEditMode ? "Organisation updated successfully!" : "Created successfully!");
            setTimeout(() => (window.location.href = "/superadmin/organisations"), 800);


        } catch (err) {
            console.error(err);
            showToast("error", "Error while saving organisation.");
        }
    });

    // Helpers
    function val(id) {
        return document.getElementById(id).value.trim();
    }

    function extractOrgIdFromUrl() {
        const match = window.location.pathname.match(/\/edit\/(\d+)/);
        return match ? match[1] : null;
    }

    async function loadOrganisation(id) {
        try {
            const res = await fetch(`/api/superadmin/organisation/${id}`);
            if (!res.ok) throw new Error("Failed to fetch organisation");
            const org = await res.json();
            console.log(org)

            document.getElementById("orgName").value = org.name || "";
            document.getElementById("orgDomain").value = org.domain || "";
            document.getElementById("logoUrl").value = org.logoUrl || "";
            document.getElementById("orgEmail").value = org.contactEmail || "";
            document.getElementById("orgPhone").value = org.contactPhone || "";
            document.getElementById("orgAddress").value = org.address || "";
            document.getElementById("orgCountry").value = org.country || "";
            document.getElementById("orgPan").value = org.pan || "";
            document.getElementById("orgTan").value = org.tan || "";
            document.getElementById("validity").value = org.validity ? org.validity.split("T")[0] : org.validity;
            document.getElementById("maxEmployees").value = org.maxEmployees || "";

            // Optional: show admin info in disabled mode
            if (org.adminEmail) {
                document.getElementById("adminFullName").value = org.adminFullName || "";
                document.getElementById("adminEmail").value = org.adminEmail || "";
            }
        } catch (err) {
            console.error("Error loading organisation data:", err);
            showToast("error", "Failed to load organisation details.");
        }
    }

    function disableAdminFields() {
        const adminFields = ["adminFullName", "adminEmail", "adminPassword"];
        adminFields.forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                el.disabled = true;
                el.closest(".col-md-6, .col-md-4").style.display = "none"; // Hide if you prefer
            }
        });

        const adminHeader = document.querySelector("h5.mt-4");
        if (adminHeader) adminHeader.style.display = "none";
    }
});
