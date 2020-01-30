//define_uniforms: modelMatrix, projectionMatrix, viewMatrix
//define_samplers: diffuse 0
//define_binds: vertex 0, textureCoords 1

//VERTEX_SHADER
#version 400 core
in vec3 vertex;
in vec2 textureCoords;
out vec2 pass_textureCoords;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;


void main(){
    vec4 worldPosition =  modelMatrix * vec4(vertex, 1.0);
    //Temporarly making everything on screen
    gl_Position = worldPosition;
    pass_textureCoords = textureCoords;
}

//FRAG_SHADER
#version 400 core
in vec2 pass_textureCoords;
out vec4 out_Color;
uniform sampler2D diffuse;

void main(){
    out_Color = texture(diffuse, pass_textureCoords);

   // out_Color = vec4(1, 1, 0, 1);
}