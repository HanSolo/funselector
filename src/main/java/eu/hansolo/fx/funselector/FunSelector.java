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

 import javafx.animation.Interpolator;
 import javafx.animation.KeyFrame;
 import javafx.animation.KeyValue;
 import javafx.animation.Timeline;
 import javafx.beans.DefaultProperty;
 import javafx.beans.property.BooleanProperty;
 import javafx.beans.property.BooleanPropertyBase;
 import javafx.beans.property.ObjectProperty;
 import javafx.beans.property.ObjectPropertyBase;
 import javafx.beans.property.ReadOnlyBooleanProperty;
 import javafx.collections.ObservableList;
 import javafx.geometry.Insets;
 import javafx.scene.Node;
 import javafx.scene.control.Label;
 import javafx.scene.layout.Background;
 import javafx.scene.layout.BackgroundFill;
 import javafx.scene.layout.CornerRadii;
 import javafx.scene.layout.Pane;
 import javafx.scene.layout.Region;
 import javafx.scene.paint.Color;
 import javafx.scene.paint.Paint;
 import javafx.scene.shape.Rectangle;
 import javafx.util.Duration;


 /**
  * User: hansolo
  * Date: 06.06.21
  * Time: 04:28
  */
 @DefaultProperty("children")
 public class FunSelector extends Region {
     public  static final Color                 DEFAULT_BACKGROUND_COLOR = Color.web("#0d1117");
     public  static final Color                 DEFAULT_FOREGROUND_COLOR = Color.web("#ffffff");
     public  static final Color                 DEFAULT_SELECTED_COLOR   = Color.web("#39d353");
     public  static final Color                 DEFAULT_DESELECTED_COLOR = Color.web("#0e4429");
     private static final double                PREFERRED_WIDTH          = 250;
     private static final double                PREFERRED_HEIGHT         = 90;
     private static final double                MINIMUM_WIDTH            = 120;
     private static final double                MINIMUM_HEIGHT           = 90;
     private static final double                MAXIMUM_WIDTH            = 1024;
     private static final double                MAXIMUM_HEIGHT           = 90;
     private static final double                INSETS                   = 10;
     private static final double                OFFSET                   = 20;
     private static final double                SIZE                     = 20;
     private static final SpringInterpolator    SPRING_INTERPOLATOR      = new SpringInterpolator(0.75, 0.058, 5.0, 0.0, true);
     private              String                userAgentStyleSheet;
     private              double                width;
     private              double                height;
     private              Rectangle             upperPath;
     private              Rectangle             lowerPath;
     private              Label                 upperLabel;
     private              Label                 lowerLabel;
     private              Pane                  pane;
     private              ObjectProperty<Paint> backgroundFill;
     private              ObjectProperty<Paint> foregroundFill;
     private              ObjectProperty<Paint> deselectedFill;
     private              ObjectProperty<Paint> upperLabelFill;
     private              ObjectProperty<Paint> selectedFill;
     private              ObjectProperty<Paint> lowerLabelFill;
     private              BooleanProperty       upperSelected;
     private              Timeline              timeline;


     // ******************** Constructors **************************************
     public FunSelector() {
         new Background(new BackgroundFill(DEFAULT_BACKGROUND_COLOR, new CornerRadii(INSETS), new Insets(INSETS)));
         backgroundFill    = new ObjectPropertyBase<>(DEFAULT_BACKGROUND_COLOR) {
             @Override protected void invalidated() { pane.setBackground(new Background(new BackgroundFill(get(), new CornerRadii(INSETS), new Insets(INSETS)))); }
             @Override public Object getBean() { return FunSelector.this; }
             @Override public String getName() { return "backgroundFill"; }
         };
         foregroundFill    = new ObjectPropertyBase<>(DEFAULT_FOREGROUND_COLOR) {
             @Override protected void invalidated() {
                 upperLabelFill.set(get());
                 lowerLabelFill.set(get());
             }
             @Override public Object getBean() { return FunSelector.this; }
             @Override public String getName() { return "foregroundFill"; }
         };
         deselectedFill    = new ObjectPropertyBase<>(DEFAULT_DESELECTED_COLOR) {
             @Override protected void invalidated() {  }
             @Override public Object getBean() { return FunSelector.this; }
             @Override public String getName() { return "upperSelectorFill"; }
         };
         upperLabelFill    = new ObjectPropertyBase<>(DEFAULT_FOREGROUND_COLOR) {
             @Override protected void invalidated() {  }
             @Override public Object getBean() { return FunSelector.this; }
             @Override public String getName() { return "upperLabelFill"; }
         };
         selectedFill      = new ObjectPropertyBase<>(DEFAULT_SELECTED_COLOR) {
             @Override protected void invalidated() {  }
             @Override public Object getBean() { return FunSelector.this; }
             @Override public String getName() { return "lowerSelectorFill"; }
         };
         lowerLabelFill    = new ObjectPropertyBase<>(DEFAULT_FOREGROUND_COLOR) {
             @Override protected void invalidated() {  }
             @Override public Object getBean() { return FunSelector.this; }
             @Override public String getName() { return "lowerLabelFill"; }
         };
         timeline          = new Timeline();
         upperSelected     = new BooleanPropertyBase(false) {
             @Override protected void invalidated() {  }
             @Override public Object getBean() { return FunSelector.this; }
             @Override public String getName() { return "upperSelected"; }
         };
         initGraphics();
         registerListeners();
     }


     // ******************** Initialization ************************************
     private void initGraphics() {
         if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
             Double.compare(getHeight(), 0.0) <= 0) {
             if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                 setPrefSize(getPrefWidth(), getPrefHeight());
             } else {
                 setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
             }
         }

         getStyleClass().add("fun-selector");

         upperPath  = new Rectangle(OFFSET, OFFSET, SIZE, SIZE);
         upperPath.setArcHeight(20);
         upperPath.setArcWidth(20);
         upperPath.fillProperty().bindBidirectional(deselectedFill);
         upperLabel = new Label("upper");
         upperLabel.setLayoutX(OFFSET + 30);
         upperLabel.setLayoutY(OFFSET + 1);
         upperLabel.textFillProperty().bindBidirectional(upperLabelFill);

         lowerPath  = new Rectangle(OFFSET, OFFSET + 30, SIZE, SIZE);
         lowerPath.setArcHeight(20);
         lowerPath.setArcWidth(20);
         lowerPath.fillProperty().bindBidirectional(selectedFill);
         lowerLabel = new Label("lower");
         lowerLabel.setLayoutX(OFFSET + 30);
         lowerLabel.setLayoutY(OFFSET + 31);
         lowerLabel.textFillProperty().bindBidirectional(lowerLabelFill);

         pane = new Pane(upperPath, upperLabel, lowerPath, lowerLabel);
         pane.setBackground(new Background(new BackgroundFill(DEFAULT_BACKGROUND_COLOR, new CornerRadii(INSETS), new Insets(INSETS))));

         getChildren().setAll(pane);
     }

     private void registerListeners() {
         widthProperty().addListener(o -> resize());
         heightProperty().addListener(o -> resize());
         setOnMousePressed(e -> select());
     }


     // ******************** Methods *******************************************
     @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
     @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
     @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
     @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
     @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
     @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

     @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

     public boolean isUpperSelected() { return upperSelected.get(); }
     public ReadOnlyBooleanProperty upperSelectedProperty() { return upperSelected; }

     public Paint getBackgroundFill() { return backgroundFill.get(); }
     public void setBackgroundFill(final Paint fill) { backgroundFill.set(fill); }
     public ObjectProperty<Paint> backgroundFillProperty() { return backgroundFill; }

     public Paint getForegroundFill() { return foregroundFill.get(); }
     public void setForegroundFill(final Paint fill) { foregroundFill.set(fill); }
     public ObjectProperty<Paint> foregroundFillProperty() { return foregroundFill; }

     public Paint getSelectedFill() { return selectedFill.get(); }
     public void setSelectedFill(final Paint color) { selectedFill.set(color); }
     public ObjectProperty<Paint> selectedFillProperty() { return selectedFill; }

     public Paint getDeselectedFill() { return deselectedFill.get(); }
     public void setDeselectedFill(final Paint color) { deselectedFill.set(color); }
     public ObjectProperty<Paint> deselectedFillProperty() { return deselectedFill; }

     public void select() {
        if (upperSelected.get()) {
            selectLower();
        } else {
            selectUpper();
        }
     }

     private void selectUpper() {
         upperPath.toFront();
         KeyValue kv0 = new KeyValue(lowerPath.heightProperty(), SIZE, Interpolator.EASE_BOTH);
         KeyValue kv1 = new KeyValue(lowerPath.heightProperty(), SIZE * 2.5, Interpolator.EASE_BOTH);
         KeyValue kv2 = new KeyValue(lowerPath.translateYProperty(), -SIZE * 1.5, Interpolator.EASE_BOTH);
         KeyValue kv3 = new KeyValue(lowerPath.heightProperty(), SIZE * 2.5);
         KeyValue kv4 = new KeyValue(lowerPath.heightProperty(), SIZE, Interpolator.EASE_BOTH);

         KeyValue kv5 = new KeyValue(upperPath.translateYProperty(), 0);
         KeyValue kv6 = new KeyValue(upperPath.translateYProperty(), SIZE * 1.5, SPRING_INTERPOLATOR);

         KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0);
         KeyFrame kf1 = new KeyFrame(Duration.millis(500), kv1, kv2, kv5);
         KeyFrame kf2 = new KeyFrame(Duration.millis(1000), kv3, kv6);
         KeyFrame kf3 = new KeyFrame(Duration.millis(1500), kv4);
         timeline.getKeyFrames().setAll(kf0, kf1, kf2, kf3);
         timeline.play();
         timeline.setOnFinished(e -> upperSelected.set(true));
     }

     private void selectLower() {
         lowerPath.toFront();
         KeyValue kv0 = new KeyValue(upperPath.heightProperty(), SIZE, Interpolator.EASE_BOTH);
         KeyValue kv1 = new KeyValue(upperPath.heightProperty(), SIZE * 2.5, Interpolator.EASE_BOTH);
         KeyValue kv2 = new KeyValue(upperPath.translateYProperty(), 0, Interpolator.EASE_BOTH);
         KeyValue kv3 = new KeyValue(upperPath.heightProperty(), SIZE * 2.5);
         KeyValue kv4 = new KeyValue(upperPath.heightProperty(), SIZE, Interpolator.EASE_BOTH);

         KeyValue kv5 = new KeyValue(lowerPath.translateYProperty(), -SIZE * 1.5);
         KeyValue kv6 = new KeyValue(lowerPath.translateYProperty(), 0, SPRING_INTERPOLATOR);

         KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0);
         KeyFrame kf1 = new KeyFrame(Duration.millis(500), kv1, kv2, kv5);
         KeyFrame kf2 = new KeyFrame(Duration.millis(1000), kv3, kv6);
         KeyFrame kf3 = new KeyFrame(Duration.millis(1500), kv4);
         timeline.getKeyFrames().setAll(kf0, kf1, kf2, kf3);
         timeline.play();
         timeline.setOnFinished(e -> upperSelected.set(false));
     }


     // ******************** Layout *******************************************
     @Override public String getUserAgentStylesheet() {
         if (null == userAgentStyleSheet) { userAgentStyleSheet = FunSelector.class.getResource("funselector.css").toExternalForm(); }
         return userAgentStyleSheet;
     }

     private void resize() {
         width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
         height = getHeight() - getInsets().getTop() - getInsets().getBottom();

         if (width > 0 && height > 0) {
             pane.setMaxSize(width, height);
             pane.setPrefSize(width, height);
             pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

         }
     }
 }
