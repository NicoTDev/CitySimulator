package Outil;

import org.joml.Vector2f;

import static java.lang.Math.*;
import static java.lang.Math.min;

public abstract class MathLocal {

    /**
     *
     * @param valeur valeur de base
     * @param min minimum acceptable
     * @param max maximum acceptable
     * @return valeur comprise entre le min et le max
     */
    public static float clamp(float valeur, float min, float max) {
        return Math.min(Math.max(min,valeur),max);
    }
}
