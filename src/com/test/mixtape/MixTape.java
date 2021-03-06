package com.test.mixtape;

public class MixTape {

	private static final String DIR_RESOURCES = "resources";

	public static void main(String[] args) {
		String srcFileName = "", changesFileName = "", outputFileName = "";
		if (args.length == 3) {
			srcFileName = args[0];
			changesFileName = args[1];
			outputFileName = args[2];
		}

		if (args.length != 3 || srcFileName.length() == 0 || changesFileName.length() == 0
				|| outputFileName.length() == 0) {
			System.out.println("Please provide valid inputs");
			System.out.println(
					"Usage: java -jar mixTapePlayList.jar <src-json-file> <changes-json-file> <output-json-file>");
			System.out.println("All required param files are under MixTapePlayList/resources/*");
			System.out.println("<src-json-file>: mixtape-data.json");
			System.out.println("<changes-json-file>: mixtape-changes.json");
			System.out.println("<output-json-file>: mixtape-output.json");
			System.exit(0);
		}

		MixTapeParser.readDataFromSrcJson(DIR_RESOURCES + "/" + srcFileName);
		MixTapeParser.readDataFromChangesJson(DIR_RESOURCES + "/" + changesFileName);
		MixTapeParser.generateOutputJson(DIR_RESOURCES + "/" + outputFileName);
	}

}
