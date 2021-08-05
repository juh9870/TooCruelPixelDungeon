/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.levels.builders;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.watabou.utils.Random;

import java.util.ArrayList;

//A simple builder which utilizes a line as its core feature.
public class LineBuilder extends RegularBuilder {

    int removedRooms = 0;
    private float revolutions = 2.5f;

    public LineBuilder setRevolutions(float revolutions){
        this.revolutions=revolutions;
        return this;
    }

    @Override
    public ArrayList<Room> build(ArrayList<Room> rooms) {

        setupRooms(rooms);
        multiConnections.addAll(mainPathRooms);
        mainPathRooms.clear();

        float step = Math.min(90,360 * revolutions / multiConnections.size());

        if (entrance == null) {
            return null;
        }
        boolean noCorridors = false;

        float direction = Random.Float(0, 360);
        if (Challenges.LINEAR.enabled()) {
            int i = Random.Int(4);
            direction = i * 90;
            noCorridors = true;
        }
        ArrayList<Room> branchable = new ArrayList<>();

        entrance.setSize();
        entrance.setPos(-entrance.width()/2, -entrance.height()/2);
        branchable.add(entrance);

        if (shop != null) {
            placeRoom(rooms, entrance, shop, direction + 180f);
        }

        int roomsOnPath = (int) (multiConnections.size() * pathLength) + Random.chances(pathLenJitterChances);
        roomsOnPath = Math.min(roomsOnPath, multiConnections.size());

        Room curr = entrance;

        float[] pathTunnels = pathTunnelChances.clone();
        for (int i = 0; i <= roomsOnPath; i++) {

            direction += step;

            if (i == roomsOnPath && exit == null)
                continue;

            int tunnels = Random.chances(pathTunnels);
            if (tunnels == -1) {
                pathTunnels = pathTunnelChances.clone();
                tunnels = Random.chances(pathTunnels);
            }
            pathTunnels[tunnels]--;

            if (!noCorridors) {
                for (int j = 0; j < tunnels; j++) {
                    ConnectionRoom t = ConnectionRoom.createRoom();
                    placeRoom(rooms, curr, t, direction + Random.Float(-pathVariance, pathVariance));
                    branchable.add(t);
                    rooms.add(t);
                    curr = t;
                }
            }

            Room r = (i == roomsOnPath ? exit : multiConnections.get(i));
            float angle;
            int tries = 4;
            do {
                angle = placeRoom(rooms, curr, r, direction += Random.Float(-pathVariance, pathVariance));
                tries--;
            } while (angle == -1 && tries >= 0);
            if (angle == -1) {
                if (r == exit || r == entrance) return null;
                r.clearConnections();
                roomsOnPath--;
                i--;
                multiConnections.remove(r);
                rooms.remove(r);
                removedRooms++;
            } else {
                branchable.add(r);
                curr = r;
            }
        }

        ArrayList<Room> roomsToBranch = new ArrayList<>();
        for (int i = roomsOnPath; i < multiConnections.size(); i++) {
            roomsToBranch.add(multiConnections.get(i));
        }
        roomsToBranch.addAll(singleConnections);
        weightRooms(branchable);
        createBranches(rooms, branchable, roomsToBranch, branchTunnelChances);

        findNeighbours(rooms);

//        for (Room r : rooms) {
//            for (Room n : r.neigbours) {
//                if (!n.connected.containsKey(r)
//                        && Random.Float() < extraConnectionChance) {
//                    r.connect(n);
//                }
//            }
//        }

        return rooms;

    }

}
