#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=2) in vec4 color;

out vec4 outTextCoord;

uniform mat4 matriceProjection;
uniform mat4 matriceVue;
uniform mat4 matriceModel;

void main()
{
    gl_Position = matriceProjection * matriceVue * matriceModel * vec4(position, 1.0);
    outTextCoord = color;
}