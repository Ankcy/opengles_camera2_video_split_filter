package com.jqkj.gles20test;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.jqkj.gles20test.util.OpenGlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CVFilter {

    private int vPosition;
    private int vCoord;
    private int vTexture;
    private int vMatrix;
    private int program;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private Context mContext;

    private final float[] vertexData = { //opengl 世界坐标系，这里只需要4个点，总共8个点，中心点是0,0，左下是-1,-1，右下是1,-1，左上是-1,1，右上是1,1，左中是-1,0，右中是1,0，点位从左到右，从下到上
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

    private final float[] textureVertexData = {// 纹理坐标系 4个点，左下是0,0，右下是1,0，左上是0,1，右上是1,1，必须与世界坐标对应
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
//            1f,1f,
//            1f,0f,
//            0f,1f,
//            0f,0f
    };

    public CVFilter(Context context) {
        this.mContext = context;
        String vertexShader = OpenGlUtils.readGlShader(context, R.raw.simple_vertex_shader1);
        String fragmentShader = OpenGlUtils.readGlShader(context, R.raw.simple_fragment_shader_one);
        //String fragmentShader = OpenGlUtils.readGlShader(context, R.raw.simple_fragment_shader_two);
        //String fragmentShader = OpenGlUtils.readGlShader(context, R.raw.simple_fragment_shader_three);
        //String fragmentShader = OpenGlUtils.readGlShader(context, R.raw.simple_fragment_shader_four);
        //String fragmentShader = OpenGlUtils.readGlShader(context, R.raw.simple_fragment_shader_six);
        //String fragmentShader = OpenGlUtils.readGlShader(context, R.raw.simple_fragment_shader_nine);

        initProgram(vertexShader, fragmentShader);
    }

    private void initProgram(String vertexShader, String fragmentShader) {
        // 准备着色器程序
        program = OpenGlUtils.loadProgram(vertexShader, fragmentShader);
        GLES20.glUseProgram(program);

        // 变量绑定
        vPosition = GLES20.glGetAttribLocation(program, "aPosition");
        vCoord = GLES20.glGetAttribLocation(program, "aTexCoord");
        vTexture = GLES20.glGetUniformLocation(program, "sTexture");
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");

        // 坐标
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.clear();
        vertexBuffer.put(vertexData);

        textureBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.clear();
        textureBuffer.put(textureVertexData);
    }

    public void onDraw(float[] mtx, int texture) {
        // 顶点坐标，确定形状
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);

        // 纹理坐标，贴图上颜色
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

        // 激活一个用来显示图片的画框
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(vTexture, 0);

        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);

        // 通知画画, GL_TRIANGLE_STRIP画的方式为以点顺序相连
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }

    public void release() {
        GLES20.glDeleteProgram(program);
    }


    public void setFilter(int i) {
        String vertexShader = OpenGlUtils.readGlShader(mContext, R.raw.simple_vertex_shader1);
        String fragmentShader = null;
        if (i == 0) {
            fragmentShader = OpenGlUtils.readGlShader(mContext, R.raw.simple_fragment_shader_one);
        } else if (i == 2) {
            fragmentShader = OpenGlUtils.readGlShader(mContext, R.raw.simple_fragment_shader_two);
        } else if (i == 3) {
            fragmentShader = OpenGlUtils.readGlShader(mContext, R.raw.simple_fragment_shader_three);
        } else if (i == 4) {
            fragmentShader = OpenGlUtils.readGlShader(mContext, R.raw.simple_fragment_shader_four);
        } else if (i == 6) {
            fragmentShader = OpenGlUtils.readGlShader(mContext, R.raw.simple_fragment_shader_six);
        } else if (i == 9) {
            fragmentShader = OpenGlUtils.readGlShader(mContext, R.raw.simple_fragment_shader_nine);
        }
        initProgram(vertexShader, fragmentShader);
    }
}