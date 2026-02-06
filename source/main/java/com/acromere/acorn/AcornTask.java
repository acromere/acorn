package com.acromere.acorn;

import com.acromere.acorncli.AcornMonitor;
import com.acromere.acorncli.AcornCounter;
import com.acromere.xenon.task.Task;

public class AcornTask extends Task<Long> {

	private final AcornCounter counter;

	public AcornTask( int threads ) {
		counter = new AcornMonitor( threads );
		setTotal( counter.getTotal() );
		counter.addListener( this::setProgress );
	}

	@Override
	public Long call() throws Exception {
		counter.start();
		counter.join();
		return counter.getScore();
	}

}
