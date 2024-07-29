package org.pahappa.systems.requisitionapp.models.utils;

import java.util.List;

public class PermissionCategory {
    private String categoryName;
    private List<String> permissions;

    public PermissionCategory(String categoryName, List<String> permissions) {
        this.categoryName = categoryName;
        this.permissions = permissions;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "PermissionCategory{" +
                "categoryName='" + categoryName + '\'' +
                '}';
    }
}
