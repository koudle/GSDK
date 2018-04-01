//
// Created by kl on 18-3-25.
//

#include "com_gizmo_gsdk_logo_LibLogoRecog.h"
#include "logoRecog.h"
#include <vector>
#include <string>
#include <fstream>
#include <iostream>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

extern "C" {
    LogoRecog rec;

    string jstring2str(JNIEnv* env, jstring jstr)
    {
        char*   rtn   =   NULL;
        jclass   clsstring   =   env->FindClass("java/lang/String");
        jstring   strencode   =   env->NewStringUTF("GB2312");
        jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");
        jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode);
        jsize   alen   =   env->GetArrayLength(barr);
        jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);
        if(alen   >   0)
        {
            rtn   =   (char*)malloc(alen+1);
            memcpy(rtn,ba,alen);
            rtn[alen]=0;
        }
        env->ReleaseByteArrayElements(barr,ba,0);
        string stemp(rtn);
        free(rtn);
        return   stemp;
    }

    char* jstringToChar(JNIEnv* env, jstring jstr) {
        char* rtn = NULL;
        jclass clsstring = env->FindClass("java/lang/String");
        jstring strencode = env->NewStringUTF("GB2312");
        jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
        jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
        jsize alen = env->GetArrayLength(barr);
        jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
        if (alen > 0) {
            rtn = (char*) malloc(alen + 1);
            memcpy(rtn, ba, alen);
            rtn[alen] = 0;
        }
        env->ReleaseByteArrayElements(barr, ba, 0);
        return rtn;
    }

    JNIEXPORT jint JNICALL Java_com_gizmo_gsdk_logo_LibLogoRecog_loadLogos(JNIEnv *env, jclass obj, jstring url, jboolean clear);
    JNIEXPORT jint JNICALL Java_com_gizmo_gsdk_logo_LibLogoRecog_loadLogos(JNIEnv *env, jclass obj, jstring url, jboolean clear){
        ifstream INF(jstringToChar(env,url));
        int nt,ni;
        INF >> nt >> ni;
        vector<string> lu;
        for (int i = 0; i < nt; i++)
        {
            string ss;
            INF >> ss;
            lu.push_back(ss);
        }
        int nlg = rec.loadLogos(lu);
        return 1;
    }


    JNIEXPORT jint JNICALL Java_com_gizmo_gsdk_logo_LibLogoRecog_recognize(JNIEnv *env, jclass obj, jstring imagePath);
    JNIEXPORT jint JNICALL Java_com_gizmo_gsdk_logo_LibLogoRecog_recognize(JNIEnv *env, jclass obj, jstring imagePath){
        string ss = jstring2str(env,imagePath);
        Mat im = imread(ss);
        if (im.rows > im.cols)
        {
            if (im.rows != 960 || im.cols != 720)
                resize(im,im,Size(720,960),0,0,INTER_AREA);
        }
        else
        {
            if (im.rows != 720 || im.cols != 960)
                resize(im,im,Size(960,720),0,0,INTER_AREA);
        }
        int r = rec.recognize(im);
        return r;
    }

    JNIEXPORT void JNICALL Java_com_gizmo_gsdk_logo_LibLogoRecog_init(JNIEnv *env,jclass obj);
    JNIEXPORT void JNICALL Java_com_gizmo_gsdk_logo_LibLogoRecog_init(JNIEnv *env,jclass obj){
        rec.init();
    }

}