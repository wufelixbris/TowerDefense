package com.felixwu.td;

import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.NodeState;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.service.UIFactory;
import com.almasb.fxgl.settings.GameSettings;
import com.felixwu.td.collision.BulletEnemyHandler;
import com.felixwu.td.collision.EnemyHomeHandler;
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
    private int levelEnemies = Config.LEVEL_ENEMY;
    private IntegerProperty numEnemies;
    private Point2D enemySpawnPoint = new Point2D(0, 40);

    private List<Entity> paths;
    private List<Entity> towers;

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
        input.addAction(new UserAction("Place Tower"){
            @Override
            protected void onActionBegin(){
                placeTower();
            }
        },MouseButton.PRIMARY);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());
        getPhysicsWorld().addCollisionHandler(new EnemyHomeHandler());
    }

    @Override
    protected void initUI() {

        Text goldText = getUIFactory().newText("", Color.YELLOW, 18);
        goldText.setTranslateX((Config.MAP_WIDTH-2)*Config.BLOCK_SIZE+10);
        goldText.setTranslateY(15);
        goldText.textProperty().bind(getGameState().intProperty("gold").asString("Gold:\n[%d]"));

        Text lifeText = getUIFactory().newText("", Color.RED, 18);
        lifeText.setTranslateX((Config.MAP_WIDTH-4)*Config.BLOCK_SIZE+10);
        lifeText.setTranslateY(15);
        lifeText.textProperty().bind(getGameState().intProperty("life").asString("Life:\n[%d]"));

        getGameScene().addUINodes(goldText, lifeText);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("gold", Config.START_GOLD);
        vars.put("life", Config.LIFE);
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
        getEventBus().addEventHandler(EnemyReachedGoal.ANY, e->onEnemyReachGoal());
    }

    private void spawnEnemy() {
        numEnemies.set(numEnemies.get()-1);
        getGameWorld().spawn("Enemy", enemySpawnPoint.getX(), enemySpawnPoint.getY());
    }

    private void placeTower(){

        if(getGameState().getInt("gold")<100){
            return;
        }

        if (paths == null) {
            paths = getGameWorld().getEntitiesByType(TdType.PATH);
        }

        int x=((int)getInput().getMouseXWorld()/Config.BLOCK_SIZE)*Config.BLOCK_SIZE;
        int y=((int)getInput().getMouseYWorld()/Config.BLOCK_SIZE)*Config.BLOCK_SIZE;

        towers = getGameWorld().getEntitiesByType(TdType.TOWER);
        //check no existing tower in tile
        for(Entity tower:towers){
            Point2D position = new Point2D(Entities.getPosition(tower).getX(), Entities.getPosition(tower).getY());
            if(position.getX()==x && position.getY()==y){
                return;
            }
        }
        //cannot place tower in paths
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
            gamewin();
        }
        GameEntity enemy = event.getEnemy();
        Point2D position = Entities.getPosition(enemy).getValue();
        getGameWorld().spawn("Explosion", new SpawnData(position.getX(), position.getY()));
        getGameState().increment("gold", Config.KILL_GOLD);
    }

    private void onEnemyReachGoal(){
        levelEnemies--;
        getGameState().setValue("life", getGameState().getInt("life") - 1);
        if(getGameState().getInt("life")==0){
            gameover();
        }
        else if(levelEnemies==0){
            gamewin();
        }
    }

    private void gameover(){

        getDisplay().showConfirmationBox("Demo over, you lose!", yes->{
            exit();
        });
    }

    private void gamewin(){
        getDisplay().showConfirmationBox("Demo over, you win!", yes->{exit();});
    }

    public static void main (String[] args) {
        launch(args);
    }
}

