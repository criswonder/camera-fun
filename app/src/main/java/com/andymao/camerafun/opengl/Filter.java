package com.andymao.camerafun.opengl;


import android.opengl.GLES20;
import android.util.Pair;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huangwei on 2015/6/8.
 */
public class Filter {

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
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    static final float COORD1[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    static final float TEXTURE_COORD1[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    static final float COORD2[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f,
    };

    static final float TEXTURE_COORD2[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    static final float COORD3[] = {
            1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            -1.0f, 1.0f,
    };

    static final float TEXTURE_COORD3[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };


    static final float COORD4[] = {
            1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            -1.0f, 1.0f,
    };

    static final float TEXTURE_COORD4[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };

    static final float COORD_REVERSE[] = {
            1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            -1.0f, 1.0f,
    };

    static final float TEXTURE_COORD_REVERSE[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };

    static final float COORD_FLIP[] = {
            1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            -1.0f, 1.0f,
    };

    static final float TEXTURE_COORD_FLIP[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };
    static final float COORD_FLIP2[] = {
            -1.0f, -1.0f,
            1.f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    static final float TEXTURE_COORD_FLIP2[] = {
            1.0f, 1.0f,
            0.f, 1.0f,
            1.0f, .0f,
            .0f, .0f,
    };

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
    protected int mUniformTexture;


    public Filter() {
        this(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public Filter(String vertexShader, String fragmentShader) {
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public void init() {
        loadVertex();
        initShader();
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
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
        mUniformTexture = GLES20.glGetUniformLocation(mProgId, "inputImageTexture");
        mAttribTexCoord = GLES20.glGetAttribLocation(mProgId,
                "inputTextureCoordinate");
    }

    public void drawFrame(int glTextureId) {
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

        if (glTextureId != GLHelper.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glTextureId);
            GLES20.glUniform1i(mUniformTexture, 0);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mAttribPosition);
        GLES20.glDisableVertexAttribArray(mAttribTexCoord);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisable(GLES20.GL_BLEND);

    }
}
