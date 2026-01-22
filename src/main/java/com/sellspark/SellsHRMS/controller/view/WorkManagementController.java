package com.sellspark.SellsHRMS.controller.view;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/work")
public class WorkManagementController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Work Dashboard");
        model.addAttribute("contentPage", "work/dashboard");
        model.addAttribute("pageScript", "work/dashboard");
        return "layout/main-layout";
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        model.addAttribute("pageTitle", "Projects");
        model.addAttribute("contentPage", "work/projects");
        model.addAttribute("pageScript", "work/projects");
        return "layout/main-layout";
    }

    @GetMapping("/projects/{id}")
    public String projectDetail(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Project Details");
        model.addAttribute("contentPage", "work/project-detail");
        model.addAttribute("pageScript", "work/project-detail");
        model.addAttribute("projectId", id);
        return "layout/main-layout";
    }

    @GetMapping("/tasks")
    public String tasks(Model model) {
        model.addAttribute("pageTitle", "Tasks");
        model.addAttribute("contentPage", "work/tasks");
        model.addAttribute("pageScript", "work/tasks");
        return "layout/main-layout";
    }

    @GetMapping("/tasks/{id}")
    public String taskDetail(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Task Details");
        model.addAttribute("contentPage", "work/task-detail");
        model.addAttribute("pageScript", "work/task-detail");
        model.addAttribute("taskId", id);
        return "layout/main-layout";
    }

    @GetMapping("/tickets")
    public String tickets(Model model) {
        model.addAttribute("pageTitle", "Tickets");
        model.addAttribute("contentPage", "work/tickets");
        model.addAttribute("pageScript", "work/tickets");
        return "layout/main-layout";
    }

    @GetMapping("/tickets/{id}")
    public String ticketDetail(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Ticket Details");
        model.addAttribute("contentPage", "work/ticket-detail");
        model.addAttribute("pageScript", "work/ticket-detail");
        model.addAttribute("ticketId", id);
        return "layout/main-layout";
    }
}