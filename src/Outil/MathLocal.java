package Outil;

import org.joml.Vector2f;

import static java.lang.Math.*;
import static java.lang.Math.min;

public abstract class MathLocal {

    public static float trouverAngleDeuxPoints(Vector2f pt1, Vector2f pt2) {
        float x = pt2.x - pt1.x;
        float y = pt2.y - pt1.y;
        float angle = (float) abs(atan(y/x));
        if (x < 0 && y < 0)
            angle += PI;
        else if (x < 0 && y > 0)
            angle = (float)(PI-angle);
        else if (x > 0 && y < 0)
            angle = (float)(2*PI-angle);
        //System.out.println(CouleurConsole.BLEU.couleur + "a : " + angle + " a : " + max(min(angle-getAngle(),(float) Math.toRadians(10)),(float) Math.toRadians(-10)));
        return angle;
    }
}
