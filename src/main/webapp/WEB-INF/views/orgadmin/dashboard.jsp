<div class="row">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Overview</h3>
            </div>
            <div class="card-body">
                <p>Welcome, <strong>
                        <c:out value="${email}" />
                    </strong></p>
                <p>Modules:
                    <c:out value="${fn:length(modules)}" />
                </p>
            </div>
        </div>
    </div>
</div>