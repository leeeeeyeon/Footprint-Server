package com.umc.footprint.utils;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GeometryUtil {
    private static WKTReader wktReader = new WKTReader();

    public static Geometry wktToGeometry(String wellKnownText) {
        System.out.println("GeometryUtil.wktToGeometry entered");
        Geometry geometry = null;

        System.out.println("wellKnownText = " + wellKnownText);
        try {
            geometry = wktReader.read(wellKnownText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("geometry = " + geometry);
        System.out.println("GeometryUtil.wktToGeometry exit");
        return geometry;
    }
}
