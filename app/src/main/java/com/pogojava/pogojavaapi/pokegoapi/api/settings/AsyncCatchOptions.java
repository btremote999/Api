/*
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pogojava.pogojavaapi.pokegoapi.api.settings;

import com.pogojava.pogojavaapi.pokegoapi.api.PokemonGo;
import com.pogojava.pogojavaapi.pokegoapi.api.inventory.ItemBag;
import com.pogojava.pogojavaapi.pokegoapi.api.inventory.Pokeball;
import com.pogojava.pogojavaapi.pokegoapi.exceptions.NoSuchItemException;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import static POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId.ITEM_GREAT_BALL;
import static POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId.ITEM_MASTER_BALL;
import static POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId.ITEM_POKE_BALL;
import static POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId.ITEM_ULTRA_BALL;
import static com.pogojava.pogojavaapi.pokegoapi.api.inventory.Pokeball.GREATBALL;
import static com.pogojava.pogojavaapi.pokegoapi.api.inventory.Pokeball.MASTERBALL;
import static com.pogojava.pogojavaapi.pokegoapi.api.inventory.Pokeball.POKEBALL;
import static com.pogojava.pogojavaapi.pokegoapi.api.inventory.Pokeball.ULTRABALL;

/**
 * Created by LoungeKatt on 8/16/16.
 */
public class AsyncCatchOptions {

	private final PokemonGo api;
	private boolean useBestPokeball;
	private boolean skipMasterBall;
	private int razzBerries;
	private Pokeball pokeBall;
	private boolean strictBallType;
	private boolean smartSelect;
	private double probability;
	private double normalizedHitPosition;
	private double normalizedReticleSizeMin;
	private double normalizedReticleSizeMax;
	private double spinModifierMin;
	private double spinModifierMax;
	private int sleepBetweenTrialMin;
	private int sleepBetweenTrialMax;
	private int numThrows;

	/**
	 * Instantiates a new CatchOptions object.
	 *
	 * @param api the api
	 */
	public AsyncCatchOptions(PokemonGo api) {
		this.api = api;
		this.razzBerries = 0;
		this.useBestPokeball = false;
		this.skipMasterBall = false;
		this.pokeBall = POKEBALL;
		this.strictBallType = false;
		this.smartSelect = false;
		this.probability = 0;
		this.normalizedHitPosition = 1.0;

		this.normalizedReticleSizeMin = 1.95;
		this.normalizedReticleSizeMax = 1.99;

		this.spinModifierMin = 0.85;
		this.spinModifierMax = 1;

		this.sleepBetweenTrialMin = 3000;
		this.sleepBetweenTrialMax = 5000;

		this.numThrows = 1;
	}

	/**
	 * Gets item ball to catch a pokemon
	 *
	 * @return the item ball
	 * @throws NoSuchItemException the no such item exception
	 */
	public Pokeball getItemBall() throws NoSuchItemException {
		ItemBag bag = api.getInventories().getItemBag();
		if (strictBallType) {
			if (bag.getItem(pokeBall.getBallType()).getCount() > 0) {
				return pokeBall;
			} else if (useBestPokeball) {
				if (!skipMasterBall && bag.getItem(ITEM_MASTER_BALL).getCount() > 0) {
					return MASTERBALL;
				} else if (bag.getItem(ITEM_ULTRA_BALL).getCount() > 0) {
					return ULTRABALL;
				} else if (bag.getItem(ITEM_GREAT_BALL).getCount() > 0) {
					return GREATBALL;
				}
			}
			if (bag.getItem(ITEM_POKE_BALL).getCount() > 0) {
				return POKEBALL;
			}
		} else {
			int index = Arrays.asList(new ItemId[]{ITEM_MASTER_BALL, ITEM_ULTRA_BALL,
					ITEM_GREAT_BALL, ITEM_POKE_BALL}).indexOf(pokeBall.getBallType());

			if (useBestPokeball) {
				if (!skipMasterBall && index >= 0 && bag.getItem(ITEM_MASTER_BALL).getCount() > 0) {
					return MASTERBALL;
				} else if (index >= 1 && bag.getItem(ITEM_ULTRA_BALL).getCount() > 0) {
					return ULTRABALL;
				} else if (index >= 2 && bag.getItem(ITEM_GREAT_BALL).getCount() > 0) {
					return GREATBALL;
				} else if (bag.getItem(ITEM_POKE_BALL).getCount() > 0) {
					return POKEBALL;
				}
			} else {
				if (index <= 3 && bag.getItem(ITEM_POKE_BALL).getCount() > 0) {
					return POKEBALL;
				} else if (index <= 2 && bag.getItem(ITEM_GREAT_BALL).getCount() > 0) {
					return GREATBALL;
				} else if (index <= 1 && bag.getItem(ITEM_ULTRA_BALL).getCount() > 0) {
					return ULTRABALL;
				} else if (!skipMasterBall && bag.getItem(ITEM_MASTER_BALL).getCount() > 0) {
					return MASTERBALL;
				}
			}
		}
		if (smartSelect) {
			strictBallType = false;
			useBestPokeball = false;
			skipMasterBall = false;
			smartSelect = false;
			return getItemBall();
		}
		throw new NoSuchItemException();
	}

	/**
	 * Set if the bestBall have to be used with particular probability
	 *
	 * @param encounterProbability the capture probability to compare
	 */
	public void checkProbability(double encounterProbability) {
		if (encounterProbability >= probability) {
			useBestPokeball = false;
		} else {
			useBestPokeball = true;
		}
	}

	/**
	 * Enable or disable the use of razzberries
	 *
	 * @param berries the numbers of berries that should be used on this catch attemp
	 * @return the AsyncCatchOptions object
	 */
	public AsyncCatchOptions setRazzBerries(int berries) {
		this.razzBerries = berries;
		return this;
	}

	/**
	 * Set the number of throws (retries)
	 *
	 * @param numThrows number of retry that should be done during this catch
	 * @return the AsyncCatchOptions object
	 */
	public AsyncCatchOptions setNumThrows(int numThrows) {
		this.numThrows = numThrows;
		return this;
	}

	/**
	 * Set a specific Pokeball to use
	 *
	 * @param pokeBall the pokeball to use
	 * @return the AsyncCatchOptions object
	 */
	public AsyncCatchOptions usePokeball(Pokeball pokeBall) {
		this.pokeBall = pokeBall;
		return this;
	}

	/**
	 * Set using the best available ball
	 *
	 * @param useBestPokeball true or false
	 * @return the AsyncCatchOptions object
	 */
	public AsyncCatchOptions useBestBall(boolean useBestPokeball) {
		this.useBestPokeball = useBestPokeball;
		return this;
	}

	/**
	 * <pre>
	 * Set using only the defined ball type
	 *   combined with useBestBall: Sets the minimum
	 *   combined with usePokeball: Sets the maximum
	 *
	 *   without either will attempt the ball specified
	 *       or throw an error
	 * </pre>
	 *
	 * @param strictBallType true or false
	 * @return the AsyncCatchOptions object
	 */
	public AsyncCatchOptions noFallback(boolean strictBallType) {
		this.strictBallType = strictBallType;
		return this;
	}

	/**
	 * Set whether or not Master balls can be used
	 *
	 * @param skipMasterBall true or false
	 * @return the AsyncCatchOptions object
	 */
	public AsyncCatchOptions noMasterBall(boolean skipMasterBall) {
		this.skipMasterBall = skipMasterBall;
		return this;
	}

	/**
	 * Set whether or not to use adaptive ball selection
	 *
	 * @param smartSelect true or false
	 * @return the AsyncCatchOptions object
	 */
	public AsyncCatchOptions useSmartSelect(boolean smartSelect) {
		this.smartSelect = smartSelect;
		return this;
	}

	/**
	 * Set a capture probability before switching balls
	 * or the minimum probability for a specific ball
	 *
	 * @param probability the probability
	 * @return the AsyncCatchOptions object
	 */
	public AsyncCatchOptions withProbability(double probability) {
		this.probability = probability;
		return this;
	}

	/**
	 * Set the normalized hit position of a pokeball throw
	 *
	 * @param normalizedHitPosition the normalized position
	 * @return the AsynCatchOptions object
	 */
	public AsyncCatchOptions setNormalizedHitPosition(double normalizedHitPosition) {
		this.normalizedHitPosition = normalizedHitPosition;
		return this;
	}

	/**
	 * Set the normalized reticle for a pokeball throw
	 *
	 * @param normalizedReticleSizeMin the normalized size min val
	 * @param normalizedReticleSizeMax the normalized size max val
	 * @return the AsynCatchOptions object
	 */
	public AsyncCatchOptions setNormalizedReticleSize(double normalizedReticleSizeMin,
													  double normalizedReticleSizeMax) {
		this.normalizedReticleSizeMin = normalizedReticleSizeMin;
		this.normalizedReticleSizeMax = normalizedReticleSizeMax;
		return this;
	}

	/**
	 * Set the spin modifier of a pokeball throw
	 *
	 * @param spinModifierMin the spin modifier min val
	 * @param spinModifierMax the spin modifier max val
	 * @return the AsynCatchOptions object
	 */
	public AsyncCatchOptions setSpinModifier(double spinModifierMin, double spinModifierMax) {
		this.spinModifierMin = spinModifierMin;
		this.spinModifierMax = spinModifierMax;
		return this;
	}

	/**
	 * Set the spin modifier of a pokeball throw
	 *
	 * @param sleepBetweenTrialMin min time of sleep between trials in milli
	 * @param sleepBetweenTrialMax max time of sleep between trials in milli
	 * @return the AsynCatchOptions object
	 */
	public AsyncCatchOptions setSleepBetweenTrial(double sleepBetweenTrialMin, double sleepBetweenTrialMax) {
		this.spinModifierMin = spinModifierMin;
		this.spinModifierMax = spinModifierMax;
		return this;
	}

	/**
	 * Get a randomized value for normalReticlePosition
	 *
	 * @return random normalReticlePosition
	 */
	public double getNormalizedReticleSize() {
		return ThreadLocalRandom.current().nextDouble(normalizedReticleSizeMin, normalizedReticleSizeMax);
	}

	/**
	 * Get a randomized value for spinModifier
	 *
	 * @return random spinModifier
	 */
	public double getSpinModifier() {
		return ThreadLocalRandom.current().nextDouble(spinModifierMin, spinModifierMax);
	}

	/**
	 * Get a randomized pause between each catch attemp
	 *
	 * @return random spinModifier
	 */
	public int getSleepTime() {
		return ThreadLocalRandom.current().nextInt(sleepBetweenTrialMin, sleepBetweenTrialMax);
	}

	public int getRazzBerries() {
		return razzBerries;
	}

	public int getNumThrows() {
		return numThrows;
	}

	public double getNormalizedHitPosition() {
		return normalizedHitPosition;
	}
}
