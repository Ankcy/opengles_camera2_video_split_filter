//3分屏
#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 vTexCoord;

uniform samplerExternalOES sTexture;

void main() {
    float x = vTexCoord.x;
    float a = 1.0/3.0;
    float b = 2.0/3.0;
    if (x < a) {
        x += a;
    } else if (x > b) {
        x -= a;
    }
    gl_FragColor = texture2D(sTexture, vec2(x, vTexCoord.y));
}