package com.acromere.acorn;

import com.acromere.xenon.Module;
import com.acromere.xenon.ToolRegistration;
import lombok.CustomLog;

@CustomLog
public class AcornMod extends Module {

	public static final String STYLESHEET = "acorn.css";

	private AcornResourceType acornResourceType;

	public AcornMod() {
		super();
	}

	@Override
	public void startup() throws Exception {
		super.startup();
		registerIcon( "acorn", new AcornIcon() );

		getProgram().getResourceManager().addScheme( new AcornScheme( getProgram() ) );

		registerResourceType( acornResourceType = new AcornResourceType( this ) );
		ToolRegistration design2dEditorRegistration = new ToolRegistration( this, AcornTool.class );
		design2dEditorRegistration.setName( "Acorn Counting Tool" );
		registerTool( acornResourceType, design2dEditorRegistration );
	}

	@Override
	public void shutdown() throws Exception {
		unregisterTool( acornResourceType, AcornTool.class );
		unregisterResourceType( acornResourceType );

		getProgram().getResourceManager().removeScheme( AcornScheme.ID );

		unregisterIcon( getCard().getArtifact(), new AcornIcon() );
		super.shutdown();
	}

}
