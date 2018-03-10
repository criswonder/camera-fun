package com.andymao.camerafun.opengl;


import android.opengl.GLES20;
import android.util.Pair;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huangwei on 2015/6/8.
 */
public class NV21ByteBuffer2RGB {

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
    protected static final String FRAGMENT_SHADER =
            "#ifdef GL_ES\n" +
                    "precision highp float;\n" +
                    "#endif\n" +

                    "varying vec2 textureCoordinate;\n" +
                    "uniform sampler2D y_texture;\n" +
                    "uniform sampler2D uv_texture;\n" +

                    "void main (void){\n" +
                    "   float r, g, b, y, u, v;\n" +

                    //We had put the Y values of each pixel to the R,G,B components by
                    //GL_LUMINANCE, that's why we're pulling it from the R component,
                    //we could also use G or B
                    "   y = texture2D(y_texture, textureCoordinate).r;\n" +

                    //We had put the U and V values of each pixel to the A and R,G,B
                    //components of the texture respectively using GL_LUMINANCE_ALPHA.
                    //Since U,V bytes are interspread in the texture, this is probably
                    //the fastest way to use them in the shader
                    "   u = texture2D(uv_texture, textureCoordinate).a - 0.5;\n" +
                    "   v = texture2D(uv_texture, textureCoordinate).r - 0.5;\n" +

                    //The numbers are just YUV to RGB conversion constants
                    "   r = y + 1.13983*v;\n" +
                    "   g = y - 0.39465*u - 0.58060*v;\n" +
                    "   b = y + 2.03211*u;\n" +

                    //We finally set the RGB color of our pixel
                    "   gl_FragColor = vec4(r, g, b, 1.0);\n" +
                    "}\n";

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
    protected int mUniformTextureUV;
    private int[] texIds;

    public NV21ByteBuffer2RGB() {
        this(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public NV21ByteBuffer2RGB(String vertexShader, String fragmentShader) {
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
        mUniformTextureY = GLES20.glGetUniformLocation(mProgId, "y_texture");
        mUniformTextureUV = GLES20.glGetUniformLocation(mProgId, "uv_texture");
        mAttribTexCoord = GLES20.glGetAttribLocation(mProgId,
                "inputTextureCoordinate");

    }

    private void readYUVBuffer(byte[] data, int width, int height) {
//        File file = new File("/sdcard/yuv_yv21_3.data");

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height)
                .order(ByteOrder.nativeOrder());
        byteBuffer.clear();
        byteBuffer.put(data, 0, width * height);
        byteBuffer.position(0);

        int capacity = width * height / 2;
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(capacity)
                .order(ByteOrder.nativeOrder());
        byteBuffer1.clear();
        byteBuffer1.put(data, width * height, capacity);
        byteBuffer1.position(0);


        texIds = new int[2];

        GLES20.glGenTextures(2, texIds, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[1]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, width / 2, height / 2, 0,
                GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, byteBuffer1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

    }

    public void drawFrame(int frameBuffer) {
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

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[0]);
        GLES20.glUniform1i(mUniformTextureY, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[1]);
        GLES20.glUniform1i(mUniformTextureUV, 1);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mAttribPosition);
        GLES20.glDisableVertexAttribArray(mAttribTexCoord);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glFinish();
    }

}
