package org.pahappa.systems.requisitionapp.models.utils;

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
                return -1;
            case SUBMITTED:
                return 0;
            case REVIEWED:
                return 1;
            case APPROVED:
                return 2;
            case DISBURSED:
                return 3;
            case REJECTED:
                return -1; // or any other value you deem appropriate for rejected status
            default:
                return -1;
        }
    }

}
