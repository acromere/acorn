package com.acromere.acorn;

import com.acromere.xenon.ProgramTool;
import com.acromere.xenon.XenonProgramProduct;
import com.acromere.xenon.resource.OpenAssetRequest;
import com.acromere.xenon.resource.Resource;
import com.acromere.xenon.workpane.ToolException;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.CustomLog;

import java.util.Timer;
import java.util.function.Consumer;

@CustomLog
public class AcornTool extends ProgramTool {

	private static final Timer timer = new Timer( true );

	private final Consumer<Double> cpuLoadListener;

	private SystemCpuLoadCheck cpuLoadCheck;

	private final ScoreGraph allScoreGraph;

	private final ScoreGraph oneScoreGraph;

	public AcornTool( XenonProgramProduct product, Resource resource ) {
		super( product, resource );
		addStylesheet( AcornMod.STYLESHEET );
		getStyleClass().addAll( "acorn-tool" );
		setIcon( "acorn" );

		cpuLoadListener = d -> log.atFine().log( "cpu=%s", d );

		int threads = Runtime.getRuntime().availableProcessors();

		AcornTest allThreadsTest = new AcornTest( this, "", threads );
		VBox testBox = new VBox( allThreadsTest );
		testBox.setPrefWidth( 700 );
		testBox.setMinWidth( 400 );

		allScoreGraph = new ScoreGraph(false);
		allScoreGraph.setPrefWidth( 300 );
		allScoreGraph.setMinWidth( 300 );

		oneScoreGraph = new ScoreGraph(true);
		oneScoreGraph.setPrefWidth( 300 );
		oneScoreGraph.setMinWidth( 300 );

		HBox graphs = new HBox( allScoreGraph, oneScoreGraph );
		HBox.setHgrow( allScoreGraph, Priority.ALWAYS );
		HBox.setHgrow( oneScoreGraph, Priority.ALWAYS );

		VBox parts = new VBox( testBox, graphs );
		VBox.setVgrow( graphs, Priority.ALWAYS );

		getChildren().add( parts );
	}

	@Override
	protected void allocate() throws ToolException {
		cpuLoadCheck = new SystemCpuLoadCheck();
		cpuLoadCheck.addListener( cpuLoadListener );
		timer.schedule( cpuLoadCheck, 0, 1000 );
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );
		setTitle( request.getResource().getName() );
	}

	@Override
	protected void deallocate() throws ToolException {
		cpuLoadCheck.cancel();
		cpuLoadCheck.removeListener( cpuLoadListener );
	}

	ScoreGraph getAllScoreGraph() {
		return allScoreGraph;
	}

	ScoreGraph getOneScoreGraph() {
		return oneScoreGraph;
	}

}
