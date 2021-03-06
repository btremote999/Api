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

package com.pogojava.pogojavaapi.pokegoapi.api.inventory;

import com.pogojava.pogojavaapi.pokegoapi.api.PokemonGo;
import com.pogojava.pogojavaapi.pokegoapi.api.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import POGOProtos.Data.PokemonDataOuterClass;
import POGOProtos.Enums.PokemonIdOuterClass;


public class PokeBank {
	private final ConcurrentMap<Long, Pokemon> pokemon = new ConcurrentHashMap<Long, Pokemon>();

	public PokeBank() {
	}

	/**
	 * Add a pokemon to the pokebank inventory.  Will not add duplicates (pokemon with same id), but update them!!
	 *
	 * @param api current api
	 * @param pokemonData proto of the new pokemon
	 */
	public void addPokemon(PokemonGo api, PokemonDataOuterClass.PokemonData pokemonData) {
		Pokemon current = pokemon.putIfAbsent(pokemonData.getId(), new Pokemon(api, pokemonData));
		if (current != null) {
			current.setProto(pokemonData);
		}

	}

	/**
	 * Gets pokemon by pokemon id.
	 *
	 * @param id the id
	 * @return the pokemon by pokemon id
	 */
	public List<Pokemon> getPokemonByPokemonId(final PokemonIdOuterClass.PokemonId id) {
		List<Pokemon> ret = new ArrayList<>();
		for (Pokemon p : pokemon.values()) {
			if (p.getPokemonId() == id) {
				ret.add(p);
			}
		}
		return ret;
	}

	/**
	 * Remove pokemon.
	 *
	 * @param pokemon the pokemon
	 */
	public void removePokemon(final Pokemon pokemon) {
		this.pokemon.remove(pokemon.getId());
	}

	/**
	 * Get a pokemon by id.
	 *
	 * @param id the id
	 * @return the pokemon
	 */
	public Pokemon getPokemonById(final Long id) {
		return pokemon.get(id);
	}


}
