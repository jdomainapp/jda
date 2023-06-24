package jda.modules.msacommon.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter
public class ChangeModel<ID>{
	private String type;
	private String action;
	ID id;
	private String path;
	private String correlationId;

	public ChangeModel() {
		
	}
}
