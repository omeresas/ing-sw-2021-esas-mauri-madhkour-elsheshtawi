package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumclasses.CliColors;

public class DevCard {

    public static CliColors getFaith() {
        return CliColors.red;
    }
    public static CliColors getShield(){
        return CliColors.cyan;
    }
    public static CliColors getCoin(){
        return CliColors.gold;
    }
    public static CliColors getServant(){
        return CliColors.purple;
    }
    public static CliColors getStone(){
        return CliColors.grey;
    }
    public static CliColors getBlueCard(){
        return CliColors.blue;
    }
    public static CliColors getYellowcard(){
        return CliColors.yellow;
    }
    public static CliColors getGreenCard(){
        return CliColors.green;
    }
    public static CliColors getPurpleCard(){
        return CliColors.purpleBright;
    }


    private static final String[] devCardDetails={



    };

    public enum CardColor{
        GREEN,BLUE,YELLOW,PURPLE;

        public static boolean contains(String string) {
            for (CardColor color : CardColor.values()) {
                if (color.name().equals(string)) {
                    return true;
                }
            }
            return false;
        }
    }

    private final int level;
    private final CardColor color;
    private final Resources LHS;
    private final Resources RHS;
    private final Resources cost;
    private final int victoryPoints;

    public DevCard (int level, CardColor color, Resources LHS, Resources RHS, Resources cost, int victoryPoints) {
        this.color=color;
        this.level=level;
        this.LHS=LHS;
        this.RHS=RHS;
        this.cost=cost;
        this.victoryPoints=victoryPoints;
    }

    public int getLevel() {
        return this.level;
    }
    public int getVictoryPoints() {
        return this.victoryPoints;
    }
    public CardColor getColor() {
        return this.color;
    }
    public Resources getLHS() {
        return this.LHS;
    }
    public Resources getRHS() {
        return this.RHS;
    }
    public Resources getCost() {
        return this.cost;
    }

    @Override
    public String toString(){
        return "Color: " + color + " Level: " + level + " RHS: " + RHS + " LHS: " + LHS + " Cost: " + cost + " VP: " + victoryPoints;
    }







}
