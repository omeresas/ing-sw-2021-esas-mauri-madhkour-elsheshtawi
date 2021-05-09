package it.polimi.ingsw.utility.messages;

import java.io.Serializable;

public class Message implements Serializable {
    private Integer userID;
    private MsgType msgtype;
    private String jsonContent;

    public Message(Integer userID, MsgType msgtype, String jsonContent) {
        this.userID = userID;
        this.msgtype = msgtype;
        this.jsonContent = jsonContent;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID){
        this.userID = userID;
    }

    public MsgType getMsgtype() {
        return msgtype;
    }

    public String getJsonContent() {
        return jsonContent;
    }
}
