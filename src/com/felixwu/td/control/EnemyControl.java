package com.felixwu.td.control;

import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.AStarNode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.FXGLApplication;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.felixwu.td.Config;
import com.felixwu.td.Main;
import com.felixwu.td.TdType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FelixWu on 27/4/2017.
 */
public class EnemyControl extends AbstractControl {

    private PositionComponent position;
    private double speed;
    private Entity home = (FXGL.getApp().getGameWorld().getEntitiesByType(TdType.HOME).remove(0));
    private final PositionComponent target = Entities.getPosition(home);

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }


    private AStarGrid grid;

    private List<AStarNode> path = new ArrayList<>();

    @Override
    public void onUpdate(Entity entity, double tpf) {

        speed = tpf * 60 * 3;

        //shortest route calculator

        if (path.isEmpty()) {
            if (grid == null) {
                grid = ((Main) FXGL.getApp()).getGrid();
            }

            int startX = (int) (position.getX() / Config.BLOCK_SIZE);
            int startY = (int) (position.getY() / Config.BLOCK_SIZE);

            int targetX = (int) ((target.getX() + 20) / Config.BLOCK_SIZE);
            int targetY = (int) ((target.getY() + 20) / Config.BLOCK_SIZE);

            path = grid.getPath(
                    startX,
                    startY,
                    targetX,
                    targetY);

            //System.out.println(startX + " " + startY + " " + targetX + " "  +targetY + " " + path.isEmpty());
        }

        if (path.isEmpty())
            return;


        AStarNode next = path.get(0);

        int nextX = next.getX() * Config.BLOCK_SIZE;
        int nextY = next.getY() * Config.BLOCK_SIZE;

        double dx = nextX - position.getX();
        double dy = nextY - position.getY();

        if (Math.abs(dx) <= speed)
            position.setX(nextX);
        else
            position.translateX(speed * Math.signum(dx));

        if (Math.abs(dy) <= speed)
            position.setY(nextY);
        else
            position.translateY(speed * Math.signum(dy));

        if (position.getX() == nextX && position.getY() == nextY) {
            path.remove(0);
        }

    }

}

