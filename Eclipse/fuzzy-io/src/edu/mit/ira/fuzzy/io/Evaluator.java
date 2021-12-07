package edu.mit.ira.fuzzy.io;

import java.util.HashMap;

import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.model.Polygon;
import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.model.Voxel;
import edu.mit.ira.fuzzy.model.VoxelArray;
import edu.mit.ira.fuzzy.objective.MultiObjective;
import edu.mit.ira.fuzzy.objective.Objective;

/**
 * Utility class for generating the MultiObjective performance of a Development
 * 
 * @author Ira Winder
 *
 */
public class Evaluator {

	/**
	 * Calculate the Multi-Objective Performance Metrics for a Development Model
	 * @param fuzzy
	 * @return a list of primary and secondary performance metrics
	 */
	public MultiObjective evaluate(Development fuzzy) {
		MultiObjective performance = new MultiObjective();
		
		// Initialize global counters
		float siteArea = 0;
		float builtArea = 0;
		HashMap<Function, Float> useArea = new HashMap<Function, Float>();
		for (Function use : Function.values()) {
			useArea.put(use, 0f);
		}
		
		// Calculate Secondary Objectives
		for (Polygon plot : fuzzy.plotSite.keySet()) {
			
			// Get Voxels for this Plot
			VoxelArray pSite = fuzzy.plotSite.get(plot);
			VoxelArray pMassing = fuzzy.plotMassing.get(plot);
			
			// Calculate total areas for this plot
			float plotSiteArea, plotBuiltArea, plotFAR;
			float voxelArea = 0;
			if (pSite.voxelList.size() > 0) {
				voxelArea = (float) Math.pow(pSite.voxelList.get(0).width, 2);
				plotSiteArea = voxelArea * pSite.voxelList.size();
				plotBuiltArea = voxelArea * pMassing.voxelList.size();
				plotFAR = plotBuiltArea / plotSiteArea;
			} else {
				plotSiteArea = 0;
				plotBuiltArea = 0;
				plotFAR = 0;
			}
			siteArea += plotSiteArea;
			builtArea += plotBuiltArea;
			
			// Add total area objectives for this plot
			String plotName = "[" + fuzzy.plotNames.get(plot) + "] ";
			performance.secondaryObjectives.add(new Objective(
				plotName + "Site Area", "total area enclosed by " + plotName, plotSiteArea, "sqft"));
			performance.secondaryObjectives.add(new Objective(
				plotName + "Built Area", "total floor area of massing on " + plotName, plotBuiltArea, "sqft"));
			performance.secondaryObjectives.add(new Objective(
				plotName + "Floor Area Ratio", "ratio of built area to site area on " + plotName, plotFAR, "sqft/sqft"));
			
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
				String useName = "[" + use + "] ";
				float plotUseArea = voxelArea * plotUseCount.get(use);
				useArea.put(use, useArea.get(use) + plotUseArea);
				float plotUseRatio;
				if (plotBuiltArea == 0) {
					plotUseRatio = 0;
				} else {
					plotUseRatio = plotUseArea / plotBuiltArea;
				}
				
				// Add plot area objectives itemized by use
				performance.secondaryObjectives.add(new Objective(
					plotName + useName + "Built Area", "total floor area of " + use + "on " + plotName, plotUseArea, "sqft"));
				performance.secondaryObjectives.add(new Objective(
					plotName + useName + "Area Ratio", "portion of total built area on " + plotName, 100 * plotUseRatio, "%"));
			}
		}
		
		// Calculate Primary Objectives
		float far = builtArea / siteArea;

		// Add total area objectives
		performance.primaryObjectives.add(new Objective(
			"Total Site Area", "total area enclosed by site", siteArea, "sqft"));
		performance.primaryObjectives.add(new Objective(
			"Total Built Area", "total floor area of massing on site", builtArea, "sqft"));
		performance.primaryObjectives.add(new Objective(
			"Floor Area Ratio (FAR)", "ratio of built area to site area", far, "sqft/sqft"));
		
		// Calculate total areas itemized by use
		for (Function use : Function.values()) {
			String useName = "[" + use + "] ";
			float uArea = useArea.get(use);
			float useRatio;
			if (builtArea == 0) {
				useRatio = 0;
			} else {
				useRatio = uArea / builtArea;
			}
			
			// Add total area objectives itemized by use
			performance.primaryObjectives.add(new Objective(
				useName + "Built Area", "total floor area of " + use + "on site", uArea, "sqft"));
			performance.primaryObjectives.add(new Objective(
				useName + "Area Ratio", "portion of total " + use + "built area on site", 100 * useRatio, "%"));
		}

		return performance;
	}
}