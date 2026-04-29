package com.sellspark.SellsHRMS.notification.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.sellspark.SellsHRMS.notification.service.TemplateRenderer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateRendererServiceImpl implements TemplateRenderer {

    private final SpringTemplateEngine templateEngine;

    @Override
    public String render(String template, Map<String, Object> vars) {
        Context context = new Context();
        context.setVariables(vars);
        return templateEngine.process(template, context);
    }
}
