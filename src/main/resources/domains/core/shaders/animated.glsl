//define_uniforms: modelMatrix, projectionMatrix, viewMatrix, jointsMatrix
//define_samplers: diffuse 0
//define_binds: vertex 0, normal 1, tangent 2, textureCoords 3, jointWeights 4, jointIndices 5

//VERTEX_SHADER
#version 400 core

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

in vec4 vertex;
in vec3 normal;
in vec3 tangent;
in vec2 textureCoords;
in vec4 jointWeights;
in ivec4 jointIndices;
out vec2 pass_textureCoords;

out vec4 pass_jointWeight;
uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 jointsMatrix[MAX_JOINTS];

void main(){

    vec4 initPos = vec4(0, 0, 0, 0);
    vec4 initNormal = vec4(0, 0, 0, 0);
    int count = 0;
    for (int i = 0; i < MAX_WEIGHTS; i++)
    {
        float weight = jointWeights[i];
        if (weight > 0) {
            count++;
            int jointIndex = jointIndices[i];
            vec4 tmpPos = jointsMatrix[jointIndex] * vertex;
            initPos += weight * tmpPos;

            vec4 tmpNormal = jointsMatrix[jointIndex] * vec4(normal, 0.0);
            initNormal += weight * tmpNormal;
        }
    }
    if (count == 0)
    {
        initPos = vertex;
        initNormal = vec4(normal, 0.0);
    }
    mat4 modelViewMatrix =  viewMatrix * modelMatrix;
    vec4 mvPos = modelViewMatrix * initPos;
    gl_Position = projectionMatrix * mvPos;
    pass_textureCoords = textureCoords;
    pass_jointWeight = jointWeights;
}

    //FRAG_SHADER
    #version 400 core
in vec2 pass_textureCoords;
out vec4 out_Color;
uniform sampler2D diffuse;
in vec4 pass_jointWeight;

void main(){
//    out_Color = texture(diffuse, pass_textureCoords);

//    out_Color = vec4(1, 1, 0, 1);
    out_Color = pass_jointWeight;
}