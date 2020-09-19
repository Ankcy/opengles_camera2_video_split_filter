//attribute ，程序中用glBindAttribLocation()来绑定，用glVertexAttribPointer()来赋值，attribute只能在vertex shader中申明和使用，用来表示顶点坐标，法线，纹理坐标，顶点颜色
//varying ，在vertex和fragment shader之间做数据传递用的，外部程序不能使用，一般vertex shader修改varying变量的值，然后fragment shader使用该varying变量的值，在vertex和shader两者申明必须一致
//uniform 常量，由程序glUniform**()传入到shader，不能修改，如果uniform变量在vertex和fragment两者之间声明方式完全一样，则它可以在vertex和fragment共享使用
attribute vec4 aPosition;//顶点位置
attribute vec2 aTexCoord;//S T 纹理坐标
varying vec2 vTexCoord;
uniform mat4 vMatrix;

void main() {
    gl_Position = aPosition;
    vTexCoord = (vMatrix * vec4(aTexCoord, 1.0, 1.0)).xy;
}







