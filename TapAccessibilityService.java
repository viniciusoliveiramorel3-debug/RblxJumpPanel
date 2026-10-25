package com.example.rblxjumppanel;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class TapAccessibilityService extends AccessibilityService {
    private static TapAccessibilityService instance;
    public static TapAccessibilityService get() { return instance; }
    @Override public void onServiceConnected() { super.onServiceConnected(); instance=this; }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
    @Override public void onDestroy() { if(instance==this) instance=null; super.onDestroy(); }
    public void tap(float x,float y) {
        Path path=new Path(); path.moveTo(x,y);
        GestureDescription.StrokeDescription stroke=new GestureDescription.StrokeDescription(path,0,65);
        dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(),null,null);
    }
}
