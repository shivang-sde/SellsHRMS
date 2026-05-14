// ---------- Global Toast Helper ----------
const activeToasts = new Set();

function showToast(type, message) {
  if (!message) return;
  
  // Prevent duplicate toasts for the same message
  if (activeToasts.has(message)) return;
  activeToasts.add(message);

  const container =
    document.getElementById("toast-container") ||
    (function () {
      const c = document.createElement("div");
      c.id = "toast-container";
      c.className = "toast-container";
      document.body.appendChild(c);
      return c;
    })();

  // Keep max 3 toasts
  if (container.children.length >= 3) {
    const first = container.firstChild;
    if (first) dismissToast(first);
  }

  const icons = {
    success: "fa-circle-check",
    error: "fa-circle-xmark",
    info: "fa-circle-info",
    warning: "fa-triangle-exclamation",
  };

  const toast = document.createElement("div");
  toast.className = `hrms-toast hrms-toast-${type} showing`;
  toast.innerHTML = `
    <div class="toast-content">
        <i class="fa-solid ${icons[type] || icons.info}"></i> 
        <span class="toast-message">${message}</span>
    </div>
    <button type="button" class="toast-close" aria-label="Close">&times;</button>
    <div class="toast-progress"></div>
  `;
  container.appendChild(toast);

  const closeBtn = toast.querySelector(".toast-close");
  closeBtn.onclick = () => dismissToast(toast);

  const autoDismiss = setTimeout(() => dismissToast(toast), 5000);

  function dismissToast(el) {
    if (!el || !el.parentNode) return;
    activeToasts.delete(message);
    el.classList.replace("showing", "hiding");
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

  iconWrap.innerHTML = `<i class="fa-solid ${config.icon || "fa-circle-info"
    }"></i>`;
  iconWrap.style.color = config.severity === "critical" ? "#ef4444" : "#f59e0b";

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
let activeErrorModal = null;

$(document).ajaxError(function (event, jqxhr) {
  try {
    const res = jqxhr.responseJSON || JSON.parse(jqxhr.responseText || "{}");
    const code = res.errorCode;
    const message = res.message || "An unexpected system error occurred.";

    // Logic to prevent duplicate intervention modals
    if (activeErrorModal === code) return;

    if (code === "ORG_LICENSE_EXPIRED" || code === "ORG_INACTIVE") {
      activeErrorModal = code;
      showIntervention({
        title: "Action Required",
        message: "Your organization's subscription is inactive. Please contact your billing administrator.",
        icon: "fa-file-invoice-dollar",
        severity: "critical",
        confirmText: "Contact Support",
        hideCancel: true,
        onConfirm: () => {
          activeErrorModal = null;
          window.location.href = "mailto:support@sellshrms.com";
        },
      });
    } else if (code === "ORG_NOT_ASSIGNED") {
      activeErrorModal = code;
      showIntervention({
        title: "Account Pending",
        message: "Your profile is not yet linked to an organization. Please wait for HR setup.",
        icon: "fa-building-shield",
        severity: "warning",
        confirmText: "Back to Login",
        onConfirm: () => {
          activeErrorModal = null;
          window.location.href = window.APP.CONTEXT_PATH + "/login";
        },
        onCancel: () => { activeErrorModal = null; }
      });
    } else if (code === "ACCESS_DENIED" || jqxhr.status === 403) {
      showToast("error", "Access Denied: You do not have permission for this action.");
    } else if (jqxhr.status === 401) {
      // Session expiry often triggers multiple parallel requests
      if (activeErrorModal === 'SESSION_EXPIRED') return;
      activeErrorModal = 'SESSION_EXPIRED';
      
      showToast("info", "Session expired. Redirecting to login...");
      setTimeout(() => {
        window.location.href = window.APP.CONTEXT_PATH + "/login";
      }, 1500);
    } else {
      showToast("error", message);
    }
  } catch (e) {
    showToast("error", "An unexpected system error occurred.");
  }
});

async function validateImage(
  file,
  { minW = 100, minH = 100, maxW = 600, maxH = 600, maxSizeMB = 2 } = {},
) {
  return new Promise((resolve, reject) => {
    if (!file) {
      reject("No file selected.");
      return;
    }

    // ---- Basic checks ----
    const allowedTypes = [
      "image/png",
      "image/jpeg",
      "image/webp",
      "image/svg+xml",
    ];

    if (!allowedTypes.includes(file.type)) {
      reject("Please upload a PNG, JPG, WEBP or SVG logo.");
      return;
    }

    if (file.size > maxSizeMB * 1024 * 1024) {
      reject(`Logo size must be under ${maxSizeMB} MB.`);
      return;
    }

    // ---- Dimension check (skip for SVG) ----
    if (file.type === "image/svg+xml") {
      resolve(true); // SVG has no pixel dimensions
      return;
    }

    const img = new Image();
    img.src = URL.createObjectURL(file);

    img.onload = () => {
      const width = img.width;
      const height = img.height;

      if (width < minW || height < minH) {
        reject(
          `Logo too small (${width}×${height}). Minimum ${minW}×${minH}px required.`,
        );
        return;
      }

      if (width > maxW || height > maxH) {
        reject(
          `Logo too large (${width}×${height}). Maximum ${maxW}×${maxH}px allowed.`,
        );
        return;
      }

      const ratio = width / height;
      if (ratio < 0.5 || ratio > 2) {
        console.warn("Aspect ratio warning:", ratio);
        showToast(
          "warning",
          "Please upload a more square logo for best appearance.",
        );
      }

      resolve(true);
    };

    img.onerror = () => reject("Invalid image file.");
  });
}

function debounce(fn, delay) {
  let timeout;
  return (...args) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => fn(...args), delay);
  }
}

// ---------- Expose globally ----------
window.showToast = showToast;
window.showConfirmation = showConfirmation;
window.showIntervention = showIntervention;
window.validateImage = validateImage;
window.debounce = debounce;

// Allow modal to close on background click
document.addEventListener("click", (e) => {
  const modal = document.getElementById("errorModal");
  if (modal && e.target === modal) modal.style.display = "none";
});
