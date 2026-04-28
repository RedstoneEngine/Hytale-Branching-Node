package dev.redengdev.test;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;

public class BranchingPathTypeAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, BranchingPathTypeAsset>> {

    //Currently not implemented!

   private String id;

    @Override
    public String getId() {
        return this.id;
    }
    
}
