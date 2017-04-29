package com.felixwu.td.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.felixwu.td.Main;
import com.felixwu.td.event.EnemyReachedGoal;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * Created by FelixWu on 27/4/2017.
 */
public class EnemyControl extends AbstractControl {

    private List<Point2D>waypoints;
    private javafx.geometry.Point2D nextWaypoint;
    private PositionComponent position;
    private double speed;

    @Override
    public void onAdded(Entity entity) {
        waypoints = ((Main) FXGL.getApp()).getwaypoints();
        position = Entities.getPosition(entity);

        nextWaypoint = waypoints.remove(0);
    }

    @Override
    public void onUpdate(Entity entity, double v) {
        speed = v*60*5;

        Point2D velocity = nextWaypoint.subtract(position.getValue())
                .normalize()
                .multiply(speed);
        position.translate(velocity);

        if(nextWaypoint.distance(position.getValue())<speed){
            position.setValue(nextWaypoint);
            if(!waypoints.isEmpty()){
                nextWaypoint = waypoints.remove(0);
            }
            else{
                FXGL.getEventBus().fireEvent(new EnemyReachedGoal());
            }
        }
    }
}

