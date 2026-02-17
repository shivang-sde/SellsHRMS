package com.sellspark.SellsHRMS.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@ControllerAdvice
public class SidebarAdvice {
    @ModelAttribute("activeModules")
    public List<String> injectActiveModules(HttpSession session) {
        Object mods = session.getAttribute("MODULES");
        return mods instanceof List<?> ? (List<String>) mods : List.of();
    }
}
