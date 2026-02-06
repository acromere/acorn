import com.acromere.acorn.AcornMod;
import com.acromere.xenon.Module;

module com.acromere.acorn {

	// Compile-time only
	requires static lombok;

	// Both compile-time and run-time
	requires com.acromere.acorncli;
	requires com.acromere.xenon;
	requires java.management;

	opens com.acromere.acorn.bundles;
	opens com.acromere.acorn.settings;

	exports com.acromere.acorn to com.acromere.xenon, com.acromere.zerra;

	provides Module with AcornMod;

}
