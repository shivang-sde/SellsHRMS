package com.sellspark.SellsHRMS.config;

import com.sellspark.SellsHRMS.entity.Project.ProjectType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectFeatureConfig {
    private boolean hasEpic;
    private boolean hasSprint;
    private boolean hasMilestone;

    public static ProjectFeatureConfig from(ProjectType type) {
        return switch (type) {
            case SOFTWARE_DEVELOPMENT, MARKETING -> new ProjectFeatureConfig(true, true, true);
            case DESIGN, SALES, FINANCE, OPERATIONS -> new ProjectFeatureConfig(true, false, true);
            case HR -> new ProjectFeatureConfig(false, false, true);
            case IT_SUPPORT, CUSTOMER_SERVICE, OTHER_PERSONAL -> new ProjectFeatureConfig(false, false, false);
        };
    }
}
