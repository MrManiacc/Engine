//define_uniforms: modelMatrix, projectionMatrix, viewMatrix
//define_samplers: diffuse 0
//define_binds: vertex 0, normal 1, tangent 2, textureCoords 3

//VERTEX_SHADER
#version 400 core

in vec4 vertex;
in vec3 normal;
in vec3 tangent;
in vec2 textureCoords;
out vec2 pass_textureCoords;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){
    mat4 modelViewMatrix =  viewMatrix * modelMatrix;
    vec4 mvPos = modelViewMatrix * vertex;
    gl_Position = projectionMatrix * mvPos;
    pass_textureCoords = textureCoords;
}

//FRAG_SHADER
#version 400 core
in vec2 pass_textureCoords;
out vec4 out_Color;
uniform sampler2D diffuse;

void main(){
    out_Color = texture(diffuse, pass_textureCoords);
}