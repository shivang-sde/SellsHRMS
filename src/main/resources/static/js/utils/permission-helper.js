// utils/permission-helper.js

window.APP = window.APP || {};

// Parse the permissions string "[PERM1, PERM2]" → ["PERM1", "PERM2"]
window.APP.getPermissions = function () {
    const raw = window.APP.PERMISSIONS || "";
    return raw.replace(/[\[\]\s]/g, "").split(",").filter(Boolean);
};

// Check if the user has a specific permission
window.APP.hasPermission = function (permission) {
    return window.APP.getPermissions().includes(permission);
};

// Check if the user has any of multiple permissions
window.APP.hasAnyPermission = function (...perms) {
    const userPerms = window.APP.getPermissions();
    return perms.some((p) => userPerms.includes(p));
};

// Optional: Auto-hide elements that require permissions
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-permission]").forEach((el) => {
        const required = el.dataset.permission;
        if (!window.APP.hasPermission(required)) {
            el.style.display = "none";
        }
    });
});

console.log("%c✅ Permission helper initialized", "color:green;");
