//define_uniforms: cameraRight, cameraUp, center, size, projectionMatrix, viewMatrix
//define_samplers: diffuse 0
//define_binds: vertex 0, textureCoords 1

//VERTEX_SHADER
#version 400 core
in vec3 vertex;
in vec2 textureCoords;
out vec2 pass_textureCoords;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraRight;
uniform vec3 cameraUp;
uniform vec3 center;
uniform vec3 size;
void main(){
    vec3 pos = center + cameraRight * vertex.x * size.x + cameraUp * vertex.y * size.y;
    gl_Position = projectionMatrix * viewMatrix * vec4(pos, 1.0);
    pass_textureCoords = textureCoords;

}

    //FRAG_SHADER
    #version 400 core
in vec2 pass_textureCoords;
out vec4 out_Color;
uniform sampler2D diffuse;

void main(){
    // out_Color = texture(diffuse, pass_textureCoords);

    out_Color = vec4(1, 1, 0, 1);
}