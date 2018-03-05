package com.ko.efarming.model;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * Created by admin on 3/5/2018.
 */

public class RectClass implements Serializable{
    public transient Rect rect;

    public RectClass(View view) {
        rect = new Rect();
        view.getDrawingRect(rect);
        ((ViewGroup) view.getParent()).offsetDescendantRectToMyCoords(view, rect);
    }

    public Rect getRect() {
        return rect;
    }
}
