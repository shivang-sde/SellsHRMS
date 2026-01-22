<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="kb-view">
  <!-- Header -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="mb-1">Knowledge Base</h2>
      <p class="text-muted mb-0">Browse organizational knowledge and documentation</p>
    </div>
    <div class="input-group" style="width: 300px;">
      <input type="search" class="form-control" id="searchTopics" placeholder="Search topics...">
      <button class="btn btn-outline-secondary" type="button" onclick="searchTopics()">
        <i class="fa fa-search"></i>
      </button>
    </div>
  </div>

  <!-- Subjects Grid -->
  <div class="row" id="subjectsGrid">
    <div class="col-12 text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
  </div>
</div>

<!-- View Subject with Topics Modal -->
<div class="modal fade" id="viewSubjectModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <div>
          <h5 class="modal-title" id="viewSubjectTitle"></h5>
          <small class="text-muted" id="viewSubjectDescription"></small>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <h6 class="mb-3">Topics</h6>
        <div id="topicsList">
          <div class="text-center py-4">
            <div class="spinner-border text-primary" role="status"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- View Topic Details Modal -->
<div class="modal fade" id="viewTopicModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="topicTitle"></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3" id="topicContent" style="white-space: pre-wrap;"></div>
        <div id="topicAttachment"></div>
      </div>
    </div>
  </div>
</div>

<!-- Search Results Modal -->
<div class="modal fade" id="searchResultsModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Search Results</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div id="searchResultsList">
          <div class="text-center py-4">
            <div class="spinner-border text-primary" role="status"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
.subject-card {
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  height: 100%;
}

.subject-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.15) !important;
}

.topic-item {
  border-left: 3px solid #0d6efd;
  transition: all 0.2s;
  cursor: pointer;
}

.topic-item:hover {
  background-color: #f8f9fa;
  border-left-color: #0b5ed7;
}
</style>