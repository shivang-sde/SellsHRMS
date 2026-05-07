<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="notification-card m-4 alert alert-warning alert-dismissible fade show" role="alert">
        <div class="d-flex align-items-start">
            <div class="icon-wrapper">
                <i class="fa-solid fa-bell fa-lg"></i>
            </div>
            <div class="flex-grow-1 ms-3">
                <strong>
                    <i class="fa-regular fa-envelope"></i> Email Notification Setup Required
                </strong>
                <p class="mb-1 small">To receive email alerts when URLs go down or recover, you need to:</p>
                <ul class="small mb-1">
                    <li>
                        Configure <a href="${pageContext.request.contextPath}/org/notifications/smtp-settings"
                            class="link-slate">SMTP Settings</a>&nbsp;&nbsp;for Email server configuration
                    </li>
                    <li>
                        Enable notifications in <a
                            href="${pageContext.request.contextPath}/org/notifications/preferences"
                            class="link-slate">Notification Preferences</a>&nbsp;&nbsp;for URL Monitor events
                    </li>
                    <li>
                        Add users to <strong>Monitor Groups</strong> – Only group members receive alerts
                    </li>
                </ul>
                <div class="mt-2">
                    <span class="note-badge">
                        <i class="fa-solid fa-clock"></i> Notifications are only sent when URL status changes (DOWN or
                        UP)
                    </span>
                </div>
            </div>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>



    <style>
        .notification-card {
            background: #f9f9fb;
            /* soft slate white */
            border: 1px solid #e0e0e5;
            border-left: 4px solid #6c63ff;
            /* accent indigo/purple */
            border-radius: 8px;
            padding: 1rem 1.25rem;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
            color: #2c2c34;
        }

        .notification-card strong {
            color: #1f1f27;
        }

        .icon-wrapper {
            color: #6c63ff;
            /* accent icon */
        }

        .link-slate {
            color: #6c63ff;
            text-decoration: none;
            font-weight: 500;
        }

        .link-slate:hover {
            text-decoration: underline;
        }

        .note-badge {
            background: #ececf5;
            color: #333;
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
            border-radius: 6px;
        }
    </style>