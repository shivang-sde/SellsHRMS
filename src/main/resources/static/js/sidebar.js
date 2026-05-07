/**
 * HRMS Sidebar - Professional Enterprise Version
 * Focus on stability, accessibility, and clean responsive behavior
 */
document.addEventListener("DOMContentLoaded", () => {
  const sidebar = document.getElementById("hrmsSidebar");
  const toggleBtn = document.getElementById("sidebarToggle");
  const overlay = document.getElementById("sidebarOverlay");
  const main = document.querySelector(".hrms-viewport");

  /* --------------------------------------------------
     Tooltip Management
  -------------------------------------------------- */
  const destroyTooltips = () => {
    document.querySelectorAll(".nav-link[data-bs-toggle='tooltip']").forEach(el => {
      const tooltip = bootstrap.Tooltip.getInstance(el);
      tooltip?.dispose();
      el.removeAttribute("data-bs-toggle");
      el.removeAttribute("title");
    });
  };

  const initTooltips = () => {
    destroyTooltips();
    if (sidebar.classList.contains("collapsed") && window.innerWidth > 992) {
      document.querySelectorAll(".nav-link").forEach(link => {
        const text = link.querySelector(".nav-text")?.textContent?.trim();
        if (text) {
          link.setAttribute("data-bs-toggle", "tooltip");
          link.setAttribute("data-bs-placement", "right");
          link.setAttribute("title", text);
          new bootstrap.Tooltip(link);
        }
      });
    }
  };

  /* --------------------------------------------------
     Toggle Logic (Desktop Collapse / Mobile Off-canvas)
  -------------------------------------------------- */
  toggleBtn?.addEventListener("click", (e) => {
    e.stopPropagation();
    const isMobile = window.innerWidth <= 992;

    if (isMobile) {
      sidebar.classList.toggle("mobile-open");
      overlay?.classList.toggle("visible", sidebar.classList.contains("mobile-open"));
    } else {
      sidebar.classList.toggle("collapsed");
      main.classList.toggle("collapsed", sidebar.classList.contains("collapsed"));
      initTooltips();
    }
  });

  // Close mobile sidebar on overlay click
  overlay?.addEventListener("click", () => {
    sidebar.classList.remove("mobile-open");
    overlay.classList.remove("visible");
  });

  /* --------------------------------------------------
     Submenu Handling
  -------------------------------------------------- */
  document.querySelectorAll(".toggle-link").forEach(link => {
    link.addEventListener("click", e => {
      e.preventDefault();
      
      // Don't open submenus if sidebar is collapsed
      if (sidebar.classList.contains("collapsed") && window.innerWidth > 992) return;

      const submenu = link.nextElementSibling;
      if (!submenu) return;

      const isOpen = submenu.classList.contains("open");

      // Accordion behavior: close other submenus
      document.querySelectorAll(".sub-menu.open").forEach(menu => {
        if (menu !== submenu) {
          menu.classList.remove("open");
          menu.style.maxHeight = null;
          menu.previousElementSibling?.classList.remove("open");
        }
      });

      submenu.classList.toggle("open", !isOpen);
      link.classList.toggle("open", !isOpen);
      submenu.style.maxHeight = !isOpen ? submenu.scrollHeight + "px" : null;
    });
  });

  /* --------------------------------------------------
     Auto-close mobile sidebar on link click
  -------------------------------------------------- */
  document.querySelectorAll(".sidebar-nav a").forEach(a => {
    // Highlight Active
    if (a.pathname === window.location.pathname && !a.classList.contains("toggle-link")) {
      a.classList.add("active");
      const parentSub = a.closest(".sub-menu");
      if (parentSub) {
        parentSub.classList.add("open");
        parentSub.style.maxHeight = "none";
        parentSub.previousElementSibling?.classList.add("open");
      }
    }

    a.addEventListener("click", () => {
      if (window.innerWidth <= 992 && !a.classList.contains("toggle-link")) {
        sidebar.classList.remove("mobile-open");
        overlay?.classList.remove("visible");
      }
    });
  });

  // Initial tooltip state
  initTooltips();

  // Reset on significant resize
  window.addEventListener("resize", () => {
    if (window.innerWidth > 992) {
      sidebar.classList.remove("mobile-open");
      overlay?.classList.remove("visible");
    }
    initTooltips();
  });
});
