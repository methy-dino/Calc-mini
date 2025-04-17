package com.github.Calc;
import com.github.calclogic.Parser;
import com.github.nameManaging.AlphabetManager;
import com.github.nameManaging.ColorStyle;
import com.github.nameManaging.TextManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.StringBuilder;
import com.github.basics.FnStorage;
import com.github.basics.VarStorage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Calc extends ApplicationAdapter {
    String currName = null;
    ArrayList<String> currFuncData = new ArrayList<String>();
    private Stage stage;
    byte building;
    private Skin skin;
    Parser parser;
    VarStorage vars;
    FnStorage funcs;
    Table basicMath;
    TooltipManager[] managerArr = new TooltipManager[1];
    StringBuilder currCalculation;
    TextButton[] buttonList = new TextButton[21];
    TextTooltip[] tooltips = new TextTooltip[7];
    String tracker;
    int mode = 0;

    String[][] insertMap = {{"1", "√"}, {"2", "∜"}, {"3", "!"}, {"+", "㏒"}, {"4", "^"}, {"5", "sin<"}, {"6", "cos<"}, {"-", "tan<"}, {"7", "π"}, {"8", "ans"}, {"9", "e"}, {"*", ""}, {".", "<"}, {"0", ","}, {"(", ">"}, {"/", ""}, {"E", "E"},{"C", "C"}, {")", "deg"}, {"=", "="}};
    String[] measurer = {"deg", "rad"};
    byte measurerPointer = 0;
    boolean lastVar = false;
    /**
     * Setups user Interface, called on viewport updates.
     */
    private void UISetup(){
        for (int i = 0; i < tooltips.length; i++){
            tooltips[i].getActor().setFontScale(Math.max(1,Math.round(skin.getFont("default").getData().scaleX / 1.8f)));
            tooltips[i].getContainer().width(stage.getWidth() / 3);
            tooltips[i].setTouchIndependent(false);
        }
        Table results = new Table().top().left();
         // -------------------------------------------------------------------------------------------------------- //
         basicMath.setFillParent(true);
        basicMath.clearChildren();
          results.defaults().grow();
          results.add(buttonList[20]);

          Table modeSwap = new Table();
          TextButton advancedCalc = new TextButton("advanced", skin);
          TextButton varsButton = new TextButton("vars", skin);
          varsButton.addListener(new ChangeListener(){
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                Window win = new Window("", skin);
                win.setWidth(stage.getWidth());
                win.setHeight(stage.getHeight());
                Table tab = new Table();
                ScrollPane scroll = new ScrollPane(tab, skin);
                scroll.setFadeScrollBars(false);
                win.add(scroll).grow();
                TextButton texts = new TextButton("ans" + "  ==  " + parser.lastAnswer, skin);
                texts.addListener(new ChangeListener() {
                    @Override
                    public void changed(final ChangeEvent event, final Actor actor) {
                        win.remove();
                        int i = 0;
                        while (AlphabetManager.isValidAlphabetChar(texts.getText().charAt(i))){
                            i++;
                        }
                        buttonList[20].setText(TextManager.update("ans"));
                    }
                });
                tab.add(texts).growX();
                tab.row();
                TextButton close = new TextButton("X", skin);
                close.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        win.remove();
                    }

                });
                win.addActor(close);
                close.setPosition(win.getWidth() - close.getWidth() - 7, win.getHeight() - close.getHeight() - 10);
               for (String i : vars.Keys()){
                Table varTable = new Table(skin);
                varTable.add(i).grow().left();
                varTable.add("==").grow().center();
                varTable.add(vars.getVar(i)).right();
                //varTable.add(deleteButton);
                Button var = new Button(skin);
                var.add(varTable).grow();
                var.addListener(new ChangeListener() {
                    @Override
                    public void changed(final ChangeEvent event, final Actor actor) {
                        win.remove();
                        buttonList[20].setText(TextManager.update(i));
                    }
                });
                tab.add(var).growX();
                TextButton deleteButton = new TextButton("X",skin);
                deleteButton.addListener(new ChangeListener() {

                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        vars.deleteVar(i);
                        vars.SaveVars();
                        deleteButton.remove();
                        var.remove();
                    }

                });
                tab.add(deleteButton);
                tab.row();
               }
               TextButton addVar = new TextButton("+", skin);
               tab.add(addVar);
               addVar.addListener(new ChangeListener() {
                @Override
                public void changed(final ChangeEvent event, final Actor actor) {
                    addVar.remove();
                    building = 1;
                    TextField typeName = new TextField("", skin);
                    typeName.setMessageText("Type variable Name");
                    win.row();
                    win.add(typeName).growX();
                    TextButton done = new TextButton("done", skin);
                    done.addListener(new ChangeListener() {
                        @Override
                        public void changed(final ChangeEvent event, final Actor actor) {
                            if (AlphabetManager.isValidName(typeName.getText())){
                                currName = typeName.getText();
                                buttonList[20].setText("declaring " + currName);
                                currCalculation.clear();
                                win.remove();
                            } else {
                                typeName.setText("");
                                stage.setKeyboardFocus(null);
                                typeName.setMessageText("invalid name");
                            }
                        }
                    });
                    win.row();
                    win.add(done).growX();
                }
               });
               stage.addActor(win);
            }
          });
          advancedCalc.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                mode = 1;
                buttonList[1].addListener(tooltips[0]);
                buttonList[10].addListener(tooltips[1]);
                buttonList[5].addListener(tooltips[2]);
                buttonList[6].addListener(tooltips[3]);
                buttonList[7].addListener(tooltips[4]);
                buttonList[8].addListener(tooltips[6]);
                buttonList[18].addListener(tooltips[5]);
                for (int i = 0; i < insertMap.length; i++){
                    buttonList[i].setText(insertMap[i][mode]);
                }
                //button9.setText(measurer[measurerPointer]);
            }
        });

          TextButton normalCalc = new TextButton("normal", skin);
          normalCalc.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                mode = 0;
                buttonList[1].removeListener(tooltips[0]);
                buttonList[10].removeListener(tooltips[1]);
                buttonList[5].removeListener(tooltips[2]);
                buttonList[6].removeListener(tooltips[3]);
                buttonList[7].removeListener(tooltips[4]);
                buttonList[8].removeListener(tooltips[6]);
                buttonList[18].removeListener(tooltips[5]);
                for (int i = 0; i < insertMap.length; i++){
                    buttonList[i].setText(insertMap[i][mode]);
                }
            }
        });
          TextButton functionsButton = new TextButton("f(x)", skin);
          functionsButton.addListener(new ChangeListener(){
            @Override
            public void changed(final ChangeEvent event, final Actor actor){
                Window win = new Window("", skin);
                win.setWidth(stage.getWidth());
                win.setHeight(stage.getHeight());
                Table tab = new Table();
                ScrollPane scroll = new ScrollPane(tab, skin);
                scroll.setFadeScrollBars(false);
                win.add(scroll).grow();
                StringBuilder inputs = new StringBuilder();
                TextButton close = new TextButton("X", skin);
                    close.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            win.remove();
                        }

                    });
                    win.addActor(close);
                    close.setPosition(win.getWidth() - close.getWidth() - 7, win.getHeight() - close.getHeight() - 10);
                for (String i : funcs.keys()){
                    Table innerTable = new Table(skin);
                    String[] data = funcs.getFuncData(i);
                    inputs.append("<");
                    for (int j = 1; j < data.length; j++){
                        inputs.append(data[j]);
                        if (data.length != j + 1) inputs.append(',');
                    }
                    inputs.append(">");
                    Button fnButton = new Button(innerTable, skin);
                    innerTable.add(i + inputs.toStringAndClear()).left();
                    innerTable.add("==").grow().center().space(6);
                    innerTable.add(TextManager.updateColors(0, AlphabetManager.varFunction(funcs.getFuncData(i)))).right();
                    for (Cell<Label> test : innerTable.getCells()){
                        test.getActor().setFontScale(Math.max(1,Math.round(skin.getFont("default").getData().scaleX / 1.5f)));
                    }

                    fnButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            //currCalculation.clear();
                            buttonList[20].setText(TextManager.addFunc(i+"<"));
                            win.remove();
                        }
                    });
                    tab.add(fnButton);
                    TextButton deleteButton = new TextButton("X",skin);
                deleteButton.addListener(new ChangeListener() {

                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        funcs.deleteFunc(i);
                        funcs.saveFunctions();
                        deleteButton.remove();
                        fnButton.remove();
                    }

                });
                tab.add(deleteButton).left();
                    tab.row();
                }
                TextButton addFn = new TextButton("+", skin);
                    tab.add(addFn);
                    addFn.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            addFn.remove();
                            building = 2;
                            TextField typeName = new TextField("", skin);
                            typeName.setMessageText("Type variable Name");
                            win.row();
                            win.add(typeName).growX();
                            TextButton argsButton = new TextButton("args", skin);
                            TextButton done = new TextButton("done", skin);
                            done.addListener(new ChangeListener() {
                                @Override
                                public void changed(final ChangeEvent event, final Actor actor) {
                                    if (AlphabetManager.isValidName(typeName.getText())){
                                        currName = typeName.getText();
                                        buttonList[20].setText("declaring " + currName + "<");
                                        currCalculation.clear();
                                        stage.addActor(argsButton);
                                        win.remove();
                                    } else {
                                        typeName.setText("");
                                        stage.setKeyboardFocus(null);
                                        typeName.setMessageText("invalid name");
                                    }
                                }
                            });
                            stage.addActor(argsButton);
                            //win.addActor(argsButton);
                            argsButton.setZIndex(1);
                            argsButton.getLabel().setFontScale(Math.max(1,Math.round(skin.getFont("default").getData().scaleX / 2f)));
                            argsButton.setPosition(0, stage.getHeight() - argsButton.getHeight() - (stage.getHeight() / 50));

                            argsButton.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    Window argWin = new Window("", skin);
                                    stage.addActor(argWin);
                                    TextButton close2 = new TextButton("X", skin);
                                    close2.addListener(new ChangeListener() {
                                        @Override
                                        public void changed(ChangeEvent event, Actor actor) {
                                            win.remove();
                                        }

                                    });
                                    argWin.setWidth(stage.getWidth());
                                    argWin.setHeight(stage.getHeight());
                                    close2.setPosition(argWin.getWidth() - close2.getWidth() - 7, argWin.getHeight() - close2.getHeight() - 10);
                                    Table argTab = new Table();
                                    ScrollPane scroller = new ScrollPane(argTab, skin);
                                    scroller.setFadeScrollBars(false);
                                    for (int i = 0; i < currFuncData.size(); i++){
                                        TextButton currArg = new TextButton(currFuncData.get(i), skin);
                                        int j = i;
                                        currArg.addListener(new ChangeListener() {
                                            int index = j;
                                            @Override
                                            public void changed(ChangeEvent event, Actor actor) {
                                                buttonList[20].setText(TextManager.addFuncVar(index, currFuncData));
                                                argWin.remove();
                                            }
                                        });
                                        argTab.add(currArg);
                                        argTab.row();
                                    }
                                    TextButton newArg = new TextButton("+", skin);
                                    newArg.addListener(new ChangeListener() {
                                        @Override
                                        public void changed(final ChangeEvent event, final Actor actor) {
                                            newArg.remove();
                                            TextField typeName = new TextField("", skin);
                                            typeName.setMessageText("Type arg Name");
                                            argWin.row();
                                            argWin.add(typeName).growX();
                                            TextButton done = new TextButton("done", skin);
                                            done.addListener(new ChangeListener() {
                                                @Override
                                                public void changed(final ChangeEvent event, final Actor actor) {
                                                    if (AlphabetManager.isValidName(typeName.getText())){
                                                        currFuncData.add(typeName.getText().toString());
                                                        argWin.remove();
                                                    } else {
                                                        typeName.setText("");
                                                        typeName.setMessageText("invalid name");
                                                        stage.setKeyboardFocus(null);
                                                    }
                                                }
                                            });
                                            argWin.row();
                                            argWin.add(done).growX();
                                        }
                                       });
                                    argTab.add(newArg);
                                    argWin.add(scroller).grow();
                                    argWin.addActor(close2);
                                }

                            });
                    win.row();
                    win.add(done).growX();
                        }
                    });
                stage.addActor(win);
            }
          });
        modeSwap.add(functionsButton).growY().width(stage.getWidth() / 4 - 3).space(3);
        modeSwap.add(varsButton).growY().width(stage.getWidth() / 4 - 3).space(3);
        modeSwap.add(advancedCalc).growY().width(stage.getWidth() / 4 - 3).space(3);
        modeSwap.add(normalCalc).growY().width(stage.getWidth() / 4 - 3).space(3);
        basicMath.defaults().space(3).width((stage.getWidth() / 4) - 3).height((stage.getHeight() / 7) - 3);
        basicMath.add(results).colspan(4).space(3).width(stage.getWidth() - 3).height((stage.getHeight() / 7) - 6);
        basicMath.row();
        basicMath.add(modeSwap).growX().colspan(4).space(3).height((stage.getHeight() / 7) - 3);
        basicMath.row();
        basicMath.add(buttonList[0]);//.height(Gdx.graphics.getHeight() / 6);
        basicMath.add(buttonList[1]);
        basicMath.add(buttonList[2]);
        basicMath.add(buttonList[3]);
        basicMath.row();
        basicMath.add(buttonList[4]);//.height(Gdx.graphics.getHeight() / 6);
        basicMath.add(buttonList[5]);
        basicMath.add(buttonList[6]);
        basicMath.add(buttonList[7]);
        basicMath.row();
        basicMath.add(buttonList[8]);//.height(Gdx.graphics.getHeight() / 6);
        basicMath.add(buttonList[9]);
        basicMath.add(buttonList[10]);
        basicMath.add(buttonList[11]);
        basicMath.row();
        basicMath.add(buttonList[12]);//.height(Gdx.graphics.getHeight() / 6);
        basicMath.add(buttonList[13]);
        basicMath.add(buttonList[14]);
        basicMath.add(buttonList[15]);
        basicMath.row();
        basicMath.add(buttonList[16]);//.height(Gdx.graphics.getHeight() / 6);
        basicMath.add(buttonList[17]);
        basicMath.add(buttonList[18]);
        basicMath.add(buttonList[19]);
        basicMath.row();
       Gdx.input.setInputProcessor(stage);
    }
    /**
     * Initializes User Interface for Persistant buttons (buttons who stay on screen the majority of the time).
     */
    private void UIInit(){
        final TextButton results = new TextButton("", skin);
        final TextButton button = new TextButton("1", skin);
        final TextButton button2 = new TextButton("2", skin);
        final TextButton button3 = new TextButton("3", skin);
        final TextButton button4 = new TextButton("+", skin);
        final TextButton button5 = new TextButton("4", skin);
        final TextButton button6 = new TextButton("5", skin);
        final TextButton button7 = new TextButton("6", skin);
        final TextButton button8 = new TextButton("-", skin);
        final TextButton button9 = new TextButton("7", skin);
        final TextButton button10 = new TextButton("8", skin);
        final TextButton button11 = new TextButton("9", skin);
        final TextButton button12 = new TextButton("*", skin);
        final TextButton button13 = new TextButton(".", skin);
        final TextButton button14 = new TextButton("0", skin);
        final TextButton button15 = new TextButton("(", skin);
        final TextButton button16 = new TextButton("/", skin);
        final TextButton button17 = new TextButton("E", skin);
        final TextButton button18 = new TextButton("C", skin);
        final TextButton button19 = new TextButton(")", skin);
        final TextButton button20 = new TextButton("=", skin);
        buttonList[0] = button;
        buttonList[1] = button2;
        buttonList[2] = button3;
        buttonList[3] = button4;
        buttonList[4] = button5;
        buttonList[5] = button6;
        buttonList[6] = button7;
        buttonList[7] = button8;
        buttonList[8] = button9;
        buttonList[9] = button10;
        buttonList[10] = button11;
        buttonList[11] = button12;
        buttonList[12] = button13;
        buttonList[13] = button14;
        buttonList[14] = button15;
        buttonList[15] = button16;
        buttonList[16] = button17;
        buttonList[17] = button18;
        buttonList[18] = button19;
        buttonList[19] = button20;
        buttonList[20] = results;
        //basicMath.setFillParent(true);
        //stage.addActor(basicMath);
        results.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                //results.setOrigin(results.getWidth() / 2, results.getHeight() / 2);
            }
        });
        button.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {

                results.setText(TextManager.update(button.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button2.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button2.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button3.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button3.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button4.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button4.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button5.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button5.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button6.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button6.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button7.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button7.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button8.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button8.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button9.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button9.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button10.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button10.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button11.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button11.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button12.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button12.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
        button13.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button13.getText()));
            }
        });
        // -------------------------------------------------------------------------------------------------------- //
         button14.addListener(new ChangeListener() {
             @Override
             public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button14.getText()));
             }
         });
         // -------------------------------------------------------------------------------------------------------- //
         button15.addListener(new ChangeListener() {
             @Override
             public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button15.getText()));
             }
         });
        // -------------------------------------------------------------------------------------------------------- //
          button16.addListener(new ChangeListener() {
              @Override
              public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.update(button16.getText()));
              }
          });
          // -------------------------------------------------------------------------------------------------------- //
          button17.addListener(new ChangeListener() {
              @Override
              public void changed(final ChangeEvent event, final Actor actor) {
                results.setText(TextManager.erase());
            }
          });
          // -------------------------------------------------------------------------------------------------------- //
          button18.addListener(new ChangeListener() {
              @Override
              public void changed(final ChangeEvent event, final Actor actor) {
                currCalculation.clear();
                lastVar = false;
                TextManager.clear();
                results.setText("");
              }
          });
          // -------------------------------------------------------------------------------------------------------- //
          button19.addListener(new ChangeListener() {
              @Override
              public void changed(final ChangeEvent event, final Actor actor) {
                if (mode == 1){
                    measurerPointer++;
                    if (measurerPointer == 2){
                        measurerPointer = 0;
                    }
                    button19.setText(measurer[measurerPointer]);
                } else{
                    results.setText(TextManager.update(button.getText()));
                }
              }
          });
          // -------------------------------------------------------------------------------------------------------- //
          button20.addListener(new ChangeListener() {
              @Override
              public void changed(final ChangeEvent event, final Actor actor) {
                String calculation = TextManager.getUnformatted();
                if (calculation.length()> 0){
                    parser.measureType = measurerPointer;
                    DecimalFormat normalNotation = new DecimalFormat("#.##");
                    normalNotation.setMaximumFractionDigits(8);
                    if (building == 1) {
                        vars.setVar(currName, String.valueOf(parser.compute(calculation)));
                        vars.SaveVars();
                        results.setText(currName + " was declared");
                        currName = null;
                        building = 0;
                    } else if (building == 2){
                        String[] converted = new String[currFuncData.size() + 1];
                        converted[0] = calculation;
                        for (int i = 0; i < currFuncData.size(); i++){
                            converted[i+1] = currFuncData.get(i);
                        }
                        currFuncData.clear();
                        stage.getActors().get(1).remove();
                        funcs.setFunc(currName, converted);
                        funcs.saveFunctions();
                        currName = null;
                        building = 0;
                    } else {
                        try {
                            parser.lastAnswer = String.valueOf(parser.compute(calculation));
                            results.setText(normalNotation.format(Double.valueOf(parser.lastAnswer)).replace(",", "."));
                            } catch (Exception e) {
                                parser.lastAnswer = "0";
                                results.setText("MATH ERROR, ANS SET TO 0");
                            }
                    }
                    // TO DO: FIND BETTER WAY TO CHANGE "12,3" TO "12.3" WITH NORMAL NOTATION OBJECT.
                    currCalculation.clear();
                }
              }
          });
         TooltipManager basicManager = new TooltipManager();
         basicManager.initialTime = 1f;
         basicManager.subsequentTime = 1f;
         basicManager.resetTime = 3f;
           TextTooltip nrtTooltip = new TextTooltip("Uses the previous number as the ∜ factor to the next", basicManager, skin);
         nrtTooltip.getActor().setWrap(true);
         nrtTooltip.getActor().setFontScale(Math.max(1,Math.round(skin.getFont("default").getData().scaleX / 1.4f)));
         tooltips[0] = nrtTooltip;
         TextTooltip eulerTooltip = new TextTooltip("Inserts Euler's mathematical constant", basicManager, skin);
         nrtTooltip.setTouchIndependent(true);
         eulerTooltip.getActor().setWrap(true);
         tooltips[1] = eulerTooltip;
         TextTooltip sinTooltip = new TextTooltip("Calculates the sin of the next number, based on set measurement mode", basicManager, skin);
         //a.enter(c, 100f, 100f, -1, basicMath);
         sinTooltip.getActor().setWrap(true);
         tooltips[2] = sinTooltip;
         TextTooltip cosTooltip = new TextTooltip("Calculates the cos of the next number, based on set measurement mode", basicManager, skin);
         //a.enter(c, 100f, 100f, -1, basicMath);
         cosTooltip.getActor().setWrap(true);
         tooltips[3] = cosTooltip;
         TextTooltip tanTooltip = new TextTooltip("Calculates the tan of the next number, based on set measurement mode", basicManager, skin);
         //a.enter(c, 100f, 100f, -1, basicMath);
         tanTooltip.getActor().setWrap(true);
         tooltips[4] = tanTooltip;
         TextTooltip modeTooltip = new TextTooltip("Sets the measurement mode for operations that use angles", basicManager, skin);
         //a.enter(c, 100f, 100f, -1, basicMath);
         modeTooltip.getActor().setWrap(true);
         tooltips[5] = modeTooltip;
         TextTooltip PiTooltip = new TextTooltip("Inserts the π mathematical constant", basicManager, skin);
         //a.enter(c, 100f, 100f, -1, basicMath);
         modeTooltip.getActor().setWrap(true);
         tooltips[6] = PiTooltip;
         for (int i = 0; i < tooltips.length; i++){
            tooltips[i].getActor().setFontScale(Math.max(1,Math.round(skin.getFont("default").getData().scaleX / 1.8f)));
            //tooltips[i].getContainer().width(stage.getWidth() / 3);
            tooltips[i].setTouchIndependent(true);
            tooltips[i].setInstant(true);
        }


    }
    @Override
    public void create() {
        //TextManager.update("2+");
        //TextManager.addFunc("abc<");
        try {
            ColorStyle.loadColorStyle("");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        skin = new Skin(Gdx.files.internal("ui/test.json"));
        skin.getFont("default").getData().markupEnabled = true;
        UIInit();

        buttonList[20].setText(TextManager.update(""));
        currCalculation = new StringBuilder();
        stage = new Stage(new ScreenViewport());
        basicMath = new Table().left().bottom();
        stage.addActor(basicMath);
        vars = Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS ? new VarStorage("calc", "variables", new Json()) : new VarStorage("CalcStorage/Variables.json", new Json());
        funcs = Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS ? new FnStorage("calc", "functions", new Json()) : new FnStorage("CalcStorage/Functions.json", new Json());
        //funcs.setFunc("areaTrianEqui", "<0>^2*sqrt3/4", "lado");
        parser = new Parser(vars, funcs);
        //funcs.setFunc("areaHexa", "6*areaTrianEqui<<0>>", "lado");
        //sidelineParse.start();
        // TABLE FOR THE BASIC FUNCTIONS.
        //stage.setDebugAll(true);
    }
    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        if (stage.getHeight() > 0 && stage.getWidth() > 0) skin.getFont("default").getData().setScale((stage.getHeight() + stage.getWidth()) / 400);
        UISetup();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
