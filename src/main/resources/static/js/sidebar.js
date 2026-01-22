/**
 * HRMS Sidebar - Final Enterprise UX Version
 * Smooth transitions + icon-only collapse mode with hover tooltips
 */
document.addEventListener("DOMContentLoaded", () => {
  const sidebar = document.getElementById("hrmsSidebar");
  const toggleBtn = document.getElementById("sidebarToggle");
  const collapseBtn = document.getElementById("sidebarCollapseBtn");
  const overlay = document.getElementById("sidebarOverlay");
  const toggleLinks = document.querySelectorAll(".toggle-link");
  const main = document.querySelector(".hrms-main");

  /* --------------------------------------------------
     Helper: Collapse / Expand Sidebar
  -------------------------------------------------- */
  const collapseSidebar = (force = null) => {
    const isCollapsed = force ?? !sidebar.classList.contains("collapsed");
    sidebar.classList.toggle("collapsed", isCollapsed);
    main.classList.toggle("collapsed", isCollapsed);

    if (isCollapsed) {
      // Close all submenus
      document.querySelectorAll(".sub-menu.open").forEach(menu => {
        menu.classList.remove("open");
        menu.style.maxHeight = null;
      });
      document.querySelectorAll(".toggle-link.open").forEach(link => link.classList.remove("open"));
    }

    // Refresh tooltips after animation
    setTimeout(initTooltips, 300);
  };

  collapseBtn?.addEventListener("click", () => collapseSidebar());

  /* --------------------------------------------------
     Toggle Sidebar (Mobile vs Desktop)
  -------------------------------------------------- */
  toggleBtn?.addEventListener("click", () => {
    const isMobile = window.innerWidth <= 992;

    if (isMobile) {
      sidebar.classList.toggle("mobile-open");
      overlay?.classList.toggle("visible", sidebar.classList.contains("mobile-open"));
      document.body.classList.toggle("sidebar-active", sidebar.classList.contains("mobile-open"));
    } else {
      collapseSidebar();
    }

    document.dispatchEvent(new CustomEvent("sidebar:toggle"));
  });

  overlay?.addEventListener("click", () => {
    sidebar.classList.remove("mobile-open");
    overlay.classList.remove("visible");
    document.body.classList.remove("sidebar-active");
  });

  /* --------------------------------------------------
     Submenu Toggle
  -------------------------------------------------- */
  toggleLinks.forEach(link => {
    link.addEventListener("click", e => {
      e.preventDefault();
      if (sidebar.classList.contains("collapsed")) return; // prevent submenu toggle when collapsed

      const submenu = link.nextElementSibling;
      if (!submenu) return;

      const isOpen = submenu.classList.contains("open");

      document.querySelectorAll(".sub-menu.open").forEach(menu => {
        if (menu !== submenu && !menu.contains(submenu)) {
          menu.classList.remove("open");
          menu.style.maxHeight = null;
          menu.previousElementSibling?.classList.remove("open");
        }
      });

      submenu.classList.toggle("open", !isOpen);
      link.classList.toggle("open", !isOpen);

      if (!isOpen) {
        submenu.style.maxHeight = submenu.scrollHeight + "px";
        setTimeout(() => {
          if (submenu.classList.contains("open")) submenu.style.maxHeight = "none";
        }, 250);
      } else {
        submenu.style.maxHeight = submenu.scrollHeight + "px";
        requestAnimationFrame(() => {
          submenu.style.maxHeight = "0px";
        });
      }
    });
  });

  /* --------------------------------------------------
     Click Outside → Close Mobile Sidebar
  -------------------------------------------------- */
  document.addEventListener("click", e => {
    if (
      window.innerWidth <= 992 &&
      sidebar.classList.contains("mobile-open") &&
      !sidebar.contains(e.target) &&
      !toggleBtn.contains(e.target)
    ) {
      sidebar.classList.remove("mobile-open");
      overlay?.classList.remove("visible");
      document.body.classList.remove("sidebar-active");
    }
  });

  /* --------------------------------------------------
     Reset on Resize
  -------------------------------------------------- */
  window.addEventListener("resize", () => {
    if (window.innerWidth > 992) {
      sidebar.classList.remove("mobile-open");
      overlay?.classList.remove("visible");
      document.body.classList.remove("sidebar-active");
    }
  });

  /* --------------------------------------------------
     Active Link Highlight
  -------------------------------------------------- */
  const currentPath = window.location.pathname;
  document.querySelectorAll(".sidebar-nav a").forEach(a => {
    if (a.href.includes(currentPath) && !a.classList.contains("toggle-link")) {
      a.classList.add("active");
      const submenu = a.closest(".sub-menu");
      if (submenu) {
        submenu.classList.add("open");
        submenu.style.maxHeight = "none";
        submenu.previousElementSibling?.classList.add("open");
      }
    }
  });

  /* --------------------------------------------------
     Tooltips for Collapsed Mode
  -------------------------------------------------- */
  const initTooltips = () => {
    // Remove existing
    document.querySelectorAll(".nav-link[data-bs-toggle='tooltip']").forEach(el => {
      bootstrap.Tooltip.getInstance(el)?.dispose();
    });

    // Apply tooltip only when collapsed
    if (sidebar.classList.contains("collapsed")) {
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

  // Initialize once
  initTooltips();
});
