package com.felixwu.td;

import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.NodeState;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.MenuEventHandler;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.scene.menu.MenuEvent;
import com.almasb.fxgl.scene.menu.MenuStyle;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.service.UIFactory;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.MenuItem;
import com.felixwu.td.collision.BulletEnemyHandler;
import com.felixwu.td.event.EnemyKilled;
import com.felixwu.td.event.EnemyReachedGoal;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class Main extends GameApplication{
    //TODO: read from level data
    private int levelEnemies = 10;
    private IntegerProperty numEnemies;
    private Point2D enemySpawnPoint = new Point2D(0, 40);

    private List<Entity> paths;

    private AStarGrid grid;
    public AStarGrid getGrid() {
        return grid;
    }


    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("TowerDefense");
        gameSettings.setVersion("1.0");
        gameSettings.setWidth(Config.MAP_WIDTH*Config.BLOCK_SIZE);
        gameSettings.setHeight(Config.MAP_HEIGHT*Config.BLOCK_SIZE);
        gameSettings.setIntroEnabled(false);
        gameSettings.setMenuEnabled(false);
        gameSettings.setProfilingEnabled(false);
        gameSettings.setCloseConfirmation(false);
    }



    @Override
    protected void initInput() {
        Input input = getInput();
        input.addAction(new UserAction("Place Tower A"){
            @Override
            protected void onActionBegin(){
                placeTower();
            }
        },MouseButton.PRIMARY);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());

    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("gold", Config.START_GOLD);
    }

    @Override
    protected void initGame() {

        TextLevelParser parser = new TextLevelParser(getGameWorld().getEntityFactory());
        Level level = parser.parse("level1.txt");
        getGameWorld().setLevel(level);
        level.getEntities().clear();

        grid = new AStarGrid(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        getGameWorld().getEntitiesByType(TdType.BLOCK)
                .stream()
                .map(e->Entities.getPosition(e).getValue())
                .forEach(point ->{
                    int x = (int)point.getX()/Config.BLOCK_SIZE;
                    int y = (int)point.getY()/Config.BLOCK_SIZE;
                    grid.setNodeState(x, y, NodeState.NOT_WALKABLE);
                });


        numEnemies = new SimpleIntegerProperty(levelEnemies);
        BooleanProperty enemiesLeft = new SimpleBooleanProperty();
        enemiesLeft.bind(numEnemies.greaterThan(0));
        getMasterTimer().runAtIntervalWhile(this::spawnEnemy, Duration.seconds(1), enemiesLeft);

        getEventBus().addEventHandler(EnemyKilled.ANY, this::onEnemyKilled);
        getEventBus().addEventHandler(EnemyReachedGoal.ANY, e->gameover());
    }

    private void spawnEnemy() {
        numEnemies.set(numEnemies.get()-1);

        getGameWorld().spawn("Enemy", enemySpawnPoint.getX(), enemySpawnPoint.getY());
    }

    private void placeTower(){

        if(getGameState().getInt("gold")<=0){
            return;
        }

        if (paths == null) {
            paths = getGameWorld().getEntitiesByType(TdType.PATH);
        }

        int x=((int)getInput().getMouseXWorld()/Config.BLOCK_SIZE)*Config.BLOCK_SIZE;
        int y=((int)getInput().getMouseYWorld()/Config.BLOCK_SIZE)*Config.BLOCK_SIZE;

        for(Entity path : paths){
            Point2D position = new Point2D(Entities.getPosition(path).getX(), Entities.getPosition(path).getY());
            if(position.getX()==x && position.getY()==y){
                return;
            }
        }

        getGameWorld().spawn("Tower", new SpawnData(x, y));
        getGameState().increment("gold", Config.TOWER_GOLD);
    }

    private void onEnemyKilled(EnemyKilled event){
        levelEnemies--;

        if(levelEnemies==0){
            gameover();
        }
        GameEntity enemy = event.getEnemy();
        Point2D position = Entities.getPosition(enemy).getValue();
        Text xMark = getUIFactory().newText("X", Color.RED, 24);

        EntityView view = new EntityView(xMark);
        view.setTranslateX(position.getX());
        view.setTranslateY(position.getY()+20);
        getGameScene().addGameView(view);
        getGameState().increment("gold", Config.KILL_GOLD);
    }

    private void gameover(){
        getDisplay().showConfirmationBox("Demo over, Thanks for playing", yes->{
            exit();
        });
    }

    public static void main (String[] args) {
        launch(args);
    }
}

