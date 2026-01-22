<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="events-view">
  <div class="mb-4">
    <h2 class="mb-1">Upcoming Events</h2>
    <p class="text-muted mb-0">View upcoming company events and activities</p>
  </div>

  <div class="card border-0 shadow-sm">
    <div class="card-body">
      <div id="eventsList">
        <div class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
.event-card {
  border-left: 4px solid #198754;
  transition: all 0.2s;
}
.event-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.date-badge {
  min-width: 80px;
}
</style>