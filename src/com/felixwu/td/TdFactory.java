package com.felixwu.td;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.felixwu.td.control.EnemyControl;
import com.felixwu.td.control.TowerControl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by FelixWu on 27/4/2017.
 */
@SetEntityFactory
public class TdFactory implements EntityFactory {

    @Spawns("Enemy")
    public GameEntity spawnEnemy(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TdType.ENEMY)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("ufo.png"))
                .with(new CollidableComponent(true))
                .with(new EnemyControl())
                .build();
    }
    @Spawns("Tower")
    public GameEntity spawnTower(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TdType.TOWER)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("tower1.png"))
                .with(new TowerControl())
                .build();
    }
    @Spawns("Bullet")
    public GameEntity spawnBullet(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TdType.BULLET)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("bullet.png"))
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanControl())
                .build();
    }
}
