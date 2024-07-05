package org.pahappa.systems.requisitionapp.models;

import javax.persistence.OneToOne;
import java.util.Arrays;
import java.util.Objects;

public class Accountability {
    private long id;
    private String description;
    private byte[] image;
    private int amountUsed;

    @OneToOne
    private Requisition requisition;

    public Accountability() {}

    private Accountability(String description, byte[] image, int amountUsed) {
        this.description = description;
        this.image = image;
        this.amountUsed = amountUsed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getAmountUsed() {
        return amountUsed;
    }

    public void setAmountUsed(int amountUsed) {
        this.amountUsed = amountUsed;
    }

    public Requisition getRequisition() {
        return requisition;
    }

    public void setRequisition(Requisition requisition) {
        this.requisition = requisition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accountability that = (Accountability) o;
        return id == that.id && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    @Override
    public String toString() {
        return "Accountability{" +
                "description='" + description + '\'' +
                '}';
    }
}
