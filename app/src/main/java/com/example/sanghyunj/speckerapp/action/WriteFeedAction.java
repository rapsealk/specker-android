package com.example.sanghyunj.speckerapp.action;

import com.example.sanghyunj.speckerapp.Controller;

/**
 * Created by sanghyunj on 15/03/2017.
 */
public class WriteFeedAction implements Action {


    @Override
    public ActionType getType() {
        return ActionType.WRITE_FEED;
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }

    @Override
    public void doAction(Controller controller) {

    }
}
