<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="container-fluid p-3">
        <div class="row mb-4">
            <div class="col-12">
                <h4 class="fw-bold">Monitor Groups</h4>
                <p class="text-muted">Organize URLs into groups for notification management</p>
            </div>
        </div>

        <jsp:include page="_notification-info.jsp" />


        <div class="card border-0 shadow-sm">
            <div class="card-header bg-white border-0 pt-4 pb-0 d-flex justify-content-between align-items-center">
                <h5 class="fw-bold">Notification Groups</h5>
                <button class="btn btn-primary btn-sm" onclick="showCreateGroupModal()">
                    <i class="fa-solid fa-plus"></i> Create Group
                </button>


            </div>

            <!-- After the Create Group button -->
            <div class="alert alert-secondary border-start border-4 border-secondary mt-3 small">
                <i class="fa-solid fa-lightbulb text-warning"></i>
                <strong>Pro Tip:</strong> Add users to your groups to receive email notifications.
                Users will only get alerts after enabling URL Monitor notifications in
                <a href="${pageContext.request.contextPath}/org/notifications/preferences"
                    class="alert-link">Notification Preferences</a>.
            </div>


            <div class="card-body">
                <div class="table-responsive ">
                    <table class="table table-hover align-middle mb-0 overflow-y-auto ">
                        <thead class="table-light">
                            <!-- FIXED: Proper <tr> tag, removed nested <table> -->
                            <tr>
                                <th scope="col">Name</th>
                                <th scope="col">Description</th>
                                <th scope="col" class="text-center">URLs</th>
                                <th scope="col" class="text-center">Members</th>
                                <th scope="col">Created</th>
                                <th scope="col" class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="groupsTableBody">
                            <tr>
                                <td colspan="6" class="text-center py-5">
                                    <div class="spinner-border text-primary" role="status"></div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Create Group Modal -->
    <div class="modal fade" id="createGroupModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Create New Group</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="groupForm">
                        <div class="mb-3">
                            <label class="form-label">Group Name *</label>
                            <input type="text" class="form-control" id="groupName" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea class="form-control" id="groupDescription" rows="3"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="createGroup()">Create</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Group Detail Modal -->
    <div class="modal fade" id="groupDetailModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="groupDetailTitle">Group Details</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body" id="groupDetailBody"></div>
            </div>
        </div>
    </div>

    <!-- Add Member Offcanvas -->
    <div class="offcanvas offcanvas-end" tabindex="-1" id="addMemberOffcanvas" aria-labelledby="addMemberLabel">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title" id="addMemberLabel">Add Member</h5>
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="offcanvas-body">
            <input type="hidden" id="targetGroupId">

            <div class="mb-3">
                <label class="form-label">Search Users</label>
                <input type="text" class="form-control" id="memberSearchInput" placeholder="Name or Email...">
            </div>

            <div class="list-group" id="userSearchResults">
                <!-- Results injected here -->
            </div>
        </div>
    </div>



    <!-- Add URL Offcanvas -->
    <div class="offcanvas offcanvas-end" tabindex="-1" id="addUrlOffcanvas" aria-labelledby="addUrlLabel">
        <div class="offcanvas-header border-bottom">
            <h5 class="offcanvas-title" id="addUrlLabel">Add URL to Group</h5>
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="offcanvas-body">
            <input type="hidden" id="targetGroupIdForUrl">

            <div class="mb-3">
                <label class="form-label fw-bold">Search URLs</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fa-solid fa-search"></i></span>
                    <input type="text" class="form-control" id="urlSearchInput" placeholder="Type URL name...">
                </div>
            </div>

            <div class="list-group list-group-flush border rounded" id="urlSearchResults"
                style="max-height: 400px; overflow-y: auto;">
                <div class="list-group-item text-muted text-center py-3">Start typing to search...</div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/monitor/groups.js"></script>