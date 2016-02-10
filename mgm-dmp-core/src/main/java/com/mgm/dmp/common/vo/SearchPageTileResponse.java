package com.mgm.dmp.common.vo;

import java.util.List;

import com.mgm.dmp.common.model.SearchPageTile;

public class SearchPageTileResponse extends SearchNPromoteResponse {

    private List<SearchPageTile> tiles;

    /**
     * @return the tiles
     */
    public List<SearchPageTile> getTiles() {
        return tiles;
    }

    /**
     * @param tiles the tiles to set
     */
    public void setTiles(List<SearchPageTile> tiles) {
        this.tiles = tiles;
    }
}
