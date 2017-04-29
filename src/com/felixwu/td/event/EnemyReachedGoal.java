package com.felixwu.td.event;

import com.almasb.fxgl.entity.GameEntity;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Created by FelixWu on 27/4/2017.
 */
public class EnemyReachedGoal extends Event{
    public static final EventType<EnemyReachedGoal> ANY = new EventType<>(Event.ANY, "EnermyReachedGoalEvent");

    public EnemyReachedGoal(){
        super(ANY);
     }
}
