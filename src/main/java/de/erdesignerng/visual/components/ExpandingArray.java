package de.erdesignerng.visual.components;

/**
 * Simple expanding array. The array is expandable by amortized doubling with
 * number of valid items stored in count field with minimum MIN_SIZE.
 */
class ExpandingArray {
	/**
	 * Minimal size of the array.
	 */
	public final int MIN_SIZE = 20;

	/**
	 * Items of the array.
	 */
	public Object[] items;

	/**
	 * The number of valid items in the array.
	 */
	public int count = 0;

	public ExpandingArray(int initCount) {
		items = new Object[Math.max(initCount, MIN_SIZE)];
		count = initCount;
	}

	/**
	 * Fills the portion [start;end] of the array by item.
	 */
	public void fill(int start, int end, Object item) {
		for (int i = start; i <= end; i++)
			items[i] = item;
	}

	/**
	 * Shifts a part of the array starting from shiftStart by shiftLength
	 * elements to the right. Expands the array if necessary. Inserts null
	 * elements to empty positions.
	 *
	 * @param shiftStart  the first element index to be shifted
	 * @param shiftLength the number of elements to insert before the first shifted one
	 */
	public void shift(int shiftStart, int shiftLength) {
		int new_count = count + shiftLength;
		Object[] new_items;

		// expands the array if necessary:
		if (new_count > items.length)
			new_items = new Object[new_count << 1];
		else
			new_items = items;

		// elements preceding inserted ones:
		if (new_items != items)
			System.arraycopy(items, 0, new_items, 0, shiftStart);

		// elements following inserted ones:
		System.arraycopy(items, shiftStart, new_items,
				shiftStart + shiftLength, count - shiftStart);

		if (new_items == items && count > shiftStart)
			fill(shiftStart, shiftStart + shiftLength - 1, null);

		items = new_items;
		count = new_count;
	}

	/**
	 * Removes shiftLength elements starting from shiftStart one shifting
	 * following elements to the left. If the array is to long (its valid
	 * elements occupies its fourth or less) than it is shortened to one half.
	 *
	 * @param shiftStart  the first element index to be shifted
	 * @param shiftLength the number of elements to insert before the first shifted one
	 */
	public void unshift(int shiftStart, int shiftLength) {
		int new_count = count - shiftLength;
		Object[] new_items;

		// shrinks the array if possible:
		if (new_count < items.length >> 2 && items.length >> 1 > MIN_SIZE)
			new_items = new Object[items.length >> 1];
		else
			new_items = items;

		// elements preceding deleted ones:
		if (new_items != items)
			System.arraycopy(items, 0, new_items, 0, shiftStart);

		// elements following deleted ones:
		System.arraycopy(items, shiftStart + shiftLength, new_items,
				shiftStart, new_count - shiftStart);

		items = new_items;
		count = new_count;
	}

}
