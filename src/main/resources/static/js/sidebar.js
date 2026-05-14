/**
 * HRMS Sidebar — Final Enterprise Edition
 * Behavior:
 * - Desktop: Toggle collapse (Width 72px, no hover expand).
 * - Mobile: Off-canvas (Overlay closes on click).
 * - Submenus: Accordion style (One open at a time), close on collapse.
 */

document.addEventListener("DOMContentLoaded", () => {
  const sidebar = document.getElementById("hrmsSidebar");
  const overlay = document.getElementById("sidebarOverlay");
  const viewport = document.querySelector(".hrms-viewport");
  const toggleBtns = document.querySelectorAll(".sidebar-toggle-btn");
  
  const isMobile = () => window.innerWidth <= 992;

  /* --------------------------------------------------
     1. Sidebar Toggle Logic
  -------------------------------------------------- */
  const toggleSidebar = (e) => {
    e?.stopPropagation();
    
    if (isMobile()) {
      const isOpen = sidebar.classList.toggle("mobile-open");
      overlay?.classList.toggle("visible", isOpen);
      document.body.style.overflow = isOpen ? "hidden" : "";
    } else {
      const isCollapsed = sidebar.classList.toggle("collapsed");
      viewport?.classList.toggle("collapsed", isCollapsed);
      
      // REQUIREMENT: Close all submenus when collapsing
      if (isCollapsed) closeAllSubmenus();
      
      initTooltips();
    }
  };

  toggleBtns.forEach(btn => btn.addEventListener("click", toggleSidebar));

  overlay?.addEventListener("click", () => {
    sidebar.classList.remove("mobile-open");
    overlay.classList.remove("visible");
    document.body.style.overflow = "";
  });

  /* --------------------------------------------------
     2. Submenu Handling (Accordion)
  -------------------------------------------------- */
  const closeAllSubmenus = () => {
    document.querySelectorAll(".sub-menu.open").forEach(menu => {
      menu.classList.remove("open");
      menu.previousElementSibling?.classList.remove("open");
    });
  };

  document.querySelectorAll(".toggle-link").forEach(link => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      
      // REQUIREMENT: Submenus disabled when collapsed on desktop
      if (sidebar.classList.contains("collapsed") && !isMobile()) return;

      const submenu = link.nextElementSibling;
      if (!submenu) return;

      const isOpen = submenu.classList.contains("open");

      // Accordion behavior
      if (!isOpen) closeAllSubmenus();

      submenu.classList.toggle("open", !isOpen);
      link.classList.toggle("open", !isOpen);
    });
  });

  /* --------------------------------------------------
     3. Active State & Mobile Auto-Close
  -------------------------------------------------- */
  const setActiveLink = () => {
    const currentPath = window.location.pathname;
    document.querySelectorAll(".sidebar-nav a").forEach(a => {
      if (a.pathname === currentPath && !a.classList.contains("toggle-link")) {
        a.classList.add("active");
        
        // Auto-open parent submenu
        const parentSub = a.closest(".sub-menu");
        if (parentSub && !sidebar.classList.contains("collapsed")) {
          parentSub.classList.add("open");
          parentSub.previousElementSibling?.classList.add("open");
        }
      }

      // Mobile auto-close on link click
      a.addEventListener("click", () => {
        if (isMobile() && !a.classList.contains("toggle-link")) {
          sidebar.classList.remove("mobile-open");
          overlay?.classList.remove("visible");
          document.body.style.overflow = "";
        }
      });
    });
  };

  /* --------------------------------------------------
     4. Tooltip Management (Collapsed Only)
  -------------------------------------------------- */
  let activeTooltips = [];

  const initTooltips = () => {
    activeTooltips.forEach(t => t.dispose());
    activeTooltips = [];

    if (sidebar.classList.contains("collapsed") && !isMobile()) {
      document.querySelectorAll(".sidebar-nav .nav-link:not(.toggle-link)").forEach(link => {
        const text = link.querySelector(".nav-text")?.textContent?.trim();
        if (text) {
          const tooltip = new bootstrap.Tooltip(link, {
            title: text,
            placement: "right",
            trigger: "hover"
          });
          activeTooltips.push(tooltip);
        }
      });
    }
  };

  /* --------------------------------------------------
     5. Resize & Init
  -------------------------------------------------- */
  window.addEventListener("resize", () => {
    if (!isMobile()) {
      sidebar.classList.remove("mobile-open");
      overlay?.classList.remove("visible");
      document.body.style.overflow = "";
    }
    initTooltips();
  });

  setActiveLink();
  initTooltips();
});