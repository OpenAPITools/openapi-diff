package com.qdesrame.openapi.diff.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ListDiff<T> {

    private List<T> increased;
    private List<T> missing;
    private List<T> sharedItem;

    private ListDiff() {
        this.sharedItem = new ArrayList<>();
        this.increased = new ArrayList<>();
        this.missing = new ArrayList<>();
    }

    public static <T> ListDiff<T> diff(List<T> left, List<T> right) {
        ListDiff<T> instance = new ListDiff<>();
        if (left == null && right == null) {
            return instance;
        }
        if (left == null) {
            instance.increased = right;
            return instance;
        }
        if (right == null) {
            instance.missing = left;
            return instance;
        }
        instance.increased = new ArrayList<>(right);
        instance.missing = new ArrayList<>();
        for (T leftItem : left) {
            if (right.contains(leftItem)) {
                instance.increased.remove(leftItem);
                instance.sharedItem.add(leftItem);
            } else {
                instance.missing.add(leftItem);
            }
        }
        return instance;
    }

}
