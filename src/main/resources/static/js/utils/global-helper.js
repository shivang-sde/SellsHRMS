// ---------- Global Toast Helper ----------
function showToast(type, message) {
  const container =
    document.getElementById("toast-container") ||
    (function () {
      const c = document.createElement("div");
      c.id = "toast-container";
      c.className = "toast-container";
      document.body.appendChild(c);
      return c;
    })();

  if (container && container.children.length >= 3) {
    container.removeChild(container.firstChild);
  }

  const icons = {
    success: "fa-circle-check text-success",
    error: "fa-circle-xmark text-danger",
    info: "fa-circle-info text-primary",
    warning: "fa-triangle-exclamation text-warning",
  };

  const borderColors = {
    success: "#10b981",
    error: "#ef4444",
    warning: "#f59e0b",
    info: "#3b82f6",
  };

  const toast = document.createElement("div");
  toast.className = "hrms-toast";
  toast.style.borderLeftColor = borderColors[type] || "#3b82f6";
  toast.innerHTML = `
    <i class="fa-solid ${icons[type] || icons.info}"></i> 
    <span>${message}</span>
    <button type="button" class="toast-close">&times;</button>
  `;
  container.appendChild(toast);

  const closeBtn = toast.querySelector(".toast-close");
  closeBtn.onclick = () => dismissToast(toast);

  const autoDismiss = setTimeout(() => dismissToast(toast), 5000);

  function dismissToast(el) {
    clearTimeout(autoDismiss);
    el.style.opacity = "0";
    el.style.transform = "translateX(20px)";
    setTimeout(() => el.remove(), 400);
  }
}

// ---------- Global Confirmation Helper ----------
function showConfirmation(config) {
  return new Promise((resolve) => {
    const modal = document.getElementById("errorModal");
    if (!modal) return resolve(false);

    document.getElementById("errorTitle").textContent =
      config.title || "Confirm Action";
    document.getElementById("errorMessage").textContent =
      config.message || "Are you sure?";

    const confirmBtn = document.getElementById("errorConfirm");
    const cancelBtn = document.getElementById("errorCancel");
    const iconWrap = document.querySelector(".modal-icon");

    confirmBtn.textContent = config.confirmText || "Confirm";
    confirmBtn.className = `btn-primary-hrms ${config.confirmClass || ""}`;

    cancelBtn.style.display = "block";
    iconWrap.innerHTML = `<i class="fa-solid fa-circle-question"></i>`;
    iconWrap.style.color = "#3b82f6";

    modal.style.display = "flex";

    confirmBtn.onclick = () => {
      modal.style.display = "none";
      resolve(true);
    };
    cancelBtn.onclick = () => {
      modal.style.display = "none";
      resolve(false);
    };
  });
}

// ---------- Global Intervention Helper ----------
function showIntervention(config) {
  const modal = document.getElementById("errorModal");
  if (!modal) return;

  document.getElementById("errorTitle").textContent = config.title;
  document.getElementById("errorMessage").textContent = config.message;

  const confirmBtn = document.getElementById("errorConfirm");
  const cancelBtn = document.getElementById("errorCancel");
  const iconWrap = document.querySelector(".modal-icon");

  confirmBtn.textContent = config.confirmText || "Okay";
  cancelBtn.textContent = config.cancelText || "Dismiss";
  cancelBtn.style.display = config.hideCancel ? "none" : "block";

  iconWrap.innerHTML = `<i class="fa-solid ${
    config.icon || "fa-circle-info"
  }"></i>`;
  iconWrap.style.color =
    config.severity === "critical" ? "#ef4444" : "#f59e0b";

  modal.style.display = "flex";

  confirmBtn.onclick = () => {
    if (config.onConfirm) config.onConfirm();
    modal.style.display = "none";
  };
  cancelBtn.onclick = () => {
    if (config.onCancel) config.onCancel();
    modal.style.display = "none";
  };
}

// ---------- Global AJAX Error Interceptor ----------
$(document).ajaxError(function (event, jqxhr) {
  try {
    const res = jqxhr.responseJSON || JSON.parse(jqxhr.responseText || "{}");
    const code = res.errorCode;

    if (code === "ORG_LICENSE_EXPIRED" || code === "ORG_INACTIVE") {
      showIntervention({
        title: "Action Required",
        message:
          "Your organization's subscription is inactive. Please contact your billing administrator.",
        icon: "fa-file-invoice-dollar",
        severity: "critical",
        confirmText: "Contact Support",
        hideCancel: true,
        onConfirm: () => {
          window.location.href = "mailto:support@sellshrms.com";
        },
      });
    } else if (code === "ORG_NOT_ASSIGNED") {
      showIntervention({
        title: "Account Pending",
        message:
          "Your profile is not yet linked to an organization. Please wait for HR setup.",
        icon: "fa-building-shield",
        severity: "warning",
        confirmText: "Back to Login",
        onConfirm: () => {
          window.location.href = window.APP.CONTEXT_PATH + "/login";
        },
      });
    } else if (code === "ACCESS_DENIED" || jqxhr.status === 403) {
      showToast("error", "Access Denied: You do not have permission for this action.");
    } else if (jqxhr.status === 401) {
      showToast("info", "Session expired. Redirecting to login...");
      setTimeout(
        () => (window.location.href = window.APP.CONTEXT_PATH + "/login"),
        1500
      );
    } else {
      showToast("error", res.message || "An unexpected system error occurred.");
    }
  } catch (e) {
    showToast("error", "Communication error with the server.");
  }
});

// ---------- Expose globally ----------
window.showToast = showToast;
window.showConfirmation = showConfirmation;
window.showIntervention = showIntervention;

// Allow modal to close on background click
document.addEventListener("click", (e) => {
  const modal = document.getElementById("errorModal");
  if (modal && e.target === modal) modal.style.display = "none";
});
