package com.andymao.camerafun.lib;

import android.graphics.ImageFormat;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongyun
 * on 22/11/2017
 */

public class PreviewFormatUtils {
    public static String floatBufferString(FloatBuffer floatBuffer) {
        int position = floatBuffer.position();
        int size = floatBuffer.capacity();
        float[] floats = new float[size];
        floatBuffer.position(0);
        floatBuffer.get(floats);
        floatBuffer.position(position);
        StringBuilder stringBuilder = new StringBuilder(size);
        for (int i = 0; i < floats.length; i++) {
            stringBuilder.append(floats[i]).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static String printFloatArray(float array[]) {
        if (array != null && array.length > 0) {
            StringBuilder stringBuilder = new StringBuilder(array.length);
            for (int i = 0; i < array.length; i++) {
                stringBuilder.append(String.valueOf(array[i]));
                if (i != array.length - 1) {
                    stringBuilder.append(",");
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }

    public static List<String> getSupportPreviewFormatStrings(List<Integer> inputFormats) {
        ArrayList<String> formatStrs = new ArrayList<>();
        for (Integer format :
                inputFormats) {
            formatStrs.add(findImageFormatStr(format));
        }
        return formatStrs;
    }

    public static String findImageFormatStr(int imgFormat) {
        switch (imgFormat) {
            case ImageFormat.DEPTH16:
                return "ImageFormat.DEPTH16";
            case ImageFormat.DEPTH_POINT_CLOUD:
                return "ImageFormat.DEPTH_POINT_CLOUD";
            case ImageFormat.FLEX_RGBA_8888:
                return "ImageFormat.FLEX_RGBA_8888";
            case ImageFormat.FLEX_RGB_888:
                return "ImageFormat.FLEX_RGB_888";
            case ImageFormat.JPEG:
                return "ImageFormat.JPEG";
            case ImageFormat.NV16:
                return "ImageFormat.NV16";
            case ImageFormat.NV21:
                return "ImageFormat.NV21";
            case ImageFormat.RAW12:
                return "ImageFormat.RAW12";
            case ImageFormat.RAW10:
                return "ImageFormat.RAW10";
            case ImageFormat.RAW_SENSOR:
                return "ImageFormat.RAW_SENSOR";
            case ImageFormat.RGB_565:
                return "ImageFormat.RGB_565";
            case ImageFormat.YUV_420_888:
                return "ImageFormat.YUV_420_888";
            case ImageFormat.YUV_422_888:
                return "ImageFormat.YUV_422_888";
            case ImageFormat.YUV_444_888:
                return "ImageFormat.YUV_444_888";
            case ImageFormat.YUY2:
                return "ImageFormat.YUY2";
            case ImageFormat.YV12:
                return "ImageFormat.YV12";
        }

        return "unknown";
    }
}
