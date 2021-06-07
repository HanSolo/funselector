 /*
  * Copyright (c) 2021 by Gerrit Grunwald
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

 package eu.hansolo.fx.funselector;

 import javafx.application.Application;
 import javafx.application.Platform;
 import javafx.geometry.Insets;
 import javafx.scene.layout.Background;
 import javafx.scene.layout.BackgroundFill;
 import javafx.scene.layout.CornerRadii;
 import javafx.scene.paint.Color;
 import javafx.stage.Stage;
 import javafx.scene.layout.StackPane;
 import javafx.scene.Scene;


 /**
  * User: hansolo
  * Date: 06.06.21
  * Time: 05:16
  */
 public class Demo extends Application {
     private FunSelector funSelector;

     @Override public void init() {
         funSelector = new FunSelector();
         funSelector.upperSelectedProperty().addListener((o, ov, nv) -> System.out.println(nv ? "Upper selected" : "Lower selected"));
     }

     @Override public void start(final Stage stage) {
         StackPane pane = new StackPane(funSelector);
         pane.setPadding(new Insets(20));
         pane.setBackground(new Background(new BackgroundFill(Color.web("#21262d"), CornerRadii.EMPTY, Insets.EMPTY)));

         Scene scene = new Scene(pane);

         stage.setTitle("FunSelector");
         stage.setScene(scene);
         stage.show();

         //funSelector.setBackgroundFill(Color.RED);
         //funSelector.setForegroundFill(Color.WHITE);
         //funSelector.setSelectedFill(Color.LIME);
         //funSelector.setDeselectedFill(Color.DARKGREEN);
     }

     @Override public void stop() {
         Platform.exit();
         System.exit(0);
     }

     public static void main(String[] args) {
         launch(args);
     }
 }
