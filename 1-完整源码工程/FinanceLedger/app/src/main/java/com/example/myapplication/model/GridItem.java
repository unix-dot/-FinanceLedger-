package com.example.myapplication.model;

/**
 * 金融功能矩阵项目实体类
 */
public class GridItem {
    private String name;
    private int iconResId;
    private String description;

    public GridItem(String name, int iconResId, String description) {
        this.name = name;
        this.iconResId = iconResId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getDescription() {
        return description;
    }
}
