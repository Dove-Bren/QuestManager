/*
 *  QuestManager: An RPG plugin for the Bukkit API.
 *  Copyright (C) 2015-2016 Github Contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.skyisland.questmanager.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Stores a list of items associated with a weight.
 * This list is designed to be used to pull out random elements, and not for simple traversal.
 *
 * @param <T> The type of the elements of the weighted list.
 */
public class WeightedList<T>  {
	
	private static class Item<E> {
		private E o;
		
		private Double weight;
		
		public Item(E o, Double weight) {
			this.o = o;
			this.weight = weight;;
		}
		
		public E getObject() {
			return o;
		}
		
		public Double getWeight() {
			return weight;
		}
	}
	
	private List<Item<T>> list;
	
	private Random rand;
	
	public WeightedList() {
		this.list = new LinkedList<>();
		rand = new Random();
	}
	
	/**
	 * Adds an element to the list.
	 * <b>Note:</b> This method does not detect or avoid duplicates in any way.
	 */
	public void add(T object, Double weight) {
		list.add(new Item<>(object, weight));
	}
	
	/**
	 * Attempts to grab a random entry in the list (based on their weight) and reutrn it.
	 * @return <i>null</i> on error or if the list is empty, an object stored otherwise
	 */
	public T getRandom() {
		if (list.isEmpty()) {
			return null;
		}
		
		double max = 0;
		
		for (Item<T> i : list) {
			max += i.getWeight();
		}
		
		double index = rand.nextDouble() * max;
		max = 0;

		for (Item<T> i : list) {
			max += i.getWeight();

			if (max >= index) {
				return i.getObject();
			}
		}
		
		//if we get here, something went wrong
		return null;
	}
	
	/**
	 * Returns all elements stored in this list, without their associated weights.
	 */
	public Set<T> getElements() {
		Set<T> set = new HashSet<>();
		
		for (Item<T> i : list) {
			set.add(i.getObject());
		}
		
		return set;
	}
	
	public void clear() {
		if (list.isEmpty()) {
			return;
		}
		
		for (Item<T> item : list) {
			item.o = null;
		}
		
		list.clear();
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
}
