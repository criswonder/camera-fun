package com.andymao.camerafun.opengl;


import android.opengl.GLES20;
import android.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huangwei on 2015/6/8.
 */
public class Filter4 {

    protected static final String VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    protected static final String FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D tex_y;\n" +
            "uniform sampler2D tex_u;\n" +
            "uniform sampler2D tex_v;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "vec4 c = vec4((texture2D(tex_y, textureCoordinate).r - 16./255.) * 1.164);\n" +
            "vec4 U = vec4(texture2D(tex_u, textureCoordinate).r - 128./255.);\n" +
            "vec4 V = vec4(texture2D(tex_v, textureCoordinate).r - 128./255.);\n" +
            " c += V * vec4(1.596, -0.813, 0, 0);\n" +
            " c += U * vec4(0, -0.392, 2.017, 0);\n" +
            " c.a = 1.0;\n" +
            "     gl_FragColor = c;\n" +
            "}";

    static Pair<Float, Float> v1 = new Pair<>(-1.0f, -1.0f);
    static Pair<Float, Float> v2 = new Pair<>(1.0f, -1.0f);
    static Pair<Float, Float> v3 = new Pair<>(-1.0f, 1.0f);
    static Pair<Float, Float> v4 = new Pair<>(1.0f, 1.0f);

    static Pair<Float, Float> t1 = new Pair<>(0f, 1f);
    static Pair<Float, Float> t2 = new Pair<>(1f, 1f);
    static Pair<Float, Float> t3 = new Pair<>(0f, 0.0f);
    static Pair<Float, Float> t4 = new Pair<>(1.0f, 0.0f);

    static final float original[] = {
            v1.first, v1.second,
            v2.first, v2.second,
            v3.first, v3.second,
            v4.first, v4.second
    };

    static final float original_texture[] = {
            t1.first, t1.second,
            t2.first, t2.second,
            t3.first, t3.second,
            t4.first, t4.second
    };

    private String mVertexShader;
    private String mFragmentShader;

    private FloatBuffer mCubeBuffer;
    private FloatBuffer mTextureCubeBuffer;

    protected int mProgId;
    protected int mAttribPosition;
    protected int mAttribTexCoord;
    protected int mUniformTextureY;
    protected int mUniformTextureU;
    protected int mUniformTextureV;
    private int mFBO;
    private int[] texIds;

    public Filter4() {
        this(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public Filter4(String vertexShader, String fragmentShader) {
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public void init() {
        loadVertex();
        initShader();
    }

    public void loadVertex() {
        float[] coord = original;
        float[] texture_coord = original_texture;
//        float[] coord = original_ccw_90_horizatal_flip;
//        float[] texture_coord = original_texture_ccw_90_horizatal_flip;
//        float[] coord = original_ccw_90;
//        float[] texture_coord = original_texture_ccw_90;

        mCubeBuffer = ByteBuffer.allocateDirect(coord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mCubeBuffer.put(coord).position(0);

        mTextureCubeBuffer = ByteBuffer.allocateDirect(texture_coord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTextureCubeBuffer.put(texture_coord).position(0);
    }

    public void initShader() {
        mProgId = GLHelper.loadProgram(mVertexShader, mFragmentShader);
        mAttribPosition = GLES20.glGetAttribLocation(mProgId, "position");
        mUniformTextureY = GLES20.glGetUniformLocation(mProgId, "tex_y");
        mUniformTextureU = GLES20.glGetUniformLocation(mProgId, "tex_u");
        mUniformTextureV = GLES20.glGetUniformLocation(mProgId, "tex_v");
        mAttribTexCoord = GLES20.glGetAttribLocation(mProgId,
                "inputTextureCoordinate");

    }

    public void loadYUV() {
        int width = 1920;
        int height = 1080;

        ByteBuffer[] dataBuffers = readYUVBuffer();

        texIds = new int[3];

        GLES20.glGenTextures(3, texIds, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, dataBuffers[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[1]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width / 2, height / 2, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, dataBuffers[1]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[2]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width / 2, height / 2, 0,
                GLES20.GL_LUMINANCE,
                GLES20.GL_UNSIGNED_BYTE, dataBuffers[2]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private ByteBuffer[] readYUVBuffer() {
        int width = 1920, height = 1080;
//        File file = new File("/sdcard/yuv.data");
//        File file = new File("/sdcard/yuv_yv21_2.data");
        File file = new File("/sdcard/yuv_yv21_3.data");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = new byte[3110400];
            inputStream.read(data);

            int i = 0;
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height)
                    .order(ByteOrder.nativeOrder());
            byteBuffer.clear();
            byteBuffer.put(data, 0, width * height - i);
            byteBuffer.position(0);

            int capacity = (width / 2) * (height / 2);
            ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(capacity)
                    .order(ByteOrder.nativeOrder());
            byteBuffer1.clear();
            byteBuffer1.put(data, width * height, capacity - i);
            byteBuffer1.position(0);

            ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(capacity)
                    .order(ByteOrder.nativeOrder());
            byteBuffer2.clear();
            byteBuffer2.put(data, width * height + capacity, capacity - i);
            byteBuffer2.position(0);

            ByteBuffer[] datas = new ByteBuffer[3];
            datas[0] = byteBuffer;
            datas[1] = byteBuffer1;
            datas[2] = byteBuffer2;

            inputStream.close();

            return datas;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteBuffer[0];
    }

    public void drawFrame() {
        if (!GLES20.glIsProgram(mProgId)) {
            initShader();
        }
        GLES20.glUseProgram(mProgId);

        mCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mCubeBuffer);
        GLES20.glEnableVertexAttribArray(mAttribPosition);

        mTextureCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mAttribTexCoord, 2, GLES20.GL_FLOAT, false, 0,
                mTextureCubeBuffer);
        GLES20.glEnableVertexAttribArray(mAttribTexCoord);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[0]);
        GLES20.glUniform1i(mUniformTextureY, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[1]);
        GLES20.glUniform1i(mUniformTextureV, 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[2]);
        GLES20.glUniform1i(mUniformTextureU, 2);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mAttribPosition);
        GLES20.glDisableVertexAttribArray(mAttribTexCoord);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glFinish();
        Filter4.checkGlError("end");
    }

    public int bindTexture2FBO(int input) {
        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, input, 0);

        return frameBuffers[0];
    }

    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            throw new RuntimeException(msg);
        }
    }
}
