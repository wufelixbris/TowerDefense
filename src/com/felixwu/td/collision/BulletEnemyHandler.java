package com.felixwu.td.collision;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.felixwu.td.TdType;
import com.felixwu.td.event.EnemyKilled;

/**
 * Created by FelixWu on 27/4/2017.
 */
public class BulletEnemyHandler extends CollisionHandler {

    public BulletEnemyHandler(){
        super(TdType.BULLET, TdType.ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity enemy) {
        bullet.removeFromWorld();
        //TODO: add HP/Damage system
        FXGL.getEventBus().fireEvent(new EnemyKilled((GameEntity) enemy));
        enemy.removeFromWorld();
    }
}