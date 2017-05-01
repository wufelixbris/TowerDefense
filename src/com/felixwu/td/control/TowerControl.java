package com.felixwu.td.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.time.LocalTimer;
import com.felixwu.td.Config;
import com.felixwu.td.TdType;
import javafx.util.Duration;

import javafx.geometry.Point2D;

/**
 * Created by FelixWu on 27/4/2017.
 */
public class TowerControl extends AbstractControl {

    private LocalTimer shootTimer;

    @Override
    public void onAdded(Entity entity) {
        shootTimer = FXGL.newLocalTimer();
    }

    @Override
    public void onUpdate(Entity entity, double v) {
        if(shootTimer.elapsed(Duration.seconds(1))){
            FXGL.getApp()
                    .getGameWorld()
                    .getClosestEntity(entity, e-> Entities.getType(e).isType(TdType.ENEMY))
                    .ifPresent(nearestEnemy -> {
                        shoot(nearestEnemy);
                        shootTimer.capture();
                    });
        }

    }

    private void shoot(Entity enemy){
        Point2D position = Entities.getPosition(getEntity()).getValue();
        Point2D direction = Entities.getPosition(enemy)
                .getValue()
                .subtract(position);

        Entity bullet = FXGL.getApp().getGameWorld().spawn("Bullet", position);
        bullet.addControl(new ProjectileControl(direction, Config.BULLET_SPEED));


    }
}
