package com.felixwu.td;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.felixwu.td.component.HPComponent;
import com.felixwu.td.control.EnemyControl;
import com.felixwu.td.control.TowerControl;
import javafx.scene.effect.BlendMode;
import javafx.util.Duration;

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
                .viewFromTextureWithBBox("backgroundG.png")
                .build();
    }

    @Spawns("Path")
    @SpawnSymbol('1')
    public GameEntity newPath(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(TdType.PATH)
                .viewFromTextureWithBBox("backgroundP.png")
                .build();
    }

    @Spawns("Home")
    @SpawnSymbol('9')
    public GameEntity newHome(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(TdType.HOME)
                .viewFromTextureWithBBox("home.jpg")
                .with(new CollidableComponent(true))
                .build();
    }
    @Spawns("Enemy")
    public GameEntity spawnEnemy(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TdType.ENEMY)
                .viewFromTextureWithBBox("ufo.png")
                .with(new CollidableComponent(true), new HPComponent(Config.ENEMY_HP))
                .with(new EnemyControl())
                .build();
    }
    @Spawns("Tower")
    public GameEntity spawnTower(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TdType.TOWER)
                .viewFromTextureWithBBox("tower1.png")
                .with(new TowerControl())
                .build();
    }
    @Spawns("Bullet")
    public GameEntity spawnBullet(SpawnData data){
        return Entities.builder()
                .from(data)
                .type(TdType.BULLET)
                .viewFromTextureWithBBox("bullet.png")
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanControl())
                .build();
    }

    @Spawns("Explosion")
    public Entity newExplosion(SpawnData data) {
        GameEntity explosion = Entities.builder()
                .at(data.getX() - 20, data.getY() - 20)
                .viewFromNode(FXGL.getAssetLoader().loadTexture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(1.5)))
                .with(new ExpireCleanControl(Duration.seconds(1.3)))
                .build();

        explosion.getView().setBlendMode(BlendMode.ADD);

        return explosion;
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
