package com.denizenscript.depenizen.bukkit.properties.worldedit;

import com.denizenscript.denizen.objects.*;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.world.World;

import java.util.ArrayList;
import java.util.List;

public interface WorldEditAreaContainmentExtensions {

    static <T extends AreaContainmentObject> void register(Class<T> type, ObjectTagProcessor<T> processor) {

        // <--[mechanism]
        // @object AreaObject
        // @name we_regenerate
        // @plugin Depenizen, WorldEdit
        // @input None
        // @description
        // Regenerates an area to its original state.
        // -->
        processor.registerMechanism("we_regenerate", false, (area, mechanism) -> {
            World world = BukkitAdapter.adapt(area.getWorld().getWorld());
            Region region;
            if (area instanceof CuboidTag cuboid) {
                LocationTag lowCorner = cuboid.pairs.get(0).low;
                LocationTag highCorner = cuboid.pairs.get(0).high;
                region = new CuboidRegion(world, BlockVector3.at(lowCorner.getX(), lowCorner.getY(), lowCorner.getZ()), BlockVector3.at(highCorner.getX(), highCorner.getY(), highCorner.getZ()));
            }
            else if (area instanceof EllipsoidTag ellipsoid) {
                LocationTag center = ellipsoid.center;
                LocationTag radius = ellipsoid.size;
                region = new EllipsoidRegion(world, BlockVector3.at(center.getX(), center.getY(), center.getZ()), Vector3.at(radius.getX(), radius.getY(), radius.getZ()));
            }
            else if (area instanceof PolygonTag polygon) {
                List<BlockVector2> points = new ArrayList<>();
                for (PolygonTag.Corner corner : polygon.corners) {
                    points.add(BlockVector2.at(corner.x, corner.z));
                }
                region = new Polygonal2DRegion(world, points, (int) Math.round(polygon.yMin), (int) Math.round(polygon.yMax));
            }
            else {
                return;
            }
            world.regenerate(region, world);
        });
    }
}
