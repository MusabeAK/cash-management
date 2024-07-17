package org.pahappa.systems.requisitionapp.models.utils;

import java.util.ArrayList;
import java.util.List;

public class StatusPipeline {
    private String label;
    private int percentage;

    public StatusPipeline(String label, int percentage) {
        this.label = label;
        this.percentage = percentage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }


    public static int getOverallProgress(RequisitionStatus status) {
        switch (status) {
            case DRAFT:
                return 0;
            case SUBMITTED:
                return 20;
            case HR_REVIEWED:
                return 50;
            case CEO_APPROVED:
                return 80;
            case DISBURSED:
                return 100;
            case REJECTED:
                return 0; // or any other value you deem appropriate for rejected status
            default:
                return 0;
        }
    }

}
