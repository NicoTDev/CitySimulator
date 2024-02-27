#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

out vec2 outTexCoord;

uniform mat4 matriceProjection;
uniform mat4 matriceModel;
uniform mat4 matriceVue;

void main()
{
    gl_Position = matriceProjection * matriceVue * matriceModel * vec4(position, 1.0);
    outTexCoord = texCoord;
}