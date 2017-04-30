package com.felixwu.td;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.scene.menu.MenuType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Created by FelixWu on 30/4/2017.
 */
public class TdMenu extends FXGLMenu {

    private VBox vbox = new VBox(50);

    public TdMenu(GameApplication app, MenuType type) {
        super(app, type);

        vbox.getChildren().addAll(new Pane(), new Pane());
        vbox.setTranslateX(50);
        vbox.setTranslateY(50);

        contentRoot.setTranslateX(280);
        contentRoot.setTranslateY(130);

        menuRoot.getChildren().add(vbox);
        contentRoot.getChildren().add(EMPTY);

        vbox.getChildren().set(0, makeMenuBar());


    }

    private HBox makeMenuBar() {
        Button tb1 = createActionButton("NEW GAME", this::fireNewGame);
        ToggleButton tb2 = new ToggleButton("OPTIONS");
        Button tb3 = createActionButton("EXIT", this::fireExit);

        tb1.setFont(FXGL.getUIFactory().newFont(18));
        tb2.setFont(FXGL.getUIFactory().newFont(18));
        tb3.setFont(FXGL.getUIFactory().newFont(18));

        ToggleGroup group = new ToggleGroup();
        tb2.setToggleGroup(group);

        group.selectedToggleProperty().addListener((obs, old, newToggle) -> {
            if (newToggle == null) {
                group.selectToggle(old);
                return;
            }
            switchMenuTo((Node)newToggle.getUserData());
        });
        group.selectToggle(tb2);

        HBox hbox = new HBox(10, tb1, tb2, tb3);
        hbox.setAlignment(Pos.TOP_CENTER);
        return hbox;
    }

    @Override
    protected Node createBackground(double width, double height) {
        return new Rectangle(app.getWidth(), app.getHeight(), Color.BROWN);
    }

    @Override
    protected Node createTitleView(String title) {
        Text titleView = FXGL.getUIFactory().newText(app.getSettings().getTitle(), 18);
        titleView.setTranslateY(30);
        return titleView;
    }

    @Override
    protected Node createVersionView(String version) {
        Text view = FXGL.getUIFactory().newText(version, 16);
        view.setTranslateX(app.getWidth() - view.getLayoutBounds().getWidth());
        view.setTranslateY(20);
        return view;
    }

    @Override
    protected Node createProfileView(String profileName) {
        Text view = FXGL.getUIFactory().newText(profileName, 24);
        view.setTranslateX(app.getWidth() - view.getLayoutBounds().getWidth());
        view.setTranslateY(50);
        return view;
    }

    protected final Button createActionButton(String name, Runnable action) {
        Button btn = FXGL.getUIFactory().newButton(name);
        btn.setOnAction(e -> action.run());
        return btn;
    }

}
