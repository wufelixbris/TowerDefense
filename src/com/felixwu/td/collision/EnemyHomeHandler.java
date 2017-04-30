package com.felixwu.td.collision;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.felixwu.td.Config;
import com.felixwu.td.TdType;
import com.felixwu.td.component.HPComponent;
import com.felixwu.td.event.EnemyReachedGoal;

/**
 * Created by FelixWu on 30/4/2017.
 */
public class EnemyHomeHandler extends CollisionHandler {

    public EnemyHomeHandler(){
        super(TdType.ENEMY, TdType.HOME);
    }


    @Override
    protected void onCollisionBegin(Entity enemy, Entity home) {
        enemy.removeFromWorld();
        HPComponent hp = home.getComponentUnsafe(HPComponent.class);
        hp.setValue(hp.getValue() - Config.ENEMY_DAMAGE);
        if(hp.getValue()<=0){
            FXGL.getEventBus().fireEvent(new EnemyReachedGoal());
        }
    }

    @Override
    protected void onCollision(Entity enemy, Entity home) {
        enemy.removeFromWorld();
        HPComponent hp = home.getComponentUnsafe(HPComponent.class);
        hp.setValue(hp.getValue() - Config.ENEMY_DAMAGE);
        if(hp.getValue()==0){
            FXGL.getEventBus().fireEvent(new EnemyReachedGoal());
        }
    }
}
