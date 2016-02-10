/**
 * 
 */
package com.mgm.dmp.common.vo;

import java.util.List;

import com.mgm.dmp.common.model.PromotionTile;

/**
 * @author ssahu6
 *
 */
public class PromotionTileResponse extends SearchNPromoteResponse {
	private List<PromotionTile> tiles;

	/**
	 * @return the tiles
	 */
	public List<PromotionTile> getTiles() {
		return tiles;
	}

	/**
	 * @param tiles the tiles to set
	 */
	public void setTiles(List<PromotionTile> tiles) {
		this.tiles = tiles;
	}

}
