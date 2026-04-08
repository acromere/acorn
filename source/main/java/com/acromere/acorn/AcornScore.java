package com.acromere.acorn;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.Getter;

public class AcornScore extends Label {

	private final BooleanProperty left;

	@Getter
	private final long score;

	@Getter
	private final int threads;

	public AcornScore( boolean left, long score, int threads, String label ) {
		this( left, score, threads, label, null );
	}

	public AcornScore( boolean left, long score, int threads, String label, Node graphic ) {
		super( getText( left, score, threads, label ), graphic );
		this.score = score;
		this.threads = threads;
		this.left = new SimpleBooleanProperty( left );
	}

	public BooleanProperty leftProperty() {
		return left;
	}

	private static String getText( boolean left, long score, int threads, String label ) {
		String threadText = "";
		String threadLabel = "";
		if( threads > 0 ) threadText = threads == 1 ? "" : "[" + threads + "]";
		if( threads > 0 ) threadLabel = threads == 1 ? "1 Thread" : threads + " Threads";

		StringBuilder builder = new StringBuilder();
		if( left ) {
			if( threads > 0 ) {
				builder.append( threadLabel );
				builder.append( " - " );
			}
			builder.append( score );
		} else {
			builder.append( score );
			if( threads > 0 ) {
				builder.append( " - " );
				builder.append( label );
				builder.append( " " );
				builder.append( threadText );
			}
		}

		return builder.toString();
	}

}
