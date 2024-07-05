package org.pahappa.systems.requisitionapp.models;

import java.util.Arrays;
import java.util.Objects;

public class Accountability {
    private long id;
    private String description;
    private byte[] image;
    private int amountUsed;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accountability that = (Accountability) o;
        return id == that.id && amountUsed == that.amountUsed && Objects.equals(description, that.description) && Objects.deepEquals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, Arrays.hashCode(image), amountUsed);
    }

    @Override
    public String toString() {
        return "Accountability{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", image=" + Arrays.toString(image) +
                ", amountUsed=" + amountUsed +
                '}';
    }

}
