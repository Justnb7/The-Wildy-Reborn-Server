package com.venenatis.game.util.chance;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles a random chance.
 * 
 * @param <T>
 *            - The representation type.
 * @author Michael | Chex
 */
public class Chance<T> {
	
	public static enum ChanceType {
		ALWAYS(100),
		
		COMMON(100),
		UNCOMMON(80),
		RARE(10),
		VERY_RARE(5);
		
		private final double weight;
		
		private ChanceType(double weight) {
			this.weight = weight;
		}
		
		public double getWeight() {
			return weight;
		}
	}

	/**
	 * The list of weighted objects.
	 */
	private final List<WeightedObject<T>> objects;
	
	/**
	 * The sum of the weights.
	 */
	private double sum;

	/**
	 * Creates a new instance of the class.
	 * 
	 * @return A new {@link Chance} object.
	 */
	public Chance(List<WeightedObject<T>> objects) {
		this.objects = objects;
		sum = objects.stream().mapToDouble(WeightedObject::getWeight).sum();
		objects.sort((first, second) -> (int) Math.signum(second.getWeight() - first.getWeight()));
	}
	
	/**
	 * Creates a new instance of the class.
	 * 
	 * @return A new {@link Chance} object.
	 */
	public Chance(int capacity) {
		this.objects = new ArrayList<>(capacity);
		sum = 0;
	}
	
	/**
	 * Creates a new instance of the class.
	 * 
	 * @return A new {@link Chance} object.
	 */
	public Chance() {
		this.objects = new ArrayList<>();
		sum = 0;
	}

	/**
	 * Adds a new {@link WeightedObject} to the {@link #objects} list.
	 * 
	 * @param weight
	 *            - The weight of the object.
	 * @param t
	 *            - The represented object to add.
	 */
	public final void add(double weight, T t) {
		objects.add(new WeightedChance<T>(weight, t));
		sum += weight;
		
		objects.sort((first, second) -> (int) Math.signum(second.getWeight() - first.getWeight()));
	}
	
	/**
	 * Adds a new {@link WeightedObject} to the {@link #objects} list.
	 * 
	 * @param type
	 *            - The type of weight.
	 * @param t
	 *            - The represented object to add.
	 */
	public final void add(ChanceType type, T t) {
		add(type.getWeight(), t);
	}

	/**
	 * Generates a {@link WeightedObject}.
	 * 
	 * @return The {@link WeightedObject}.
	 */
	public WeightedObject<T> nextObject() {
		double rnd = Math.random() * sum;
		double hit = 0;

		for (WeightedObject<T> obj : objects) {
			hit += obj.getWeight();

			if (hit >= rnd) {
				return obj;
			}
		}

		throw new AssertionError("The random number [" + rnd + "] is too large!");
	}

	/**
	 * Generates a {@link WeightedObject}.
	 * 
	 * @param boost
	 *            A boost to make items from the rare drop table more common.
	 *            The boost is represented as a percentage increase, so the
	 *            domain must be between [0, 1). A boost of 0.5 is a 50% drop
	 *            rate increase, and a boost of 1.0 is 100% (or x2) drop rate
	 *            increase.
	 * @return The {@link WeightedObject}.
	 */
	public WeightedObject<T> nextObject(double boost) {
		if (boost <= 0 || boost > 1) {
			throw new IllegalArgumentException("Boost is outside of the domain: [0, 1).");
		}
		
		double rnd = Math.random() * sum;
		double hit = 0;
		
		for (WeightedObject<T> obj : objects) {
			hit += obj.getWeight() + boost;
			
			if ((int) (hit * (1 + boost)) >= (int) rnd) {
				return obj;
			}
		}
		
		throw new AssertionError("The random number [" + rnd + "] is too large!");
	}
	
	public double getSum() {
		return sum;
	}
	
	public List<WeightedObject<T>> getObjects() {
		return objects;
	}
	
	@Override
	public String toString() {
		return objects.toString();
	}
}