#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 color;

out vec3 outColor;

uniform mat4 matriceProjection;
uniform mat4 matriceModel;

void main()
{
    gl_Position = matriceProjection * matriceModel * vec4(position, 1.0);
    outColor = color;
}