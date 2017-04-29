package com.felixwu.td;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.service.UIFactory;
import com.almasb.fxgl.settings.GameSettings;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends GameApplication{
    //TODO: read from level data
    private int levelEnemies = 10;
    private IntegerProperty numEnemies;
    private javafx.geometry.Point2D enemySpawnPoint = new Point2D(50, 0);

    private List<Point2D> waypoints = new ArrayList<>();

    public List<Point2D> getwaypoints() {
        return new ArrayList<>(waypoints);
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("TowerDefense");
        gameSettings.setVersion("1.0");
        gameSettings.setWidth(800);
        gameSettings.setHeight(600);
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
    protected void initGame() {
        //TODO: read this from external level data
        waypoints.addAll(Arrays.asList(
                new Point2D(700, 0),
                new Point2D(700, 300),
                new Point2D(50, 300),
                new Point2D(50, 550),
                new Point2D(700, 550)
        ));

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
        getGameWorld().spawn("Tower",
                new SpawnData(getInput().getMouseXWorld(), getInput().getMouseYWorld())
        );
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

    }

    private void gameover(){
        getDisplay().showConfirmationBox("Demo over, Thanks for Playing!", yes->{
            exit();
        });
    }

    public static void main (String[] args) {
        launch(args);
    }
}

