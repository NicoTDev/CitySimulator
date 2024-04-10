package Outil;

import org.joml.Vector2f;

import static java.lang.Math.*;
import static java.lang.Math.min;

public abstract class MathLocal {

    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(min,value),max);
    }
}
