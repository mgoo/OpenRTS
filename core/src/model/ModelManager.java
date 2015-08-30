package model;

import java.io.File;
import java.util.logging.Logger;

import brainless.openrts.event.BattleFieldUpdateEvent;
import brainless.openrts.event.EventManager;
import util.MapArtisanUtil;
import model.battlefield.Battlefield;
import model.battlefield.BattlefieldFactory;
import model.builders.entity.definitions.DefParser;

public class ModelManager {

	private static final Logger logger = Logger.getLogger(ModelManager.class.getName());

	public static final String CONFIG_PATH = "assets/data";
	public static final String DEFAULT_MAP_PATH = "assets/maps/";
	private static final double UPDATE_DELAY = 1000;
	private static final int DEFAULT_WIDTH = 64;
	private static final int DEFAULT_HEIGHT = 32;

	private static final BattlefieldFactory factory;

	private static Battlefield battlefield;
	private final static DefParser parser;
	private static double nextUpdate = 0;
	public static boolean battlefieldReady = true;

	static {
		parser = new DefParser(CONFIG_PATH);

		factory = new BattlefieldFactory();
		// setNewBattlefield();
	}

	
	ModelManager() {

	}

	public static void updateConfigs() {
		if (System.currentTimeMillis() > nextUpdate) {
			nextUpdate = System.currentTimeMillis() + UPDATE_DELAY;
			parser.readFiles();
		}
	}

	public static void loadBattlefield() {
		Battlefield loadedBattlefield = factory.loadWithFileChooser();
		setBattlefield(loadedBattlefield);
	}

	public static void loadBattlefield(String file) {
		Battlefield loadedBattlefield = factory.load(file);
		setBattlefield(loadedBattlefield);
	}

	public static void saveBattlefield() {
		factory.save(battlefield);
	}

	public static void setNewBattlefield() {
		setBattlefield(factory.getNew(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	static void setBattlefield(Battlefield battlefield) {
		if (battlefield != null) {
			ModelManager.battlefield = battlefield;
			battlefieldReady = true;
			MapArtisanUtil.act(getBattlefield().getMap());
			getBattlefield().getEngagement().reset();
			EventManager.post(new BattleFieldUpdateEvent());
			logger.info("Done.");

		}
	}

	public static void reload() {
		saveBattlefield();
		Battlefield loadedBattlefield = factory.load(battlefield.getFileName());
		setBattlefield(loadedBattlefield);
	}

	public static Battlefield getBattlefield() {
		if(battlefieldReady) {
			return battlefield;
		} else {
			throw new RuntimeException("Trying to acces to battlefield while it is unavailable");
		}
	}

	public static void setBattlefieldUnavailable(){
		battlefieldReady = false;
	}
	public static void setBattlefieldReady(){
		battlefieldReady = true;
	}

	public static Battlefield loadOnlyStaticValues(File file) {
		return factory.loadOnlyStaticValues(file);
	}
	
}
