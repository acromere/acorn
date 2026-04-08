package com.acromere.acorn;

import com.acromere.product.Rb;
import com.acromere.xenon.RbKey;
import com.acromere.xenon.Xenon;
import com.acromere.xenon.task.TaskEvent;
import com.acromere.zerra.javafx.Fx;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import lombok.CustomLog;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CustomLog
public class AcornTest extends HBox {

	private final AcornTool tool;

	private final String title;

	private final int threads;

	@Getter
	private final Button button;

	private final ProgressIndicator progress;

	//private final Label result;

	private final Label message;

	private AcornScore allScore;

	private AcornScore oneScore;

	public AcornTest( AcornTool tool, String title, int threads ) {
		this.tool = tool;
		this.title = title;
		this.threads = threads;

		getStyleClass().addAll( "layout" );

		String waitingText = Rb.text( "message", "waiting-to-start" );

		Label titleLabel = new Label( title );
		titleLabel.getStyleClass().addAll( "icon" );
//		result = new Label( "----" );
//		result.getStyleClass().addAll( "result" );
		button = new Button();
		//button.getStyleClass().addAll( "button" );
		progress = new ProgressBar( 0 );
		progress.getStyleClass().addAll( "progress" );
		message = new Label( waitingText );
		message.getStyleClass().addAll( "message" );

		button.setOnAction( e -> toggle() );
		updateButtonState();

		getChildren().addAll( button, titleLabel, progress );
	}

	private Xenon getProgram() {
		return tool.getProgram();
	}

	private AcornTask allChecker;

	private AcornTask oneChecker;

	private boolean isRunning() {
		return !(allChecker == null || allChecker.isDone()) & !(oneChecker == null || oneChecker.isDone());
	}

	private void toggle() {
		if( isRunning() ) {
			if( allChecker != null ) allChecker.cancel( true );
			if( oneChecker != null ) oneChecker.cancel( true );
		} else {
			start();
		}
	}

	private void updateButtonState() {
		String startText = Rb.text( RbKey.LABEL, "start" );
		String cancelText = Rb.text( RbKey.LABEL, "cancel" );
		Node pauseIcon = getProgram().getIconLibrary().getIcon( "pause" );
		Node playIcon = getProgram().getIconLibrary().getIcon( "play" );
		Fx.run( () -> {
			button.setGraphic( isRunning() ? pauseIcon : playIcon );
			//button.setText( isRunning() ? cancelText : startText );
		} );
	}

	private void start() {
		allChecker = new AcornTask( threads );
		oneChecker = new AcornTask( 1 );

		allChecker.register( TaskEvent.SUBMITTED, e -> {
			Fx.run( () -> progress.setProgress( 0 ) );
			updateButtonState();
		} );

		allChecker.register( TaskEvent.PROGRESS, e -> Fx.run( () -> progress.setProgress( 0.5 * e.getTask().getPercent() ) ) );
		oneChecker.register( TaskEvent.PROGRESS, e -> Fx.run( () -> progress.setProgress( 0.5 + 0.5 * e.getTask().getPercent() ) ) );

		allChecker.register( TaskEvent.SUCCESS, e -> Fx.run( () -> {
			try {
				setAllScore( allChecker.get() );
			} catch( InterruptedException | ExecutionException exception ) {
				log.atWarning().withCause( exception ).log( "Error computing acorn count" );
			}
		} ) );
		oneChecker.register( TaskEvent.SUCCESS, e -> Fx.run( () -> {
			try {
				setOneScore( oneChecker.get() );
			} catch( InterruptedException | ExecutionException exception ) {
				log.atWarning().withCause( exception ).log( "Error computing acorn count" );
			}
		} ) );

		allChecker.register( TaskEvent.CANCEL, e -> Fx.run( () -> progress.setProgress( 0 ) ) );
		oneChecker.register( TaskEvent.CANCEL, e -> Fx.run( () -> progress.setProgress( 0 ) ) );

		oneChecker.register( TaskEvent.FINISH, e -> updateButtonState() );

		CompletableFuture<Void> task = CompletableFuture.runAsync( allChecker, getProgram().getTaskManager().getExecutor() );
		task.thenRunAsync( oneChecker, getProgram().getTaskManager().getExecutor() );
	}

	private void setAllScore( long score ) {
		Fx.run( () -> {
			tool.getAllScoreGraph().removeScore( this.allScore );
			//result.setText( String.valueOf( score ) );
			tool.getAllScoreGraph().addScore( this.allScore = new AcornScore( true, score, threads, "This computer" ) );
		} );
	}

	private void setOneScore( long score ) {
		Fx.run( () -> {
			tool.getOneScoreGraph().removeScore( this.oneScore );
			//result.setText( String.valueOf( score ) );
			tool.getOneScoreGraph().addScore( this.oneScore = new AcornScore( true, score, 1, "This computer" ) );
		} );
	}

}
