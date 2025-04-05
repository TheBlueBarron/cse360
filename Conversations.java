package application;

public class Conversations {
	private int id;
	private String participent_1_id;
	private String participent_2_id;


public Conversations(int id, String participent_1_id, String participent_2_id) {
	this.id = id;
	this.participent_1_id = participent_1_id;
	this.participent_2_id = participent_2_id;
}

public int getId() {
	return id;
}

public String getParticipent_1_id() {
	return participent_1_id;
}

public String getParticipent_2_id() {
	return participent_2_id;
}

public void setId(int id) {
	this.id = id;
}

public void setParticipent_1_id(String participent_1_id) {
	this.participent_1_id=participent_1_id;
}

public void setParticipent_2_id(String participent_2_id) {
	this.participent_2_id = participent_2_id;
}
@Override
public String toString() {
	return "conversation[id="+id+", participent_1_id="+participent_1_id+ ", participent_2_id="+ participent_1_id;
}

}