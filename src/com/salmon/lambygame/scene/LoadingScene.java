package com.salmon.lambygame.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.salmon.lambygame.base.BaseScene;
import com.salmon.lambygame.manager.SceneManager.SceneType;


public class LoadingScene extends BaseScene {

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		attachChild(new Text(400, 240, resourcesManager.font, "Loading...", vbom));
	}

	@Override
	public void onBackKeyPressed() {
		return;
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		
	}

}
