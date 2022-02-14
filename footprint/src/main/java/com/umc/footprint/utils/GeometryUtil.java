package com.umc.footprint.utils;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

@Slf4j
public class GeometryUtil {
    private static WKTReader wktReader = new WKTReader();

    public static Geometry wktToGeometry(String wellKnownText) {
        Geometry geometry = null;

        log.info("wellKnownText: {}", wellKnownText);
        try {
            geometry = wktReader.read(wellKnownText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        log.info("geometry: {}", geometry);
        return geometry;
    }
}
