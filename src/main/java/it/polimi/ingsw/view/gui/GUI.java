package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.utility.messages.*;
import it.polimi.ingsw.view.IView;

public class GUI implements IView, Publisher<VCEvent>, Listener<Event> {
    public GUI(Client client) {
    }

    @Override
    public void displayGreet() {

    }

    @Override
    public void displaySetup() {

    }

    @Override
    public void displayLogin() {

    }

    @Override
    public void displayIdle() {

    }

    @Override
    public boolean shouldStopDisplayIdle() {
        return false;
    }

    @Override
    public void stopDisplayIdle() {

    }

    @Override
    public void displayGeneralMsg(Message msg) {

    }

    @Override
    public void transitionToDisplay(String displayName) {

    }

    public void startDisplay() {

    }

    @Override
    public void update(Event event) {

    }

    @Override
    public void subscribe(Listener<VCEvent> listener) {

    }

    @Override
    public void unsubscribe(Listener<VCEvent> listener) {

    }

    @Override
    public void publish(VCEvent event) {

    }
}
