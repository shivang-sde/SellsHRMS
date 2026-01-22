<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="announcements-view">
  <div class="mb-4">
    <h2 class="mb-1">Announcements</h2>
    <p class="text-muted mb-0">View company announcements and updates</p>
  </div>

  <div class="card border-0 shadow-sm">
    <div class="card-body">
      <div id="announcementsList">
        <div class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
.announcement-card {
  border-left: 4px solid #0d6efd;
  transition: all 0.2s;
}
.announcement-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
</style>