LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
include ../../../../native/jni/OpenCV.mk
LOCAL_MODULE := OpenCV
LOCAL_SRC_FILES := com_gizmo_gsdk_logo_LibLogoRecog.cpp \
logoRecog.cpp
LOCAL_LDLIBS += -llog -ldl -lm -latomic
LOCAL_CFLAGS += -std=c++11
include $(BUILD_SHARED_LIBRARY)