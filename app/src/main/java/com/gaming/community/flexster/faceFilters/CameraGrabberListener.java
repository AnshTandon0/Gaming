package com.gaming.community.flexster.faceFilters;

@SuppressWarnings("ALL")
public interface CameraGrabberListener {
    void onCameraInitialized();
    void onCameraError(String errorMsg);
}
