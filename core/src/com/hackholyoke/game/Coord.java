package com.hackholyoke.game;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Coord {
    float x, y;

    static Coord polar (double r, double theta)
    {
        return new Coord((float)Math.cos(theta)*r, (float)Math.sin(theta)*r);
    }

    public Coord (double x, double y)
    {
        this.x = (float)x; this.y = (float)y;
    }

    public Coord rotated (double angle)
    {
        // Return a rotated vector by making a new one with the same radius
        // and adding the angles.
        return Coord.polar(radius(), angle + theta());
    }

    public float theta_deg ()
    {
        return (float)(theta() / (Math.PI*2) * 360);
    }

    Coord theta_deg (double t)
    {
        return theta( (t / 360) * Math.PI*2 );
    }

    float theta ()
    {
        return (float)Math.atan2(y, x);
    }

    Coord theta (double t)
    {
        return polar(radius(), t);
    }

    float radius ()
    {
        return (float)Math.sqrt(x*x+y*y);
    }

    Coord radius (double r)
    {
        return polar(r, theta());
    }

    Coord plus (Coord o)
    {
        return new Coord(x+o.x, y+o.y);
    }

    Coord minus (Coord o)
    {
        return new Coord(x-o.x, y-o.y);
    }

    Coord times (Coord o)
    {
        return new Coord(x*o.x, y*o.y);
    }

    Coord times (double d)
    {
        return times(new Coord(d,d));
    }

    Coord position (Sprite s)
    {
        s.setOriginBasedPosition(x, y);
        return this;
    }

    Coord rotation (Sprite s)
    {
        s.setRotation(theta_deg());
        return this;
    }

    public String toString ()
    {
        return "("+x+","+y+")";
    }
}
