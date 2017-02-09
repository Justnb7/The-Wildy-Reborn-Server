package com.model.game.character.npc.drops;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Percentage roll based system
 * 
 * used for npc drops
 * @author Optimum
 */
public final class PercentageRoll 
{
	
	/**
	 * Secure random to generate a random roll
	 */
	private static final Random RANDOM = new SecureRandom();
	
	/**
	 * Decimal formatter to format the 
	 * random percentage roll to 6 decimal places
	 */
	private static final DecimalFormat DF = new DecimalFormat("#.########");
	
	
	/**
	 * Attempts to roll with a percentage
	 * 
	 * @param percentage - the percentage
	 * 
	 * @return true if the roll is successful
	 * false if the roll fails
	 * 
	 * @throws IllegalArgumentException 
	 * if the percentage is above 100 or below 0
	 */
	public static boolean roll(float percentage) {
		if(percentage > 100) percentage = 100;
		if(percentage < 0) return false;
		
			double r = RANDOM.nextDouble() * 100;
			String f = DF.format(r);
			//System.out.println(String.format("double %f parsed %s", r, f));
			float roll = Float.parseFloat(f.replace(",", "."));
			// its just preference to Uk english rather than american english so . not ,
		return percentage >= roll;
	}
	
}//Optimums Code
