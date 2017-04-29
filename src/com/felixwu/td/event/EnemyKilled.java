package com.felixwu.td.event;

import com.almasb.fxgl.entity.GameEntity;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Created by FelixWu on 27/4/2017.
 */
public class EnemyKilled extends Event {

    public static final EventType<EnemyKilled> ANY = new EventType<>(Event.ANY, "Enermy_killed");
    private GameEntity enemy;

    public GameEntity getEnemy(){
        return enemy;
    }

    public EnemyKilled(GameEntity enemy){
        super(ANY);
        this.enemy = enemy;
    }
}
