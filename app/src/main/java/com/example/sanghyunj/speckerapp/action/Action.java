package com.example.sanghyunj.speckerapp.action;

import com.example.sanghyunj.speckerapp.Controller;

/**
 * Created by sanghyunj on 15/03/2017.
 */
public interface Action {
    ActionType getType();
    boolean shouldUpdate();
    void doAction(Controller controller);
}
