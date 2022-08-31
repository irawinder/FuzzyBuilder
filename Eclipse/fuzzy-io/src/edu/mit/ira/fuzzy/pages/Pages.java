package edu.mit.ira.fuzzy.pages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.mit.ira.fuzzy.FuzzyIO;

public class Pages {
	
	private static String PAGES_PATH = FuzzyIO.RELATIVE_DATA_PATH + File.separator + "pages";
	private static String HEAD_PATH = PAGES_PATH + File.separator + "components" + File.separator + "head.txt";
	
	public static String generalSite() {
		String head = makeHead();
		String body = makeGeneralBody();
		return assemblePage(head, body);
	}
	
	public static String nullSite() {
		String head = makeHead();
		String body = make404Body();
		return assemblePage(head, body);
	}
	
	private static String assemblePage(String head, String body) {
		return "<!DOCTYPE html><html>" + head + body + "</html>";
	}
	
	private static String makeHead() {
		try {
			return Files.readString(Path.of(HEAD_PATH));
		} catch (IOException e) {
			e.printStackTrace();
			return "<head></head>";
		}
	}
	
	private static String makeGeneralBody() {
		
		String body = "<body>";
		
		body += wrapText("h1", "FuzzyIO");
		body += wrapText("p", "FuzzyIO is a server for generating \"fuzzy\" resolution real estate development scenarios.");
		body += wrapText("p", "This is just the \"back end\". If you want to use fuzzy builder,  you need to use a \"front end\" user interface like <a href=\"http://opensui.org/\" target=\"_blank\">openSUI</a>.");

		body += wrapText("h2", "Model Components");
		body += wrapText("p", "A <b>Function</b> describes the principal activity within a volume of enclosed space. (e.g. \"Residential\" or \"Retail\")");
		body += wrapText("p", "A <b>Voxel</b> is a small, indivisible unit of enclosed space consisting of a square base, height, and single <b>Function</b>");
		
		body += wrapText("p", "A <b>Zone</b> is a grid-based array of <b>Voxels</b> with a common <b>Function</b>. If a <b>Zone</b> consists of multiple floors, all floors share the same footprint.");
		body += wrapText("p", "A <b>Volume</b> is a stack of <b>Zones</b> that all share a common footprint, but each zone may have a different <b>Function</b> and number of floors");
		body += wrapText("p", "A <b>Podium Volume</b> is a special <b>Volume</b> with a footprint defined by an off-set from a <b>Parcel</b>'s edges and subtracting any <b>Podium Exclusion Areas</b>");
		body += wrapText("p", "A <b>Podium Exclusion Area</b> is an area, defined by a polygon, where <b>Voxels</b> cannot be placed.");
		body += wrapText("p", "A <b>Tower Volume</b> is a special <b>Volume</b> with a rectangular footprint. It must be placed within a <b>Parcel</b>.");
		body += wrapText("p", "A <b>Parcel</b> is the set of flat, grid-based <b>Voxels</b> that fit within the boundaries of a defined polygon. Parcels cannot overlap each other.");
		body += wrapText("p", "A <b>Scenario</b> is a specific configuration of <b>Voxels</b> specified by <b>Parcels</b>, <b>Volumes</b>, and <b>Zones</b>.");

		body += wrapText("h2", "Resources");
		
		body += "<ul>";
		  body += wrapText("li", "Launch <a href=\"http://opensui.org/\" target=\"_blank\">openSUI</a> (to use FuzzyIO)");
		  body += wrapText("li", "<a href=\"https://youtu.be/cNoS-bhRPEw\" target=\"_blank\">FuzzyIO Tutorial</a> on Youtube");
		  body += wrapText("li", "<a href=\"https://youtu.be/z7514vh02u0\" target=\"_blank\">FuzzyIO Usage</a> (Timelapse) on Youtube");
		body += "</ul>";

		body += wrapText("h2", "Contact");
		body += wrapText("p", "fuzzy-io <i>[at]</i> mit <i>[dot]</i> edu");
		
		body += "</body>";
		
		return body;
	}

	private static String make404Body() {

		String body = "<body>";

		body += wrapText("h1", "404");
		body += wrapText("p", "Resource Not Found");

		body += "</body>";

		return body;
	}
	
	private static String wrapText(String method, String text) {
		return "<" + method + ">" + text + "</" + method + ">";
	}
}
