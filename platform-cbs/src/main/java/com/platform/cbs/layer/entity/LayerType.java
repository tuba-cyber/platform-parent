package com.platform.cbs.layer.entity;

public enum LayerType {
    WMS,        // Web Map Service
    WFS,        // Web Feature Service
    VECTOR,     // Lokal vektör
    RASTER,     // Lokal raster
    XYZ,        // Tile layer (OSM, Google vb.)
    GEOJSON     // GeoJSON
}