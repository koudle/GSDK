package com.gizmo.gsdk.logo;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class LogoRecog {
    LibLogoRecog libLogoRecog;

    public LogoRecog(Context context){
        libLogoRecog = new LibLogoRecog();
        libLogoRecog.init();
        putAssetsToSDCard(context,"logo",context.getFilesDir().toString());
        libLogoRecog.loadLogos(context.getFilesDir().toString()+File.separator+"vlist.data",true);
    }

    public int recognize(String imagePath) {
        return libLogoRecog.recognize(imagePath);
    }

    /**
     * 将assets下的文件放到sd指定目录下
     *
     * @param context    上下文
     * @param assetsPath assets下的路径
     * @param sdCardPath sd卡的路径
     */
    public static void putAssetsToSDCard(Context context, String assetsPath,
                                         String sdCardPath) {
        try {
            String mString[] = context.getAssets().list(assetsPath);
            if (mString.length > 0) { // 说明assetsPath为空,或者assetsPath是一个文件
                File vlist = new File(sdCardPath+File.separator+"vlist.data");
                if(vlist.exists()) return;
                for (int i=0;i<mString.length;i++) {
                    String filename = mString[i];
                    InputStream mIs = context.getAssets().open(assetsPath+File.separator+filename); // 读取流
                    byte[] mByte = new byte[1024];
                    int bt = 0;
                    File file = new File(sdCardPath + File.separator
                            +filename);
                    if (!file.exists()) {
                        file.createNewFile(); // 创建文件
                    } else {
                        break;//已经存在直接退出
                    }
                    FileOutputStream fos = new FileOutputStream(file); // 写入流
                    while ((bt = mIs.read(mByte)) != -1) { // assets为文件,从文件中读取流
                        fos.write(mByte, 0, bt);// 写入流到文件中
                    }
                    fos.flush();// 刷新缓冲区
                    mIs.close();// 关闭读取流
                    fos.close();// 关闭写入流
                }

                if(!vlist.exists()){
                    vlist.createNewFile();
                }else {
                    return;
                }
                FileOutputStream listFos = new FileOutputStream(vlist);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(listFos,"UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write("13 249\n");
                for(int i=0;i<13;i++){
                    bufferedWriter.write(sdCardPath+File.separator+String.valueOf(i)+".txt"+"\n");
                }
                bufferedWriter.close();
                outputStreamWriter.close();
                listFos.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
