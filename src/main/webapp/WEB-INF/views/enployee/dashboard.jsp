<div class="card">
    <div class="card-header">
        <h3 class="card-title">My Dashboard</h3>
    </div>
    <div class="card-body">
        <p>Welcome back, <strong>
                <c:out value="${email}" />
            </strong></p>
        <p>Your permissions:
            <c:out value="${fn:length(permissions)}" />
        </p>
    </div>
</div>