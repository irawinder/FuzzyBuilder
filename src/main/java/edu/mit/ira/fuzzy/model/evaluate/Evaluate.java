package edu.mit.ira.fuzzy.model.evaluate;

import java.util.HashMap;

import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.model.Polygon;
import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.model.Voxel;
import edu.mit.ira.fuzzy.model.VoxelArray;
import edu.mit.ira.opensui.objective.MultiObjective;
import edu.mit.ira.opensui.objective.Objective;

/**
 * Utility class for generating the MultiObjective performance of a Development
 * 
 * @author Ira Winder
 *
 */
public class Evaluate {

	/**
	 * Calculate the Multi-Objective Performance Metrics for a Development Model
	 * @param fuzzy
	 * @return a list of primary and secondary performance metrics
	 */
	public static MultiObjective development(Development fuzzy) {
		MultiObjective performance = new MultiObjective();
		
		// Initialize global counters
		float siteArea = 0;
		//float coverArea = 0;
		float builtArea = 0;
		float undergroundArea = 0;
		float sitePeak = 0;
		HashMap<Function, Float> useArea = new HashMap<Function, Float>();
		for (Function use : Function.values()) {
			useArea.put(use, 0f);
		}
		
		// Calculate Secondary Objectives
		if (fuzzy != null) {
			for (Polygon plot : fuzzy.plotSite.keySet()) {
				
				// Get Voxels for this Plot
				VoxelArray pSite = fuzzy.plotSite.get(plot);
				VoxelArray pMassing = fuzzy.plotMassing.get(plot);
				
				// Calculate total areas for this plot
				float plotSiteArea, plotBuiltArea, plotUndergroundArea, plotPeak;
				//float plotCoverArea, plotFAR, plotCoverageRatio;
				float voxelArea = 0;
				if (pSite.voxelList.size() > 0) {
					voxelArea = (float) Math.pow(pSite.voxelList.get(0).width, 2);
					plotSiteArea = voxelArea * pSite.voxelList.size();
					plotBuiltArea = voxelArea * pMassing.voxelList.size();
					plotUndergroundArea = voxelArea * pMassing.belowGroundVoxelCount();
					//float adjustedPlotBuiltArea = Math.max(0f, plotBuiltArea - plotUndergroundArea); // Exlude below-ground use from FAR Calc
					//plotFAR = adjustedPlotBuiltArea / plotSiteArea;
					plotPeak = pMassing.peakZ();
					
					// Total ground level build area
					//plotCoverArea = 0;
					//for(Voxel t : pMassing.voxelList) {
					//	if (t.w == 0) {
					//		plotCoverArea += voxelArea;
					//	}
					//}
					//plotCoverageRatio = plotCoverArea / plotSiteArea;
				} else {
					plotSiteArea = 0;
					//plotCoverArea = 0;
					plotBuiltArea = 0;
					plotUndergroundArea = 0;
					//plotFAR = 0;
					//plotCoverageRatio = 0;
					plotPeak = 0;
				}
				siteArea += plotSiteArea;
				//coverArea += plotCoverArea;
				builtArea += plotBuiltArea;
				undergroundArea += plotUndergroundArea;
				sitePeak = Math.max(sitePeak, plotPeak);
				
				/*
				// Add total area objectives for this plot
				String plotName = "[" + fuzzy.plotNames.get(plot) + "] ";
				performance.secondaryObjectives.add(new Objective(
					plotName + "Site Area", "Total area enclosed by " + plotName, plotSiteArea, "sqft"));
				performance.secondaryObjectives.add(new Objective(
					plotName + "Built Area", "Total floor area of massing on " + plotName, plotBuiltArea, "sqft"));
				performance.secondaryObjectives.add(new Objective(
					plotName + "Floor Area Ratio", "Ratio of built area to site area on " + plotName, plotFAR, "sqft/sqft"));
				performance.secondaryObjectives.add(new Objective(
					plotName + "Coverage Ratio", "Ratio of plot that is occupied by building on " + plotName, plotCoverageRatio, "sqft/sqft"));
				performance.secondaryObjectives.add(new Objective(
					plotName + "Plot Peak", "The tallest building height on " + plotName, plotPeak, "ft"));
				
				*/
				// Calculate plot areas itemized by Use
				HashMap<Function, Integer> plotUseCount = new HashMap<Function, Integer>();
				for (Function use : Function.values()) {
					plotUseCount.put(use, 0);
				}
				for (Voxel t : pMassing.voxelList) {
					int count = plotUseCount.get(t.type);
					plotUseCount.put(t.type, count + 1);
				}
				for (Function use : Function.values()) {
					//String useName = "[" + use + "] ";
					float plotUseArea = voxelArea * plotUseCount.get(use);
					useArea.put(use, useArea.get(use) + plotUseArea);
					
					//float plotUseRatio;
					//if (plotBuiltArea == 0) {
					//	plotUseRatio = 0;
					//} else {
					//	plotUseRatio = plotUseArea / plotBuiltArea;
					//}
					
					// Add plot area objectives itemized by use
					//performance.secondaryObjectives.add(new Objective(
					//	plotName + useName + "Built Area", "Total floor area of " + use + " on " + plotName, plotUseArea, "sqft"));
					//performance.secondaryObjectives.add(new Objective(
					///	plotName + useName + "Area Ratio", "Portion of total built area on " + plotName, 100 * plotUseRatio, "%"));
				}
			}
		}
		
		// Calculate Primary Objectives
		float adjustedBuiltArea = Math.max(0f, builtArea - undergroundArea); // Exclude below-ground use from FAR Calc
		float far = adjustedBuiltArea / siteArea;
		//float coverage = coverArea / siteArea;

		// Add total area objectives
		performance.primaryObjectives.add(new Objective(
				"Floor Area Ratio (FAR)", "Ratio of Gross Above-ground Floor Area to Gross Land Area", far, ""));
		performance.primaryObjectives.add(new Objective(
			"Gross Land Area (GLA)", "Total area of land defined by all parcels", siteArea, "sqft"));
		performance.primaryObjectives.add(new Objective(
			"Gross Floor Area (GFA)", "Total floor area of all buildings", builtArea, "sqft"));
		//performance.primaryObjectives.add(new Objective(
		//	"Building Coverage Ratio (BCR)", "Ratio of Gross Building Footprint Area (when viewed from above) to Gross Land Area", coverage, "sqft/sqft"));
		//performance.primaryObjectives.add(new Objective(
		//	"Site Peak", "The tallest building height on the Site", sitePeak, "ft"));
		
		// Calculate total areas itemized by use
		for (Function use : Function.values()) {
			String useName = use.toString();
			float uArea = useArea.get(use);
			
			//float useRatio;
			//if (builtArea == 0) {
			//	useRatio = 0;
			//} else {
			//	useRatio = uArea / builtArea;
			//}
			
			
			// Add total area objectives itemized by use
			performance.primaryObjectives.add(new Objective(
				"Floor Area [" + useName + "]", "Total floor area of " + use + " use on site", uArea, "sqft"));
			//performance.primaryObjectives.add(new Objective(
			//	"Area Ratio [" + useName + "]", "Portion of total " + use + " use floor area on site", 100 * useRatio, "%"));
		}
		
		return performance;
	}
}