package it.polimi.ingsw.view.cli;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.utility.InputConsumer;
import it.polimi.ingsw.utility.messages.*;
import it.polimi.ingsw.view.IView;

import static it.polimi.ingsw.utility.messages.LeaderActionContext.ActionStep.*;
import static it.polimi.ingsw.utility.messages.ActivateProdActionContext.ActionStep.*;
import static it.polimi.ingsw.utility.messages.TakeResActionContext.ActionStep.*;
import static it.polimi.ingsw.utility.messages.BuyDevCardActionContext.ActionStep.*;
import static it.polimi.ingsw.utility.messages.CVEvent.EventType.*;
import static it.polimi.ingsw.utility.messages.VCEvent.EventType.*;

import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Cli class , it is the client interface
 *
 * @author
 */

public class CLI implements IView, Publisher<VCEvent>, Listener<Event> {
    private final Client client;
    private final PrintWriter out;
    private final Scanner in;
    private Map<String, Runnable> displayNameMap = new HashMap<>();
    private Queue<Runnable> displayTransitionQueue = new ArrayDeque<>();
    private boolean shouldTerminateClient;
    private boolean stopIdle;
    private String generalmsg;
    private TakeResActionContext takeResContext;
    private BuyDevCardActionContext buyDevCardContext;
    private ActivateProdActionContext activateProdContext;
    private LeaderActionContext activateLeaderContext;
    private CVEvent initialCVevent;
    private List<Listener<VCEvent>> listenerList = new ArrayList<>();
    private String marketTrayDescription;
    private String devCardMatrixDescription;
    private Map<Integer, PersonalBoardDescription> userIDtoBoardDescriptions = new HashMap<Integer, PersonalBoardDescription>();
    private Map<Integer, String> userIDtoUsernames = new HashMap<Integer, String>();
    private boolean majorActionDone;

    /**
     * Constructor of the class
     *
     * @param client player object
     *               also initialize the in and out stream.
     */
    public CLI(Client client) {
        this.client = client;
        this.out = new PrintWriter(System.out, true);
        this.in = new Scanner(System.in);
        this.shouldTerminateClient = false;
    }

    @Override
    public void startDisplay() {
        displayNameMap.put("displayGreet", this::displayGreet);
        displayNameMap.put("displaySetup", this::displaySetup);
        displayNameMap.put("displayIdle", this::displayIdle);
        displayNameMap.put("displayFirstLogin", this::displayFirstLogin);
        displayNameMap.put("displayLogin", this::displayLogin);
        displayNameMap.put("displayGeneralMsg", this::displayGeneralMsg);
        displayNameMap.put("displayFourLeaderCard", this::displayFourLeaderCard);
        displayNameMap.put("displayTurnAssign", this::displayTurnAssign);
        displayNameMap.put("displayAllActionSelection", this::displayAllActionSelection);
        displayNameMap.put("displayMinorActionSelection", this::displayMinorActionSelection);
        displayNameMap.put("chooseLeaderAction", this::chooseLeaderAction);
        displayNameMap.put("chooseRowColumnNumber", this::chooseRowColumnNumber);
        displayNameMap.put("chooseShelvesToPut", this::chooseShelvesToPut);
        displayNameMap.put("chooseColorLevel", this::chooseColorLevel);
        displayNameMap.put("displayWarehouse", this::displayWarehouse);
        displayNameMap.put("displayMarketTray", this::displayMarketTray);
        displayNameMap.put("displayDevCardMatrix", this::displayDevCardMatrix);
        displayNameMap.put("displayMinorActionSelection", this::displayMinorActionSelection);
        displayNameMap.put("displayStrongbox", this::displayStrongbox);
        displayNameMap.put("displayDevSlots", this::displayDevSlots);
        displayNameMap.put("displayFaithTrack", this::displayFaithTrack);
        displayNameMap.put("displayLeaderCards", this::displayLeaderCards);
        displayNameMap.put("displayAllPersonalBoards", this::displayAllPersonalBoards);

        //TODO add used methods at the end

//        displayNameMap.put("displayBuyDevCardAction", this::displayBuyDevCardAction);
//        displayNameMap.put("displayActivateProdAction", this::displayActivateProdAction);
//        displayNameMap.put("displayWarehouseAndStrongbox", this::displayWarehouseAndStrongbox);
//
//
//
//        displayNameMap.put("displayOtherPlayers", this::displayOtherPlayers);
//        displayNameMap.put("displayEndTurn", this::displayEndTurn);
        displayNameMap.put("displayDevCardMatrix", this::displayDevCardMatrix);
        displayNameMap.put("displayWarehouse", this::displayWarehouse);
        displayNameMap.put("displayStrongbox", this::displayStrongbox);
        displayNameMap.put("displayDevSlots", this::displayDevSlots);
        displayNameMap.put("displayFaithTrack", this::displayFaithTrack);
        displayNameMap.put("displayLeaderCards", this::displayLeaderCards);
        displayNameMap.put("displayOtherPersonalBoards", this::displayOtherPersonalBoards);
        displayNameMap.put("displayEndTurn", this::displayEndTurn);
        displayNameMap.put("chooseDevSlotToPutDevCard", this::chooseDevSlotToPutDevCard);
        displayNameMap.put("choosePayDevCardCostFromWhere", this::choosePayDevCardCostFromWhere);


        addNextDisplay("displayGreet");
        addNextDisplay("displaySetup");
        startDisplayTransition();
    }

    private void startDisplayTransition() {
        boolean stop;
        synchronized (this) {
            stop = shouldTerminateClient;
        }
        while (!stop) {
            if (displayTransitionQueue.peek() == null) {
                // for debug display idle cancel, maybe for demo too
//                displayNameMap.get("displayIdle").run();
            } else {
                displayTransitionQueue.poll().run();
            }
            synchronized (this) {
                stop = shouldTerminateClient;
            }
        }
    }

    public synchronized void addNextDisplay(String displayName) {
        if (displayTransitionQueue.peek() == null)
            stopDisplayIdle();
        displayTransitionQueue.add(displayNameMap.get(displayName));
    }

    @Override
    public void displayGreet() {
        out.println("Welcome to Masters of Renaissance!");
    }

    @Override
    public void displaySetup() {
//        out.println("Enter IP address of the server:");
//        String ip = InputConsumer.getIP(in);
//        out.println("Enter port number of the server:");
//        int portNumber = InputConsumer.getPortNumber(in);
        String ip = "localhost";
        int portNumber = 3000; //for debug
        out.println("Connecting to server...");
        client.connectToServer(ip, portNumber);
    }

    @Override
    public void displayFirstLogin() {
        out.println("Choose a username:");
        // TODO FOR DEBUG, DONT FORGET TO GO BACK TO MANUAL INPUT
//        String username = InputConsumer.getUserName(in, out);
        String username = "omer";
        out.println("Choose number of players you would like to play with:");
//        Integer numberOfPlayers = InputConsumer.getNumberOfPlayers(in, out);
        Integer numberOfPlayers = 2;
        Map<String, String> firstLoginMap = new HashMap<>();
        firstLoginMap.put("numberOfPlayers", numberOfPlayers.toString());
        firstLoginMap.put("username", username);
        Message loginmsg = new Message(Message.MsgType.REQUEST_FIRST_LOGIN, firstLoginMap);
        client.sendToServer(loginmsg);
    }

    @Override
    public void displayLogin() {
        out.println("Choose a username:");
        // TODO FOR DEBUG, DONT FORGET TO GO BACK TO MANUAL INPUT
//        String username = InputConsumer.getUserName(in, out);
        String username = "John";
        Message loginmsg = new Message(Message.MsgType.REQUEST_LOGIN, username);
        client.sendToServer(loginmsg);
    }

    /**
     * method that handle the beginning aspect of the game like choosing the leader  card,
     * assigning order of the players, and displaying the player menu
     */
    private void routeInitialActionsDisplay() {
        switch (initialCVevent.getEventType()) {
            case CHOOSE_TWO_LEADER_CARD:
                addNextDisplay("displayFourLeaderCard");
                break;
            case ASSIGN_TURN_ORDER:
                initEmptyPersonalBoards();
                addNextDisplay("displayTurnAssign");
                break;
        }
    }

    private void initEmptyPersonalBoards(){
        for(Integer userID: userIDtoUsernames.keySet()){
            PersonalBoardDescription pbd = new PersonalBoardDescription();
            Map<PersonalBoard.PopeArea, Boolean> map = new HashMap<>();
            map.put(PersonalBoard.PopeArea.FIRST, false);
            map.put(PersonalBoard.PopeArea.SECOND, false);
            map.put(PersonalBoard.PopeArea.THIRD, false);
            pbd.setTileMap(map);
            pbd.setFaithPoints(0); //initial faith point will be given if necessary after turn assign
            userIDtoBoardDescriptions.put(userID, pbd);
        }
    }

    /**
     * method that handle the draw of the 4 leader cards and manage the two leader that the player wants to keep.
     * after the choice, an event from the View to the Controller (VCEvent) is published so that the controller
     * can know about the player's choice
     */
    public void displayFourLeaderCard() {
        out.println("Here are the four leader card options, select two of them:");
        Type type = new TypeToken<List<LeaderCard>>() {
        }.getType();
        List<LeaderCard> fourLeaderCards = (List<LeaderCard>) initialCVevent.getEventPayload(type);
        for (int i = 0; i < fourLeaderCards.size(); i++) {
            out.println(i + 1 + ") " + fourLeaderCards.get(i).describeLeaderCard());
        }
        out.println("Enter the index of first leader card to keep:");
        // TODO FOR DEBUG, DONT FORGET TO GO BACK TO MANUAL INPUT
//        Integer firstIndex = InputConsumer.getANumberBetween(in, out, 1, 4);
        Integer firstIndex = 1;
        out.println("Enter the index of second leader card to keep:");
//        Integer secondIndex = InputConsumer.getANumberBetween(in, out, 1, 4);
        Integer secondIndex = 2;
        while (firstIndex.equals(secondIndex)) {
            out.println("Please enter a different index than first selection:");
            secondIndex = InputConsumer.getANumberBetween(in, out, 1, 4);
        }
        List<LeaderCard> twoLeaderCards = new ArrayList<>();
        twoLeaderCards.add(fourLeaderCards.get(firstIndex - 1));
        twoLeaderCards.add(fourLeaderCards.get(secondIndex - 1));
        VCEvent vcEvent = new VCEvent(LEADER_CARDS_CHOOSEN, twoLeaderCards);
        publish(vcEvent);
    }

    /**
     * method that handle the assign of the order of the players and give them based on the order the initial resources
     */
    public void displayTurnAssign() {
        Integer turn = (Integer) initialCVevent.getEventPayload(Integer.class);
        switch (turn) {
            case 0: // SOLO PLAYER
                Resources.ResType initResTypeSolo = InputConsumer.getResourceType(in, out);
                Resources initResourceSolo = new Resources(initResTypeSolo, 1);
                VCEvent vcEventSolo = new VCEvent(INIT_RES_CHOOSEN, initResourceSolo);
                publish(vcEventSolo);
                break;
            case 1:
                out.println("You are the first player.");
                out.println("You have the inkwell but no initial resources or faith points.");
                // displayIdle automatically called
                break;
            case 2:
                out.println("You are the second player.");
                out.println("You will have one initial resource of your choosing in the warehouse.");
                // TODO FOR DEBUG, DONT FORGET TO GO BACK TO MANUAL INPUT
//                Resources.ResType initResType = InputConsumer.getResourceType(in, out);
                Resources.ResType initResType = Resources.ResType.COIN;
                Resources initResource = new Resources(initResType, 1);
                VCEvent vcEvent = new VCEvent(INIT_RES_CHOOSEN, initResource);
                publish(vcEvent);
                break;
            case 3:
                out.println("You are the third player.");
                out.println("You will start with one faith point on your faith track.");
                out.println("You will have one initial resource of your choosing in the warehouse.");
                Resources.ResType initResTypeTwo = InputConsumer.getResourceType(in, out);
                Resources initResourceTwo = new Resources(initResTypeTwo, 1);
                VCEvent vcEventTwo = new VCEvent(INIT_RES_CHOOSEN, initResourceTwo);
                publish(vcEventTwo);
                break;
            case 4:
                out.println("You are the fourth player.");
                out.println("You will start with one faith point on your faith track.");
                out.println("You will have two initial resources of your choosing in the warehouse.");
                Resources.ResType initResTypeThree = InputConsumer.getResourceType(in, out);
                Resources initResourceThree = new Resources(initResTypeThree, 1);
                initResTypeThree = InputConsumer.getResourceType(in, out);
                initResourceThree.add(initResTypeThree, 1);
                VCEvent vcEventThree = new VCEvent(INIT_RES_CHOOSEN, initResourceThree);
                publish(vcEventThree);
                break;
        }
    }

    /**
     * methods that handle the menu of the player's actions and based on the action chosen call respectively
     * the method that handle, except for the 3 normal actions and the leader card activation.
     * When the player chooses one of the 3 normal or the leader card
     * actions an event it created and sent so that the server to handle it
     */
    public void displayAllActionSelection() {
        VCEvent vcEvent;
        out.println("It is your turn now!");
        out.println("[1] Take resource from market");
        out.println("[2] Buy one development card");
        out.println("[3] Activate the production");
        out.println("[4] Activate a leader card");
        out.println("[5] View market tray");
        out.println("[6] View development card matrix");
        out.println("[7] View warehouse");
        out.println("[8] View strongbox");
        out.println("[9] View development slots");
        out.println("[10] View faith track");
        out.println("[11] View leader cards");
        out.println("[12] View other personal boards");
        out.println("[13] End turn");
        out.println("Enter the index of the action you want to take:");
        int index = InputConsumer.getANumberBetween(in, out, 1, 13);
        switch (index) {
            case 1:
                vcEvent = new VCEvent(TAKE_RES_ACTION_SELECTED);
                publish(vcEvent);
                break;
            case 2:
                vcEvent = new VCEvent(BUY_DEVCARD_ACTION_SELECTED);
                publish(vcEvent);
                break;
            case 3:
                vcEvent = new VCEvent(ACTIVATE_PROD_ACTION_SELECTED);
                publish(vcEvent);
                break;
            case 4:
                // todo AMOR leader card starts here
                vcEvent = new VCEvent(ACTIVATE_LEADER_CONTEXT_SELECTED);
                publish(vcEvent);
                break;
            case 5:
                addNextDisplay("displayMarketTray");
                addNextDisplay("displayAllActionSelection");
                break;
            case 6:
                addNextDisplay("displayDevCardMatrix");
                addNextDisplay("displayAllActionSelection");
                break;
            case 7:
                addNextDisplay("displayWarehouse");
                addNextDisplay("displayAllActionSelection");
                break;
            case 8:
                addNextDisplay("displayStrongbox");
                addNextDisplay("displayAllActionSelection");
                break;
            case 9:
                addNextDisplay("displayDevSlots");
                addNextDisplay("displayAllActionSelection");
                break;
            case 10:
                addNextDisplay("displayFaithTrack");
                addNextDisplay("displayAllActionSelection");
                break;
            case 11:
                addNextDisplay("displayLeaderCards");
                addNextDisplay("displayAllActionSelection");
                break;
            case 12:
                addNextDisplay("displayOtherPersonalBoards");
                addNextDisplay("displayAllActionSelection");
                break;
            case 13:
                addNextDisplay("displayEndTurn");
                break;
        }
    }

    /**
     * method that handle a further menu of minor action, to allow the player to make more
     * action during the game. It is only a display method of the personal board
     */
    public void displayMinorActionSelection() {
        out.println("Do you want to execute any other action?");
        out.println("Enter the index of the action you want to take:");
        out.println("[1] View market tray");
        out.println("[2] View warehouse");
        out.println("[3] View strongbox");
        out.println("[4] View development slots");
        out.println("[5] View faith track");
        out.println("[6] View or modify leader cards");
        out.println("[7] View other personal boards");
        out.println("[8] End Turn");
        int index = InputConsumer.getANumberBetween(in, out, 1, 8);
        switch (index) {
            case 1:
                addNextDisplay("displayMarketTray");
                addNextDisplay("displayMinorActionSelection");
                break;
            case 2:
                addNextDisplay("displayWarehouse");
                addNextDisplay("displayMinorActionSelection");
                break;
            case 3:
                addNextDisplay("displayStrongbox");
                addNextDisplay("displayMinorActionSelection");
                break;
            case 4:
                addNextDisplay("displayDevSlots");
                addNextDisplay("displayMinorActionSelection");
                break;
            case 5:
                addNextDisplay("displayFaithTrack");
                addNextDisplay("displayMinorActionSelection");
                break;
            case 6:
                addNextDisplay("displayLeaderCards");
                addNextDisplay("displayMinorActionSelection");
                break;
            case 7:
                addNextDisplay("displayOtherPersonalBoards");
                addNextDisplay("displayMinorActionSelection");
                break;
            case 8:
                addNextDisplay("displayEndTurn");
                break;
            case 9:
                //TODO recheck after merge
                VCEvent vcEvent = new VCEvent(END_TURN);
                publish(vcEvent);
                break;
        }
    }

    public void displayMarketTray() {
        out.println(marketTrayDescription);
    }

    public void displayDevCardMatrix() {
        out.println(devCardMatrixDescription);
    }

    public void displayWarehouse() {
        out.println(userIDtoBoardDescriptions.get(client.getUserID()).getWarehouseDescription());
    }

    public void displayStrongbox() {
        out.println(userIDtoBoardDescriptions.get(client.getUserID()).getStrongboxDescription());
    }

    public void displayDevSlots() {
        out.println(userIDtoBoardDescriptions.get(client.getUserID()).getDevSlotsDescription());
    }

    public void displayFaithTrack() {
        out.println(userIDtoBoardDescriptions.get(client.getUserID()).getFaithTrackDescription());
    }

    public void displayLeaderCards() {
        out.println(userIDtoBoardDescriptions.get(client.getUserID()).getActiveLeaderCardsDescription());
        out.println(userIDtoBoardDescriptions.get(client.getUserID()).getInactiveLeaderCardsDescription());
    }

    public void displayEndTurn(){
        out.println("Ending turn...");
        VCEvent vcEvent = new VCEvent(END_TURN);
        publish(vcEvent);
    }

    public void displayBuyDevActionEnd() {
        out.println("Ending buy development card action...");
        VCEvent vcEvent = new VCEvent(BUY_DEVCARD_ACTION_ENDED);
        publish(vcEvent);
    }

    public void displayActivationProdActionEnd() {
        out.println("Ending activation production phase...");

        VCEvent vcEvent = new VCEvent(ACTIVATE_PROD_ACTION_ENDED);
        publish(vcEvent);
    }

    public void displayOtherPersonalBoards() {
        LinkedList<Integer> userIDs = new LinkedList<>();
        userIDs.addAll(userIDtoBoardDescriptions.keySet());
        userIDs.remove(client.getUserID());
        int index = 0;
        int numberOfOtherPlayers = userIDtoBoardDescriptions.keySet().size() - 1;
        out.println("Enter [1] for next board, [2] for previous board, [3] to choose action:");
        int input = InputConsumer.getANumberBetween(in, out, 1, 3);
        while (input == 1 || input == 2) {
            if (input == 1) index = (index + 1) % numberOfOtherPlayers;
            if (input == 2) index = (index - 1 + numberOfOtherPlayers) % numberOfOtherPlayers;
            Integer userIDtoDisplay = userIDs.get(index);
            String usernameToDisplay = userIDtoUsernames.get(userIDtoDisplay);
            PersonalBoardDescription boardToDisplay = userIDtoBoardDescriptions.get(userIDtoDisplay);
            out.println(usernameToDisplay + "'s personal board:");
            out.println(boardToDisplay);
            out.println("Enter [1] for next board, [2] for previous board, [3] to choose action:");
            input = InputConsumer.getANumberBetween(in, out, 1, 3);
        }
    }

    /**
     * methods that handle the take resource action. It check the resources context based on the last step
     * (then next action that the player have to make )call the methods that:
     * -handle the draw from the market tray,
     * -handle the activation of the white converter leader card
     * -handle in which shelves the player put the resources
     */
    private void routeTakeResActionDisplay() {
        switch (takeResContext.getLastStep()) {
            case CHOOSE_ROW_COLUMN:
                addNextDisplay("chooseRowColumnNumber");
                break;
            case CHOOSE_LEADER_TO_CONVERT_WHITE:
                addNextDisplay("chooseWhiteConverters");
                break;
            case CHOOSE_SHELVES:
                addNextDisplay("chooseShelvesToPut");
                break;
        }
    }

    /**
     * methods that handle the draw from the market tray.
     * Ask to the player to choose a row or column and so that he can take the resources that correspond to that choice
     * then sent an event to the server so that , it take the resources from the market and change the market configuration
     * After the player fill the TAKE_RES_CONTEXT_FILLED it publish an VC(view to controller)
     * event that check and manage the transformation
     */
    public void chooseRowColumnNumber() {
        out.println(this.marketTrayDescription);
        String rowColumnNumber = InputConsumer.getMarketRowColumnIndex(in, out);
        char firstLetter = rowColumnNumber.charAt(0);
        int index = Integer.parseInt(String.valueOf(rowColumnNumber.charAt(2)));
        if (firstLetter == 'R') {
            takeResContext.chooseRow(true);
        } else if (firstLetter == 'C') {
            takeResContext.chooseRow(false);
        } else takeResContext.setErrorTrue();
        takeResContext.setIndex(index);
        takeResContext.setLastStep(ROW_COLUMN_CHOSEN);
        VCEvent vcEvent = new VCEvent(TAKE_RES_CONTEXT_FILLED, takeResContext);
        publish(vcEvent);
    }

    /**
     * methods that handle the activation of the white converter leader card
     * if the player draw white marbles ang has a white converter leader card active
     * he can convert that white marbles in some other resources based on the leader card ability.
     * After the player fill the takeResContext it publish an VC(view to controller)
     * event that check and manage the transformation
     */
    public void chooseWhiteConverters() {
        Resources.ResType firstResOption = takeResContext.getWhiteConverters().get(0).getAbility().getResType();
        Resources.ResType secondResOption = takeResContext.getWhiteConverters().get(1).getAbility().getResType();
        int whiteMarbles = takeResContext.getWhiteMarbleNumber();
        out.println("You have two active white marble converter leader cards, and received " + whiteMarbles + " white marble from market tray");
        out.println("You can convert the white marbles into [1]" + firstResOption.toString() + " or [2]" + secondResOption.toString());
        while (whiteMarbles > 0) {
            out.println("Enter the index of resource type into which you want to convert one white marble");
            int index = InputConsumer.getANumberBetween(in, out, 1, 2);
            if (index == 1) {
                takeResContext.addOneConvertedRes(firstResOption);
            } else
                takeResContext.addOneConvertedRes(secondResOption);
            whiteMarbles--;
            out.println("You now have " + whiteMarbles + " white marble to convert.");
        }
        takeResContext.setLastStep(RES_FROM_WHITE_ADDED_TO_CONTEXT);
        VCEvent vcEvent = new VCEvent(TAKE_RES_CONTEXT_FILLED, takeResContext);
        publish(vcEvent);
    }

    /**
     * methods that handle the phase where the player manage the resources and the shelves configuration,
     * the shelves manager
     */
    public void chooseShelvesToPut() {
        out.println("Your warehouse looks like:");
        displayWarehouse();
        Resources resources = takeResContext.getResources();
        String resourceString = " Nothing";
        if (resources != null)
            resourceString = resources.toString();

        out.println("You have " + resourceString + " that you can put to your warehouse.");
        out.println("Extra resources that you don't put will be discarded automatically");
        out.println("Select one of the options below:");
        out.println("[1] Clear shelf");
        out.println("[2] Swap shelves");
        out.println("[3] Select resource type and shelf to put that kind of resources");
        out.println("[4] End take resource action");
        int index = InputConsumer.getANumberBetween(in, out, 1, 4);
        if (index == 1) {
            out.println("Select a shelf that you want to remove all resources from:");
            Shelf.shelfPlace place = InputConsumer.getShelfPlace(in, out);
            takeResContext.setShelf(place);
            takeResContext.setLastStep(CLEAR_SHELF_CHOSEN);
        } else if (index == 2) {
            out.println("Select two shelves that you want to swap, extra resources will be discarded automatically:");
            Shelf.shelfPlace firstPlace = InputConsumer.getShelfPlace(in, out);
            Shelf.shelfPlace secondPlace = InputConsumer.getShelfPlace(in, out);
            if (firstPlace.equals(secondPlace)) {
                out.println("You cannot select the same shelf.");
                addNextDisplay("chooseShelvesToPut");
            }
            takeResContext.setShelves(firstPlace, secondPlace);
            takeResContext.setLastStep(SWAP_SHELVES_CHOSEN);
        } else if (index == 3) {
            List<Resources.ResType> resTypeList = new ArrayList<>();
            List<Resources.ResType> resType = null;
            if (resources != null)
                resType = resources.getResTypes();
            if (resType != null)
                resTypeList.addAll(resources.getResTypes());
            Map<Shelf.shelfPlace, Resources.ResType> shelfToResMap = new HashMap<>();
            for (Shelf.shelfPlace place : Shelf.shelfPlace.values()) {
                if (resTypeList.isEmpty())
                    continue;
                out.println("Do you want to add a resource into " + place.toString() + " shelf?");
                Boolean answer = InputConsumer.getYesOrNo(in, out);
                if (!answer) continue;
                if (resTypeList.isEmpty()) break;
                out.println("Which type of resource you want to put into " + place.toString() + " shelf?");
                Resources.ResType selectedType = InputConsumer.getATypeAmongSet(in, out, resTypeList);
                resTypeList.remove(selectedType);
                shelfToResMap.put(place, selectedType);
            }
            takeResContext.setShelftoResTypeMap(shelfToResMap);
            takeResContext.setLastStep(PUT_RESOURCES_CHOSEN);
        } else {
            takeResContext.addDiscardedRes(takeResContext.getResources().sumOfValues());
            out.println("Ending take resource action...");
            VCEvent vcEvent = new VCEvent(TAKE_RES_ACTION_ENDED, takeResContext);
            publish(vcEvent);
            return;
        }
        VCEvent vcEvent = new VCEvent(TAKE_RES_CONTEXT_FILLED, takeResContext);
        publish(vcEvent);
    }

    /**
     * methods that handle the dev card buying. Based on the CV event and last step of BuyCardContext
     * that has been set
     * in the server side this methods call the action that correspond to that event.
     */
    private void routeBuyDevCardActionDisplay() {
        switch (buyDevCardContext.getLastStep()) {
            case CHOOSE_COLOR_LEVEL:
                addNextDisplay("chooseColorLevel");
                break;
            case EMPTY_DEVCARD_DECK_ERROR:
                setGeneralMsg("There is no available development card in that color and level.");
                addNextDisplay("displayGeneralMsg");
                addNextDisplay("chooseColorLevel");
                break;
            case NOT_ENOUGH_RES_FOR_DEVCARD_ERROR:
                setGeneralMsg("You don't have enough resources to buy that development card.");
                addNextDisplay("displayGeneralMsg");
                addNextDisplay("chooseColorLevel");
                break;
            case UNSUITABLE_FOR_DEVSLOTS_ERROR:
                setGeneralMsg("There are no suitable slots on your personal board for you to put the selected card on.");
                addNextDisplay("displayGeneralMsg");
                addNextDisplay("chooseColorLevel");
                break;
            case CHOOSE_DEV_SLOT:
                addNextDisplay("chooseDevSlotToPutDevCard");
                break;
            case CHOOSE_PAY_COST_FROM_WHERE:
                addNextDisplay("choosePayDevCardCostFromWhere");
                break;
            case NOT_ENOUGH_RES_IN_WAREHOUSE:
                setGeneralMsg("You selected more resources from warehouse than you can pay from there!");
                addNextDisplay("displayGeneralMsg");
                addNextDisplay("choosePayDevCardCostFromWhere");
                break;
            case NOT_ENOUGH_RES_IN_STRONGBOX:
                setGeneralMsg("You selected more resources from strongbox than you can pay from there!");
                addNextDisplay("displayGeneralMsg");
                addNextDisplay("choosePayDevCardCostFromWhere");
                break;
            case COST_PAID_DEVCARD_PUT:
                //general msg not needed
                out.println("Your development slots now looks like:");
                addNextDisplay("displayDevSlots");
                VCEvent vcEvent = new VCEvent(BUY_DEVCARD_ACTION_ENDED);
                publish(vcEvent);
                break;
        }
    }

    /**
     * methods that handle the choice of the color and level of the Development card.
     * After the player fill the buyDevCardContext context  it publish an VC(view to controller)
     * THen server check if the action it is correct based on the rule and the PLAYER personal board
     */
    public void chooseColorLevel() {
        String colorAndLevel = InputConsumer.getColorAndLevel(in, out);
        String[] parts = colorAndLevel.split("-");
        buyDevCardContext.setColor(DevCard.CardColor.valueOf(parts[0]));
        buyDevCardContext.setLevel(Integer.parseInt(parts[1]));
        buyDevCardContext.setLastStep(COLOR_LEVEL_CHOSEN);
        VCEvent vcEvent = new VCEvent(BUY_DEVCARD_CONTEXT_FILLED, buyDevCardContext);
        publish(vcEvent);
    }

    /**
     * methods that handle the choice of slot where to put put the Development Card.
     * After the player fill the buyDevCardContext context  it publish an VC(view to controller)
     * THen server check if the action it is correct based on the rule and the PLAYER personal board
     */
    public void chooseDevSlotToPutDevCard() {
        List<DevSlot.slotPlace> suitableSlots = buyDevCardContext.getSuitableSlots();
        out.println("Select which development slot you want to put the selected card on.");
        DevSlot.slotPlace place = InputConsumer.getSlotPlace(in, out, suitableSlots);
        buyDevCardContext.setSelectedSlot(place);
        buyDevCardContext.setLastStep(DEVSLOT_CHOSEN);
        VCEvent vcEvent = new VCEvent(BUY_DEVCARD_CONTEXT_FILLED, buyDevCardContext);
        publish(vcEvent);

    }

    /**
     * methods that handle the payment of dev cards. Only ask to the player from where paying for that card. where to put put the Development Card.
     * After the player fill the buyDevCardContext context  it publish an VC(view to controller)
     * THen server check if the payment action it is correct based on the rule and the PLAYER personal board event
     */
    public void choosePayDevCardCostFromWhere() {
        if(buyDevCardContext.isDiscountApplied()){
            out.println("Total discount of " + buyDevCardContext.getTotalDiscount().describeResource() + " applied!");
        }
        out.println("Select warehouse or strongbox to pay the cost of the selected development card.");
        Resources remainingCost = buyDevCardContext.getRemainingCost();
        Resources payFromWarehouse = new Resources();
        Resources payFromStrongbox = new Resources();
        List<Resources.ResType> resTypeList = remainingCost.getResTypes(); //store list type in order to prevent modification of reamining cost while iterating it
        for (Resources.ResType resType : resTypeList) {
            while (remainingCost.getNumberOfType(resType) > 0) {
                out.println("From where do you want to pay 1 " + resType.toString());
                boolean warehouseSelected = InputConsumer.getWorS(in, out);
                if (warehouseSelected) payFromWarehouse.add(resType, 1);
                else payFromStrongbox.add(resType, 1);
                remainingCost.subtract(resType, 1);
            }
        }
        buyDevCardContext.setPayFromWarehouse(payFromWarehouse);
        buyDevCardContext.setPayFromStrongbox(payFromStrongbox);
        buyDevCardContext.setLastStep(PAY_FROM_WHERE_CHOSEN);
        VCEvent vcEvent = new VCEvent(BUY_DEVCARD_CONTEXT_FILLED, buyDevCardContext);
        publish(vcEvent);
    }

    /**
     * methods that handle the Activate Prod Action. Based on the CV event and last step of activateProdContext
     * that has been set
     * in the server side this methods call the action that correspond to that event.
     */
    private void routeActivateProdActionDisplay() {
        switch (activateProdContext.getLastStep()) {
            case CHOOSE_DEV_SLOTS:
                // TODO maybe add pay from where for activate production
                addNextDisplay("chooseDevSlots");
                break;
            case NOT_ENOUGH_RES_FOR_PRODUCTION:
                setGeneralMsg("You don't have enough resources!");
                addNextDisplay("displayGeneralMsg");
                addNextDisplay("chooseDevSlots");
                break;
            case COST_PAID:
                checkFurtherAction(2);
                break;
        }
    }

    /**
     * methods that handle that fill the activateProdContext context.
     * The player chooses:
     * -the dev cards slots,
     * -choose if activate the leader card production a if the player owned a card with that ability choose resources get
     * -choose to activate the default Production and that means that he has to choose the LHS and RHS
     * After the player fill the activateProdContext it publish an VC(view to controller)
     * event so that the server can check if everything is ok
     */
    public void chooseDevSlots() {
        DevCard baseProd;
        Resources costLhsLeader = new Resources();
        int numberOfSlotAvailable = activateProdContext.getSlotAvailable().size(), j = 0;
        List<DevSlot> slotAvailable = activateProdContext.getSlotAvailable();
        List<DevSlot> slotChosen = InputConsumer.getDevSlotIndexs(in, out, numberOfSlotAvailable, slotAvailable);
        int numberOfActiveProduceLeaderCard = 0;

        if (activateProdContext.getNumberOfActiveLeaderProduction() == 0) {
            while (j < activateLeaderContext.getActiveLeaderCard().size()) {
                if (activateLeaderContext.getActiveLeaderCard().get(j).getAbility().getAbilityType() == SpecialAbility.AbilityType.ADDPROD) {
                    costLhsLeader.add(activateLeaderContext.getActiveLeaderCard().get(j).getAbility().getResType(), 1);
                    numberOfActiveProduceLeaderCard++;
                }
                j++;
            }
            activateProdContext.setLhlLeaderCard(costLhsLeader);
        }
        if (activateProdContext.getNumberOfActiveLeaderProduction() > 0) {
            out.println("Do want to use LeaderCard Production ability ? ");
            boolean leaderActivate = InputConsumer.getYesOrNo(in, out);
            if (leaderActivate) {
                activateProdContext.setActivationLeaderCardProduction(true);
                activateProdContext.setNumberOfActiveLeaderProduction(numberOfActiveProduceLeaderCard);
                chooseLeaderProdAction(numberOfActiveProduceLeaderCard);
            }
        }
        out.println("Do want to activate base production power ? ");
        boolean answer = InputConsumer.getYesOrNo(in, out);
        if (answer) {
            baseProd = InputConsumer.chooseBaseProdRes(in, out);
            activateProdContext.setBaseProdPower(true);
            activateProdContext.setBaseProductionCard(baseProd);
        }
        activateProdContext.setSlots(slotChosen);
            activateProdContext.setLastStep(DEV_SLOTS_CHOSEN);
        VCEvent vcEvent = new VCEvent(ACTIVATE_PROD_CONTEXT_FILLED, activateProdContext);
        publish(vcEvent);
    }
    /**
     * methods that handle the payment of production. Ask to the player from where paying for the production.
     * After the player fill the activateProdContext context  it publish an VC(view to controller)
     * THen server check if the payment action it is correct based on the PLAYER personal board
     * */
    // TODO actually it is not more needed
    /*vcEvent = new VCEvent(ACTIVATE_LEADER_CONTEXT_SELECTED);
    publish(vcEvent);*/
    /*public void choosePayProductionCostFromWhere(){
        if(activateProdContext.getNumberOfActiveLeaderProduction()>0)
        {
                out.println("Select warehouse or strongbox to pay the left side for leader production.");
                boolean warehouseSelected = InputConsumer.getWorS(in, out);
                activateProdContext.setFromWhereToPayForLeader(warehouseSelected);
            }
            if (activateProdContext.getBaseProdPower()) {
                out.println("Select warehouse or strongbox to pay the left side for default production.");
                boolean warehouseSelectedForDefault = InputConsumer.getWorS(in, out);
                activateProdContext.setFromWhereToPayForDefault(warehouseSelectedForDefault);
            }
            if (activateProdContext.getSelectedCard().size() > 0) {
                out.println("Select warehouse or strongbox to pay the left side of Development card for production.");
                boolean warehouseSelectedForDevslots = InputConsumer.getWorS(in, out);
                activateProdContext.setFromWhereToPayForDevslots(warehouseSelectedForDevslots);
            }
            activateProdContext.setLastStep(PAY_PRODUCTION_FROM_WHERE_CHOSEN);
            VCEvent vcEvent = new VCEvent(ACTIVATE_PROD_CONTEXT_FILLED, activateProdContext);
            publish(vcEvent);
    }*/

    /**
     * methods that handle the usage of leader cards with additional production. Ask to the player
     * the number of cards that he has to use and the res that he want to get from the production.
     * @param numberOfActiveProduceCard is the number of available card on the player board with that ability
     * */
    public void chooseLeaderProdAction ( int numberOfActiveProduceCard){

            Resources RHS = new Resources();
            out.println("You have " + numberOfActiveProduceCard + " active produce leader cards ");
            out.println("How many Leader card with produce ability do you want to use?  ");
            int numOfCard = InputConsumer.getANumberBetween(in, out, 1, numberOfActiveProduceCard);
            RHS.add(InputConsumer.chooseRhsLeaderCard(in, out, numOfCard));
            activateProdContext.setRhlLeaderCard(RHS);
    }

    /**
     * methods that handle the Leader Action based on the CV event and last step of activateLeaderContext that has been set
     * in the server side after the player chose this action in his turn.This methods call the action that correspond to that event.
     * */
    //handle ActivateLeaderAction
    private void routeActivateLeaderActionDisplay () {
        activateLeaderContext.setActivationLeaderCardBefore(true);
            switch (activateLeaderContext.getLastStep()) {
                case CHOOSE_ACTION:
                    addNextDisplay("chooseLeaderAction");
                    break;
            case END_LEADER_ACTION:
//todo add  a var that check if a normal action it is played so that we can terminate the player turn
                    break;
            }
        }

    /**
     * methods that handle the Leader Action. Only ask to the player which action between discard, activate or both and then based on the
     * choice call the method that handle the choice.If the player choice to activate both first call the discard methods then the activation.
     * */
    public void chooseLeaderAction () {

        out.println("What do you want to do with the leader cards?");
        out.println("[1]Discard\n[2]Activation\n[3]Both?");
        int numOfActionChoosen = InputConsumer.getANumberBetween(in, out, 1, 3);
        if(activateLeaderContext.getPlayerCard().size()>0)
            switch (numOfActionChoosen) {
                case 1:
                case 3:
                    chooseDiscardLeaderAction(numOfActionChoosen);
                    break;
                case 2:
                    chooseLeaderActivationAction();
                    break;
            }
    }

    /**
     * methods that handle the Discard Leader Action. Ask to the player which Cards want to discard, fill the activateLeaderContext.
     * After the player fill the  context  it publish an VC(view to controller)
     * THen server update the personal board based on the choice.
     * @param numOfActionChoosen  it is used to distinguish the discard action([1]) from the both action([3]).
     *                            if both call then activation methods
     * */
    public void chooseDiscardLeaderAction(int numOfActionChoosen ) {
        int j=0;
        Set<LeaderCard> discardedLeaderCard = new HashSet<>();
        out.println("Do want to Discard LeaderCard ? ");
        boolean discard = InputConsumer.getYesOrNo(in, out);

        if (discard && activateLeaderContext.getPlayerCard().size() > 0 ) {
            out.println("You can discard  " +activateLeaderContext.getPlayerCard().size()+ "  leader cards ");
            out.println("Your Leader Card:");
            while (j < activateLeaderContext.getPlayerCard().size()) {
                out.println("Do you want to discard this Leader Card:" + "[" + (j + 1) + "] :" + activateLeaderContext.getPlayerCard().get(j));
                if (InputConsumer.getYesOrNo(in, out))
                    discardedLeaderCard.add(activateLeaderContext.getPlayerCard().get(j));
                j++;
            }
        }
        activateLeaderContext.setDiscardedPlayerCard(discardedLeaderCard);
        activateLeaderContext.changePlayerCard(discardedLeaderCard);

        if(numOfActionChoosen==3 && discardedLeaderCard.size()<2){
            activateLeaderContext.setLastStep(BOTH_ACTIONS);
            chooseLeaderActivationAction();
        }else {
            activateLeaderContext.setLastStep(DISCARD_LEADER_CARD);
            VCEvent vcEvent = new VCEvent(ACTIVATE_PROD_CONTEXT_FILLED, activateProdContext);
            publish(vcEvent);
        }

    }
    /**
     * methods that handle the Activation Leader Action. Ask to the player which Cards want to Activate based on the card that has in his hands,
     * this methods used another one that check if the requirements are satisfied or not by the player ,
     * fill the activateLeaderContext with thee choice.
     * After the player fill the  context  it publish an VC(view to controller)
     * THen server update the personal board based on the choice.
     *
     * */
        public void chooseLeaderActivationAction () {
            int j = 0;
        Set<LeaderCard> activeLeaderCard = new HashSet<>();
        out.println("You can active  " + activateLeaderContext.getPlayerCard().size() + "  leader cards ");
        if (activateLeaderContext.getPlayerCard().size() > 0) {
                    out.println("Your Leader Card:");
                while (j < activateLeaderContext.getPlayerCard().size()) {
                        out.println("Do you want to activate this Leader Card:" + "[" + (j + 1) + "] :" + activateLeaderContext.getPlayerCard().get(j));
                        if (InputConsumer.getYesOrNo(in, out)) {
                            if (checkLeaderActivationAction(activateLeaderContext.getPlayerCard().get(j)))
                                activeLeaderCard.add(activateLeaderContext.getPlayerCard().get(j));
                            else
                                out.println("You don't satisfy the requirement:");
                        }
                        j++;
                    }
                    if (activeLeaderCard.size() > 0) {
                    activateLeaderContext.setLastStep(LEADER_CARD_ACTIVATED_CHOOSEN);
                    } else {
                    activateLeaderContext.setLastStep(LEADER_CARD_NOT_ACTIVATED_CHOOSEN);
                }
            }
            activateLeaderContext.setActiveLeaderCard(activeLeaderCard);
            VCEvent vcEvent = new VCEvent(ACTIVATE_PROD_CONTEXT_FILLED, activateProdContext);
            publish(vcEvent);
        }

    /**
     * methods that handle the check of the requirements of leader cards,
     * @param leaderToCheck is the card to check
     * */
        private boolean checkLeaderActivationAction (LeaderCard leaderToCheck){
        int discConvert =0;
        int firstCount = 0;
        int secondCount = 0;
            int i = 0;
        if (leaderToCheck.getAbility().getAbilityType() == SpecialAbility.AbilityType.ADDPROD) {
            while(i < activateLeaderContext.getOwnedCard().size()){
                if (leaderToCheck.getRequirement().getColor(0).equals(activateLeaderContext.getOwnedCard().get(i).getColor()) &&
                        activateLeaderContext.getOwnedCard().get(i).getLevel() == 2) {
                    return true;
                }i++;
            }
        }
        if (leaderToCheck.getAbility().getAbilityType() == SpecialAbility.AbilityType.EXTRASLOT) {
            Resources totalRes = new Resources();
            totalRes.add(activateLeaderContext.getTotalResources());
            if ((totalRes.getNumberOfType(leaderToCheck.getRequirement().getResource().getOnlyType()) == 5)) {
                return true;
            }
        }else {
            if (leaderToCheck.getAbility().getAbilityType() == SpecialAbility.AbilityType.CONVERTWHITE) {
                discConvert = 1;
            }else {
                discConvert = 2;
            }
           while (i < activateLeaderContext.getOwnedCard().size()) {
                if (leaderToCheck.getRequirement().getColor(0).equals(activateLeaderContext.getOwnedCard().get(i).getColor()) && activateLeaderContext.getOwnedCard().get(i).getLevel() == 1)
                    firstCount++;
                if (leaderToCheck.getRequirement().getColor(1).equals(activateLeaderContext.getOwnedCard().get(i).getColor()) && activateLeaderContext.getOwnedCard().get(i).getLevel() == 1)
                    secondCount++;

               if (firstCount >= 1 && secondCount >= 1 && discConvert == 1)
                   return true;

               if (firstCount >= 2 && secondCount >= 1 && discConvert == 2)
                   return true;

               i++;
           }

        }
        return false;
    }
    public void checkFurtherAction(int i) {

            out.println("Do you want to play leader action? ");
            boolean leaderAction = InputConsumer.getYesOrNo(in, out);
            if(leaderAction && !activateLeaderContext.getActivationLeaderCardBefore()) {
                VCEvent vcEvent = new VCEvent(ACTIVATE_LEADER_CONTEXT_SELECTED);
                publish(vcEvent);
            }else {
                setGeneralMsg("You selected more resources from warehouse than you can pay from there!");
                addNextDisplay("displayActivationProdActionEnd ");
            }



    }
    @Override
    public void update(Event event) {
        if (event instanceof CVEvent) {
            CVEvent cvEvent = (CVEvent) event;
            CVEvent.EventType eventType = cvEvent.getEventType();
            if (eventType.equals(SELECT_ALL_ACTION)) {
                majorActionDone = false;
                addNextDisplay("displayAllActionSelection");
            }
            else if (eventType.equals(TAKE_RES_FILL_CONTEXT)){
                takeResContext = (TakeResActionContext) cvEvent.getEventPayload(TakeResActionContext.class);
                routeTakeResActionDisplay();
            } else if (eventType.equals(BUY_DEVCARD_FILL_CONTEXT)) {
                buyDevCardContext = (BuyDevCardActionContext) cvEvent.getEventPayload(BuyDevCardActionContext.class);
                routeBuyDevCardActionDisplay();
            } else if (eventType.equals(ACTIVATE_PROD_FILL_CONTEXT)) {
                activateProdContext = (ActivateProdActionContext) cvEvent.getEventPayload(ActivateProdActionContext.class);
                routeActivateProdActionDisplay();
            }else if (eventType.equals(ACTIVATE_LEADER_FILL_CONTEXT)) {
                activateLeaderContext = (LeaderActionContext) cvEvent.getEventPayload(LeaderActionContext.class);
                routeActivateLeaderActionDisplay();
            } else if (eventType.equals(SELECT_MINOR_ACTION)){
                majorActionDone = true;
                addNextDisplay("displayMinorActionSelection");
            } else {
                initialCVevent = cvEvent;
                routeInitialActionsDisplay();
            }
        } else if (event instanceof MVEvent) {
            MVEvent mvEvent = (MVEvent) event;
            Integer userIDofUpdatedBoard = mvEvent.getUserID();
            switch (mvEvent.getEventType()) {
                case MARKET_TRAY_UPDATE:
                    MarketTray marketTray = (MarketTray) mvEvent.getEventPayload(MarketTray.class);
                    marketTrayDescription = marketTray.describeMarketTray();
                    break;
                case DEVCARD_MATRIX_UPDATE:
                    Type devCardListType = new TypeToken<List<DevCard>>() {}.getType();
                    List<DevCard> topDevCards = (List<DevCard>) mvEvent.getEventPayload(devCardListType);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i<12; i++){
                        sb.append(i+1 + ") " + topDevCards.get(i).describeDevCard());
                        sb.append("\n");
                    }
                    devCardMatrixDescription = sb.toString();
                    break;
                case WAREHOUSE_UPDATE:
                    Type shelfListType = new TypeToken<List<Shelf>>() {}.getType();
                    List<Shelf> shelves = (List<Shelf>) mvEvent.getEventPayload(shelfListType);
                    StringBuilder sb1 = new StringBuilder();
                    for (int i = 0; i<3; i++){
                        sb1.append(shelves.get(i).describeShelfFancy());
                    }
                    userIDtoBoardDescriptions.get(userIDofUpdatedBoard).setWarehouseDescription(sb1.toString());
                    break;
                case STRONGBOX_UPDATE:
                    Resources strongboxRes = (Resources) mvEvent.getEventPayload(Resources.class);
                    String strongboxDescription = strongBoxPrinter(strongboxRes);
                    userIDtoBoardDescriptions.get(userIDofUpdatedBoard).setStrongboxDescription(strongboxDescription);
                    break;
                case DEVSLOTS_UPDATE:
                    Type devSlotListType = new TypeToken<List<DevSlot>>() {}.getType();
                    List<DevSlot> devSlots = (List<DevSlot>) mvEvent.getEventPayload(devSlotListType);
                    StringBuilder sb2 = new StringBuilder();
                    for (int i = 0; i<3; i++){
                        sb2.append(devSlots.get(i).describeDevSlot() + "\n");
                    }
                    userIDtoBoardDescriptions.get(userIDofUpdatedBoard).setDevSlotsDescription(sb2.toString());
                    break;
                case FAITHPOINT_UPDATE:
                    Integer faithpoints = (Integer) mvEvent.getEventPayload(Integer.class);
                    userIDtoBoardDescriptions.get(userIDofUpdatedBoard).setFaithPoints(faithpoints);
                    Map<PersonalBoard.PopeArea, Boolean> tileMap = userIDtoBoardDescriptions.get(userIDofUpdatedBoard).getTileMap();
                    String faithTrackDescription = faithTrackPrinter(tileMap, faithpoints);
                    userIDtoBoardDescriptions.get(userIDofUpdatedBoard).setFaithTrackDescription(faithTrackDescription);
                    break;
                case VATICAN_REPORT_TAKEN:
                    Type mapType = new TypeToken<Map<PersonalBoard.PopeArea, Boolean>>() {}.getType();
                    Map<PersonalBoard.PopeArea, Boolean> tileMapTwo = (Map<PersonalBoard.PopeArea, Boolean>) mvEvent.getEventPayload(mapType);
                    int faithPointsTwo = userIDtoBoardDescriptions.get(userIDofUpdatedBoard).getFaithPoints();
                    String faithTrackDescriptionTwo = faithTrackPrinter(tileMapTwo, faithPointsTwo);
                    userIDtoBoardDescriptions.get(userIDofUpdatedBoard).setFaithTrackDescription(faithTrackDescriptionTwo);
                    break;
                    //TODO OMER I am going to write code for leader card update because it is needed for checking if discount works
                case ACTIVE_LEADER_CARD_UPDATE:
                    Type activeLeaderListType = new TypeToken<List<LeaderCard>>() {}.getType();
                    List<LeaderCard> activeLeaderList = (List<LeaderCard>) mvEvent.getEventPayload(activeLeaderListType);
                    String activeLeaderDescription = leaderListPrinter(activeLeaderList, true);
                    userIDtoBoardDescriptions.get(userIDofUpdatedBoard).setActiveLeaderCardsDescription(activeLeaderDescription);
                    break;
                case INACTIVE_LEADER_CARD_UPDATE:
                    Type inactiveLeaderListType = new TypeToken<List<LeaderCard>>() {}.getType();
                    List<LeaderCard> inactiveLeaderList = (List<LeaderCard>) mvEvent.getEventPayload(inactiveLeaderListType);
                    String inactiveLeaderDescription = leaderListPrinter(inactiveLeaderList, false);
                    userIDtoBoardDescriptions.get(userIDofUpdatedBoard).setInactiveLeaderCardsDescription(inactiveLeaderDescription);
                    break;
            }
        } else {
            out.println("Unidentified MV or CV event");
        }
    }

    private static String leaderListPrinter(List<LeaderCard> list, boolean active){
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            if (active) sb.append("You don't have active leader cards...");
            else sb.append("You don't have inactive leader cards...");
        } else {
            if (active) sb.append("Active Leader Cards:");
            else sb.append("Inactive Leader Cards:");
            for(LeaderCard card: list){
                sb.append("\n" + card.describeLeaderCard());
            }
        }
        return sb.toString();
    }

    private static String faithTrackPrinter(Map<PersonalBoard.PopeArea, Boolean> map, int faithPoints){
        StringBuilder sb = new StringBuilder();
        String resetAnsi = "\u001B[0m";
        String redAnsi = "\u001B[35m";
        String greyAnsi = "\033[1;37m";
        String crossAnsi = "\u271e";
        sb.append(faithTrackDrawVP());
        sb.append("\n");
        sb.append(faithTrackDrawTopPart());
        for(int i=0; i<10; i++){
            sb.append("\u2502");
            if (i == faithPoints)
                sb.append(" " + redAnsi + " " + crossAnsi + resetAnsi + " ");
            else
                sb.append(" " + greyAnsi + " " + i + resetAnsi + " ");
            sb.append("\u2502");
        }
        for(int i=10; i<25; i++){
            sb.append("\u2502");
            if (i == faithPoints)
                sb.append(" " + redAnsi + " " + crossAnsi + resetAnsi + " ");
            else
                sb.append(" " + greyAnsi  + i + resetAnsi + " ");
            sb.append("\u2502");
        }
        sb.append("\n");
        sb.append(faithTrackDrawBottomPart());
        sb.append("\n");
        sb.append(faithTrackDrawVaticanReport(map));
        return sb.toString();
    }

    private static String faithTrackDrawVP(){
        StringBuilder sb = new StringBuilder();
        String empty = " ";
        for(int i=0; i<20; i++){
            sb.append(empty);
        }
        sb.append("1VP");
        for(int i=0; i<15; i++){
            sb.append(empty);
        }
        sb.append("2VP");
        for(int i=0; i<15; i++){
            sb.append(empty);
        }
        sb.append("4VP");
        for(int i=0; i<15; i++){
            sb.append(empty);
        }
        sb.append("6VP");
        for(int i=0; i<15; i++){
            sb.append(empty);
        }
        sb.append("9VP");
        for(int i=0; i<14; i++){
            sb.append(empty);
        }
        sb.append("12VP");
        for(int i=0; i<14; i++){
            sb.append(empty);
        }
        sb.append("16VP");
        for(int i=0; i<14; i++){
            sb.append(empty);
        }
        sb.append("20VP");
        return sb.toString();
    }

    private static String faithTrackDrawTopPart(){
        StringBuilder sb = new StringBuilder();
        String yellowAnsi = "\u001B[33m";
        String redAnsi = "\u001B[35m";
        String whiteAnsi = "";
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(yellowAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(yellowAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(redAnsi));
        sb.append(faithTrackDrawOneTop(yellowAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(yellowAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(yellowAnsi));
        sb.append(faithTrackDrawOneTop(redAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(yellowAnsi)); //18
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(yellowAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(whiteAnsi));
        sb.append(faithTrackDrawOneTop(redAnsi));
        sb.append("\n");
        return sb.toString();
    }

    private static String faithTrackDrawOneTop(String colorCode){
        StringBuilder sb = new StringBuilder();
        String resetAnsi = "\u001B[0m";
        sb.append(colorCode + "\u2552\u2550\u2550\u2550\u2550\u2555" + resetAnsi);
        return sb.toString();
    }

    private static String faithTrackDrawBottomPart(){
        StringBuilder sb = new StringBuilder();
        String yellowAnsi = "\u001B[33m";
        String redAnsi = "\u001B[35m";
        String whiteAnsi = "";
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(yellowAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(yellowAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(redAnsi));
        sb.append(faithTrackDrawOneBottom(yellowAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(yellowAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(yellowAnsi));
        sb.append(faithTrackDrawOneBottom(redAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(yellowAnsi)); //18
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(yellowAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(whiteAnsi));
        sb.append(faithTrackDrawOneBottom(redAnsi));
        return sb.toString();
    }

    private static String faithTrackDrawOneBottom(String colorCode){
        StringBuilder sb = new StringBuilder();
        String resetAnsi = "\u001B[0m";
        sb.append(colorCode + "\u2558\u2550\u2550\u2550\u2550\u255b" + resetAnsi);
        return sb.toString();
    }

    private static String faithTrackDrawVaticanReport(Map<PersonalBoard.PopeArea, Boolean> map){
        StringBuilder sb = new StringBuilder();
        String empty = " ";
        String opening = "\u250d";
        String straight = "\u2501";
        String closing = "\u2511";
        String redAnsi = "\u001B[35m";
        String resetAnsi = "\u001B[0m";
        String tick = "\u2714";
        String cross = "\u2718";
        for(int i=0; i<30; i++){
            sb.append(empty);
        }
        sb.append(redAnsi + opening);
        for(int i=0; i<22; i++){
            sb.append(straight);
        }
        sb.append(closing + resetAnsi);
        for(int i=0; i<18; i++){
            sb.append(empty);
        }
        sb.append(redAnsi + opening);
        for(int i=0; i<28; i++){
            sb.append(straight);
        }
        sb.append(closing + resetAnsi);
        for(int i=0; i<12; i++){
            sb.append(empty);
        }
        sb.append(redAnsi + opening);
        for(int i=0; i<34; i++){
            sb.append(straight);
        }
        sb.append(closing + resetAnsi);
        sb.append("\n");
        for(int i=0; i<39; i++){
            sb.append(empty);
        }
        sb.append("2VP ");
        if(map.get(PersonalBoard.PopeArea.FIRST))
            sb.append(tick);
        else
            sb.append(cross);
        for(int i=0; i<40; i++){
            sb.append(empty);
        }
        sb.append("3VP ");
        if(map.get(PersonalBoard.PopeArea.SECOND))
            sb.append(tick);
        else
            sb.append(cross);
        for(int i=0; i<40; i++){
            sb.append(empty);
        }
        sb.append("4VP ");
        if(map.get(PersonalBoard.PopeArea.THIRD))
            sb.append(tick);
        else
            sb.append(cross);

        return sb.toString();
    }

//    public static void main(String[] args){
//        Map<PersonalBoard.PopeArea, Boolean> map = new HashMap<>();
//        map.put(PersonalBoard.PopeArea.FIRST, true);
//        map.put(PersonalBoard.PopeArea.SECOND, false);
//        map.put(PersonalBoard.PopeArea.THIRD, false);
//        int faithpoints = 15;
//        System.out.println(faithTrackPrinter(map, faithpoints));
//    }

    private String  strongBoxPrinter(Resources res){
        Resources res1 = res.cloneThisType(Resources.ResType.COIN);
        Resources res2 = res.cloneThisType(Resources.ResType.STONE);
        Resources res3 = res.cloneThisType(Resources.ResType.SHIELD);
        Resources res4 = res.cloneThisType(Resources.ResType.SERVANT);
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for(int i = 0; i<13; i++){
            sb.append("\u2509");
        }
        sb.append("\n\u254f " + res1.describeResource() + "  " + res2.describeResource() + " \u254f\n");
        sb.append("\u254f " + res3.describeResource() + "  " + res4.describeResource() + " \u254f\n");
        sb.append("  ");
        for(int i = 0; i<13; i++){
            sb.append("\u2509");
        }
        return sb.toString();
    }

        @Override
        public void subscribe (Listener < VCEvent > listener) {
            listenerList.add(listener);
        }

        @Override
        public void unsubscribe (Listener < VCEvent > listener) {
            listenerList.remove(listener);
        }

        @Override
        public void publish (VCEvent event){
            for (Listener<VCEvent> listener : listenerList)
                listener.update(event);
        }

        @Override
        public synchronized void displayIdle () {
            try {
            this.wait(2000);
            } catch (InterruptedException e) {
            }
            String idleSymbols = "✞⨎⌬☺⌺";
            String backSpace = "\b";
            StringBuilder idleSymbolBar = new StringBuilder();
            int symbolIndex = 0;
            boolean appendtoRight = true;
            int lastBarSize = 0;
            out.print("Waiting for the other players... ");
            out.flush();

            while (!shouldStopDisplayIdle()) {
                out.print(idleSymbolBar);
                out.flush();
                lastBarSize = idleSymbolBar.length();

                try {
                    this.wait(400);
                } catch (InterruptedException e) {
                }
                if (appendtoRight) {
                    idleSymbolBar.append(idleSymbols.charAt(symbolIndex));
                    if (idleSymbolBar.length() == 6) {
                        appendtoRight = false;
                        symbolIndex = (symbolIndex + 1) % idleSymbols.length();
                    }
                } else {
                    if (idleSymbolBar.length() > 0)
                        idleSymbolBar.deleteCharAt(idleSymbolBar.length() - 1);
                    else {
                        appendtoRight = true;
                        idleSymbolBar.append(idleSymbols.charAt(symbolIndex));
                    }
                }
                for (int i = 0; i < lastBarSize; i++) {
                    out.print(backSpace);
                }
            }

            stopIdle = false;
            for (int i = 0; i < lastBarSize + 15; i++)
                out.print(backSpace);
            out.flush();
        }

        @Override
        public synchronized boolean shouldStopDisplayIdle () {
            return stopIdle;
        }

        @Override
        public synchronized void stopDisplayIdle () {
            stopIdle = true;
            notifyAll();
        }

        @Override
        public synchronized void displayGeneralMsg () {
            out.println(generalmsg);
        }

        @Override
        public void setGeneralMsg (String msg){
            generalmsg = msg;
        }

        public void setUserIDtoUsernames (Map < Integer, String > userIDtoUsernames){
            this.userIDtoUsernames = userIDtoUsernames;
        }

        // METHODS THAT WON'T BE USED
        @Override
        public synchronized void displayLobby () {
            out.println("Waiting users in the lobby are:");
            for (String username : client.getUserIDtoUserNames().values())
                out.println(username);
        }

    }
    }
