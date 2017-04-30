package com.felixwu.td;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.felixwu.td.component.HPComponent;
import com.felixwu.td.control.EnemyControl;
import com.felixwu.td.control.TowerControl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by FelixWu on 27/4/2017.
 */
@SetEntityFactory
public class TdFactory implements TextEntityFactory {

    @Spawns("Block")
    @SpawnSymbol('0')
    public GameEntity newBlock(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(TdType.BLOCK)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("backgroundG.png"))
                .build();
    }

    @Spawns("Path")
    @SpawnSymbol('1')
    public GameEntity newPath(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(TdType.PATH)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("backgroundP.png"))
                .build();
    }

    @Spawns("Home")
    @SpawnSymbol('9')
    public GameEntity newHome(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(TdType.HOME)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("home.jpg"))
                .with(new CollidableComponent(true), new HPComponent(Config.HOME_HP))
                .build();
    }

    @Spawns("Enemy")
    public GameEntity spawnEnemy(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TdType.ENEMY)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("ufo.png"))
                .with(new CollidableComponent(true), new HPComponent(Config.ENEMY_HP))
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

    @Override
    public char emptyChar() {
        return ' ';
    }

    @Override
    public int blockWidth() {
        return 40;
    }

    @Override
    public int blockHeight() {
        return 40;
    }
}
