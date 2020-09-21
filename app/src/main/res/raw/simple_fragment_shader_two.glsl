//2分屏
#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 vTexCoord;

uniform samplerExternalOES sTexture;//sTexture相当于图片，采样器

// 横两屏
/*void main() {
    float x = vTexCoord.x;
    if (x < 0.5) {
        x += 0.25;
    } else {
        x -= 0.25;
    }
    gl_FragColor = texture2D(sTexture, vec2(x, vTexCoord.y));
}*/

// 竖两屏
void main() {
    float y = vTexCoord.y;
    if (y < 0.5) {
        y += 0.25;
    } else {
        y -= 0.25;
    }
    gl_FragColor = texture2D(sTexture, vec2(vTexCoord.x, y));
}