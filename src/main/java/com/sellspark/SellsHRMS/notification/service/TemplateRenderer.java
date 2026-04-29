package com.sellspark.SellsHRMS.notification.service;

import java.util.Map;

public interface TemplateRenderer {

    public String render(String template, Map<String, Object> vars);
}
