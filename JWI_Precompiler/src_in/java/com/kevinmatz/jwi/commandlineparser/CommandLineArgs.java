package com.kevinmatz.jwi.commandlineparser;

import java.io.File;
import java.util.List;

import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

/**
 * Uses JewelCli
 */
public interface CommandLineArgs {

	@Option(/* shortName = "ojava", */ longName = "ojava", description = "Directory to store generated .java files")
	File getGeneratedJavaOutputDir();
	
	// @Option(shortName = "oclass")
	// File getClassOutputDir();
	
	@Unparsed    // (description = "List of .jwi files to process")
	List<File> getJwiInputFiles();

}
