package application;

public class Message {
	private int id;
	private int conversation_id;
	private String sender_id;
	private String text;
public Message(int id, int conversation_id, String sender_id, String text) {
	if (!isValidMessageText(text)) {
        throw new IllegalArgumentException("Message text cannot be empty.");
    }this.id = id;
	this.conversation_id =conversation_id;
	this.sender_id = sender_id;
	this.text = text;
}
public static boolean isValidMessageText(String text) {
    return text != null && !text.trim().isEmpty();
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public int getConversationId() {
	return conversation_id;
}

public void setConversationId(int conversation_id) {
	this.conversation_id = conversation_id;
}

public String getSenderId() {
	return sender_id;
}

public void setSenderId(String sender_id) {
	this.sender_id = sender_id;
}
public String getText() {
	return text;
}
public void setText(String text) {   
	if (!isValidMessageText(text)) {
    throw new IllegalArgumentException("Question text cannot be empty.");
}
	this.text = text;
}
@Override
public String toString() {
	return "Message [id =" +id+", conversation_id="+conversation_id+", sender_id="+sender_id+", text="+text+"]";
}
}
